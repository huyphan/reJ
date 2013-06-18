/* Copyright (C) 2004-2007 Sami Koivu
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.rej;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.files.FileSet;
import net.sf.rej.files.Project;
import net.sf.rej.gui.IterationContext;
import net.sf.rej.gui.IteratorAgent;
import net.sf.rej.gui.editor.iteration.IterationNotificationListener;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Code;
import net.sf.rej.java.Field;
import net.sf.rej.java.Interface;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.Attribute;
import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.java.attribute.LocalVariableTableAttribute;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.instruction.DecompilationContext;
import net.sf.rej.java.instruction.Instruction;

/**
 * Class that iterates through a <code>Project</code>, calling the Iterator agent for all the
 * components of the <code>Project</code>.
 * 
 * @author Sami Koivu
 */

public class ProjectIterator implements Runnable {
	private Project project;
	private List<? super IteratorAgent> agents = null;
	private IterationNotificationListener listener = null;

	public ProjectIterator(Project project, IteratorAgent agent) {
		this.project = project;
		this.agents = new ArrayList<IteratorAgent>();
		this.agents.add(agent);
	}

	public ProjectIterator(Project project, List<? super IteratorAgent> agents, IterationNotificationListener listener) {
		this.project = project;
		this.agents = agents;
		this.listener = listener;
	}

	public static void iterate(Project project, IteratorAgent agent) {
		ProjectIterator pi = new ProjectIterator(project, agent);
		new Thread(pi).start();
	}

	public static void iterate(Project project, List<? super IteratorAgent> agents, IterationNotificationListener listener) {
		ProjectIterator pi = new ProjectIterator(project, agents, listener);
		new Thread(pi).start();
	}

	public void run() {
		for(Object agent : this.agents) {
			iterateOneAgent((IteratorAgent)agent);
		}
		
		if (this.listener != null) {
			this.listener.finished();
		}
	}
	
	private void iterateOneAgent(IteratorAgent agent) {
		IterationContext ic = new IterationContext();
		ic.setProject(this.project);
		FileSet fs = this.project.getFileSet();
		List<String> contents = fs.getContentsList();
		agent.scopeChanged(0, contents.size());
		int progress = 0;
		for (String filename : contents) {
			agent.progressed(progress++);
			ic.setFilename(filename);
			agent.processFile(filename);
			if (!filename.endsWith(".class"))
				continue;
			try {
				ClassFile cf = this.project.getClassFile(filename);
				ic.setCf(cf);
				agent.processClass(ic, cf);
				
				ConstantPool cp = cf.getPool();
				for (int i=0; i < cp.size(); i++) {
					ConstantPoolInfo cpi = cp.get(i);
					if (cpi != null) {
						agent.processConstantPoolInfo(ic, cpi);						
					}
				}

				List<Interface> interfaces = cf.getInterfaces();
				for (Interface interface0 : interfaces) {
					agent.processInterface(ic, interface0.getName());
				}

				List<Field> fields = cf.getFields();
				for (Field field : fields) {
					ic.setField(field);
					agent.processField(ic, field);
				}

				List<Method> methods = cf.getMethods();
				for (Method method : methods) {
					ic.setMethod(method);
					agent.processMethod(ic, method);

					CodeAttribute ca = method.getAttributes().getCode();
					ic.setCodeAttribute(ca);
					if (ca != null) {
						processAttributes(ic, ca.getAttributes(), agent);
					}
					// method variables
					LocalVariableTableAttribute lvt = null;
					if (ca != null) {
						lvt = ca.getAttributes().getLocalVariableTable();
					}
					if (lvt != null) {
						List<LocalVariable> lvs = lvt.getLocalVariables();

						for (LocalVariable lv : lvs) {
							agent.processLocalVariable(ic, lv);
						}
					}

					// method code
					Code code = null;
					if (ca != null)
						code = ca.getCode();
					if (code != null) {
						DecompilationContext dc = code
								.createDecompilationContext();
						ic.setDc(dc);
						dc.setPosition(0);
						List<Instruction> instructions = code.getInstructions();
						for (Instruction instruction : instructions) {
							agent.processInstruction(ic, instruction);
							dc.incrementPosition(instruction);
						}
					}

					processAttributes(ic, method.getAttributes(), agent);

				}

				processAttributes(ic, cf.getAttributes(), agent);

				agent.postProcessFile(ic);
			} catch (Exception ex) {
				ex.printStackTrace();
				agent.processException(ex);
			}
		}
		agent.progressed(progress);
		agent.finished(ic, contents.size());
	}

	public void processAttributes(IterationContext ic, Attributes attrs, IteratorAgent agent) {
		for (Attribute attr : attrs.getAttributes()) {
			agent.processAttribute(ic, attr);
		}
	}

}

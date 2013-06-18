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
package net.sf.rej.gui.editor.iteration;

import net.sf.rej.gui.IterationContext;
import net.sf.rej.gui.IteratorAgentAdapter;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.tab.Tab;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Field;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.RefInfo;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.ParameterType;
import net.sf.rej.java.instruction.Parameters;

/**
 * <code>IteratorAgent</code> which gets a class name, field name and field
 * descriptor as parameters and finds all field references (and definitions)
 * which match them.
 * 
 * @author Sami Koivu
 */
public class FindFieldRefs extends IteratorAgentAdapter {
	private int resultCount = 0;

	private String className;

	private String fieldName;

	private Descriptor desc;

	public FindFieldRefs(String className, String fieldName, Descriptor desc) {
		this.className = className;
		this.fieldName = fieldName;
		this.desc = desc;
	}

	@Override
	public void processField(IterationContext sc, Field field) {
		boolean classNamesMatch = sc.getCf().getFullClassName().equals(
				className);
		boolean fieldNamesMatch = field.getName().equals(fieldName);
		boolean descriptorsMatch = field.getDescriptor().equals(desc);
		if (classNamesMatch && fieldNamesMatch && descriptorsMatch) {
			Link link = new Link();
			link.setText("Field definition : " + sc.getCf().getFullClassName() + "." + field.getName());
			link.setAnchor(Link.ANCHOR_FIELD_DEF);
			link.setProject(sc.getProject());
			link.setFile(sc.getFilename());
			link.setTab(Tab.EDITOR);
			link.setField(field);
			MainWindow.getInstance().getSearchTab().addResult(link);
			this.resultCount++;
		}
	}

	@Override
	public void processInstruction(IterationContext sc, Instruction instruction) {
		Parameters params = instruction.getParameters();
		for (int i = 0; i < params.getCount(); i++) {
			if (params.getType(i) == ParameterType.TYPE_CONSTANT_POOL_FIELD_REF) {
				ConstantPoolInfo cpi = sc.getDc().getConstantPool().get(
						params.getInt(i));
				RefInfo ri = (RefInfo) cpi;
				boolean classNamesMatch = ri.getClassName().equals(className);
				boolean fieldNamesMatch = ri.getTargetName().equals(fieldName);
				boolean descriptorsMatch = ri.getDescriptor().equals(desc);

				if (classNamesMatch && fieldNamesMatch && descriptorsMatch) {
					String instructionLine = instruction.getMnemonic() + " "
							+ instruction.getParameters().getString(sc.getDc());
					Link link = new Link();
					link.setText("Field Ref : " + sc.getCf().getFullClassName() + "." + sc.getMethod().getName() + " / " + instructionLine);
					link.setAnchor(Link.ANCHOR_METHOD_CODE);
					link.setProject(sc.getProject());
					link.setFile(sc.getFilename());
					link.setTab(Tab.EDITOR);
					link.setMethod(sc.getMethod());
					link.setPosition(sc.getDc().getPosition());

					MainWindow.getInstance().getSearchTab().addResult(link);
					this.resultCount++;
				}

			}
		}
	}

}

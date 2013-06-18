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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.rej.files.FileSet;
import net.sf.rej.gui.IterationContext;
import net.sf.rej.gui.IteratorAgentAdapter;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.Undoable;
import net.sf.rej.gui.action.ModifyClassInfoAction;
import net.sf.rej.gui.action.ModifyDescriptor;
import net.sf.rej.gui.action.ModifyNameAndTypeInfoAction;
import net.sf.rej.gui.action.RenameFieldAction;
import net.sf.rej.gui.action.RenameFileAction;
import net.sf.rej.gui.action.RenameMethodAction;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Field;
import net.sf.rej.java.JavaType;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.Method;
import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.DescriptorEnabled;
import net.sf.rej.java.constantpool.NameAndTypeInfo;
import net.sf.rej.java.constantpool.RefInfo;

public class RefactoringIterator extends IteratorAgentAdapter {
	
	private RefactoringAdvisor advisor;
	private boolean batchMode;
	private Map<String, String> oldClassNames = new HashMap<String, String>();
	
	public RefactoringIterator(RefactoringAdvisor advisor, boolean batchMode) {
		this.advisor = advisor;
		this.batchMode = batchMode;
	}
	
	@Override
	public void postProcessFile(IterationContext ic) {
		ClassFile cf = ic.getCf();
		String oldClassName = this.oldClassNames.get(ic.getCf().getFullClassName());
		if (oldClassName != null) {
			String newFileName = getNewFileName(ic.getFilename(), oldClassName, cf.getFullClassName());
			Undoable u = new RenameFileAction(ic.getProject(), ic.getFilename(), newFileName, ic.getCf());
			if (this.batchMode) {
				FileSet fs = ic.getProject().getFileSet();
				fs.removeFile(ic.getFilename());
				fs.addFile(newFileName);
			} else {
				SystemFacade.getInstance().performProjectAction(u);
			}
			ic.setFilename(newFileName);
		}
	}
	/**
	 * Determine new file name based on new class name.
	 * @param filename old filename
	 * @param oldClassName old class name
	 * @param newClassName new class name
	 * @return new filename
	 */
	private static String getNewFileName(String filename, String oldClassName, String newClassName) {
		// remove .class extension
		String newFileName = filename.substring(0, filename.lastIndexOf(".class"));
		String oldClassPart = oldClassName.replace('.', '/');
		String newClassPart = newClassName.replace('.', '/');
		newFileName = newFileName.substring(0, newFileName.lastIndexOf(oldClassPart));
		newFileName = newFileName + newClassPart + ".class";
		return newFileName;
	}
	
	private void processDescriptor(IterationContext ic, Descriptor desc, DescriptorEnabled descEnabled) {
		JavaType ret = desc.getReturn();
		boolean match = false;
		String newClassName = this.advisor.newClassNameFor(ret.getType());
		
		if (newClassName != null) {
			this.oldClassNames.put(newClassName, ret.getType());
			match = true;
			desc.setReturn(new JavaType(newClassName, ret.getDimensionCount()));
		}
		
		List params = desc.getParamList();
		List<JavaType> newParams = new ArrayList<JavaType>();
		for (int i=0; i < params.size(); i++) {
			JavaType param = (JavaType)params.get(i);
			newClassName = this.advisor.newClassNameFor(param.getType());
			if (newClassName != null) {
				this.oldClassNames.put(newClassName, param.getType());
				match = true;
				newParams.add(new JavaType(newClassName, param.getDimensionCount()));
			} else {
				newParams.add(param);
			}
		}
		
		if (match) {
			desc.setParamList(newParams);
			Undoable u = new ModifyDescriptor(ic.getCf().getPool(), desc, descEnabled);
			if (this.batchMode) {
				u.execute();
			} else {
				SystemFacade.getInstance().performAction(u, ic.getFilename());
			}
		}
	}
		
	@Override
	public void processConstantPoolInfo(IterationContext ic, ConstantPoolInfo cpi) {
		if(cpi.getType() == ConstantPoolInfo.CLASS) {
			ClassInfo ci = (ClassInfo)cpi;
			JavaType jt = new JavaType(ci.getName());
			String newClassName = this.advisor.newClassNameFor(jt.getType());
			if(newClassName != null) {
				this.oldClassNames.put(newClassName, jt.getType());
				Undoable u = new ModifyClassInfoAction(ic.getCf().getPool(), ci, newClassName + jt.getDimensions());
				if (this.batchMode) {
					u.execute();
				} else {
					SystemFacade.getInstance().performAction(u, ic.getFilename());
				}
			}
		} if(cpi.getType() == ConstantPoolInfo.NAME_AND_TYPE) {
			NameAndTypeInfo nati = (NameAndTypeInfo)cpi;
			Descriptor desc = nati.getDescriptor();
			JavaType ret = desc.getReturn();
			boolean match = false;
			String newClassName = this.advisor.newClassNameFor(ret.getType());
			if (newClassName != null) {
				this.oldClassNames.put(newClassName, ret.getType());
				match = true;
				desc.setReturn(new JavaType(newClassName, ret.getDimensionCount()));
			}
			
			List params = desc.getParamList();
			List<JavaType> newParams = new ArrayList<JavaType>();
			for (int i=0; i < params.size(); i++) {
				JavaType param = (JavaType)params.get(i);
				newClassName = this.advisor.newClassNameFor(param.getType());
				if (newClassName != null) {
					this.oldClassNames.put(newClassName, param.getType());
					match = true;
					newParams.add(new JavaType(newClassName, param.getDimensionCount()));
				} else {
					newParams.add(param);
				}
			}
			
			if (match) {
				desc.setParamList(newParams);
				Undoable u = new ModifyNameAndTypeInfoAction(ic.getCf().getPool(), nati, desc);
				if (this.batchMode) {
					u.execute();
				} else {
					SystemFacade.getInstance().performAction(u, ic.getFilename());
				}
			}
		} if(cpi.getType() == ConstantPoolInfo.FIELD_REF) {
			RefInfo ri = (RefInfo)cpi;
			String className = ri.getClassName();
			String newFieldName = this.advisor.newFieldNameFor(className, ri.getTargetName(), ri.getDescriptor());
			if (newFieldName != null) {
				NameAndTypeInfo nati = ri.getNameAndTypeInfo();
				
				Undoable u = new ModifyNameAndTypeInfoAction(ic.getCf().getPool(), nati, newFieldName);
				if (this.batchMode) {
					u.execute();
				} else {
					SystemFacade.getInstance().performAction(u, ic.getFilename());					
				}
			}
		} else if(cpi.getType() == ConstantPoolInfo.METHOD_REF || cpi.getType() == ConstantPoolInfo.INTERFACE_METHOD_REF) {
			RefInfo ri = (RefInfo)cpi;
			String className = ri.getClassName();
			String newMethodName = this.advisor.newMethodNameFor(className, ri.getTargetName(), ri.getDescriptor());
			if (newMethodName != null) {
				NameAndTypeInfo nati = ri.getNameAndTypeInfo();
				Undoable u = new ModifyNameAndTypeInfoAction(ic.getCf().getPool(), nati, newMethodName);
				if (this.batchMode) {
					u.execute();
				} else {
					SystemFacade.getInstance().performAction(u, ic.getFilename());					
				}
			}
		}
	}
	
	@Override
	public void processLocalVariable(IterationContext ic, LocalVariable lv) {
		// references to class which should be refactored in local variable type?
		Descriptor desc = lv.getDescriptor();
		processDescriptor(ic, desc, lv);
	}

	@Override
	public void processField(IterationContext ic, Field field) {
		// references to class which should be refactored in field type?
		Descriptor desc = field.getDescriptor();
		processDescriptor(ic, desc, field);

		// field should be refactored?		
		String className = ic.getCf().getFullClassName();
		String newFieldName = this.advisor.newFieldNameFor(className, field.getName(), field.getDescriptor());
		if (newFieldName != null) {
			Undoable u = new RenameFieldAction(ic.getCf().getPool(), field, newFieldName);
			if (this.batchMode) {
				u.execute();
			} else {
				SystemFacade.getInstance().performAction(u, ic.getFilename());					
			}
		}
	}
	
	@Override
	public void processMethod(IterationContext ic, Method method) {
		// references to class which should be refactored in parameters or return type?
		Descriptor desc = method.getDescriptor();
		processDescriptor(ic, desc, method);

		// method should be refactored?
		String className = ic.getCf().getFullClassName();
		String newMethodName = this.advisor.newMethodNameFor(className, method.getName(), method.getDescriptor());
		if (newMethodName != null) {
			Undoable u = new RenameMethodAction(ic.getCf().getPool(), method, newMethodName);
			if (this.batchMode) {
				u.execute();
			} else {
				SystemFacade.getInstance().performAction(u, ic.getFilename());					
			}
		}
	}
	
	@Override
	public void processException(Exception ex) {
		ex.printStackTrace();
	}

}

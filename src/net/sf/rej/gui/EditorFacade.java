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
package net.sf.rej.gui;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import net.sf.rej.AbstractIteratorAgent;
import net.sf.rej.Imports;
import net.sf.rej.ProjectIterator;
import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.files.Project;
import net.sf.rej.gui.action.AddConstantPoolInfoAction;
import net.sf.rej.gui.action.AddStringInfoAction;
import net.sf.rej.gui.action.GroupAction;
import net.sf.rej.gui.action.InsertFieldAction;
import net.sf.rej.gui.action.InsertInstructionAction;
import net.sf.rej.gui.action.InsertMethodAction;
import net.sf.rej.gui.action.ModifyClassPropertiesAction;
import net.sf.rej.gui.action.ModifyDoubleInfoAction;
import net.sf.rej.gui.action.ModifyFieldAction;
import net.sf.rej.gui.action.ModifyFloatInfoAction;
import net.sf.rej.gui.action.ModifyIntegerInfoAction;
import net.sf.rej.gui.action.ModifyLongInfoAction;
import net.sf.rej.gui.action.ModifyMethodAction;
import net.sf.rej.gui.action.ModifyStringInfoAction;
import net.sf.rej.gui.action.ModifyUTF8InfoAction;
import net.sf.rej.gui.action.MoveInstructionDownAction;
import net.sf.rej.gui.action.MoveInstructionUpAction;
import net.sf.rej.gui.action.RemoveFieldAction;
import net.sf.rej.gui.action.RemoveInstructionAction;
import net.sf.rej.gui.action.RemoveLastConstantPoolInfo;
import net.sf.rej.gui.action.RemoveMethodAction;
import net.sf.rej.gui.editor.Breakpoint;
import net.sf.rej.gui.editor.LineIdentifierMode;
import net.sf.rej.gui.editor.iteration.FindClassDefinition;
import net.sf.rej.gui.editor.iteration.FindClassRefs;
import net.sf.rej.gui.editor.iteration.FindFieldDefinition;
import net.sf.rej.gui.editor.iteration.FindFieldRefs;
import net.sf.rej.gui.editor.iteration.FindMethodDefinition;
import net.sf.rej.gui.editor.iteration.FindMethodRefs;
import net.sf.rej.gui.editor.iteration.RefactorClassNameAdvisor;
import net.sf.rej.gui.editor.iteration.RefactorFieldNameAdvisor;
import net.sf.rej.gui.editor.iteration.RefactorMethodNameAdvisor;
import net.sf.rej.gui.editor.iteration.RefactoringAdvisor;
import net.sf.rej.gui.editor.iteration.RefactoringIterator;
import net.sf.rej.gui.editor.row.CodeRow;
import net.sf.rej.gui.editor.row.FieldDefRow;
import net.sf.rej.gui.editor.row.MethodDefRow;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.tab.DebugTab;
import net.sf.rej.gui.tab.Tab;
import net.sf.rej.gui.tab.Tabbable;
import net.sf.rej.java.AccessFlags;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Code;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Field;
import net.sf.rej.java.Interface;
import net.sf.rej.java.JavaType;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.LocalVariableTypeTableAttribute;
import net.sf.rej.java.attribute.RuntimeInvisibleAnnotationsAttribute;
import net.sf.rej.java.attribute.RuntimeVisibleAnnotationsAttribute;
import net.sf.rej.java.attribute.SignatureAttribute;
import net.sf.rej.java.attribute.annotations.Annotation;
import net.sf.rej.java.attribute.annotations.ArrayValue;
import net.sf.rej.java.attribute.annotations.ClassInfoValue;
import net.sf.rej.java.attribute.annotations.ConstantValue;
import net.sf.rej.java.attribute.annotations.ElementValue;
import net.sf.rej.java.attribute.annotations.EnumValue;
import net.sf.rej.java.attribute.annotations.NestedAnnotationValue;
import net.sf.rej.java.attribute.generics.BoundTypeArgument;
import net.sf.rej.java.attribute.generics.FormalTypeParameter;
import net.sf.rej.java.attribute.generics.GenericJavaType;
import net.sf.rej.java.attribute.generics.LocalVariableTypeEntry;
import net.sf.rej.java.attribute.generics.Signature;
import net.sf.rej.java.attribute.generics.Signatures;
import net.sf.rej.java.attribute.generics.TypeArgument;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.DoubleInfo;
import net.sf.rej.java.constantpool.FloatInfo;
import net.sf.rej.java.constantpool.IntegerInfo;
import net.sf.rej.java.constantpool.LongInfo;
import net.sf.rej.java.constantpool.NameAndTypeInfo;
import net.sf.rej.java.constantpool.StringInfo;
import net.sf.rej.java.constantpool.UTF8Info;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Parameters;

/**
 * <code>EditorFacade</code> class is a common entrance point for all actions
 * in the editor.
 * 
 * @author Sami Koivu
 */
public class EditorFacade implements EventObserver {

	private static EditorFacade instance = new EditorFacade();

	private LineIdentifierMode lineMode = new LineIdentifierMode();

	private String openFile = null;
	private Project openProject = null;
	
	private Collection<Breakpoint> breakpoints = new HashSet<Breakpoint>();
	
	private EventDispatcher dispatcher;

	private ConstantPoolTranslationMode cpTranslationMode = ConstantPoolTranslationMode.TRANSLATION;

	private EditorFacade() {
		// empty private constructor
	}

	public synchronized static EditorFacade getInstance() {
		return instance;
	}

	public void remove(List list) {
		GroupAction ga = new GroupAction();
		for (int i = list.size() - 1; i >= 0; i--) {
			Object obj = list.get(i);
			if (obj instanceof CodeRow) {
				CodeRow cr = (CodeRow) obj;
				RemoveInstructionAction ria = new RemoveInstructionAction(cr
						.getParentCode(), cr.getInstruction());
				ga.add(ria);
			} else if (obj instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow) obj;
				RemoveMethodAction rma = new RemoveMethodAction(mdr
						.getClassFile(), mdr.getMethod());
				ga.add(rma);
			} else if (obj instanceof FieldDefRow) {
				FieldDefRow fdr = (FieldDefRow) obj;
				RemoveFieldAction rfa = new RemoveFieldAction(fdr
						.getClassFile(), fdr.getField());
				ga.add(rfa);
			} else {
				throw new AssertionError("Object of invalid type in list: " + obj.getClass());
			}

		}
		
		SystemFacade.getInstance().performAction(ga);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void insertInstruction(Instruction inst, int pc, Code code) {
		InsertInstructionAction iia = new InsertInstructionAction(inst, pc,
				code);
		SystemFacade.getInstance().performAction(iia, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void insertMethod(ClassFile cf, String methodName, Descriptor desc,
			AccessFlags accessFlags, int maxStackSize, int maxLocals,
			List<String> exceptions) {
		InsertMethodAction ima = new InsertMethodAction(cf, methodName, desc,
				accessFlags, maxStackSize, maxLocals, exceptions);
		SystemFacade.getInstance().performAction(ima, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void insertField(ClassFile cf, String fieldName, Descriptor desc,
			AccessFlags flags) {
		InsertFieldAction ifa = new InsertFieldAction(cf, fieldName, desc,
				flags);
		SystemFacade.getInstance().performAction(ifa, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void insertLocalVariable() {
		// TODO: implement insert local variable
	}

	public void performUndo() {
		SystemFacade.getInstance().performUndo(this.openFile);
	}

	public void performRedo() {
		SystemFacade.getInstance().performRedo(this.openFile);
	}

	public void moveInstructionUp(Instruction instruction, Code code) {
		MoveInstructionUpAction miua = new MoveInstructionUpAction(instruction,
				code);
		SystemFacade.getInstance().performAction(miua, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void moveInstructionDown(Instruction instruction, Code code) {
		MoveInstructionDownAction mida = new MoveInstructionDownAction(
				instruction, code);
		SystemFacade.getInstance().performAction(mida, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void modifyClass(ClassFile cf, AccessFlags flags, String className,
			String superName, List<Interface> remainingInterfaces, List<String> newInterfaces) {
		ModifyClassPropertiesAction mcpa = new ModifyClassPropertiesAction(cf,
				flags, className, superName, remainingInterfaces, newInterfaces);
		SystemFacade.getInstance().performAction(mcpa, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void modifyMethod(ConstantPool cp, Method method, String name,
			Descriptor desc, AccessFlags flags, int maxStack, int maxLocals,
			List exceptions) {
		ModifyMethodAction mma = new ModifyMethodAction(cp, method, name, desc,
				flags, maxStack, maxLocals, exceptions);
		SystemFacade.getInstance().performAction(mma, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void modifyField(ConstantPool pool, Field field, String name,
			Descriptor desc, AccessFlags flags) {
		ModifyFieldAction mfa = new ModifyFieldAction(pool, field, name, desc,
				flags);
		SystemFacade.getInstance().performAction(mfa, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void removeLastConstantPoolItem(ConstantPool cp) {
		RemoveLastConstantPoolInfo rlcpi = new RemoveLastConstantPoolInfo(cp);
		SystemFacade.getInstance().performAction(rlcpi, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));		
	}

	public void modifyUTF8Info(UTF8Info info, String newValue) {
		ModifyUTF8InfoAction mui = new ModifyUTF8InfoAction(info, newValue);
		SystemFacade.getInstance().performAction(mui, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void modifyDoubleInfo(DoubleInfo info, double newValue) {
		ModifyDoubleInfoAction mdia = new ModifyDoubleInfoAction(info, newValue);
		SystemFacade.getInstance().performAction(mdia, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void modifyStringInfo(StringInfo info, String newValue) {
		ModifyStringInfoAction msia = new ModifyStringInfoAction(info, newValue);
		SystemFacade.getInstance().performAction(msia, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void addStringInfo(ConstantPool cp, String str) {
		AddStringInfoAction asia = new AddStringInfoAction(cp, str);
		SystemFacade.getInstance().performAction(asia, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void addConstantPoolInfo(ConstantPool cp, ConstantPoolInfo item) {
		AddConstantPoolInfoAction asia = new AddConstantPoolInfoAction(cp, item);
		SystemFacade.getInstance().performAction(asia, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void modifyLongInfo(LongInfo info, long newValue) {
		ModifyLongInfoAction mlia = new ModifyLongInfoAction(info, newValue);
		SystemFacade.getInstance().performAction(mlia, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void modifyIntegerInfo(IntegerInfo info, int newValue) {
		ModifyIntegerInfoAction miia = new ModifyIntegerInfoAction(info,
				newValue);
		SystemFacade.getInstance().performAction(miia, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public void modifyFloatInfo(FloatInfo info, float newValue) {
		ModifyFloatInfoAction mfia = new ModifyFloatInfoAction(info, newValue);
		SystemFacade.getInstance().performAction(mfia, this.openFile);
		this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
	}

	public LineIdentifierMode getLineIdentifierMode() {
		return this.lineMode;
	}

	public void setLineMode(int mode) {
		this.lineMode.setMode(mode);
		this.dispatcher.notifyObservers(new Event(EventType.DISPLAY_PARAMETER_UPDATE));
	}
	
	public void setConstantPoolTranslationMode(ConstantPoolTranslationMode mode) {
		this.cpTranslationMode  = mode;
		this.dispatcher.notifyObservers(new Event(EventType.DISPLAY_PARAMETER_UPDATE));		
	}
	
	public ConstantPoolTranslationMode getConstantPoolTranslationMode() {
		return this.cpTranslationMode;
	}

	public void findMethodRefs(String className, String methodName,
			Descriptor desc) {
		MainWindow.getInstance().getSearchTab().clear();
		AbstractIteratorAgent iterator = new FindMethodRefs(className, methodName, desc);
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
		MainWindow.getInstance().setTab(Tab.SEARCH);
		SystemFacade.getInstance().search(iterator);
	}

	public void findFieldRefs(String className, String fieldName, Descriptor desc) {
		MainWindow.getInstance().getSearchTab().clear();
		AbstractIteratorAgent iterator = new FindFieldRefs(className, fieldName, desc); 
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
		MainWindow.getInstance().setTab(Tab.SEARCH);
		SystemFacade.getInstance().search(iterator);
	}

	public void findClassRefs(String className) {
		MainWindow.getInstance().getSearchTab().clear();
		AbstractIteratorAgent iterator = new FindClassRefs(className); 
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
		MainWindow.getInstance().setTab(Tab.SEARCH);
		SystemFacade.getInstance().search(iterator);
	}

	public void gotoMethodDefinition(String className, String methodName,
			Descriptor desc) {
		try {
			ClassIndex ci = SystemFacade.getInstance().getClassIndex();
			ClassLocator cl = ci.getLocator(className);
			if (cl != null && cl.getFileSet().equals(this.openProject.getFileSet())) {
				ClassFile cf = SystemFacade.getInstance().getClassFile(cl);
				List methods = cf.getMethods();
				Method method = null;
				for (int i = 0; i < methods.size(); i++) {
					Method m = (Method) methods.get(i);
					if (m.getName().equals(methodName)
							&& m.getDescriptor().equals(desc)) {
						method = m;
						break;
					}
				}

				if (method != null) {
					Link link = new Link();
		        	link.setText("Method definition : " + className + "." + methodName);
		        	link.setAnchor(Link.ANCHOR_METHOD_DEF);
		        	link.setProject(this.openProject);
		        	link.setFile(cl.getFile());
		        	link.setTab(Tab.EDITOR);
		        	link.setMethod(method);

					SystemFacade.getInstance().goTo(link);
				}
			}
		} catch (Exception e) {
			SystemFacade.getInstance().handleException(e);
		}
	}

	public void gotoFieldDefinition(String className, String fieldName,
			Descriptor desc) {
		try {
			ClassIndex ci = SystemFacade.getInstance().getClassIndex();
			ClassLocator cl = ci.getLocator(className);
			if (cl != null && cl.getFileSet().equals(this.openProject.getFileSet())) {
				ClassFile cf = SystemFacade.getInstance().getClassFile(cl);
				List fields = cf.getFields();
				Field field = null;
				for (int i = 0; i < fields.size(); i++) {
					Field f = (Field) fields.get(i);
					if (f.getName().equals(fieldName)
							&& f.getDescriptor().equals(desc)) {
						field = f;
						break;
					}
				}

				if (field != null) {
					Link link = new Link();
					link.setText("Field definition : " + className + "." + fieldName);
					link.setAnchor(Link.ANCHOR_FIELD_DEF);
					link.setProject(this.openProject);
					link.setFile(cl.getFile());
					link.setTab(Tab.EDITOR);
					link.setField(field);
					SystemFacade.getInstance().goTo(link);
				}
			}
		} catch (Exception e) {
			SystemFacade.getInstance().handleException(e);
		}
	}

	public void gotoClassDefinition(String className) {
		try {
			ClassIndex ci = SystemFacade.getInstance().getClassIndex();
			ClassLocator cl = ci.getLocator(className);

			if (cl != null && cl.getFileSet().equals(this.openProject.getFileSet())) {
				ClassFile cf = SystemFacade.getInstance().getClassFile(cl);
				Link link = new Link();
				link.setText("Class definition : " + cf.getFullClassName());
				link.setAnchor(Link.ANCHOR_CLASS_DEF);
				link.setProject(this.openProject);
				link.setFile(cl.getFile());
				link.setTab(Tab.EDITOR);
				SystemFacade.getInstance().goTo(link);
			} else {
				SystemFacade.getInstance().setStatus("No class definition found in project for class " + className);
			}
		} catch (Exception e) {
			SystemFacade.getInstance().handleException(e);
		}
	}

	public void findMethodDefinition(String className,
			String methodName, Descriptor desc) {
		MainWindow.getInstance().getSearchTab().clear();
		AbstractIteratorAgent iterator = new FindMethodDefinition(className, methodName, desc); 
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
		MainWindow.getInstance().setTab(Tab.SEARCH);
		SystemFacade.getInstance().search(iterator);
	}

	public void findFieldDefinition(String className,
			String fieldName, Descriptor desc) {
		MainWindow.getInstance().getSearchTab().clear();
		AbstractIteratorAgent iterator = new FindFieldDefinition(className, fieldName, desc);
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
		MainWindow.getInstance().setTab(Tab.SEARCH);
		SystemFacade.getInstance().search(iterator);
	}

	public void findClassDefinition(String className) {
		MainWindow.getInstance().getSearchTab().clear();
		AbstractIteratorAgent iterator = new FindClassDefinition(className); 
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
		MainWindow.getInstance().setTab(Tab.SEARCH);
		SystemFacade.getInstance().search(iterator);
	}

	public void refactorClassName(String oldClassName, String newClassName) {
		RefactoringAdvisor advisor = new RefactorClassNameAdvisor(oldClassName, newClassName);
		AbstractIteratorAgent iterator = new RefactoringIterator(advisor, false);
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
        ProjectIterator.iterate(this.openProject, iterator);
	}

	public void refactorMethodName(String className, Descriptor desc, String oldMethodName, String newMethodName) {
		RefactoringAdvisor advisor = new RefactorMethodNameAdvisor(className, desc, oldMethodName, newMethodName);
		AbstractIteratorAgent iterator = new RefactoringIterator(advisor, false);
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
        ProjectIterator.iterate(this.openProject, iterator);
	}

	public void refactorFieldName(String className, Descriptor desc, String oldFieldName, String newFieldName) {
		RefactoringAdvisor advisor = new RefactorFieldNameAdvisor(className, desc, oldFieldName, newFieldName);
		AbstractIteratorAgent iterator = new RefactoringIterator(advisor, false);
		iterator.setProgressMonitor(SystemFacade.getInstance()
				.getProgressMonitor());
        ProjectIterator.iterate(this.openProject, iterator);
	}

    public Imports getImports(ClassFile cf) {
		Imports imports = new Imports(cf.getPackageName());
		ConstantPool pool = cf.getPool();

		for (int i = 0; i < pool.size(); i++) {
			ConstantPoolInfo cpi = pool.get(i);
			if (cpi != null) {
				if (cpi.getType() == ConstantPoolInfo.CLASS) {
					// there can be array definitions here, too
					JavaType cls = new JavaType(cpi.getValue());
					imports.addType(cls.getType());
				} else if (cpi.getType() == ConstantPoolInfo.NAME_AND_TYPE) {
					NameAndTypeInfo nati = (NameAndTypeInfo) cpi;
					Descriptor desc = nati.getDescriptor();
					if (!desc.getReturn().isPrimitive()) {
						imports.addType(desc.getReturn().getType());
					}

					List al = desc.getParamList();
					for (int j = 0; j < al.size(); j++) {
						JavaType item = (JavaType) al.get(j);
						if (!item.isPrimitive()) {
							imports.addType(item.getType());
						}
					}
				}

			}
		}
		
		getAnnotationTypeImports(imports, cf.getAttributes());
		getSignatureImports(imports, cf.getAttributes());
		for (Field field : cf.getFields()) {
			getAnnotationTypeImports(imports, field.getAttributes());
			getDescriptorImports(imports, field.getDescriptor());
			getSignatureImports(imports, field.getAttributes());
		}
		
		for (Method method : cf.getMethods()) {
			getAnnotationTypeImports(imports, method.getAttributes());
			getDescriptorImports(imports, method.getDescriptor());
			getSignatureImports(imports, method.getAttributes());
			getLocalVariableTypeTableImports(imports, method.getAttributes());
		}

		return imports;
	}
    
    private static void getDescriptorImports(Imports imports, Descriptor desc) {
    	JavaType retType = desc.getReturn();
    	if (!retType.isPrimitive()) {
    		imports.addType(retType.getType());
    	}
    	
    	for (JavaType param : desc.getParamList()) {
    		if (!param.isPrimitive()) {
    			imports.addType(param.getType());
    		}
    	}
    }
    
    private static void getSignatureImports(Imports imports, Attributes attrs) {
    	SignatureAttribute attr = attrs.getSignatureAttribute();
    	if (attr != null) {
    		Signature signature = Signatures.getSignature(attr.getSignatureString());
    		List<FormalTypeParameter> typeParams = signature.getFormalTypeParameters();
    		if (typeParams != null) {
    			for (FormalTypeParameter typeParam : typeParams) {
    				for (GenericJavaType type : typeParam.getTypeUnion()) {
    					getGenericJavaTypeImports(imports, type);
    				}
    			}
    		}
    		
    		List<GenericJavaType> methodParams = signature.getMethodParameters();
    		if (methodParams != null) {
        		for (GenericJavaType param : methodParams) {
        			getGenericJavaTypeImports(imports, param);
        		}    			
    		}
    		List<GenericJavaType> types = signature.getTypes();
    		for (GenericJavaType type : types) {
    			getGenericJavaTypeImports(imports, type);
    		}
    	}
    }

    private static void getGenericJavaTypeImports(Imports imports, GenericJavaType type) {
		JavaType jt = type.getBaseType();
		if (!jt.isPrimitive()) {
			imports.addType(jt.getType());
		}
		for (TypeArgument arg : type.getTypeArguments()) {
			if (arg instanceof GenericJavaType) {
				getGenericJavaTypeImports(imports, (GenericJavaType) arg);
			} else if (arg instanceof BoundTypeArgument) {
				BoundTypeArgument bound = (BoundTypeArgument) arg;
				getGenericJavaTypeImports(imports, bound.getBound());				
			}
		}
	}

	private static void getLocalVariableTypeTableImports(Imports imports, Attributes attrs) {
    	LocalVariableTypeTableAttribute attr = attrs.getLocalVariableTypeTable();
    	if (attr != null) {
    		for (LocalVariableTypeEntry entry : attr.getEntries()) {
    			Signature sig = Signatures.getSignature(entry.getSignatureString());
        		List<GenericJavaType> types = sig.getTypes();
        		for (GenericJavaType type : types) {
        			getGenericJavaTypeImports(imports, type);
        		}    			
    		}
    	}
    }

    private static void getAnnotationTypeImports(Imports imports, Attributes attrs) {
    	RuntimeInvisibleAnnotationsAttribute attrInv = attrs.getRuntimeInvisibleAnnotationsAttribute();
    	if (attrInv != null) {
    		getAnnotationTypeImports(imports, attrInv.getAnnotations());
    	}
    	RuntimeVisibleAnnotationsAttribute attr = attrs.getRuntimeVisibleAnnotationsAttribute();
    	if (attr != null) {
    		getAnnotationTypeImports(imports, attr.getAnnotations());
    	}
    }
    
    private static void getAnnotationTypeImports(Imports imports, List<Annotation> annotations) {
    	for (Annotation annotation : annotations) {
    		getAnnotationTypeImports(imports, annotation);
    	}
    }
    
    private static void getAnnotationTypeImports(Imports imports, Annotation annotation) {
		imports.addType(annotation.getName());
		for (Entry<String, ElementValue> entry : annotation.getElementValues().entrySet()) {
			getAnnotationTypeImports(imports, entry.getValue());
		}
    	
    }

    private static void getAnnotationTypeImports(Imports imports, ElementValue ev) {
    	if (ev instanceof ArrayValue) {
    		ArrayValue av = (ArrayValue)ev;
    		List<ElementValue> values = av.getArray();
    		for (ElementValue value : values) {
    			getAnnotationTypeImports(imports, value);
    		}
    	} else if (ev instanceof ClassInfoValue) {
    		imports.addType(ev.getValue());
    	} else if (ev instanceof ConstantValue) {
    		// no types
    	} else if (ev instanceof EnumValue) {
    		EnumValue en = (EnumValue)ev;
    		imports.addType(en.getTypeName());
    	} else if (ev instanceof NestedAnnotationValue) {
    		NestedAnnotationValue nested = (NestedAnnotationValue)ev;
    		getAnnotationTypeImports(imports, nested.getAnnotation());
    	}
    }
    
    /**
     * Return a String describing a Method in this class, using the import automizer
     * given as a parameter.
	 *
	 * @param imports Imports object for determining short names of types
     * @param desc object describing the type and parameters of the method.
     * @param className name of the class.
     * @param methodName name of the method.
     * @return A String with the class names edited to reflect the imports.
     */
    public String getMethodString(Imports imports, Descriptor desc, String className, String methodName) {
        StringBuffer sb = new StringBuffer();
        JavaType ret = desc.getReturn();
        if (ret.isPrimitive()) {
            sb.append(ret);
        } else {
            sb.append(imports.getShortName(ret.getType()));
            sb.append(ret.getDimensions());
        }
        sb.append(" ");
        sb.append(imports.getShortName(className));
        sb.append(".");
        sb.append(methodName);
        sb.append("(");
        List al = desc.getParamList();
        for (int i = 0; i < al.size(); i++) {
            JavaType jt = (JavaType) al.get(i);
            if (i > 0) sb.append(", ");
            if (jt.isPrimitive()) {
                sb.append(jt.toString());
            } else {
                sb.append(imports.getShortName(jt.getType()));
                sb.append(jt.getDimensions());
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
	 * Return a String describing a Field in this class, using the import
	 * automizer given as a parameter.
	 * 
	 * @param imports Imports object for determining short names of types
	 * @param desc object describing the type of the field
	 * @param className name of the class.
	 * @param fieldName name of the field.
	 * @return A String with the class names edited to reflect the
	 *         imports.
	 */
    public String getFieldString(Imports imports, Descriptor desc, String className, String fieldName) {
        StringBuffer sb = new StringBuffer();
        JavaType ret = desc.getReturn();
        if (ret.isPrimitive()) {
            sb.append(ret);
        } else {
            sb.append(imports.getShortName(ret.getType()));
            sb.append(ret.getDimensions());
        }
        sb.append(" ");
        sb.append(imports.getShortName(className));
        sb.append(".");
        sb.append(fieldName);
        return sb.toString();
    }

	public void addBreakPoint(Breakpoint breakpoint) {
		Tabbable debugTab = MainWindow.getInstance().getDebugTab();
		if (debugTab instanceof DebugTab) {
			this.breakpoints.add(breakpoint);
			((DebugTab)debugTab).addBreakpoint(breakpoint);
		}
	}

	public void removeBreakpoint(Breakpoint breakpoint) {
		Tabbable debugTab = MainWindow.getInstance().getDebugTab();
		if (debugTab instanceof DebugTab) {
			this.breakpoints.remove(breakpoint);
			((DebugTab)debugTab).removeBreakpoint(breakpoint);
		}
	}

	public Breakpoint getBreakpoint(String className, String methodName, Descriptor descriptor, int pc) {
		for (Breakpoint bp : this.breakpoints) {
			if (bp.getClassName().equals(className)
			 && bp.getMethodName().equals(methodName)
			 && bp.getMethodDesc().equals(descriptor)
			 && bp.getPc() == pc) {
				return bp;
			}
		}
		return null;
	}
	
	public Collection<Breakpoint> getBreakpoints() {
		return this.breakpoints;
	}

	public void processEvent(Event event) {
		switch (event.getType()) {
		case INIT:
			this.dispatcher = event.getDispatcher();
			break;
		case PROJECT_UPDATE:
			this.openProject = event.getProject();
			break;
		case CLASS_OPEN:
			this.openFile = event.getFile();
			break;
		case CLASS_UPDATE:
		case CLASS_REPARSE:
		case CLASS_PARSE_ERROR:
		case DISPLAY_PARAMETER_UPDATE:
		case DEBUG_ATTACH:
		case DEBUG_DETACH:
		case DEBUG_RESUMED:
		case DEBUG_SUSPENDED:
		case DEBUG_THREAD_CHANGE_REQUESTED:
		case DEBUG_STEP_INTO_REQUESTED:
		case DEBUG_STEP_OUT_REQUESTED:
		case DEBUG_STEP_OVER_REQUESTED:
		case DEBUG_RESUME_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGE_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGED:
		case DEBUG_SUSPEND_REQUESTED:
		case DEBUG_THREAD_CHANGED:
			// do nothing
			break;
		}
	}

}
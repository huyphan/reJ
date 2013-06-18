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
package net.sf.rej.gui.editor.rendering;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.sf.rej.Imports;
import net.sf.rej.gui.ConstantPoolTranslationMode;
import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.editor.LineIdentifierMode;
import net.sf.rej.gui.editor.row.ClassAnnotationDefRow;
import net.sf.rej.gui.editor.row.ClassCommentRow;
import net.sf.rej.gui.editor.row.ClassDefRow;
import net.sf.rej.gui.editor.row.CodeRow;
import net.sf.rej.gui.editor.row.DeprecatedAnnotationDefRow;
import net.sf.rej.gui.editor.row.EditorRow;
import net.sf.rej.gui.editor.row.FieldAnnotationDefRow;
import net.sf.rej.gui.editor.row.FieldDefRow;
import net.sf.rej.gui.editor.row.ImportDefRow;
import net.sf.rej.gui.editor.row.LabelRow;
import net.sf.rej.gui.editor.row.LocalVariableDefRow;
import net.sf.rej.gui.editor.row.MethodAnnotationDefRow;
import net.sf.rej.gui.editor.row.MethodDefRow;
import net.sf.rej.gui.editor.row.PackageDefRow;
import net.sf.rej.gui.preferences.Settings;
import net.sf.rej.java.AccessFlags;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Field;
import net.sf.rej.java.Interface;
import net.sf.rej.java.JavaType;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.java.attribute.ExceptionDescriptor;
import net.sf.rej.java.attribute.LocalVariableTableAttribute;
import net.sf.rej.java.attribute.SignatureAttribute;
import net.sf.rej.java.attribute.annotations.Annotation;
import net.sf.rej.java.attribute.annotations.ArrayValue;
import net.sf.rej.java.attribute.annotations.ClassInfoValue;
import net.sf.rej.java.attribute.annotations.ConstantValue;
import net.sf.rej.java.attribute.annotations.ElementValue;
import net.sf.rej.java.attribute.annotations.EnumValue;
import net.sf.rej.java.attribute.annotations.NestedAnnotationValue;
import net.sf.rej.java.attribute.generics.Any;
import net.sf.rej.java.attribute.generics.BoundTypeArgument;
import net.sf.rej.java.attribute.generics.ClassSignature;
import net.sf.rej.java.attribute.generics.FieldSignature;
import net.sf.rej.java.attribute.generics.FormalTypeParameter;
import net.sf.rej.java.attribute.generics.GenericJavaType;
import net.sf.rej.java.attribute.generics.MethodSignature;
import net.sf.rej.java.attribute.generics.Signatures;
import net.sf.rej.java.attribute.generics.TypeArgument;
import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.DoubleInfo;
import net.sf.rej.java.constantpool.FloatInfo;
import net.sf.rej.java.constantpool.IntegerInfo;
import net.sf.rej.java.constantpool.LongInfo;
import net.sf.rej.java.constantpool.RefInfo;
import net.sf.rej.java.constantpool.StringInfo;
import net.sf.rej.java.constantpool.UTF8Info;
import net.sf.rej.java.instruction.DecompilationContext;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Label;
import net.sf.rej.java.instruction.Parameters;
import net.sf.rej.java.instruction._newarray;

/**
 * Class provides transformation from EditorRow objects into syntax highlighted
 * text (whether the text is made available as text or graphics is up to the
 * <code>JavaBytecodeSyntaxDrawer</code> class given as a parameter.
 * 
 * @author Sami Koivu
 */
public class BytecodeRenderer {
	
	private static final Logger logger = Logger.getLogger(BytecodeRenderer.class.getName());
	
	public void render(EditorRow er, JavaBytecodeSyntaxDrawer sd, Imports ia) {
        if (er instanceof PackageDefRow) {
            PackageDefRow pdr = (PackageDefRow)er;
            if (pdr.getPackage().equals("")) {
            	sd.drawComment("// Class is in the default package.");
            } else {
            	sd.drawKeyword("package ");
            	sd.drawDefault(pdr.getPackage() + ";");
            }
        } else if (er instanceof ImportDefRow) {
            ImportDefRow idr = (ImportDefRow)er;
            sd.drawKeyword("import ");
            sd.drawDefault(idr.getImport() + ";");
        } else if (er instanceof ClassCommentRow) {
        	ClassCommentRow ccr = (ClassCommentRow) er;
        	sd.drawComment("// ");
        	sd.drawComment(ccr.getComment());
        } else if (er instanceof ClassDefRow) {
            ClassDefRow cdr = (ClassDefRow)er;
            if (cdr.isClosing()) {
                sd.drawDefault("}");
            } else {
                ClassFile cf = cdr.getClassFile();
                ClassSignature classSig = null;
                
        		boolean displayGenerics = SystemFacade.getInstance().getPreferences().isSettingTrue(Settings.DISPLAY_GENERICS);
                if (displayGenerics) {
                	SignatureAttribute signature = cf.getAttributes().getSignatureAttribute();
                	if (signature != null) {
                		classSig = Signatures.getClassSignature(signature.getSignatureString());
                	}
                }
                
                String access = cf.getAccessString();
                if (access.length() > 0) {
                    sd.drawKeyword(access + " ");
                }

                if (AccessFlags.isEnum(cf.getAccessFlags())) {
                	sd.drawKeyword("enum ");
                } else if (AccessFlags.isAnnotation(cf.getAccessFlags())) {
                	sd.drawKeyword("@interface ");
                } else if (AccessFlags.isInterface(cf.getAccessFlags())) {
                    	sd.drawKeyword("interface ");
                } else {
                	sd.drawKeyword("class ");
                }

                sd.drawDefault(cf.getShortClassName());
                String superClass = cf.getSuperClassName();
                
               	if (classSig != null) {
                   	renderFormalTypeParameters(sd, ia, classSig.getFormalTypeParameters());
                }
               	
               	sd.drawDefault(" ");
                
                if (superClass != null) {
                	boolean displayExtendsObject = SystemFacade.getInstance().getPreferences().isSettingTrue(Settings.DISPLAY_EXTENDS_OBJECT);
                	if (!superClass.equals("java.lang.Object") || 
                		displayExtendsObject) {
                		sd.drawKeyword("extends ");
                		if (classSig == null) {
                			sd.drawDefault(ia.getShortName(superClass));
                		} else {
                			renderGenericJavaType(sd, ia, classSig.getSuperClassSignature());
                		}
                		sd.drawDefault(" ");
                	}
                }

                List interfaces = cf.getInterfaces();
                if (interfaces.size() > 0) {
                    if (AccessFlags.isInterface(cf.getAccessFlags())) {
                    	sd.drawKeyword("extends ");
                    } else {
                    	sd.drawKeyword("implements ");
                    }
                    if (classSig == null) {
                    	for (int i = 0; i < interfaces.size(); i++) {
                    		Interface interface0 = (Interface) interfaces.get(i);
                    		if (i > 0) {
                    			sd.drawDefault(", ");
                    		}
                    		sd.drawDefault(ia.getShortName(interface0.getName()));
                    	}
                    } else {
                    	boolean first = true;
                    	for (GenericJavaType intf : classSig.getSuperInterfaceSignatures()) {
                    		if (first) {
                    			first = false;
                    		} else {
                    			sd.drawDefault(", ");
                    		}
                			renderGenericJavaType(sd, ia, intf);                    		
                    	}
                    }

                    sd.drawDefault(" ");
                }

                sd.drawDefault("{");
            }
        } else if (er instanceof FieldDefRow) {
            FieldDefRow fdr = (FieldDefRow)er;
            Field f = fdr.getField();
    		FieldSignature fieldSig = null;
            boolean displayGenerics = SystemFacade.getInstance().getPreferences().isSettingTrue(Settings.DISPLAY_GENERICS);
            if (displayGenerics) {
            	SignatureAttribute signature = f.getAttributes().getSignatureAttribute();
            	if (signature != null) {
            		fieldSig = Signatures.getFieldSignature(signature.getSignatureString());
            	}
            }

            sd.drawIndent();
            String access = f.getAccessString();
            if (access.length() > 0) {
                sd.drawKeyword(access + " ");
            }
            JavaType ret = f.getDescriptor().getReturn();
            if (fieldSig == null) {
            	if (ret.isPrimitive()) {
            		sd.drawKeyword(ret.getType());
            	} else {
            		sd.drawDefault(ia.getShortName(ret.getType()));
            	}
            	sd.drawDefault(ret.getDimensions());
            } else {
            	renderGenericJavaType(sd, ia, fieldSig.getType());
            }
            
        	sd.drawDefault(" ");

            sd.drawField(f.getName());
            ConstantPoolInfo constant = f.getConstant();
            if (constant != null) {
            	sd.drawDefault(" = ");
            	drawConstant(sd, constant);
            }
            sd.drawDefault(";");
        } else if (er instanceof DeprecatedAnnotationDefRow) {
        	sd.drawIndent();
	        sd.drawAnnotation("@Deprecated");
        } else if (er instanceof ClassAnnotationDefRow || er instanceof MethodAnnotationDefRow || er instanceof FieldAnnotationDefRow) {
        	Annotation ann = null;
        	if (er instanceof ClassAnnotationDefRow) {
        		ann = ((ClassAnnotationDefRow)er).getAnnotation();
        	} else if (er instanceof MethodAnnotationDefRow) {
        		sd.drawIndent();
        		ann = ((MethodAnnotationDefRow)er).getAnnotation();
        	} else {
        		sd.drawIndent();
        		ann = ((FieldAnnotationDefRow)er).getAnnotation();
        	}
        	
	        sd.drawAnnotation("@" + ia.getShortName(ann.getName()));
	        if (ann.getElementValueCount() > 0) {
	        	sd.drawDefault("(");
	        	Map<String, ElementValue> elementValues = ann.getElementValues();
	        	boolean first = true;
	        	for (Entry<String, ElementValue> entry : elementValues.entrySet()) {
	        		if (!first) {
	        			sd.drawDefault(", ");
	        		} else {
	        			first = false;
	        		}
		        	if (entry.getKey().equals("value") && elementValues.size() == 1) {
		        		// only one element, named "value", don't show the display name, just show the value
		        	} else {
		        		sd.drawDefault(entry.getKey() + "=");
		        	}
	        		ElementValue ev = entry.getValue();
        			drawElementValue(sd, ev, ia);
	        	}
	        	sd.drawDefault(")");
	        }
        } else if (er instanceof MethodDefRow) {
            MethodDefRow mdr = (MethodDefRow)er;
        	sd.drawIndent();
        	if (mdr.isClosing()) {
                sd.drawDefault("}");
            } else {
                Method m = mdr.getMethod();

                MethodSignature methodSig = null;
                boolean displayGenerics = SystemFacade.getInstance().getPreferences().isSettingTrue(Settings.DISPLAY_GENERICS);
                if (displayGenerics) {
                	SignatureAttribute signature = m.getAttributes().getSignatureAttribute();
                	if (signature != null) {
                		methodSig = Signatures.getMethodSignature(signature.getSignatureString());
                	}
                }

                String access = m.getAccessString();
                if (access.length() > 0) {
                    sd.drawKeyword(access + " ");
                }
                
               	if (methodSig != null) {
               		List<FormalTypeParameter> typeParams = methodSig.getFormalTypeParameters();
                   	renderFormalTypeParameters(sd, ia, typeParams);
               		if (typeParams != null && typeParams.size() > 0) {
               			sd.drawKeyword(" ");
               		}
                }
                
                JavaType ret = m.getDescriptor().getReturn();
                if (methodSig == null) {
                	if (ret.isPrimitive()) {
                		sd.drawKeyword(ret.getType());
                	} else {
                		sd.drawDefault(ia.getShortName(ret.getType()));
                	}

                	sd.drawDefault(ret.getDimensions());
                } else {
                	renderGenericJavaType(sd, ia, methodSig.getReturnType());
                }
                
                sd.drawDefault(" ");

                if (m.isDeprecated()) {
                	sd.drawDefaultOverstrike(m.getName());
                } else {
                	sd.drawDefault(m.getName());
                }

                CodeAttribute ca = m.getAttributes().getCode();
                LocalVariableTableAttribute lvs = null;
                if (ca != null) {
                	lvs = ca.getAttributes().getLocalVariableTable();
                }

                int paramLVDefOffset = 0;
                if (!AccessFlags.isStatic(m.getAccessFlags())) {
                	paramLVDefOffset = 1;
                }

                sd.drawDefault("(");
                List<JavaType> params = m.getDescriptor().getParamList();
                List<GenericJavaType> genParams = null;
                if (methodSig != null) {
                	genParams = methodSig.getParameters();
                }
        		boolean displayVarargs = SystemFacade.getInstance().getPreferences().isSettingTrue(Settings.DISPLAY_VARARGS);
        		for (int i = 0; i < params.size(); i++) {
                    if (i > 0) {
                        sd.drawDefault(", ");
                    }
                    JavaType item = params.get(i);
                    // last time, method has varargs flag and type is a one dimensional array
                    boolean isLastItem = (i == params.size()-1);
                    if (displayVarargs
                     && isLastItem
                     && AccessFlags.isVarArgs(m.getAccessFlags())
                     && (item.getDimensionCount() > 0)) {
                    	item.dropDimension();
                    	if (methodSig == null) {
                    		if (item.isPrimitive()) {
                    			sd.drawKeyword(item.getType());
                    		} else {
                    			sd.drawDefault(ia.getShortName(item.getType()));
                    		}
                    		sd.drawDefault(item.getDimensions());
                    	} else {
                    		GenericJavaType genType = genParams.get(i);
                    		genType.getBaseType().dropDimension();
                    		renderGenericJavaType(sd, ia, genType);
                    	}

                    	sd.drawDefault(" ... ");
                    	LocalVariable lv = null;
                    	if (lvs != null) {
                    		lv = lvs.getLocalVariable(paramLVDefOffset + i, 0);
                    	}
                    	if (lv != null) {
                    		sd.drawDefault(lv.getName());
                    	} else {
                    		sd.drawDefault("p" + i);
                    	}
                    	
                    } else {
                    	if (methodSig == null) {
                    		if (item.isPrimitive()) {
                    			sd.drawKeyword(item.getType());
                    		} else {
                    			sd.drawDefault(ia.getShortName(item.getType()));
                    		}
                    		sd.drawDefault(item.getDimensions());
                    	} else {
                    		renderGenericJavaType(sd, ia, genParams.get(i));
                    	}
                    	sd.drawDefault(" ");
                    	LocalVariable lv = null;
                    	if (lvs != null) {
                    		lv = lvs.getLocalVariable(paramLVDefOffset + i, 0);
                    	}
                    	if (lv != null) {
                    		sd.drawDefault(lv.getName());
                    	} else {
                    		sd.drawDefault("p" + i);
                    	}
                    }
                }

                sd.drawDefault(")");
                List exc = m.getExceptions();
                for (int i = 0; i < exc.size(); i++) {
                    if (i == 0) {
                        sd.drawKeyword(" throws ");
                    } else {
                        sd.drawDefault(", ");
                    }
                    sd.drawDefault(ia.getShortName(((ExceptionDescriptor) exc.get(i)).getName()));
                }
                if (mdr.hasBody()) {
                	sd.drawDefault(" {");
                } else {
                	sd.drawDefault(";");
                }
            }
        } else if (er instanceof LocalVariableDefRow) {
            LocalVariableDefRow lvdr = (LocalVariableDefRow)er;
            LocalVariable lv = lvdr.getLocalVariable();
            JavaType ret = lv.getDescriptor().getReturn();
        	sd.drawIndent();
        	sd.drawIndent();

            if (ret.isPrimitive()) {
                sd.drawKeyword(ret.getType());
            } else {
                sd.drawDefault(ia.getShortName(ret.getType()));
            }
            sd.drawDefault(ret.getDimensions() + " " + lv.getName() + " (#" + lv.getIndex() + " " + lv.getStartPc() + " - " + lv.getEndPc() + ")");
        } else if (er instanceof LabelRow) {
            LabelRow lr = (LabelRow)er;
        	sd.drawIndent();
        	sd.drawIndent();
            sd.drawDefault(lr.getLabel().getId() + ":");
        } else if (er instanceof CodeRow) {
            CodeRow cr = (CodeRow)er;
            
            // execution row
            if (cr.isExecutionRow()) {
            	sd.setExecutionBackground();
            }
            // breakpoint
            if (cr.getBreakpoint() != null) {
            	sd.drawBreakpoint();
            }
            
            // line identifier
            LineIdentifierMode mode = EditorFacade.getInstance().getLineIdentifierMode();
            switch (mode.getMode()) {
                case LineIdentifierMode.MODE_OFF:
                    break;
                case LineIdentifierMode.MODE_PC:
                    sd.drawSmall(String.valueOf(cr.getPosition()), 15);
                    break;
                case LineIdentifierMode.MODE_SOURCELINE:
                    if (cr.hasLineNumber()) {
                        sd.drawSmall(String.valueOf(cr.getLineNumber()), 0);
                    }
                    break;
            }

            sd.setOffset(0);

            Instruction inst = cr.getInstruction();
            DecompilationContext dc = cr.getDecompilationContext();
            LocalVariableTableAttribute lvs = dc.getLocalVariableTable();
            ConstantPool pool = dc.getConstantPool();
        	sd.drawIndent();
        	sd.drawIndent();
            sd.drawInstruction(inst.getMnemonic());
            Parameters params = inst.getParameters();
            for (int i = 0; i < params.getCount(); i++) {
                try {
                    switch (params.getType(i)) {
                        case TYPE_LOCAL_VARIABLE:
                        case TYPE_LOCAL_VARIABLE_WIDE:
                        case TYPE_LOCAL_VARIABLE_READONLY:
                            if (lvs == null) {
                                sd.drawDefault(" " + params.getInt(i));
                            } else {
                                LocalVariable lv = lvs.getLocalVariable(params.getInt(i), cr.getPosition());
                                if (lv == null) {
                                    sd.drawDefault(" " + params.getInt(i));
                                } else {
                                    sd.drawDefault(" " + lv.getName());
                                }
                            }
                            break;
                        case TYPE_CONSTANT_POOL_METHOD_REF: {
                        	int index = params.getInt(i);
                            ConstantPoolInfo cpi = pool.get(index);
                            renderMethodRef(sd, ia, (RefInfo) cpi, index);
                            break;
                        }
                        case TYPE_CONSTANT_POOL_FIELD_REF: {
                        	int index = params.getInt(i);
                            ConstantPoolInfo cpi = pool.get(index);
                            renderFieldRef(sd, ia, (RefInfo) cpi, index);
                            break;
                        }
                        case TYPE_CONSTANT_POOL_CLASS: {
                        	int index = params.getInt(i);
                            ConstantPoolInfo cpi = pool.get(index);
                            renderClassRef(sd, ia, (ClassInfo) cpi, index);
                            break;
                        }
                        case TYPE_CONSTANT_POOL_CONSTANT: {
                       		int index = params.getInt(i);
                       		ConstantPoolInfo cpi = pool.get(index);
                       		renderConstant(sd, cpi, index);
                       		break;
                        }
                        case TYPE_LABEL: {
                            Label label = (Label) params.getObject(i);
                            sd.drawDefault(" " + label.getId());
                            break;
                        }
                        case TYPE_SWITCH: {
                        	@SuppressWarnings("unchecked")
                            Map<Integer, Label> offsets = (Map<Integer, Label>) params.getObject(i);
                            for (Entry<Integer, Label> entry : offsets.entrySet()) {
                                sd.drawDefault(" " + entry.getKey() + "=" + entry.getValue().getId());
                            }
                            break;
                        }
                        case TYPE_CONSTANT:
                        case TYPE_CONSTANT_READONLY:
                        case TYPE_CONSTANT_WIDE: {
                            sd.drawDefault(" " + params.getInt(i));
                            break;
                        }
                        case TYPE_ARRAYTYPE: {
                            sd.drawDefault(" " + _newarray.getTypeName(params.getInt(i)));
                            break;
                        }
                    }
                } catch (Exception ee) {
                	logger.warning("Error rendering instruction. Instruction = " + inst.getMnemonic() + " params = " + params);
                	StringWriter sw = new StringWriter();
                	ee.printStackTrace(new PrintWriter(sw));
                    logger.warning(sw.toString());
                }

            }

        }

    }

	private void renderFormalTypeParameters(JavaBytecodeSyntaxDrawer sd, Imports ia, List<FormalTypeParameter> typeParams) {
		boolean displayExtendsObject = SystemFacade.getInstance().getPreferences().isSettingTrue(Settings.DISPLAY_EXTENDS_OBJECT);
		if (typeParams != null && typeParams.size() > 0) {
			sd.drawDefault("<");
			boolean isFirstTypeParam = true;
			for (FormalTypeParameter typeParam : typeParams) {
				if (isFirstTypeParam) {
					isFirstTypeParam = false;
				} else {
					sd.drawDefault(", ");
				}
				sd.drawDefault(typeParam.getIdentifier());
				List<GenericJavaType> union = typeParam.getTypeUnion();
				if (union.size() == 1 && union.get(0).getBaseType().equals(JavaType.JAVA_LANG_OBJECT)) {
					// special case where there's just extends Object
					if (displayExtendsObject) {
						sd.drawKeyword(" extends ");
						sd.drawDefault(ia.getShortName(JavaType.JAVA_LANG_OBJECT.toString()));
					}
				} else {
					sd.drawKeyword(" extends ");
					boolean isFirstType = true;
					for (GenericJavaType gjt : union) {
						if (isFirstType) {
							isFirstType = false;
						} else {
							sd.drawDefault(" & ");
						}
						renderGenericJavaType(sd, ia, gjt);
					}
				}
			}
			sd.drawDefault(">");
		}
	}

	private void renderGenericJavaType(JavaBytecodeSyntaxDrawer sd, Imports ia, GenericJavaType gjt) {
		JavaType baseType = gjt.getBaseType();
        if (baseType.isPrimitive()) {
            sd.drawKeyword(baseType.getType());
        } else {
            sd.drawDefault(ia.getShortName(baseType.getType()));
        }
             
        List<TypeArgument> typeArgs = gjt.getTypeArguments();
        if (typeArgs.size() > 0) {
        	sd.drawDefault("<");
        	boolean isFirstTypeArg = true;
        	for (TypeArgument typeArg : typeArgs) {
        		if (isFirstTypeArg) {
        			isFirstTypeArg = false;
        		} else {
        			sd.drawDefault(", ");
        		}
        		if (typeArg instanceof GenericJavaType) {
        			renderGenericJavaType(sd, ia, (GenericJavaType) typeArg);
        		} else if (typeArg instanceof BoundTypeArgument) {
        			BoundTypeArgument bta = (BoundTypeArgument) typeArg;
        			sd.drawDefault("? ");
        			sd.drawKeyword(bta.getBoundString());
        			sd.drawDefault(" ");
        			renderGenericJavaType(sd, ia, bta.getBound());
        		} else if (typeArg instanceof Any) {
        			sd.drawDefault("?");
        		} else {
        			throw new AssertionError("Invalid TypeArgument : " + typeArg.getClass().getName());
        		}
        	}
        	sd.drawDefault(">");
        }
        sd.drawDefault(baseType.getDimensions());		
	}

	private void renderConstant(JavaBytecodeSyntaxDrawer sd, ConstantPoolInfo cpi, int index) {
		ConstantPoolTranslationMode mode = EditorFacade.getInstance().getConstantPoolTranslationMode();
		sd.drawDefault(" ");
		if (mode == ConstantPoolTranslationMode.OFF || mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawDefault("#" + index + ";");
		}
		
		if (mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawComment(" //");
		}
		
		if (mode == ConstantPoolTranslationMode.TRANSLATION || mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawDefault(cpi.getTypeString() + " ");
			drawConstant(sd, cpi);
		}
	}

	private void renderClassRef(JavaBytecodeSyntaxDrawer sd, Imports ia, ClassInfo ci, int index) {
		ConstantPoolTranslationMode mode = EditorFacade.getInstance().getConstantPoolTranslationMode();
		sd.drawDefault(" ");
		if (mode == ConstantPoolTranslationMode.OFF || mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawDefault("#" + index + ";");
		}

		if (mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawComment(" //");
		}
		
		if (mode == ConstantPoolTranslationMode.TRANSLATION || mode == ConstantPoolTranslationMode.HYBRID) {
			JavaType jt = new JavaType(ci.getName());
			sd.drawDefault(ia.getShortName(jt.getType()) + jt.getDimensions());
		}
	}

	private void renderFieldRef(JavaBytecodeSyntaxDrawer sd, Imports ia, RefInfo ri, int index) {
		ConstantPoolTranslationMode mode = EditorFacade.getInstance().getConstantPoolTranslationMode();
		sd.drawDefault(" ");
		if (mode == ConstantPoolTranslationMode.OFF || mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawDefault("#" + index + ";");
		}

		if (mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawComment(" //");
		}
		
		if (mode == ConstantPoolTranslationMode.TRANSLATION || mode == ConstantPoolTranslationMode.HYBRID) {
			Descriptor desc = ri.getDescriptor();
			JavaType ret = desc.getReturn();
			if (ret.isPrimitive()) {
				sd.drawKeyword(ret.getType());
			} else {
				sd.drawDefault(ia.getShortName(ret.getType()));
			}
			sd.drawDefault(ret.getDimensions() + " " + ia.getShortName(ri.getClassName()) + ".");
			sd.drawField(ri.getTargetName());
		}
	}

	private void renderMethodRef(JavaBytecodeSyntaxDrawer sd, Imports ia, RefInfo ri, int index) {
		ConstantPoolTranslationMode mode = EditorFacade.getInstance().getConstantPoolTranslationMode();
		sd.drawDefault(" ");
		if (mode == ConstantPoolTranslationMode.OFF || mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawDefault("#" + index + ";");
		}

		if (mode == ConstantPoolTranslationMode.HYBRID) {
			sd.drawComment(" //");
		}
		
		if (mode == ConstantPoolTranslationMode.TRANSLATION || mode == ConstantPoolTranslationMode.HYBRID) {
			Descriptor desc = ri.getDescriptor();
			JavaType ret = desc.getReturn();
			if (ret.isPrimitive()) {
				sd.drawKeyword(ret.getType());
			} else {
				sd.drawDefault(ia.getShortName(ret.getType()));
			}
			sd.drawDefault(ret.getDimensions() + " " +
		               ia.getShortName(ri.getClassName()) + "." +
		               ri.getTargetName());
			sd.drawDefault("(");
			List al = desc.getParamList();
			for (int j = 0; j < al.size(); j++) {
				JavaType item = (JavaType) al.get(j);
				if (j > 0) {
					sd.drawDefault(", ");
				}
				if (item.isPrimitive()) {
					sd.drawKeyword(item.getType());
				} else {
					sd.drawDefault(ia.getShortName(item.getType()));
				}
				sd.drawDefault(item.getDimensions());
			}
			sd.drawDefault(")");
		}
	}
  
    private void drawConstant(JavaBytecodeSyntaxDrawer sd, ConstantPoolInfo constant) {
    	switch (constant.getType()) {
    	case ConstantPoolInfo.DOUBLE:
    		DoubleInfo di = (DoubleInfo) constant;
    		sd.drawDefault("(");
    		sd.drawKeyword("double");
    		sd.drawDefault(") " + di.getDoubleValue());
    		break;
    	case ConstantPoolInfo.FLOAT:
    		FloatInfo fi = (FloatInfo) constant;
    		sd.drawDefault("(");
    		sd.drawKeyword("float");
    		sd.drawDefault(") " + fi.getFloatValue());
    		break;
    	case ConstantPoolInfo.INTEGER:
    		IntegerInfo ii = (IntegerInfo) constant;
    		sd.drawDefault("(");
    		sd.drawKeyword("int");
    		sd.drawDefault(") " + ii.getIntValue());
    		break;
    	case ConstantPoolInfo.LONG:
    		LongInfo li = (LongInfo) constant;
    		sd.drawDefault("(");
    		sd.drawKeyword("long");
    		sd.drawDefault(") " + li.getLongValue());
    		break;
    	case ConstantPoolInfo.UTF8:
    		UTF8Info ui = (UTF8Info) constant;
    		sd.drawString("\"");
    		sd.drawString(ui.getValue());
    		sd.drawString("\"");
    		break;
    	case ConstantPoolInfo.STRING:
    		StringInfo si = (StringInfo) constant;
    		sd.drawString("\"");
    		sd.drawString(si.getString());
    		sd.drawString("\"");
    		break;
    	}

    }
    
    private void drawElementValue(JavaBytecodeSyntaxDrawer sd, ElementValue ev, Imports ia) {
    	if (ev instanceof ArrayValue) {
    		ArrayValue av = (ArrayValue)ev;
    		List<ElementValue> values = av.getArray();
    		boolean first = true;
    		sd.drawDefault("{");
    		for (ElementValue value : values) {
    			if (!first) {
    				sd.drawDefault(", ");
    			} else {
    				first = false;
    			}
    			drawElementValue(sd, value, ia);
    		}
    		sd.drawDefault("}");
    	} else if (ev instanceof ClassInfoValue) {
    		sd.drawDefault(ia.getShortName(ev.getValue()));
    	} else if (ev instanceof ConstantValue) {
    		ConstantValue cv = (ConstantValue)ev;
    		drawConstant(sd, cv.getConstantPoolInfo());
    	} else if (ev instanceof EnumValue) {
    		EnumValue en = (EnumValue)ev;
    		sd.drawDefault(ia.getShortName(en.getTypeName()));
    		sd.drawDefault(".");
    		sd.drawField(en.getConstName());
    	} else if (ev instanceof NestedAnnotationValue) {
    		sd.drawDefault(ev.getValue());
    	}
    }

	public Color getBackgroundColor(EditorRow er) {
		if (er instanceof CodeRow) {
			CodeRow cr = (CodeRow) er;
			if (cr.isExecutionRow()) {
				return new Color(255, 190, 190);
			}
		} else if (er instanceof MethodDefRow) {
			MethodDefRow mdr = (MethodDefRow) er;
			if (mdr.isExecutionRow()) {
				return new Color(255, 190, 190);
			}
			
		}
		return Color.white;
	}
}

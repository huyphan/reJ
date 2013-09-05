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
package net.sf.rej.gui.tab;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.rej.Imports;
import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.files.FieldLocator;
import net.sf.rej.files.MethodLocator;
import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.InstructionHints;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.Undoable;
import net.sf.rej.gui.action.*;
import net.sf.rej.gui.debug.DebugControlPanel;
import net.sf.rej.gui.debug.wrappers.IField;
import net.sf.rej.gui.debug.wrappers.IMethod;
import net.sf.rej.gui.debug.wrappers.IReferenceType;
import net.sf.rej.gui.debug.wrappers.IStackFrame;
import net.sf.rej.gui.dialog.QuickOutlineDialog;
import net.sf.rej.gui.editor.ArrayTypeChooser;
import net.sf.rej.gui.editor.Breakpoint;
import net.sf.rej.gui.editor.CaseInsensitiveMatcher;
import net.sf.rej.gui.editor.ClassChooser;
import net.sf.rej.gui.editor.ClassEditor;
import net.sf.rej.gui.editor.ConstantChooser;
import net.sf.rej.gui.editor.ConstantpoolConstantChooser;
import net.sf.rej.gui.editor.FieldChooser;
import net.sf.rej.gui.editor.FieldEditor;
import net.sf.rej.gui.editor.InstructionEditor;
import net.sf.rej.gui.editor.LocalVariableChooser;
import net.sf.rej.gui.editor.MethodChooser;
import net.sf.rej.gui.editor.MethodEditor;
import net.sf.rej.gui.editor.rendering.BytecodeRenderer;
import net.sf.rej.gui.editor.rendering.CodeEditorRenderer;
import net.sf.rej.gui.editor.rendering.HTMLSyntaxDrawer;
import net.sf.rej.gui.editor.rendering.PlaintextSyntaxDrawer;
import net.sf.rej.gui.editor.row.BlankRow;
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
import net.sf.rej.gui.editor.transfer.BytecodeEditorTransferHandler;
import net.sf.rej.gui.editor.transfer.TransferComponent;
import net.sf.rej.gui.editor.transfer.Transferrable;
import net.sf.rej.gui.editor.transfer.TransferrableField;
import net.sf.rej.gui.editor.transfer.TransferrableMethod;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.split.BytecodeSplitSynchronizer;
import net.sf.rej.java.AccessFlags;
import net.sf.rej.java.ClassFactory;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Code;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Field;
import net.sf.rej.java.FieldFactory;
import net.sf.rej.java.InstructionCopier;
import net.sf.rej.java.Interface;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.Method;
import net.sf.rej.java.MethodFactory;
import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.java.attribute.ExceptionDescriptor;
import net.sf.rej.java.attribute.LineNumberTableAttribute;
import net.sf.rej.java.attribute.LocalVariableTableAttribute;
import net.sf.rej.java.attribute.RuntimeInvisibleAnnotationsAttribute;
import net.sf.rej.java.attribute.RuntimeVisibleAnnotationsAttribute;
import net.sf.rej.java.attribute.SourceFileAttribute;
import net.sf.rej.java.attribute.annotations.Annotation;
import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.RefInfo;
import net.sf.rej.java.instruction.DecompilationContext;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Label;
import net.sf.rej.java.instruction.Parameters;
import net.sf.rej.util.Range;

/**
 * <code>EditorTab</code> is the bytecode editor. The editor itself is a
 * <code>JList</code> with a custom renderer.
 * 
 * @author Sami Koivu
 */

public class EditorTab extends JPanel implements Tabbable, EventObserver, TransferComponent {

	private static final long serialVersionUID = 1L;
	
	ClassEditor classEditor = new ClassEditor(MainWindow.getInstance());
	MethodEditor methodEditor = new MethodEditor(MainWindow.getInstance());
	FieldEditor fieldEditor = new FieldEditor(MainWindow.getInstance());
	private CaseInsensitiveMatcher lastSearch = null;
	private String lastQueryString = null;
	private InstructionHints hints = new InstructionHints();
	
	JPopupMenu codeContextMenu = new JPopupMenu();
	JPopupMenu labelContextMenu = new JPopupMenu();
	JPopupMenu methodContextMenu = new JPopupMenu();
	JPopupMenu fieldContextMenu = new JPopupMenu();
	JPopupMenu importContextMenu = new JPopupMenu();
	JPopupMenu classContextMenu = new JPopupMenu();
	JPopupMenu insertContextMenu = new JPopupMenu();

	private PackageDefRow packageDef = null;
	private List<ImportDefRow> importDefs = null;
	ClassDefRow classDef = null;
	private List<EditorRow> rows;
	private EditorRow executionRow = null;

	private CodeEditorRenderer renderer = new CodeEditorRenderer();

	DefaultListModel model = new DefaultListModel();

	JList list = new JList(this.model) {
		@Override
		public String getToolTipText(MouseEvent event) {
			int index = locationToIndex(event.getPoint());
			Object obj = null;
			if (index > 0 && index < model.size()) {
				obj = model.get(index);
			}
			if (obj instanceof CodeRow) {
				CodeRow cr = (CodeRow) obj;
				return hints.getHint(cr.getInstruction());
			}

			return super.getToolTipText(event);
		}
	};
	DebugControlPanel debugPanel = null;
	private JScrollPane editorScrollPane = new JScrollPane(this.list);
	
	private EventDispatcher dispatcher;
	
	private ClassFile cf;
	private Map<Object, Range> offsets;
	private BytecodeSplitSynchronizer sync = null;
	private boolean upToDate = false;
	private boolean isOpen = false;

	private JLabel label = new JLabel("Bytecode Editor");

	private final Action refactorRenameAction = new AbstractAction("Rename..") {
		public void actionPerformed(ActionEvent e) {
			Object o = list.getSelectedValue();
			if (o instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow) o;
				String methodName = mdr.getMethod().getName();
				String value = JOptionPane.showInputDialog(EditorTab.this,
						"Enter new name for method", methodName);
				if (value != null && !value.equals(methodName)) {
					EditorFacade.getInstance().refactorMethodName(
							mdr.getClassFile().getFullClassName(),
							mdr.getMethod().getDescriptor(), methodName, value);
				}
			} else if (o instanceof FieldDefRow) {
				FieldDefRow mdr = (FieldDefRow) o;
				String fieldName = mdr.getField().getName();
				String value = JOptionPane.showInputDialog(EditorTab.this,
						"Enter new name for field", fieldName);
				if (value != null && !value.equals(fieldName)) {
					EditorFacade.getInstance().refactorFieldName(
							mdr.getClassFile().getFullClassName(),
							mdr.getField().getDescriptor(), fieldName, value);
				}
			} else if (o instanceof ClassDefRow) {
				ClassDefRow cdr = (ClassDefRow) o;
				String className = cdr.getClassFile().getFullClassName();
				String value = JOptionPane.showInputDialog(EditorTab.this,
						"Enter new name for class", className);
				if (value != null && !value.equals(className)) {
					EditorFacade.getInstance().refactorClassName(
							cdr.getClassFile().getFullClassName(), value);
				}
			}
		}
	};

	private final Action findRefsAction = new AbstractAction(
			"Find references..") {
		public void actionPerformed(ActionEvent e) {
			Object o = list.getSelectedValue();
			if (o instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow) o;
				EditorFacade.getInstance().findMethodRefs(
						mdr.getClassFile().getFullClassName(),
						mdr.getMethod().getName(),
						mdr.getMethod().getDescriptor());
			} else if (o instanceof FieldDefRow) {
				FieldDefRow fdr = (FieldDefRow) o;
				EditorFacade.getInstance().findFieldRefs(
						fdr.getClassFile().getFullClassName(),
						fdr.getField().getName(),
						fdr.getField().getDescriptor());
			} else if (o instanceof ClassDefRow) {
				ClassDefRow cdr = (ClassDefRow) o;
				EditorFacade.getInstance().findClassRefs(
						cdr.getClassFile().getFullClassName());
			}
		}
	};

	private final Action findDefinitionAction = new AbstractAction(
			"Find definition..") {
		@SuppressWarnings("incomplete-switch")
		public void actionPerformed(ActionEvent e) {
			Object o = list.getSelectedValue();
			if (o instanceof CodeRow) {
				CodeRow cr = (CodeRow) o;
				Parameters params = cr.getInstruction().getParameters();
				for (int i = 0; i < params.getCount(); i++) {
					switch (params.getType(i)) {
					case TYPE_CONSTANT_POOL_METHOD_REF: {
						RefInfo ri = (RefInfo) cr.getDecompilationContext()
								.getConstantPool().get(params.getInt(i));
						EditorFacade.getInstance().findMethodDefinition(
								ri.getClassName(), ri.getTargetName(),
								ri.getDescriptor());
						break;
					}
					case TYPE_CONSTANT_POOL_FIELD_REF: {
						RefInfo ri = (RefInfo) cr.getDecompilationContext()
								.getConstantPool().get(params.getInt(i));
						EditorFacade.getInstance().findFieldDefinition(
								ri.getClassName(), ri.getTargetName(),
								ri.getDescriptor());
						break;
					}
					case TYPE_CONSTANT_POOL_CLASS: {
						ClassInfo ci = (ClassInfo) cr.getDecompilationContext()
								.getConstantPool().get(params.getInt(i));
						EditorFacade.getInstance().findClassDefinition(
								ci.getName());
						break;
					}
					}
				}

			} else if (o instanceof ImportDefRow) {
				ImportDefRow idr = (ImportDefRow) o;
				EditorFacade.getInstance().findClassDefinition(idr.getImport());
			}
		}
	};

	private Action gotoDefinitionAction = new AbstractAction(
			"Go to definition..") {
		@SuppressWarnings("incomplete-switch")
		public void actionPerformed(ActionEvent e) {
			Object o = list.getSelectedValue();
			if (o instanceof CodeRow) {
				CodeRow cr = (CodeRow) o;
				Parameters params = cr.getInstruction().getParameters();
				for (int i = 0; i < params.getCount(); i++) {
					switch (params.getType(i)) {
					case TYPE_CONSTANT_POOL_METHOD_REF: {
						RefInfo ri = (RefInfo) cr.getDecompilationContext()
								.getConstantPool().get(params.getInt(i));
						EditorFacade.getInstance().gotoMethodDefinition(
								ri.getClassName(), ri.getTargetName(),
								ri.getDescriptor());
						break;
					}
					case TYPE_CONSTANT_POOL_FIELD_REF: {
						RefInfo ri = (RefInfo) cr.getDecompilationContext()
								.getConstantPool().get(params.getInt(i));
						EditorFacade.getInstance().gotoFieldDefinition(
								ri.getClassName(), ri.getTargetName(),
								ri.getDescriptor());
						break;
					}
					case TYPE_CONSTANT_POOL_CLASS: {
						ClassInfo ci = (ClassInfo) cr.getDecompilationContext()
								.getConstantPool().get(params.getInt(i));
						EditorFacade.getInstance().gotoClassDefinition(
								ci.getName());
						break;
					}
					}
				}
			} else if (o instanceof ImportDefRow) {
				ImportDefRow idr = (ImportDefRow) o;
				EditorFacade.getInstance().gotoClassDefinition(idr.getImport());
			}
		}
	};

	private Action modifyAction = new AbstractAction("Modify..") {
		public void actionPerformed(ActionEvent e) {
			modifyRow();
		}
	};

	private Action insertInstructionAction = new AbstractAction(
			"Insert instruction..") {
		public void actionPerformed(ActionEvent e) {
			insertInstruction();
		}
	};

	private Action insertBeforeAction = new AbstractAction("Insert before..") {
		public void actionPerformed(ActionEvent e) {
			insert(true);
		}
	};

	private Action insertAfterAction = new AbstractAction("Insert after..") {
		public void actionPerformed(ActionEvent e) {
			insert(false);
		}
	};

	private Action removeAction = new AbstractAction("Remove") {
		public void actionPerformed(ActionEvent e) {
			remove();
		}
	};

	private final Action toggleBreakPointAction = new AbstractAction(
			"Toggle breakpoint") {
		public void actionPerformed(ActionEvent e) {
			Object o = list.getSelectedValue();
			if (o instanceof CodeRow) {
				CodeRow cr = (CodeRow) o;
				Breakpoint bp = cr.getBreakpoint();
				if (bp == null) {
					// add breakpoint
					String className = cr.getEnclosingMethodDef()
							.getClassFile().getFullClassName();
					String methodName = cr.getEnclosingMethodDef().getMethod()
							.getName();
					Descriptor methodDesc = cr.getEnclosingMethodDef()
							.getMethod().getDescriptor();
					int pc = cr.getPosition();
					bp = new Breakpoint(className, methodName, methodDesc, pc);
					EditorFacade.getInstance().addBreakPoint(bp);
					cr.setBreakpoint(bp);
				} else {
					// remove breakpoint
					EditorFacade.getInstance().removeBreakpoint(
							cr.getBreakpoint());
					cr.setBreakpoint(null);
				}
			} else if (o instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow) o;
				// TODO: Method breakpoint (needed to put breakpoints on
				// methods which have no code (native, for instance)
			}
		}

	};

	private Action moveUpAction = new AbstractAction("Move up") {

		public void actionPerformed(ActionEvent e) {
			Object o = list.getSelectedValue();
			if (o instanceof CodeRow) {
				CodeRow cr = (CodeRow) o;
				EditorFacade.getInstance().moveInstructionUp(
						cr.getInstruction(), cr.getParentCode());
			} else if (o instanceof LabelRow) {
				LabelRow lr = (LabelRow) o;
				EditorFacade.getInstance().moveInstructionUp(lr.getLabel(),
						lr.getParentCode());
			}
		}
	};

	private Action moveDownAction = new AbstractAction("Move down") {
		public void actionPerformed(ActionEvent e) {
			Object o = list.getSelectedValue();
			if (o instanceof CodeRow) {
				CodeRow cr = (CodeRow) o;
				EditorFacade.getInstance().moveInstructionDown(
						cr.getInstruction(), cr.getParentCode());
			} else if (o instanceof LabelRow) {
				LabelRow lr = (LabelRow) o;
				EditorFacade.getInstance().moveInstructionDown(lr.getLabel(),
						lr.getParentCode());
			}
		}
	};

	private Action insertMethodAction = new AbstractAction("Insert Method..") {
		public void actionPerformed(ActionEvent e) {
			MethodEditor editor = new MethodEditor(MainWindow.getInstance());
			int initialFlags = 0;
			Integer initialMaxStack = Integer.valueOf(1);
			Integer initialMaxLocals = Integer.valueOf(1);
			editor.invoke("method1", Descriptor.NO_PARAM_VOID, initialFlags,
					initialMaxStack, initialMaxLocals, new ArrayList());
			if (!editor.wasCancelled()) {
				EditorFacade.getInstance().insertMethod(
						EditorTab.this.classDef.getClassFile(),
						editor.getMethodName(), editor.getDescriptor(),
						editor.getAccessFlags(), editor.getMaxStack(),
						editor.getMaxLocals(), editor.getExceptions());
			}
		}
	};

	private Action insertFieldAction = new AbstractAction("Insert Field..") {
		public void actionPerformed(ActionEvent e) {
			FieldEditor editor = new FieldEditor(MainWindow.getInstance());
			int initialFlags = 0;
			editor.invoke("field1", Descriptor.NO_PARAM_VOID, initialFlags);
			if (!editor.wasCancelled()) {
				EditorFacade.getInstance().insertField(
						EditorTab.this.classDef.getClassFile(),
						editor.getFieldName(), editor.getDescriptorType(),
						editor.getAccessFlags());
			}
		}
	};

	public EditorTab() {
		this.setLayout(new BorderLayout());
		this.list.setCellRenderer(this.renderer);
		this.list.setTransferHandler(new BytecodeEditorTransferHandler(this));
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(this.list);

		this.add(this.label, BorderLayout.NORTH);
		this.add(this.editorScrollPane, BorderLayout.CENTER);
		
		this.list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				splitSynchronize();
			}
		});

		this.list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if (me.getClickCount() == 2
						&& me.getButton() == MouseEvent.BUTTON1) {
					modifyRow();
				} else if (me.getButton() == MouseEvent.BUTTON3) {
					Object obj = list.getSelectedValue();
					if (obj instanceof CodeRow) {
						EditorTab.this.codeContextMenu.show(
								EditorTab.this.list, me.getPoint().x, me
										.getPoint().y);
					} else if (obj instanceof LabelRow) {
						EditorTab.this.labelContextMenu.show(
								EditorTab.this.list, me.getPoint().x, me
										.getPoint().y);
					} else if (obj instanceof MethodDefRow) {
						EditorTab.this.methodContextMenu.show(
								EditorTab.this.list, me.getPoint().x, me
										.getPoint().y);
					} else if (obj instanceof FieldDefRow) {
						EditorTab.this.fieldContextMenu.show(
								EditorTab.this.list, me.getPoint().x, me
										.getPoint().y);
					} else if (obj instanceof ImportDefRow) {
						EditorTab.this.importContextMenu.show(
								EditorTab.this.list, me.getPoint().x, me
										.getPoint().y);
					} else if (obj instanceof ClassDefRow) {
						EditorTab.this.classContextMenu.show(
								EditorTab.this.list, me.getPoint().x, me
										.getPoint().y);
					}
				}
			}
		});

		createContextMenus();
	}

	private void createContextMenus() {
		// code row context sensitive pop up menu
		this.codeContextMenu.add(new JMenuItem(this.modifyAction));
		this.codeContextMenu.add(new JMenuItem(this.insertBeforeAction));
		this.codeContextMenu.add(new JMenuItem(this.insertAfterAction));
		this.codeContextMenu.add(new JMenuItem(this.removeAction));
		this.codeContextMenu.add(new JMenuItem(this.moveUpAction));
		this.codeContextMenu.add(new JMenuItem(this.moveDownAction));
		this.codeContextMenu.add(new JMenuItem(this.findDefinitionAction));
		this.codeContextMenu.add(new JMenuItem(this.gotoDefinitionAction));
		this.codeContextMenu.add(new JMenuItem(this.toggleBreakPointAction));

		// label row context sensitive popup menu
		// this.labelContextMenu.add(new JMenuItem(this.insertBeforeAction));
		// this.labelContextMenu.add(new JMenuItem(this.insertAfterAction));
		this.labelContextMenu.add(new JMenuItem(this.moveUpAction));
		this.labelContextMenu.add(new JMenuItem(this.moveDownAction));

		// method definition context sensitive menu
		this.methodContextMenu.add(new JMenuItem(this.findRefsAction));
		this.methodContextMenu.add(new JMenuItem(this.insertInstructionAction));
		this.methodContextMenu.add(new JMenuItem(this.removeAction));
		this.methodContextMenu.add(new JMenuItem(this.modifyAction));
		JMenu methodRefactoring = new JMenu("Refactoring");
		methodRefactoring.add(new JMenuItem(this.refactorRenameAction));
		this.methodContextMenu.add(methodRefactoring);
		this.methodContextMenu.add(new JMenuItem(this.toggleBreakPointAction));

		// field definition context sensitive menu
		this.fieldContextMenu.add(new JMenuItem(this.findRefsAction));
		this.fieldContextMenu.add(new JMenuItem(this.removeAction));
		this.fieldContextMenu.add(new JMenuItem(this.modifyAction));
		JMenu fieldRefactoring = new JMenu("Refactoring");
		fieldRefactoring.add(new JMenuItem(this.refactorRenameAction));
		this.fieldContextMenu.add(fieldRefactoring);

		// insert button context sensitive menu
		this.insertContextMenu.add(new JMenuItem(this.insertMethodAction));
		this.insertContextMenu.add(new JMenuItem(this.insertFieldAction));

		// import row context sensitive menu
		this.importContextMenu.add(new JMenuItem(this.findDefinitionAction));
		this.importContextMenu.add(new JMenuItem(this.gotoDefinitionAction));

		// class definition row context sensitive menu
		this.classContextMenu.add(new JMenuItem(this.findRefsAction));
		this.classContextMenu.add(new JMenuItem(this.modifyAction));
		JMenu classRefactoring = new JMenu("Refactoring");
		classRefactoring.add(new JMenuItem(this.refactorRenameAction));
		this.classContextMenu.add(classRefactoring);
	}

	public void processEvent(Event event) {
		switch (event.getType()) {
		case INIT:
			this.dispatcher = event.getDispatcher();
			break;
		case CLASS_PARSE_ERROR:
			this.model.clear();
			this.label.setText("Invalid class file.");
			break;
		case CLASS_OPEN:
			 // intentional fall-through
		case CLASS_UPDATE:
			if (event.getType() == EventType.CLASS_OPEN) {
				// open new file at the top
				this.list.ensureIndexIsVisible(0);
			}
			if (event.getClassFile() != null) {
				if (this.cf != null && event.getClassFile() == this.cf) {
					// avoid reprocessing an already open file
					break;
				}
				this.cf = event.getClassFile();
			}
			
			if (this.isOpen && this.cf != null) {
				load(this.cf);
			} else {
				upToDate = false;
			}
			break;
		case CLASS_REPARSE:
			this.cf = event.getClassFile();
			upToDate = false;
			break;
		case DEBUG_STACK_FRAME_CHANGED:
			openStackFrame(event.getStackFrame());
			break;
		case DEBUG_DETACH:
			clearExecutionRow();
			repaint();
			break;
		case DEBUG_RESUMED:
			clearExecutionRow();
			repaint();
			break;
		case DISPLAY_PARAMETER_UPDATE:
			repaint();
			break;
		case PROJECT_UPDATE:
		case DEBUG_ATTACH:
		case DEBUG_SUSPENDED:
		case DEBUG_THREAD_CHANGE_REQUESTED:
		case DEBUG_STEP_INTO_REQUESTED:
		case DEBUG_STEP_OUT_REQUESTED:
		case DEBUG_STEP_OVER_REQUESTED:
		case DEBUG_RESUME_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGE_REQUESTED:
		case DEBUG_SUSPEND_REQUESTED:
		case DEBUG_THREAD_CHANGED:
			// do nothing
			break;
		}

	}

	private void load(ClassFile cf) {
		// TODO: write a description of the hierarchy of elements in the table
		
		this.offsets = cf.getOffsetMap();
		if (this.sync != null) {
			this.sync.setOffsets(this.offsets);
		}
		
		this.rows = new ArrayList<EditorRow>();

		// Package
		this.packageDef = new PackageDefRow(cf);
		this.rows.add(this.packageDef);
		this.rows.add(new BlankRow());

		// Imports
		Imports imports = EditorFacade.getInstance().getImports(cf);
		this.renderer.setImports(imports);
		Set<String> ts = imports.getImports();
		this.importDefs = new ArrayList<ImportDefRow>(ts.size());
		for (String imp : ts) {
			ImportDefRow idr = new ImportDefRow(imp);
			this.rows.add(idr);
			this.importDefs.add(idr);
		}

		if (ts.size() > 0) {
			this.rows.add(new BlankRow());
			/* empty space between imports and class def */
		}
		
		// Add some useful information as comments

		// Source file name
		SourceFileAttribute sf = cf.getAttributes().getSourceFileAttribute();
		if (sf != null) {
			ClassCommentRow sfComment = new ClassCommentRow("SourceFile = " + sf.getSourceFile());
			this.rows.add(sfComment);
		}
		
		// Class version
	    ClassCommentRow versionComment = new ClassCommentRow("Class Version: " + cf.getMajorVersion() + "." + cf.getMinorVersion());
	    this.rows.add(versionComment);

		// Class annotations
		RuntimeInvisibleAnnotationsAttribute annInvisible = cf.getAttributes()
				.getRuntimeInvisibleAnnotationsAttribute();
		RuntimeVisibleAnnotationsAttribute annVisible = cf.getAttributes()
				.getRuntimeVisibleAnnotationsAttribute();
		List<Annotation> classAnnotations = new ArrayList<Annotation>();
		if (annInvisible != null) {
			classAnnotations.addAll(annInvisible.getAnnotations());
		}
		if (annVisible != null) {
			classAnnotations.addAll(annVisible.getAnnotations());
		}
		for (Annotation annotation : classAnnotations) {
			ClassAnnotationDefRow adr = new ClassAnnotationDefRow(annotation);
			this.rows.add(adr);
		}

		// Class
		this.classDef = new ClassDefRow(cf, true);
		this.rows.add(this.classDef);
		this.rows.add(new BlankRow());

		// Fields
		java.util.List fields = cf.getFields();
		for (int i = 0; i < fields.size(); i++) {
			Field field = (Field) fields.get(i);

			// Field annotations
			RuntimeInvisibleAnnotationsAttribute fieldAnnInvisible = field
					.getAttributes().getRuntimeInvisibleAnnotationsAttribute();
			RuntimeVisibleAnnotationsAttribute fieldAnnVisible = field
					.getAttributes().getRuntimeVisibleAnnotationsAttribute();
			List<Annotation> fieldAnnotations = new ArrayList<Annotation>();
			if (fieldAnnInvisible != null) {
				fieldAnnotations.addAll(fieldAnnInvisible.getAnnotations());
			}
			if (fieldAnnVisible != null) {
				fieldAnnotations.addAll(fieldAnnVisible.getAnnotations());
			}
			for (Annotation annotation : fieldAnnotations) {
				FieldAnnotationDefRow fadr = new FieldAnnotationDefRow(
						annotation);
				this.rows.add(fadr);
			}

			FieldDefRow fdr = new FieldDefRow(cf, field);
			this.rows.add(fdr);
			this.classDef.addField(fdr);
		}

		if (fields.size() > 0) {
			this.rows.add(new BlankRow());
		}

		// Methods
		java.util.List methods = cf.getMethods();
		for (int i = 0; i < methods.size(); i++) {
			Method method = (Method) methods.get(i);

			// Method annotations
			boolean deprecatedAnnotationAdded = false;
			RuntimeInvisibleAnnotationsAttribute methodAnnInvisible = method
					.getAttributes().getRuntimeInvisibleAnnotationsAttribute();
			RuntimeVisibleAnnotationsAttribute methodAnnVisible = method
					.getAttributes().getRuntimeVisibleAnnotationsAttribute();
			List<Annotation> methodAnnotations = new ArrayList<Annotation>();
			if (methodAnnInvisible != null) {
				methodAnnotations.addAll(methodAnnInvisible.getAnnotations());
			}
			if (methodAnnVisible != null) {
				methodAnnotations.addAll(methodAnnVisible.getAnnotations());
			}
			for (Annotation annotation : methodAnnotations) {
				MethodAnnotationDefRow madr = new MethodAnnotationDefRow(
						annotation);
				this.rows.add(madr);
				if ("java.lang.Deprecated".equals(annotation.getName())) {
					deprecatedAnnotationAdded = true;
					// store this information so that
					// the Deprecated attribute isn't used to
					// create another deprecation EditorRow
				}
			}

			Attributes attr = method.getAttributes();
			CodeAttribute codeAttr = attr.getCode();
			MethodDefRow mdr = new MethodDefRow(cf, method, true,
					codeAttr != null);
			if (!deprecatedAnnotationAdded && method.isDeprecated()) {
				DeprecatedAnnotationDefRow ddr = new DeprecatedAnnotationDefRow();
				this.rows.add(ddr);
			}
			this.rows.add(mdr);
			this.classDef.addMethod(mdr);
			LineNumberTableAttribute lnAttr = null;
			LocalVariableTableAttribute lvs = null;
			if (codeAttr != null) {
				if (codeAttr.getAttributes() != null) {
					lnAttr = codeAttr.getAttributes().getLineNumberTable();
					lvs = codeAttr.getAttributes().getLocalVariableTable();
				}
				Code code = codeAttr.getCode();
				DecompilationContext dc = code.createDecompilationContext();
				dc.setPosition(0);
				for (Instruction instruction : code.getInstructions()) {

					if (instruction instanceof Label) {
						LabelRow lr = new LabelRow((Label) instruction, mdr);
						lr.setParentCode(code);
						this.rows.add(lr);
						mdr.addCodeRow(lr);
					} else {
						int lineNumber = -1;

						if (lnAttr != null) {
							lineNumber = lnAttr.getLineNumber(dc.getPosition());
						}
						if (lvs != null) {
							List locals = lvs
									.getLocalVariable(dc.getPosition());
							for (int k = 0; k < locals.size(); k++) {
								LocalVariable lv = (LocalVariable) locals
										.get(k);
								LocalVariableDefRow lvdr = new LocalVariableDefRow(
										lv, mdr);
								this.rows.add(lvdr);
								mdr.addLocalVariable(lvdr);
							}
						}

						CodeRow cd = new CodeRow(cf, mdr, instruction);
						cd.setPosition(dc.getPosition());
						cd.setDecompilationContext(dc);
						cd.setParentCode(code);
						cd.setBreakpoint(EditorFacade.getInstance()
								.getBreakpoint(cf.getFullClassName(),
										method.getName(),
										method.getDescriptor(),
										dc.getPosition()));

						if (lineNumber != -1) {
							cd.setLineNumber(lineNumber);
						}

						this.rows.add(cd);
						mdr.addCodeRow(cd);

						dc.incrementPosition(instruction);
					}

				}

				this.rows.add(new MethodDefRow(cf, method, false, true));
			}
			this.rows.add(new BlankRow());
		}

		this.rows.add(new ClassDefRow(cf, false));

		this.label.setText("Bytecode Editor: " + cf.getFullClassName());
		this.model.clear();
		for (EditorRow er : rows) {
			this.model.addElement(er);
		}
		
		// mark as up to date
		this.upToDate = true;
	}

	public void redo() {
		EditorFacade.getInstance().performRedo();
	}

	public void undo() {
		EditorFacade.getInstance().performUndo();
	}

	public void insert() {
		MainWindow main = MainWindow.getInstance();
		Point pt = main.getMousePosition(true);

		int x = (int) pt.getX();
		int y = (int) pt.getY();
		EditorTab.this.insertContextMenu.show(MainWindow.getInstance(), x, y);
	}

	public void goTo(Link link) {
		if (link.getAnchor() == Link.ANCHOR_UNDEFINED) {
			throw new RuntimeException("Undefined anchor @ Link " + link.dump());
		}

		switchLabel:
		switch (link.getAnchor()) {
		case Link.ANCHOR_SOURCE_LINE_NUMBER: {
			for (int i = 0; i < this.rows.size(); i++) {
				Object obj = this.rows.get(i);
				if (obj instanceof CodeRow) {
					CodeRow cr = (CodeRow) obj;
					if (cr.getLineNumber() == link.getPosition()) {
						int index = this.rows.indexOf(cr);
						this.list.setSelectedIndex(index);
						this.list.ensureIndexIsVisible(index);
						break;
					}
				}
			}
			break;
		}
		case Link.ANCHOR_PC_OFFSET: {
			Object row = this.list.getSelectedValue();
			MethodDefRow mdr = null;
			if (row instanceof CodeRow) {
				CodeRow cr = (CodeRow) row;
				mdr = cr.getEnclosingMethodDef();
			} else if (row instanceof LabelRow) {
				LabelRow lr = (LabelRow) row;
				mdr = lr.getEnclosingMethodDef();
			} else if (row instanceof LocalVariableDefRow) {
				LocalVariableDefRow lvdr = (LocalVariableDefRow) row;
				mdr = lvdr.getEnclosingMethodDef();
			} else if (row instanceof MethodDefRow) {
				mdr = (MethodDefRow) row;
			}

			if (mdr != null) {
				List codeRows = mdr.getCodeRows();
				for (int i = 0; i < codeRows.size(); i++) {
					if (codeRows.get(i) instanceof LabelRow)
						continue;

					CodeRow cr = (CodeRow) codeRows.get(i);
					if (cr.getPosition() >= link.getPosition()) {
						int index = this.rows.indexOf(cr);
						this.list.setSelectedIndex(index);
						this.list.ensureIndexIsVisible(index);
						break;
					}
				}
			}
			break;
		}
		case Link.ANCHOR_CLASS_DEF: {
			int index = this.rows.indexOf(this.classDef);
			this.list.setSelectedIndex(index);
			break;
		}
		case Link.ANCHOR_FIELD_DEF: {
			List fields = this.classDef.getFields();
			for (int i = 0; i < fields.size(); i++) {
				FieldDefRow fdr = (FieldDefRow) fields.get(i);
				if (fdr.getField().getSignatureLine().equals(
						link.getField().getSignatureLine())) {
					int index = this.rows.indexOf(fdr);
					this.list.setSelectedIndex(index);
					this.list.ensureIndexIsVisible(index);
					break switchLabel;
				}
			}
			throw new AssertionError("Field in link not found: " + link.dump());
		}
		case Link.ANCHOR_METHOD_CODE: {
			List methods = this.classDef.getMethods();
			for (int i = 0; i < methods.size(); i++) {
				MethodDefRow mdr = (MethodDefRow) methods.get(i);
				if (mdr.getMethod().getSignatureLine().equals(
						link.getMethod().getSignatureLine())) {
					for (EditorRow er : mdr.getCodeRows()) {
						if (er instanceof CodeRow) {
							CodeRow cr = (CodeRow) er;
							if (cr.getPosition() == link.getPosition()) {
								int index = this.rows.indexOf(cr);
								this.list.setSelectedIndex(index);
								this.list.ensureIndexIsVisible(index);
								break switchLabel;
							}
						}
					}
					throw new AssertionError(
							"Position of method in link not found: "
									+ link.dump());
				}
			}
			throw new AssertionError("Method in link not found: " + link.dump());
		}
		case Link.ANCHOR_METHOD_DEF: {
			List methods = this.classDef.getMethods();
			for (int i = 0; i < methods.size(); i++) {
				MethodDefRow mdr = (MethodDefRow) methods.get(i);
				if (mdr.getMethod().getSignatureLine().equals(
						link.getMethod().getSignatureLine())) {
					int index = this.rows.indexOf(mdr);
					this.list.setSelectedIndex(index);
					this.list.ensureIndexIsVisible(index);
					break switchLabel;
				}
			}
			throw new AssertionError("Method in link not found: " + link.dump());

		}
		case Link.ANCHOR_METHOD_LV: {
			List methods = this.classDef.getMethods();
			for (int i = 0; i < methods.size(); i++) {
				MethodDefRow mdr = (MethodDefRow) methods.get(i);
				if (mdr.getMethod().getSignatureLine().equals(
						link.getMethod().getSignatureLine())) {
					java.util.List lvRows = mdr.getLocalVariables();
					for (int j = 0; j < lvRows.size(); j++) {
						LocalVariableDefRow lvdr = (LocalVariableDefRow) lvRows
								.get(j);
						if (lvdr.getLocalVariable().getSignatureLine().equals(
								link.getLv().getSignatureLine())) {
							int index = this.rows.indexOf(lvdr);
							this.list.setSelectedIndex(index);
							this.list.ensureIndexIsVisible(index);
							break switchLabel;
						}
					}
				}
			}
			throw new AssertionError("Method in link not found: " + link.dump());
		}
		}
	}
	
	private void modifyInstruction(int pc, Instruction instruction, DecompilationContext dc, Code code) {
		InstructionEditor editor = new InstructionEditor();
		editor.setPC(pc);
		editor.setInstruction(instruction);
        editor.setDecompilationContext(dc);
		editor.setClassFile(this.cf);
		editor.invokeModify();
		if (!editor.wasCancelled()) {
			GroupAction group = new GroupAction();
			List choosers = editor.getChoosers();
            Instruction newInstruction = editor.getInstruction();
            try {
                newInstruction = newInstruction.createNewInstance();
                newInstruction.setParameters(instruction.getParameters());
            } catch(Exception e) {
                SystemFacade.getInstance().handleException(e);
            }
            ModifyInstructionAction mia = new ModifyInstructionAction(newInstruction, pc, code);
            group.add(mia);
			modifyInstructionParameters(choosers, group, newInstruction);
            SystemFacade.getInstance().performAction(group);
		}
	}

	private void insertInstruction(MethodDefRow mdr, int pc, DecompilationContext dc, Code code) {
		InstructionEditor editor = new InstructionEditor();
		editor.setClassFile(this.cf);
		editor.setPC(pc);
		editor.setDecompilationContext(dc);
		editor.invokeInsert();
		if (!editor.wasCancelled()) {
            GroupAction group = new GroupAction();
            if (code == null) {
            	group.add(new CreateCodeAttributeAction(mdr.getMethod().getAttributes(), this.cf.getPool()));
            }
            List choosers = editor.getChoosers();
            Instruction instruction = editor.getInstruction();
            try {
            	instruction = instruction.createNewInstance();
            } catch(Exception e) {
            	SystemFacade.getInstance().handleException(e);
            }

    		InsertInstructionAction iia = new InsertInstructionAction(instruction, pc, mdr.getMethod());
            group.add(iia);
            modifyInstructionParameters(choosers, group, instruction);
            SystemFacade.getInstance().performAction(group);
		}
	}
	
	private void modifyInstructionParameters(List choosers, GroupAction group, Instruction instruction) {
		Parameters params = instruction.getParameters();
        for (int i = 0; i < params.getCount(); i++) {
            switch (params.getType(i)) {
            case TYPE_LOCAL_VARIABLE_WIDE:
            case TYPE_LOCAL_VARIABLE: {
                LocalVariableChooser chooser = (LocalVariableChooser) choosers.get(i);
                group.add(new ParamModifyAction(instruction, i, chooser.getValue()));
                break;
            }
            case TYPE_CONSTANT_POOL_METHOD_REF: {
                MethodChooser chooser = (MethodChooser) choosers.get(i);
                Object obj = chooser.getValue();
                if(obj instanceof Integer) {
                    group.add(new ParamModifyAction(instruction, i, chooser.getValue()));
                } else {
                    MethodLocator ml = (MethodLocator)obj;
                    String className = ml.getClassLocator().getFullName();
                    String methodName = ml.getMethod().getName();
                    String typeName = ml.getMethod().getDescriptor().getRawDesc();
                    int index = this.cf.getPool().indexOfMethodRef(className, methodName, typeName);
                    // TODO: verify that this makes sense.
                    // The logic could be in the action?
                    if (index != -1) {
                        group.add(new ParamModifyAction(instruction, i, Integer.valueOf(index)));
                    } else {
                        group.add(new AddMethodRefAction(className, methodName, typeName, instruction, i, this.cf.getPool()));
                    }
                }
                break;
            }
            case TYPE_CONSTANT_POOL_FIELD_REF: {
                FieldChooser chooser = (FieldChooser) choosers.get(i);
                Object obj = chooser.getValue();
                if(obj instanceof Integer) {
                    group.add(new ParamModifyAction(instruction, i, chooser.getValue()));
                } else {
                    FieldLocator fl = (FieldLocator)obj;
                    String className = fl.getClassLocator().getFullName();
                    String fieldName = fl.getField().getName();
                    String typeName = fl.getField().getDescriptor().getRawDesc();
                    int index = this.cf.getPool().indexOfFieldRef(className, fieldName, typeName);
                    // TODO: verify that this makes sense.
                    // The logic could be in the action?
                    if (index != -1) {
                        group.add(new ParamModifyAction(instruction, i, Integer.valueOf(index)));
                    } else {
                        group.add(new AddFieldRefAction(className, fieldName, typeName, instruction, i, this.cf.getPool()));
                    }
                }
                break;
            }
            case TYPE_CONSTANT_POOL_CLASS: {
                ClassChooser chooser = (ClassChooser) choosers.get(i);
                Object obj = chooser.getValue();
                if (obj instanceof Integer) {
                    //params.setValue(i, obj);
                    group.add(new ParamModifyAction(instruction, i, obj));
                } else {
                    String className = (String)obj;
                    int index = this.cf.getPool().indexOfClassRef(className);
                    if(index != -1) {
                        group.add(new ParamModifyAction(instruction, i, Integer.valueOf(index)));
                    } else {
                        group.add(new AddClassRefAction(className, instruction, i, this.cf.getPool()));
                    }
                }
                break;
            }
            case TYPE_CONSTANT: {
                ConstantChooser chooser = (ConstantChooser) choosers.get(i);
                Integer value = (Integer)chooser.getValue();
                if(value.intValue() < -128 || value.intValue() > 127) {
                    throw new RuntimeException("Constant value out of range.");
                }
                group.add(new ParamModifyAction(instruction, i, chooser.getValue()));
                break;
            }
            case TYPE_CONSTANT_WIDE: {
                ConstantChooser chooser = (ConstantChooser) choosers.get(i);
                /* @TODO range check? */
                group.add(new ParamModifyAction(instruction, i, chooser.getValue()));
                break;
            }
            case TYPE_ARRAYTYPE: {
                ArrayTypeChooser chooser = (ArrayTypeChooser) choosers.get(i);
                group.add(new ParamModifyAction(instruction, i, chooser.getValue()));
                break;
            }
            case TYPE_CONSTANT_POOL_CONSTANT: {
                ConstantpoolConstantChooser chooser = (ConstantpoolConstantChooser) choosers.get(i);
                group.add(new ParamModifyAction(instruction, i, chooser.getValue()));
                break;
            }
            case TYPE_LOCAL_VARIABLE_READONLY:
                // no action required
                break;
            case TYPE_CONSTANT_READONLY:
                // no action required
                break;
            case TYPE_LABEL:
                // no action required

                break;
            case TYPE_SWITCH:
                // no action required
                break;
            }
        }

	}

	public void insertInstruction() {
		Object row = this.list.getSelectedValue();
		if (row instanceof MethodDefRow) {
			MethodDefRow mdr = (MethodDefRow) row;
			Code code = null;
			LocalVariableTableAttribute lvAttr = null;
            DecompilationContext dc = null;
			if (mdr.getMethod().getAttributes().getCode() != null) {
				code = mdr.getMethod().getAttributes().getCode().getCode();
				lvAttr = mdr.getMethod().getAttributes().getCode().getAttributes().getLocalVariableTable(); 
			}
			int pc = 0;
			if (code != null && mdr.isClosing() && mdr.getCodeRows().size() > 0) {
				dc = code.createDecompilationContext();
				EditorRow er = mdr.getCodeRows().get(
						mdr.getCodeRows().size() - 1);
				if (er instanceof CodeRow) {
					CodeRow cr = (CodeRow) er;
					pc = cr.getPosition();
					dc.setPosition(pc);
					pc += cr.getInstruction().getSize(dc);
				}
			}
			
			insertInstruction(mdr, pc, dc, code);
		}
	}

	public void insert(boolean before) {
		if (this.list.getSelectedIndex() == -1) {
			return;
		}

		Object row = this.list.getSelectedValue();
		if (row instanceof CodeRow) {
			CodeRow cr = (CodeRow) row;
			DecompilationContext dc = cr.getDecompilationContext();
			int pos = cr.getPosition();
			if (!before) {
				dc.setPosition(pos);
				pos += cr.getInstruction().getSize(dc);
			}
			insertInstruction(cr.getEnclosingMethodDef(), pos, dc, cr.getEnclosingMethodDef().getMethod().getAttributes().getCode().getCode());
		}

	}

	public void remove() {
		List<EditorRow> removeList = new ArrayList<EditorRow>();
		// first, add all the methods and fields in the list of to-be-removed
		// items
		for (Object o : this.list.getSelectedValues()) {

			if (o instanceof MethodDefRow || o instanceof FieldDefRow) {
				removeList.add((EditorRow) o);
			}
		}

		// then, add all the instructions that aren't in the methods that were
		// removed(because there is no point in creating a remove for the
		// instruction since it's going to be removed anyway
		for (Object o : this.list.getSelectedValues()) {
			if (o instanceof CodeRow) {
				CodeRow cr = (CodeRow) o;
				if (!removeList.contains(cr.getEnclosingMethodDef())) {
					removeList.add(cr);
				}
			}
		}

		EditorFacade.getInstance().remove(removeList);
	}

	void modifyRow() {
		Object o = this.list.getSelectedValue();
		if (o instanceof CodeRow) {
			CodeRow cr = (CodeRow) o;
			modifyInstruction(cr.getPosition(), cr.getInstruction(), cr.getDecompilationContext(), cr.getEnclosingMethodDef().getMethod().getAttributes().getCode().getCode());
			this.list.repaint();
		} else if (o instanceof MethodDefRow) {
			MethodDefRow mdr = (MethodDefRow) o;
			Method method = mdr.getMethod();
			CodeAttribute code = method.getAttributes().getCode();
			int flags = method.getAccessFlags();
			if (!AccessFlags.isNative(flags) && !AccessFlags.isAbstract(flags)) {
				this.methodEditor.invoke(method.getName(), method
						.getDescriptor(), method.getAccessFlags(), Integer.valueOf(
						code.getMaxStackSize()), Integer.valueOf(code
						.getMaxLocals()), method.getExceptions());
			} else {
				this.methodEditor.invoke(method.getName(), method
						.getDescriptor(), method.getAccessFlags(), null, null,
						method.getExceptions());
			}

			if (!this.methodEditor.wasCancelled()) {
				AccessFlags af = this.methodEditor.getAccessFlags();
				String name = this.methodEditor.getMethodName();
				Descriptor desc = this.methodEditor.getDescriptor();
				int maxStack = this.methodEditor.getMaxStack();
				int maxLocals = this.methodEditor.getMaxLocals();

				EditorFacade.getInstance().modifyMethod(
						mdr.getClassFile().getPool(), mdr.getMethod(), name,
						desc, af, maxStack, maxLocals,
						methodEditor.getExceptions());
			}

		} else if (o instanceof FieldDefRow) {
			FieldDefRow fdr = (FieldDefRow) o;
			Field field = fdr.getField();
			int flags = field.getAccessFlags();
			this.fieldEditor.invoke(field.getName(), field.getDescriptor(),
					flags);

			if (!this.fieldEditor.wasCancelled()) {
				AccessFlags af = this.fieldEditor.getAccessFlags();
				String name = this.fieldEditor.getFieldName();
				Descriptor desc = this.fieldEditor.getDescriptorType();

				EditorFacade.getInstance().modifyField(
						fdr.getClassFile().getPool(), fdr.getField(), name,
						desc, af);
			}
		} else if (o instanceof ClassDefRow) {
			ClassDefRow cdr = (ClassDefRow) o;
			ClassFile cf = cdr.getClassFile();
			this.classEditor.invoke(cf.getFullClassName(), cf
					.getSuperClassName(), cf.getAccessFlags(), cf
					.getInterfaces());

			if (!this.classEditor.wasCancelled()) {
				String name = this.classEditor.getClassName();
				String superName = this.classEditor.getSuperClassname();
				AccessFlags flags = this.classEditor.getFlags();
				List<String> intfs = this.classEditor.getInterfaces();
				/* new list as user has modified it */
				List<Interface> remainingInterfaces = new ArrayList<Interface>();
				/* interfaces that remain untouched */
				List<Interface> old = cf.getInterfaces();
				/* interfaces before modification */
				List<String> oldNames = new ArrayList<String>();
				/* Names of interfaces before modification */

				for (int i = 0; i < old.size(); i++) {
					Interface intf = old.get(i);
					oldNames.add(intf.getName());
					if (intfs.contains(intf.getName())) {
						remainingInterfaces.add(intf);
					}
				}

				List<String> newInterfaces = new ArrayList<String>();
				newInterfaces.addAll(intfs);
				newInterfaces.removeAll(oldNames);
				EditorFacade.getInstance().modifyClass(cf, flags, name,
						superName, remainingInterfaces, newInterfaces);
			}
		}
	}

	public void find() {
		String query = (String)JOptionPane.showInputDialog(this, "Search for..", "Search", JOptionPane.QUESTION_MESSAGE, null, null, this.lastQueryString);
		if (query == null)
			return; // early return

		this.lastQueryString = query;
		this.lastSearch = new CaseInsensitiveMatcher(query);
		BytecodeRenderer renderer = new BytecodeRenderer();
		PlaintextSyntaxDrawer sd = new PlaintextSyntaxDrawer();
		Imports imports = EditorFacade.getInstance().getImports(cf);

		for (int i = 0; i < model.size(); i++) {
			sd.clear();
			renderer.render((EditorRow) model.elementAt(i), sd, imports);
			if (this.lastSearch.matches(sd.getText())) {
				list.setSelectedIndex(i);
				list.ensureIndexIsVisible(i);
				SystemFacade.getInstance().setStatus("Found '" + query + "'.");
				return; // early return
			}
		}
		this.lastSearch = null;
		SystemFacade.getInstance().setStatus("No occurances of '" + query + "' found.");		
	}

	public void findNext() {
		if (EditorTab.this.lastSearch == null) {
			find();
		} else {
			BytecodeRenderer renderer = new BytecodeRenderer();
			PlaintextSyntaxDrawer sd = new PlaintextSyntaxDrawer();
			Imports imports = EditorFacade.getInstance().getImports(cf);

			for (int i = EditorTab.this.list.getSelectedIndex() + 1; i < model
					.size(); i++) {
				sd.clear();
				renderer.render((EditorRow) model.elementAt(i), sd, imports);
				if (EditorTab.this.lastSearch.matches(sd.getText())) {
					EditorTab.this.list.setSelectedIndex(i);
					EditorTab.this.list.ensureIndexIsVisible(i);
					SystemFacade.getInstance().setStatus("Found '" + this.lastQueryString + "'.");
					return; // early return
				}
			}
			SystemFacade.getInstance().setStatus("No more occurances of '" + this.lastQueryString + "' found.");		
		}
	}

	public String getSelectionPlainText() {
		BytecodeRenderer renderer = new BytecodeRenderer();
		PlaintextSyntaxDrawer sd = new PlaintextSyntaxDrawer();
		Imports imports = EditorFacade.getInstance().getImports(
				this.classDef.getClassFile());
		for (Object obj : this.list.getSelectedValues()) {
			EditorRow er = (EditorRow) obj;
			renderer.render(er, sd, imports);
			sd.drawLineBreak();
		}

		return sd.getText();
	}

	public String getSelectionHTML() {
		BytecodeRenderer renderer = new BytecodeRenderer();
		HTMLSyntaxDrawer sd = new HTMLSyntaxDrawer();
		Imports imports = EditorFacade.getInstance().getImports(
				this.classDef.getClassFile());
		for (Object obj : this.list.getSelectedValues()) {
			EditorRow er = (EditorRow) obj;
			renderer.render(er, sd, imports);
			sd.drawLineBreak();
		}

		return "<HTML><FONT FACE=\"Courier New\">" + sd.getHTML() + "</FONT></HTML>";
	}

	public Object getSelectionObject() {
		List<Transferrable> transferables = new ArrayList<Transferrable>();
		List<EditorRow> code = new ArrayList<EditorRow>();
		int rows[] = this.list.getSelectedIndices();
		for (int row : rows) {
			Object obj = this.model.elementAt(row);
			if (obj instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow) obj;
				if (!mdr.isClosing()) {
					Method method = mdr.getMethod();
					List<String> exceptionNames = new ArrayList<String>();
					for (ExceptionDescriptor ex : method
							.getExceptions()) {
						exceptionNames.add(ex.getName());
					}
					// TODO: Attributes are not being copied

					TransferrableMethod transferrable = new TransferrableMethod();
					transferrable.setMethodName(method.getName());
					transferrable.setDescriptor(method.getDescriptor());
					transferrable.setAccessFlags(method.getAccessFlags());
					CodeAttribute ca = method.getAttributes().getCode();
					if (ca != null) {
						transferrable.setMaxStack(ca.getMaxStackSize());
						transferrable.setMaxLocals(ca.getMaxLocals());
						List<Instruction> list = new ArrayList<Instruction>();
						for (EditorRow er : mdr.getCodeRows()) {
							if (er instanceof CodeRow) {
								list.add(((CodeRow) er).getInstruction());
							} else if (er instanceof LabelRow) {
								list.add(((LabelRow) er).getLabel());
							} else {
								throw new AssertionError(
										"Object of invalid class (not CodeRow and not LabelRow) in list: "
												+ er.getClass());
							}
						}

						ConstantPool newPool = new ConstantPool();
						Code codeBlock = new Code(newPool);
						InstructionCopier instructionCopier = new InstructionCopier();
						for (EditorRow er : mdr.getCodeRows()) {
							if (er instanceof LabelRow)
								continue;
							CodeRow cr = (CodeRow) er;

							Instruction inst = cr.getInstruction();

							Instruction copy = instructionCopier
									.copyInstruction(inst, cr
											.getDecompilationContext()
											.getConstantPool(), newPool);
							
							if (copy == null) {
								throw new AssertionError("Copied instruction is null for instruction: " + inst);
							}
							int index = list.indexOf(inst);
							list.set(index, copy);
							List<Label> labels = inst.getLabels();
							List<Label> copyLabels = copy.getLabels();
							for (int i = 0; i < labels.size(); i++) {
								if (copyLabels.get(i) == null) {
									throw new AssertionError("Copied label is null for instruction: " + inst + " : " + copyLabels);
								}
								Label label = labels.get(i);
								int labelIndex = list.indexOf(label);
								if (labelIndex != -1) {
									list.set(labelIndex, copyLabels.get(i));
								} else {
									list.add(copyLabels.get(i));
								}
							}

						}
						codeBlock.add(0, list);
						transferrable.setCode(codeBlock);
					}
					transferrable.setExceptions(exceptionNames);
					transferables.add(transferrable);
				}

			} else if (obj instanceof FieldDefRow) {
				FieldDefRow fdr = (FieldDefRow) obj;
				Field field = fdr.getField();
				// TODO: Attributes are not being copied

				TransferrableField transferrable = new TransferrableField();
				transferrable.setFieldName(field.getName());
				transferrable.setDescriptor(field.getDescriptor());
				transferrable.setAccessFlags(field.getAccessFlags());
				transferables.add(transferrable);
			} else if (obj instanceof CodeRow || obj instanceof LabelRow) {
				code.add((EditorRow) obj);
			}
		}

		if (!transferables.isEmpty()) {
			return transferables;
		} else {
			List<Instruction> list = new ArrayList<Instruction>();
			for (EditorRow er : code) {
				if (er instanceof CodeRow) {
					list.add(((CodeRow) er).getInstruction());
				} else if (er instanceof LabelRow) {
					list.add(((LabelRow) er).getLabel());
				} else {
					throw new AssertionError(
							"Object of invalid class (not CodeRow and not LabelRow) in list: "
									+ er.getClass());
				}
			}

			ConstantPool newPool = new ConstantPool();
			Code codeBlock = new Code(newPool);
			InstructionCopier instructionCopier = new InstructionCopier();
			for (EditorRow er : code) {
				if (er instanceof LabelRow)
					continue;
				CodeRow cr = (CodeRow) er;
				Instruction inst = cr.getInstruction();

				Instruction copy = instructionCopier.copyInstruction(inst, cr
						.getDecompilationContext().getConstantPool(), newPool);
				int index = list.indexOf(inst);
				list.set(index, copy);
				List<Label> labels = inst.getLabels();
				List<Label> copyLabels = copy.getLabels();
				for (int i = 0; i < labels.size(); i++) {
					Label label = labels.get(i);
					int labelIndex = list.indexOf(label);
					if (labelIndex != -1) {
						list.set(labelIndex, copyLabels.get(i));
					} else {
						list.add(copyLabels.get(i));
					}
				}

			}
			codeBlock.add(0, list);

			return codeBlock;
		}
	}

	public void pasteRows(Object data) {
		if (data instanceof List) {
			GroupAction ga = new GroupAction();
			// methods or fields involved
			@SuppressWarnings("unchecked")
			List<Transferrable> transferrables = (List) data;
			for (Transferrable transferrable : transferrables) {
				if (transferrable instanceof TransferrableField) {
					TransferrableField field = (TransferrableField) transferrable;

					Undoable insertFieldAction = new InsertFieldAction(
							this.classDef.getClassFile(), field.getFieldName(),
							field.getDescriptor(), new AccessFlags(field
									.getAccessFlags()));
					ga.add(insertFieldAction);
				} else if (transferrable instanceof TransferrableMethod) {
					TransferrableMethod method = (TransferrableMethod) transferrable;
					int maxStack = 0;
					int maxLocals = 0;
					if (method.getMaxStack() != null) {
						maxStack = method.getMaxStack();
					}
					if (method.getMaxLocals() != null) {
						maxLocals = method.getMaxLocals();
					}
					// TODO: Attributes are not being copied
					InsertMethodAction insertMethodAction = new InsertMethodAction(
							this.classDef.getClassFile(), method
									.getMethodName(), method.getDescriptor(),
							new AccessFlags(method.getAccessFlags()), maxStack,
							maxLocals, method.getExceptions());
					ga.add(insertMethodAction);
					InsertCodeToNewMethodAction ictnma = new InsertCodeToNewMethodAction(
							this.classDef.getClassFile(), insertMethodAction,
							method.getCode());
					ga.add(ictnma);
				} else {
					throw new AssertionError(
							"Invalid object in List of pasted data.");
				}
			}

			if (!ga.isEmpty()) {
				SystemFacade.getInstance().performAction(ga);
			}

		} else {
			if (this.list.getSelectedIndex() == -1) {
				return;
			}

			Object row = this.list.getSelectedValue();
			if (row instanceof CodeRow) {
				// before selected instruction
				CodeRow cr = (CodeRow) row;
				int pos = cr.getPosition();
				InsertCodeAction ica = new InsertCodeAction(this.classDef
						.getClassFile(), cr.getParentCode(), pos, (Code) data);
				SystemFacade.getInstance().performAction(ica);
			} else if (row instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow) row;
				if (mdr.isClosing()) {
					// at the end of the code block
					CodeAttribute ca = mdr.getMethod().getAttributes()
							.getCode();
					Code code = ca.getCode();
					InsertCodeAction ica = new InsertCodeAction(this.classDef
							.getClassFile(), code, code.getMaxPC(), (Code) data);
					SystemFacade.getInstance().performAction(ica);
				} else {
					// at the beginning of the code block
					CodeAttribute ca = mdr.getMethod().getAttributes()
							.getCode();
					Code code = ca.getCode();
					InsertCodeAction ica = new InsertCodeAction(this.classDef
							.getClassFile(), code, 0, (Code) data);
					SystemFacade.getInstance().performAction(ica);
				}
			}
		}
	}
	
	private void clearExecutionRow() {
		if (this.executionRow != null) {
			if (this.executionRow instanceof CodeRow) {
				((CodeRow)this.executionRow).setExecutionRow(false);
			} else if (this.executionRow instanceof MethodDefRow) {
				((MethodDefRow)this.executionRow).setExecutionRow(false);
			}
			
			this.executionRow = null;
		}
	}
	
	public void setExecutionRow(String methodName, Descriptor desc, Integer pc) {
		clearExecutionRow();
		List methods = this.classDef.getMethods();
		for (int i = 0; i < methods.size(); i++) {
			MethodDefRow mdr = (MethodDefRow) methods.get(i);
			if (mdr.getMethod().getName().equals(methodName)
			 && mdr.getMethod().getDescriptor().equals(desc)) {
				if (pc == null) {
					// Method row
					this.executionRow = mdr;
					mdr.setExecutionRow(true);
					int index = this.rows.indexOf(mdr);
					this.list.ensureIndexIsVisible(index);
					repaint();					
					return;
				}
				
				// Code row
				for (EditorRow er : mdr.getCodeRows()) {
					if (er instanceof CodeRow) {
						CodeRow cr = (CodeRow) er;
						if (cr.getPosition() == pc.intValue()) {
							final int index = this.rows.indexOf(cr);
							this.executionRow = cr;
							cr.setExecutionRow(true);
							// Workaround for a problem where ensureIndexIsVisible
							// is not working for some reason even though this
							// method is always called from the event thread
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									list.ensureIndexIsVisible(index);
									repaint();
								}
							});
							return;
						}
					}
				}
				throw new AssertionError(
						"Position of method in not found: " + methodName + " " + desc + " " + pc);
			}
		}
		throw new AssertionError("Method not found: " + methodName + " " + desc);

	}
	
	private void openStackFrame(IStackFrame sf) {
		ClassIndex ci = SystemFacade.getInstance().getClassIndex();
		ClassLocator cl = ci.getLocator(sf.location().declaringType().name());
		if (cl != null) {
			try {
				ClassFile cf = SystemFacade.getInstance().getClassFile(cl);
				IMethod bpMethod = sf.location().method(); 
				for (net.sf.rej.java.Method method : cf.getMethods()) {
					if (method.getName().equals(bpMethod.name()) && method.getDescriptor().getRawDesc().equals(bpMethod.signature())) {
						Integer pc = null;
						if (sf.location().codeIndex() != -1) {
							pc = (int)sf.location().codeIndex();
						}
						
						Event event = new Event(EventType.CLASS_OPEN);
						event.setClassFile(cf);
						event.setFile(cl.getFile());
						this.dispatcher.notifyObservers(event);
						setExecutionRow(method.getName(), method.getDescriptor(), pc);
						break;
					}
				}
			} catch(Exception ioe) {
				ioe.printStackTrace();
			}
		} else {
			ClassFactory factory = new ClassFactory();
			IReferenceType rt = sf.location().declaringType();
			String superClass = null;
			if (!"java.lang.Object".equals(rt.name())) {
				superClass = rt.getSuperClassName(); 
			}
			ClassFile cf = factory.createClass(rt.name(), superClass);
			ConstantPool cp = cf.getPool();
			FieldFactory fieldFactory = new FieldFactory();
			for (IField field : rt.visibleFields()) {
				AccessFlags flags = new AccessFlags(field.modifiers());
				Field fieldToAdd = fieldFactory.createField(cf, flags, cp.optionalAddUtf8(field.name()), cp.optionalAddUtf8(field.signature()));
				cf.add(fieldToAdd);
			}
			
			MethodFactory methodFactory = new MethodFactory();
			for (IMethod method : rt.visibleMethods()) {
				AccessFlags flags = new AccessFlags(method.modifiers());
				net.sf.rej.java.Method methodToAdd = methodFactory.createMethod(cf, flags, cp.optionalAddUtf8(method.name()), cp.optionalAddUtf8(method.signature()), cp.optionalAddUtf8("Code"), 0, 0, cp.optionalAddUtf8("Exceptions"), new ArrayList<ExceptionDescriptor>());
				cf.add(methodToAdd);
			}

			SystemFacade.getInstance().setStatus("Class definition pulled from VM: " + sf.location().declaringType().name());
			Event event = new Event(EventType.CLASS_OPEN);
			event.setClassFile(cf);
			this.dispatcher.notifyObservers(event);
		}

	}

	public void outline() {
		List<EditorRow> list = new ArrayList<EditorRow>();
		list.add(this.packageDef);
		list.add(this.classDef);
		list.addAll(this.classDef.getFields());
		list.addAll(this.classDef.getMethods());
		
		QuickOutlineDialog qod = new QuickOutlineDialog(MainWindow.getInstance(), list);
		qod.invoke();
		EditorRow er = qod.getSelected();
		if (er != null) {
			int index = this.rows.indexOf(er);
			this.list.setSelectedIndex(index);
			this.list.ensureIndexIsVisible(index);
		}
	}

	public void leavingTab() {
		this.isOpen = false;
	}

	public void setSplitSynchronizer(BytecodeSplitSynchronizer sync) {
		this.sync = sync;
		this.sync.setOffsets(this.offsets);
		splitSynchronize();
	}
	
	private void splitSynchronize() {
		if (this.sync != null && this.isOpen) {
			this.sync.sync((EditorRow) this.list.getSelectedValue());
		}
	}
	
	public String getTabTitle() {
		return "Editor";
	}

	public void enteringTab() {
		this.isOpen = true;
		if (!this.upToDate) {
			if (this.cf != null) {
				load(this.cf);
			} else {
				this.upToDate = true;
			}
		}
		splitSynchronize();
	}

}

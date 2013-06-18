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
package net.sf.rej.gui.compare;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import net.sf.rej.Imports;
import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.dialog.QuickOutlineDialog;
import net.sf.rej.gui.editor.CaseInsensitiveMatcher;
import net.sf.rej.gui.editor.rendering.BytecodeRenderer;
import net.sf.rej.gui.editor.rendering.HTMLSyntaxDrawer;
import net.sf.rej.gui.editor.rendering.PlaintextSyntaxDrawer;
import net.sf.rej.gui.editor.row.BlankRow;
import net.sf.rej.gui.editor.row.ClassCommentRow;
import net.sf.rej.gui.editor.row.ClassDefRow;
import net.sf.rej.gui.editor.row.CodeRow;
import net.sf.rej.gui.editor.row.DeprecatedAnnotationDefRow;
import net.sf.rej.gui.editor.row.EditorRow;
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
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Code;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Field;
import net.sf.rej.java.Interface;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.java.attribute.LineNumberTableAttribute;
import net.sf.rej.java.attribute.LocalVariableTableAttribute;
import net.sf.rej.java.attribute.RuntimeInvisibleAnnotationsAttribute;
import net.sf.rej.java.attribute.RuntimeVisibleAnnotationsAttribute;
import net.sf.rej.java.attribute.SourceFileAttribute;
import net.sf.rej.java.attribute.annotations.Annotation;
import net.sf.rej.java.instruction.DecompilationContext;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Label;

/**
 * Bytecode compare display. Uses a
 * <code>JList</code> with a custom renderer.
 * 
 * @author Sami Koivu
 */

public class ComparePanel extends JPanel implements TransferComponent {

	private static final long serialVersionUID = 1L;

	private CaseInsensitiveMatcher lastSearch = null;
	private String lastQueryString = null;
	
	private List<EditorRow> rowsAll;
	private Collection<EditorRow> rowsA;
	private Collection<EditorRow> rowsB;

	private CodeCompareRenderer renderer = new CodeCompareRenderer();

	DefaultListModel model = new DefaultListModel();

	JList list = new JList(this.model);
	private JScrollPane editorScrollPane = new JScrollPane(this.list);
	
	private ClassFile cfA;
	private ClassFile cfB;

	private JLabel label = new JLabel("Bytecode Compare");

	BufferedImage clueImage = null;

	public ComparePanel() {
		this.setLayout(new BorderLayout());
		this.list.setCellRenderer(this.renderer);
		this.list.setTransferHandler(new BytecodeEditorTransferHandler(this));
		this.add(this.label, BorderLayout.NORTH);
		this.add(this.editorScrollPane, BorderLayout.CENTER);
		this.editorScrollPane.setVerticalScrollBar(new JScrollBar() {
			final AlphaComposite SEMI_OPAQUE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
        	@Override
        	public void paint(Graphics g) {
        		g.setPaintMode();
        		super.paint(g);
        		if (clueImage != null) {
        			Graphics2D g2 = (Graphics2D)g;
        			g2.setComposite(SEMI_OPAQUE);
        			g2.drawImage(clueImage, 2, 17, getWidth()-3, getHeight()-34, null);
        		}
        	}
        });
	}
	
	public void setClassFiles(ClassFile cfA, ClassFile cfB) {
		this.cfA = cfA;
		this.cfB = cfB;
		
		if (cfA.getFullClassName().equals(cfB.getFullClassName())) {
			this.label.setText("Bytecode Compare: " + cfA.getFullClassName());
		} else {
			this.label.setText("Bytecode Compare: " + cfA.getFullClassName() + " / " + cfB.getFullClassName());			
		}
		
		load(cfA, cfB);
	}

	private void load(ClassFile cfA, ClassFile cfB) {
		this.rowsAll = new ArrayList<EditorRow>();
		this.rowsA = new HashSet<EditorRow>();
		this.rowsB = new HashSet<EditorRow>();

		// Package
		if (cfA.getPackageName().equals(cfB.getPackageName())) {
			this.rowsAll.add(new PackageDefRow(cfA));			
		} else {
			PackageDefRow pdrA = new PackageDefRow(cfA);
			PackageDefRow pdrB = new PackageDefRow(cfB);
			this.rowsA.add(pdrA);
			this.rowsB.add(pdrB);
			this.rowsAll.add(pdrA);
			this.rowsAll.add(pdrB);
		}
		
		this.rowsAll.add(new BlankRow());

		// Imports
		Imports importsA = EditorFacade.getInstance().getImports(cfA);
		Imports importsB = EditorFacade.getInstance().getImports(cfB);
		this.renderer.setImports(importsA, importsB);
		
		Set<String> tsA = importsA.getImports();
		Set<String> tsB = importsB.getImports();
		Set<String> allOrdered = new TreeSet<String>();
		allOrdered.addAll(tsA);
		allOrdered.addAll(tsB);
		for (String imp : allOrdered) {
			ImportDefRow idr = new ImportDefRow(imp);
			this.rowsAll.add(idr);
			if (!tsA.contains(imp)) {
				this.rowsB.add(idr);
			}
			if (!tsB.contains(imp)) {
				this.rowsA.add(idr);
			}
		}

		if (allOrdered.size() > 0) {
			this.rowsAll.add(new BlankRow());
			/* empty space between imports and class def */
		}
		
		// Add some useful information as comments

		// Source file name
		SourceFileAttribute sfA = cfA.getAttributes().getSourceFileAttribute();
		SourceFileAttribute sfB = cfB.getAttributes().getSourceFileAttribute();
		if (sfA == null && sfB == null) {
			// Add nothing, neither has a source file attribute
		} else if (sfA == null || sfB == null) {
			// Only one has a source file attribute
			if (sfA != null) {
				ClassCommentRow sfComment = new ClassCommentRow("SourceFile = " + sfA.getSourceFile());
				this.rowsA.add(sfComment);
				this.rowsAll.add(sfComment);
			}
			
			if (sfB != null) {
				ClassCommentRow sfComment = new ClassCommentRow("SourceFile = " + sfB.getSourceFile());
				this.rowsB.add(sfComment);
				this.rowsAll.add(sfComment);				
			}
			
		} else {
			// Both have source file attributes
			if (sfA.getSourceFile().equals(sfB.getSourceFile())) {
				ClassCommentRow sfComment = new ClassCommentRow("SourceFile = " + sfA.getSourceFile());
				this.rowsAll.add(sfComment);
			} else {
				ClassCommentRow sfCommentA = new ClassCommentRow("SourceFile = " + sfA.getSourceFile());
				this.rowsA.add(sfCommentA);
				this.rowsAll.add(sfCommentA);

				ClassCommentRow sfCommentB = new ClassCommentRow("SourceFile = " + sfB.getSourceFile());
				this.rowsB.add(sfCommentB);
				this.rowsAll.add(sfCommentB);
			}
		}
		
		// Class version
		if (cfA.getMajorVersion() == cfB.getMajorVersion()
		 && cfA.getMinorVersion() == cfB.getMinorVersion()) {
		    ClassCommentRow versionComment = new ClassCommentRow("Class Version: " + cfA.getMajorVersion() + "." + cfA.getMinorVersion());
		    this.rowsAll.add(versionComment);
		} else {
			ClassCommentRow versionCommentA = new ClassCommentRow("Class Version: " + cfA.getMajorVersion() + "." + cfA.getMinorVersion());
			this.rowsA.add(versionCommentA);
			ClassCommentRow versionCommentB = new ClassCommentRow("Class Version: " + cfB.getMajorVersion() + "." + cfB.getMinorVersion());
			this.rowsB.add(versionCommentB);
			this.rowsAll.add(versionCommentA);
			this.rowsAll.add(versionCommentB);
		}

		// Class annotations
		/* TODO:
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
		 */
		
		List<Interface> interfacesA = cfA.getInterfaces();
		List<Interface> interfacesB = cfB.getInterfaces();
		boolean interfacesAreEqual = interfacesA.equals(interfacesB);

		// Class
		if (cfA.getShortClassName().equals(cfB.getShortClassName())
		 && cfA.getAccessFlags() == cfB.getAccessFlags()
		 && cfA.getSuperClassName().equals(cfB.getSuperClassName())
		 && interfacesAreEqual) {
			this.rowsAll.add(new ClassDefRow(cfA, true));
		} else {
			ClassDefRow cdrA = new ClassDefRow(cfA, true);
			ClassDefRow cdrB = new ClassDefRow(cfB, true);
			this.rowsA.add(cdrA);
			this.rowsB.add(cdrB);
			this.rowsAll.add(cdrA);
			this.rowsAll.add(cdrB);
		}
		
		this.rowsAll.add(new BlankRow());

		// Fields
		// TODO: Constant value compare
		Map<String, Field> fieldsA = new HashMap<String, Field>();
		for (Field field : cfA.getFields()) {
			fieldsA.put(field.getSignatureLine(), field);
		}
		Map<String, Field> fieldsB = new HashMap<String, Field>();
		for (Field field : cfB.getFields()) {
			fieldsB.put(field.getSignatureLine(), field);
		}
		Set<String> allFields = new TreeSet<String>();
		allFields.addAll(fieldsA.keySet());
		allFields.addAll(fieldsB.keySet());
		for (String fieldSignature : allFields) {
			// Field annotations
			/* TODO
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
			 */

			FieldDefRow fdr = null;
			Field field = fieldsA.get(fieldSignature);
			if (field == null) {
				field = fieldsB.get(fieldSignature);
				fdr = new FieldDefRow(cfB, field);
				this.rowsB.add(fdr);
			} else {
				fdr = new FieldDefRow(cfA, field);
				if (!fieldsB.keySet().contains(fieldSignature)) {
					this.rowsA.add(fdr);
				}
			}
			
			this.rowsAll.add(fdr);
		}

		if (allFields.size() > 0) {
			this.rowsAll.add(new BlankRow());
		}

		// Methods
		Map<String, Method> methodsA = new HashMap<String, Method>();
		for (Method method : cfA.getMethods()) {
			Descriptor desc = method.getDescriptor();
			methodsA.put(desc.getReturn() + method.getName() + " " + desc.getParams(), method);
		}
		Map<String, Method> methodsB = new HashMap<String, Method>();
		for (Method method : cfB.getMethods()) {
			Descriptor desc = method.getDescriptor();
			methodsB.put(desc.getReturn() + method.getName() + " " + desc.getParams(), method);
		}

		Set<String> allMethods = new TreeSet<String>();
		allMethods.addAll(methodsA.keySet());
		allMethods.addAll(methodsB.keySet());
		for (String methodTypeNameParams : allMethods) {
			Method methodA = methodsA.get(methodTypeNameParams);
			Method methodB = methodsB.get(methodTypeNameParams);
			if (methodA == null) {
				List<EditorRow> methodRows = getMethodRows(cfB, methodB);
				this.rowsB.addAll(methodRows);
				this.rowsAll.addAll(methodRows);
				// method only exists in B
			} else if (methodB == null) {
				List<EditorRow> methodRows = getMethodRows(cfA, methodA);
				this.rowsA.addAll(methodRows);
				this.rowsAll.addAll(methodRows);
				// method only exists in A
			} else {
				// method exists in both
				addMethodRows(cfA, cfB, methodA, methodB);
			}

			this.rowsAll.add(new BlankRow());
		}

		this.rowsAll.add(new ClassDefRow(cfA, false));
		
		this.renderer.setRedSet(this.rowsA);
		this.renderer.setYellowSet(this.rowsB);

		this.model.clear();
		for (EditorRow er : this.rowsAll) {
			this.model.addElement(er);
		}
		
		// Scrollbar clue image
		int all = rowsAll.size();
		this.clueImage = new BufferedImage(10, all, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.clueImage.getGraphics();
		g.setColor(Color.RED);
		for (EditorRow er : rowsA) {
			int i = rowsAll.indexOf(er);
    		g.fillRect(0, i, 10, 1);        				
		}
		g.setColor(Color.YELLOW.darker());
		for (EditorRow er : rowsB) {
			int i = rowsAll.indexOf(er);
    		g.fillRect(0, i, 10, 1);
		}
	}

	public void find() {
		if (this.rowsAll == null) return; // early return
		
		String query = (String)JOptionPane.showInputDialog(this, "Search for..", "Search", JOptionPane.QUESTION_MESSAGE, null, null, this.lastQueryString);
		if (query == null)
			return; // early return

		this.lastQueryString = query;
		this.lastSearch = new CaseInsensitiveMatcher(query);
		BytecodeRenderer renderer = new BytecodeRenderer();
		PlaintextSyntaxDrawer sd = new PlaintextSyntaxDrawer();
		Imports imports = EditorFacade.getInstance().getImports(cfA);

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
		if (this.lastSearch == null) {
			find();
		} else {
			BytecodeRenderer renderer = new BytecodeRenderer();
			PlaintextSyntaxDrawer sd = new PlaintextSyntaxDrawer();
			Imports imports = EditorFacade.getInstance().getImports(cfA);

			for (int i = this.list.getSelectedIndex() + 1; i < model
					.size(); i++) {
				sd.clear();
				renderer.render((EditorRow) model.elementAt(i), sd, imports);
				if (this.lastSearch.matches(sd.getText())) {
					this.list.setSelectedIndex(i);
					this.list.ensureIndexIsVisible(i);
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
		Imports imports = EditorFacade.getInstance().getImports(this.cfA);
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
		Imports imports = EditorFacade.getInstance().getImports(this.cfA);
		for (Object obj : this.list.getSelectedValues()) {
			EditorRow er = (EditorRow) obj;
			renderer.render(er, sd, imports);
			sd.drawLineBreak();
		}

		return "<HTML><FONT FACE=\"Courier New\">" + sd.getHTML() + "</FONT></HTML>";
	}

	public Object getSelectionObject() {
		return new ArrayList<Transferrable>();
	}

	public void pasteRows(Object data) {
	}
	
	public void outline() {
		if (this.rowsAll == null) {
			return; // early return
		}
		
		List<EditorRow> list = new ArrayList<EditorRow>();
		for (EditorRow er : this.rowsAll) {
			if (er instanceof PackageDefRow) {
				list.add(er);
			} else if (er instanceof ClassDefRow) {
				ClassDefRow cdr = (ClassDefRow) er;
				if (!cdr.isClosing()) {
					list.add(er);
				}
			} else if (er instanceof FieldDefRow) {
				list.add(er);
			} else if (er instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow) er;
				if (!mdr.isClosing()) {
					list.add(er);
				}
			}
		}
		
		QuickOutlineDialog qod = new QuickOutlineDialog(MainWindow.getInstance(), list);
		qod.invoke();
		EditorRow er = qod.getSelected();
		if (er != null) {
			int index = this.rowsAll.indexOf(er);
			this.list.setSelectedIndex(index);
			this.list.ensureIndexIsVisible(index);
		}
	}
	
	/**
	 * A straightforward method processing. This method is for obtaining a rows
	 * of a method which only exists in one of the classes under comparison.
	 * @param cf
	 * @param method
	 * @return a list of <code>EditorRow</code> objects.
	 */
	private List<EditorRow> getMethodRows(ClassFile cf, Method method) {
		List<EditorRow> list = new ArrayList<EditorRow>();
		
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
			list.add(madr);
			if ("java.lang.Deprecated".equals(annotation.getName())) {
				deprecatedAnnotationAdded = true;
				// store this information so that
				// the Deprecated attribute isn't used to
				// create another deprecation EditorRow
			}
		}

		if (!deprecatedAnnotationAdded && method.isDeprecated()) {
			DeprecatedAnnotationDefRow ddr = new DeprecatedAnnotationDefRow();
			list.add(ddr);
		}

		MethodDefRow mdr = new MethodDefRow(cf, method, true, method.getAttributes().getCode() != null);
		list.add(mdr);

		Attributes attr = method.getAttributes();
		CodeAttribute codeAttr = attr.getCode();

		LineNumberTableAttribute lnAttr = null;
		LocalVariableTableAttribute lvs = null;
		if (codeAttr != null) {
			if (codeAttr.getAttributes() != null) {
				lnAttr = codeAttr.getAttributes().getLineNumberTable();
				lvs = codeAttr.getAttributes().getLocalVariableTable();
			}
			Code code = codeAttr.getCode();
			DecompilationContext dc = code.createDecompilationContext();
			List instructions = code.getInstructions();
			dc.setPosition(0);
			for (int j = 0; j < instructions.size(); j++) {
				Instruction instruction = (Instruction) instructions.get(j);

				if (instruction instanceof Label) {
					LabelRow lr = new LabelRow((Label) instruction, mdr);
					lr.setParentCode(code);
					list.add(lr);
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
							list.add(lvdr);
							mdr.addLocalVariable(lvdr);
						}
					}

					CodeRow cd = new CodeRow(cf, mdr, instruction);
					cd.setPosition(dc.getPosition());
					cd.setDecompilationContext(dc);
					cd.setParentCode(code);

					if (lineNumber != -1) {
						cd.setLineNumber(lineNumber);
					}

					list.add(cd);
					mdr.addCodeRow(cd);

					dc.incrementPosition(instruction);
				}

			}

			list.add(new MethodDefRow(cf, method, false, true));
		}

		return list; 
	}
	
	/**
	 * Method contents (bytecode) diff
	 * @param cfA
	 * @param cfB
	 * @param methodA
	 * @param methodB
	 */
	private void addMethodRows(ClassFile cfA, ClassFile cfB, Method methodA, Method methodB) {
		// TODO: Annotations and non-annotation deprecation
		
		// We matched these methods based on descriptor (=type & params) and name
		// but the thrown exceptions and access type may still be different
		// and if it is, we want that highlighted
		MethodDefRow mdrA = null;
		MethodDefRow mdrB = null;
		if (methodA.getAccessFlags() == methodB.getAccessFlags()
		&& methodA.getExceptions().equals(methodB.getExceptions())) {
			// everything is equal, just add one line
			MethodDefRow mdr = new MethodDefRow(cfA, methodA, true, methodA.getAttributes().getCode() != null);
			mdrA = mdr;
			mdrB = mdr;
			this.rowsAll.add(mdr);
		} else {
			mdrA = new MethodDefRow(cfA, methodA, true, methodA.getAttributes().getCode() != null);
			rowsA.add(mdrA);
			mdrB = new MethodDefRow(cfB, methodB, true, methodB.getAttributes().getCode() != null);
			rowsB.add(mdrB);
			rowsAll.add(mdrA);
			rowsAll.add(mdrB);
		}

		Attributes attrA = methodA.getAttributes();
		Attributes attrB = methodB.getAttributes();
		CodeAttribute codeAttrA = attrA.getCode();
		CodeAttribute codeAttrB = attrB.getCode();

		LineNumberTableAttribute lnAttrA = null;
		LineNumberTableAttribute lnAttrB = null;
		LocalVariableTableAttribute lvsA = null;
		LocalVariableTableAttribute lvsB = null;
		if (codeAttrA != null && codeAttrB != null) {
			// TODO: deal with the case where only one is null
			// ie, one method has code, the other doesn't
			
			if (codeAttrA.getAttributes() != null) {
				lnAttrA = codeAttrA.getAttributes().getLineNumberTable();
				lvsA = codeAttrA.getAttributes().getLocalVariableTable();
			}
			if (codeAttrB.getAttributes() != null) {
				lnAttrB = codeAttrB.getAttributes().getLineNumberTable();
				lvsB = codeAttrB.getAttributes().getLocalVariableTable();
			}

			Code codeA = codeAttrA.getCode();
			Code codeB = codeAttrB.getCode();
			
			DecompilationContext dcA = codeA.createDecompilationContext();
			DecompilationContext dcB = codeB.createDecompilationContext();
			
			List<Instruction> instructionsA = codeA.getInstructions();
			List<Instruction> instructionsB = codeB.getInstructions();
			dcA.setPosition(0);
			dcB.setPosition(0);
			
			List<EditorRow> methodRowsA = getMethodRows(instructionsA, codeA, mdrA, lnAttrA, lvsA, dcA, cfA);
			List<EditorRow> methodRowsB = getMethodRows(instructionsB, codeB, mdrB, lnAttrB, lvsB, dcB, cfB);
			
			// find out the equal instructions at the beginning of the block
			int startEqCount = 0;
			while (true) {
				if (startEqCount == methodRowsA.size()) break;
				if (startEqCount == methodRowsB.size()) break;
				
				EditorRow erA = methodRowsA.get(startEqCount);
				EditorRow erB = methodRowsB.get(startEqCount);
				boolean equal = rowsAreEqual(erA, erB);
				
				if (!equal) break;
				startEqCount++;
			}
			for (int i=0; i < startEqCount; i++) {
				this.rowsAll.add(methodRowsA.get(0));
				methodRowsA.remove(0);
				methodRowsB.remove(0);
			}

			// find out the equal instructions at the end of each code block
			int endEqCount = 0;
			while (true) {
				if (endEqCount == methodRowsA.size()) break;
				if (endEqCount == methodRowsB.size()) break;
				
				EditorRow erA = methodRowsA.get((methodRowsA.size()-1)-endEqCount);
				EditorRow erB = methodRowsB.get((methodRowsB.size()-1)-endEqCount);
				boolean equal = rowsAreEqual(erA, erB);
				
				if (!equal) break;
				endEqCount++;
			}
			List<EditorRow> equalRowsAtTheEnd = new ArrayList<EditorRow>();
			for (int i=0; i < endEqCount; i++) {
				equalRowsAtTheEnd.add(methodRowsA.get(methodRowsA.size()-1));
				methodRowsA.remove(methodRowsA.size()-1);
				methodRowsB.remove(methodRowsB.size()-1);
			}
			
		    int m = methodRowsA.size();
		    int n = methodRowsB.size();
			int[][] C = new int[m+1][n+1];
		    lcs(C, methodRowsA, methodRowsB);
		    
		    List<EditorRow> common = new ArrayList<EditorRow>();
		    bt(C, methodRowsA, methodRowsB, m, n, common);

		    for (EditorRow commonRow : common) {
		    	// rows from set A before the next common row
		    	while (!rowsAreEqual(methodRowsA.get(0), commonRow)) {
		    		this.rowsAll.add(methodRowsA.get(0));
		    		this.rowsA.add(methodRowsA.get(0));
		    		methodRowsA.remove(0);
		    	}

		    	// rows from set B before the next common row
		    	while (!rowsAreEqual(methodRowsB.get(0), commonRow)) {
		    		this.rowsAll.add(methodRowsB.get(0));
		    		this.rowsB.add(methodRowsB.get(0));
		    		methodRowsB.remove(0);
		    	}
		    	
		    	this.rowsAll.add(commonRow);
		    	methodRowsA.remove(0);
		    	methodRowsB.remove(0);
		    }
		    
		    this.rowsA.addAll(methodRowsA);
		    this.rowsAll.addAll(methodRowsA);
		    
		    this.rowsB.addAll(methodRowsB);
		    this.rowsAll.addAll(methodRowsB);

			this.rowsAll.addAll(equalRowsAtTheEnd);
			this.rowsAll.add(new MethodDefRow(cfA, methodA, false, true));
		}

	}
	
	private List<EditorRow> getMethodRows(List<Instruction> instructions, Code code, MethodDefRow mdr, LineNumberTableAttribute lnAttr, LocalVariableTableAttribute lvs, DecompilationContext dc, ClassFile cf) {
		List<EditorRow> list = new ArrayList<EditorRow>();
		
		for (Instruction instruction : instructions) {

			if (instruction instanceof Label) {
				LabelRow lr = new LabelRow((Label) instruction, mdr);
				lr.setParentCode(code);
				list.add(lr);
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
						list.add(lvdr);
						mdr.addLocalVariable(lvdr);
					}
				}

				CodeRow cd = new CodeRow(cf, mdr, instruction);
				cd.setPosition(dc.getPosition());
				cd.setDecompilationContext(dc);
				cd.setParentCode(code);

				if (lineNumber != -1) {
					cd.setLineNumber(lineNumber);
				}

				list.add(cd);
				mdr.addCodeRow(cd);

				dc.incrementPosition(instruction);
			}

		}
		
		return list;
	}
	
	public static boolean rowsAreEqual(EditorRow erA, EditorRow erB) {
		if (!erA.getClass().equals(erB.getClass())) return false;
		
		if (erA instanceof LocalVariableDefRow) {
			LocalVariableDefRow lvdrA = (LocalVariableDefRow) erA;
			LocalVariableDefRow lvdrB = (LocalVariableDefRow) erB;
			return lvdrA.getLocalVariable().getName().equals(lvdrB.getLocalVariable().getName());
		} else if (erA instanceof LabelRow) {
			LabelRow lrA = (LabelRow) erA;
			LabelRow lrB = (LabelRow) erB;
			return lrA.getLabel().getId().equals(lrB.getLabel().getId());
		} else if (erA instanceof CodeRow) {
			CodeRow crA = (CodeRow) erA;
			CodeRow crB = (CodeRow) erB;
			Instruction instA = crA.getInstruction();
			Instruction instB = crB.getInstruction();
			boolean opCodesEqual = (instA.getOpcode() == instB.getOpcode());
			if (!opCodesEqual) return false;
						
			return instA.getParameters().getString(crA.getDecompilationContext()).equals(instB.getParameters().getString(crB.getDecompilationContext()));
		} else {
			throw new AssertionError("Invalid object type: " + erA.getClass());
		}
	}
	
	public static void lcs(int[][] C, List<EditorRow> X, List<EditorRow> Y) {
	    for (int i = 1; i < X.size()+1; i++) {
	        for(int j = 1; j < Y.size()+1; j++) {
	        	if (rowsAreEqual(X.get(i-1), Y.get(j-1))) {
	                C[i][j] = C[i-1][j-1] + 1;
	            } else {
	                C[i][j] = Math.max(C[i][j-1], C[i-1][j]);
	            }
	        }
	    }
	}
	
	public static void bt(int[][] C, List<EditorRow> X, List<EditorRow> Y, int i, int j, List<EditorRow> list) {
	    if (i == 0 || j == 0) {
	    	return;
	    } else if (rowsAreEqual(X.get(i-1), Y.get(j-1))) {
	    	bt(C, X, Y, i-1, j-1, list);
	    	list.add(X.get(i-1));
	    } else {
	        if (C[i][j-1] > C[i-1][j]) {
	        	bt(C, X, Y, i, j-1, list);
	        } else {
	        	bt(C, X, Y, i-1, j, list);
	        }
	    }
	}

}

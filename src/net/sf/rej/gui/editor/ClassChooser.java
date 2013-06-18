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
package net.sf.rej.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.rej.Imports;
import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.dialog.ClassChooseDialog;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.util.Wrapper;

/**
 * <code>ClassChooser</code> is a JPanel GUI component for selecting a class in the
 * project in the instruction editor
 * 
 * @author Sami Koivu
 */
public class ClassChooser extends JPanel {
	private Action chooserAction = new AbstractAction("...") {
		public void actionPerformed(ActionEvent e) {
			ClassIndex cpIndex = SystemFacade.getInstance().getClassIndex();
			ClassChooseDialog ccd = new ClassChooseDialog(MainWindow
					.getInstance(), cpIndex);
			ccd.invoke();
			ClassLocator cl = ccd.getSelected();
			if (cl != null) {
				ClassChooser.this.model.addElement(cl);
				ClassChooser.this.combo.setSelectedItem(cl);
				if (layoutChangeListener != null) {
					layoutChangeListener.layoutChanged(ClassChooser.this);
				}
			}
		}
	};

	DefaultComboBoxModel model = new DefaultComboBoxModel();

	private JLabel label = new JLabel("Class: ");

	JComboBox combo = new JComboBox(this.model);

	private JButton button = new JButton(this.chooserAction);

	private ConstantPool cp = null;

	private ClassFile cf = null;
	private Imports imports = null;
	private LayoutChangeListener layoutChangeListener = null;

	public ClassChooser(LayoutChangeListener layoutChangeListener) {
		super();
		this.layoutChangeListener = layoutChangeListener;
		this.setLayout(new GridBagLayout());
		this.add(this.label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		this.add(this.combo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		this.add(this.button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
	}

	public void setClassFile(ClassFile cf) {
		this.cf = cf;
	}
	
	public void setConstantPool(ConstantPool pool) {
		this.cp = pool;
		
		this.imports = EditorFacade.getInstance().getImports(this.cf);

		for (int i = 0; i < this.cp.size(); i++) {
			ConstantPoolInfo cpi = this.cp.get(i);
			if (cpi != null) {
				if (cpi.getType() == ConstantPoolInfo.CLASS) {
					this.model.addElement(createWrapper((ClassInfo) cpi));
				}
			}
		}

	}

	public Wrapper<ClassInfo> createWrapper(ClassInfo ci) {
		Wrapper<ClassInfo> wrapper = new Wrapper<ClassInfo>();
		wrapper.setContent(ci);
		wrapper.setDisplay(this.imports.getShortName(ci.getName()));
		return wrapper;
	}

	/**
	 * Returns an integer value(an index to the constant pool) or a String with
	 * the class name.
	 * 
	 * @return Object Selected value
	 */
	public Object getValue() {
		Object o = this.combo.getSelectedItem();
		int value = 0;
		// TODO: calls to addClassRef cannot be done outside of undo
		if (o instanceof String) {
			return o;
		} else if (o instanceof ClassLocator) {
			ClassLocator cl = (ClassLocator) o;
			return cl.getFullName();
		} else {
			ClassInfo ci = (ClassInfo) ((Wrapper) o).getContent();
			value = this.cp.optionalAdd(ci);
		}

		return Integer.valueOf(value);
	}

	public void setSelected(Object o) {
		this.combo.setSelectedItem(createWrapper((ClassInfo) o));
	}

	public void setEditable(boolean b) {
		this.combo.setEditable(b);
	}

	public void setReadOnly() {
		this.combo.setEnabled(false);
	}

}
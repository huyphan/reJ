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
package net.sf.rej.gui.dialog;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.java.JavaType;

public class TypeChooseDialog extends JDialog {

	private JPanel jContentPane = null;
	private JLabel typeLabel = null;
	private JLabel arrayLabel = null;
	JComboBox typeCombo = null;
	JComboBox arrayCombo = null;
	private JButton typeChooserButton = null;
	private JPanel okCancelPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;

	DefaultComboBoxModel typeModel;
	DefaultComboBoxModel arrayModel;
	boolean cancelled = false;

	/**
	 * This is the default constructor.
	 * @param owner the calling, parent dialog.
	 */
	public TypeChooseDialog(Dialog owner) {
		super(owner, true);
		initialize();
	}

	private void initialize() {
		this.setSize(300, 200);
		this.setTitle("Type Chooser");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridwidth = 3;
			gridBagConstraints21.gridy = 2;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridwidth = 1;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints1.gridy = 1;
			arrayLabel = new JLabel();
			arrayLabel.setText("Array Dimensions: ");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			typeLabel = new JLabel();
			typeLabel.setText("Type: ");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(typeLabel, gridBagConstraints);
			jContentPane.add(arrayLabel, gridBagConstraints1);
			jContentPane.add(getTypeCombo(), gridBagConstraints2);
			jContentPane.add(getSizeCombo(), gridBagConstraints3);
			jContentPane.add(getTypeChooserButton(), gridBagConstraints11);
			jContentPane.add(getOkCancelPanel(), gridBagConstraints21);
		}
		return jContentPane;
	}

	/**
	 * This method initializes typeCombo
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getTypeCombo() {
		if (typeCombo == null) {
			this.typeModel = new DefaultComboBoxModel();
			this.typeCombo = new JComboBox(this.typeModel);
		}
		return typeCombo;
	}

	/**
	 * This method initializes sizeCombo
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSizeCombo() {
		if (arrayCombo == null) {
			this.arrayModel = new DefaultComboBoxModel();
			this.arrayCombo = new JComboBox(this.arrayModel);
			this.arrayCombo.setEditable(true);
		}
		return arrayCombo;
	}

	/**
	 * This method initializes typeChooserButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTypeChooserButton() {
		if (typeChooserButton == null) {
			typeChooserButton = new JButton();
			typeChooserButton.setText("...");
			typeChooserButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
		            ClassIndex cpIndex = SystemFacade.getInstance().getClassIndex();
		            ClassChooseDialog ccd = new ClassChooseDialog(TypeChooseDialog.this, cpIndex);
		            ccd.invoke();
		            ClassLocator cl = ccd.getSelected();
		            if (cl != null) {
		                TypeChooseDialog.this.typeModel.addElement(cl);
		                TypeChooseDialog.this.typeCombo.setSelectedItem(cl);
		            }
				}
			});
		}
		return typeChooserButton;
	}

	/**
	 * This method initializes okCancelPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getOkCancelPanel() {
		if (okCancelPanel == null) {
			okCancelPanel = new JPanel();
			okCancelPanel.add(getOkButton(), null);
			okCancelPanel.add(getCancelButton(), null);
		}
		return okCancelPanel;
	}

	/**
	 * This method initializes okButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					TypeChooseDialog.this.cancelled = false;
					setVisible(false);
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					TypeChooseDialog.this.cancelled = true;
					setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	public void invoke(JavaType retType, boolean enableVoid) {
		this.typeModel.removeAllElements();
		if (enableVoid) {
			this.typeModel.addElement("void");
		}
		this.typeModel.addElement("byte");
		this.typeModel.addElement("short");
		this.typeModel.addElement("int");
		this.typeModel.addElement("long");
		this.typeModel.addElement("char");
		this.typeModel.addElement("float");
		this.typeModel.addElement("double");
		if (!retType.isPrimitive()) {
			// if type is not primitive, ie. is not on the list already; add it.
			this.typeModel.addElement(retType.getType());
		}
		this.typeCombo.setSelectedItem(retType.getType());

		this.arrayModel.removeAllElements();
		this.arrayModel.addElement("None");
		this.arrayModel.addElement("1");
		this.arrayModel.addElement("2");
		this.arrayModel.addElement("3");
		if (retType.getDimensionCount() > 3) {
			this.arrayModel.addElement(String.valueOf(retType.getDimensionCount()));
		}

		if (retType.getDimensionCount() == 0) {
			this.arrayCombo.setSelectedItem("None");
		} else {
			this.arrayCombo.setSelectedItem(String.valueOf(retType.getDimensionCount()));
		}

		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	public boolean wasCancelled() {
		return this.cancelled;
	}

	public JavaType getType() {
		Object item = this.typeCombo.getSelectedItem();
		String name = null;
		if (item instanceof String ) {
			name = (String)item;
		} else {
			ClassLocator cl = (ClassLocator)item;
			name = cl.getFullName();
		}

		int dimensions = 0;
		String arrayString = (String)this.arrayCombo.getSelectedItem();
		if ("None".equals(arrayString)) {
			dimensions = 0;
		} else {
			dimensions = Integer.parseInt(arrayString);
		}
		return new JavaType(name, dimensions);
	}

}

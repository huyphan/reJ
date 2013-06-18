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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.rej.gui.dialog.TypeChooseDialog;
import net.sf.rej.java.AccessFlags;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.JavaType;

public class FieldEditor extends JDialog {

	private JPanel jContentPane = null;
	private JPanel accessorPanel = null;
	private JCheckBox nativeCheck = null;
	private JCheckBox staticCheck = null;
	private JCheckBox protectedCheck = null;
	private JCheckBox publicCheck = null;
	private JCheckBox synchronizedCheck = null;
	private JCheckBox privateCheck = null;
	private JCheckBox abstractCheck = null;
	private JCheckBox finalCheck = null;
	private JPanel signaturePanel = null;
	private JTextField nameField = null;
	JTextField typeField = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JLabel nameLabel = null;
	private JLabel typeLabel = null;
	private JButton typeButton = null;

	Descriptor desc = null;
	boolean cancelled = false;
	/**
	 * This is the default constructor.
	 * @param owner the calling, parent dialog.
	 */
	public FieldEditor(Frame owner) {
		super(owner, "Field Editor");
		initialize();
	}

    public void invoke(String name, Descriptor type, int flags) {
        this.nameField.setText(name);

        this.desc = type;
        this.typeField.setText(type.getReturn().toString());

        this.publicCheck.setSelected(AccessFlags.isPublic(flags));
        this.staticCheck.setSelected(AccessFlags.isStatic(flags));
        this.synchronizedCheck.setSelected(AccessFlags.isSynchronized(flags));
        this.nativeCheck.setSelected(AccessFlags.isNative(flags));
        this.protectedCheck.setSelected(AccessFlags.isProtected(flags));
        this.privateCheck.setSelected(AccessFlags.isPrivate(flags));
        this.abstractCheck.setSelected(AccessFlags.isAbstract(flags));
        this.finalCheck.setSelected(AccessFlags.isFinal(flags));

        pack();
        setModal(true);
		setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    public boolean wasCancelled() {
    	return this.cancelled;
    }

    public Descriptor getDescriptorType() {
    	return this.desc;
    }

    public String getFieldName() {
    	return this.nameField.getText();
    }

    public AccessFlags getAccessFlags() {
        AccessFlags flags = new AccessFlags();
        flags.setAbstract(this.abstractCheck.isSelected());
        flags.setFinal(this.finalCheck.isSelected());
        flags.setNative(this.nativeCheck.isSelected());
        flags.setPrivate(this.privateCheck.isSelected());
        flags.setProtected(this.protectedCheck.isSelected());
        flags.setPublic(this.publicCheck.isSelected());
        flags.setStatic(this.staticCheck.isSelected());
        flags.setSynchronized(this.synchronizedCheck.isSelected());

        return flags;
    }


	private void initialize() {
		this.setSize(270, 234);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints41.gridy = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getAccessorPanel(), gridBagConstraints1);
			jContentPane.add(getSignaturePanel(), gridBagConstraints3);
			jContentPane.add(getButtonPanel(), gridBagConstraints41);
		}
		return jContentPane;
	}

	/**
	 * This method initializes accessorPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getAccessorPanel() {
		if (accessorPanel == null) {
			GridLayout gridLayout = new GridLayout(0, 2);
			accessorPanel = new JPanel();
			accessorPanel.setLayout(gridLayout);
			accessorPanel.add(getPublicCheck(), null);
			accessorPanel.add(getStaticCheck(), null);
			accessorPanel.add(getProtectedCheck(), null);
			accessorPanel.add(getNativeCheck(), null);
			accessorPanel.add(getSynchronizedCheck(), null);
			accessorPanel.add(getPrivateCheck(), null);
			accessorPanel.add(getAbstractCheck(), null);
			accessorPanel.add(getFinalCheck(), null);
		}
		return accessorPanel;
	}

	/**
	 * This method initializes nativeCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getNativeCheck() {
		if (nativeCheck == null) {
			nativeCheck = new JCheckBox();
			nativeCheck.setText("native");
		}
		return nativeCheck;
	}

	/**
	 * This method initializes staticCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getStaticCheck() {
		if (staticCheck == null) {
			staticCheck = new JCheckBox();
			staticCheck.setText("static");
		}
		return staticCheck;
	}

	/**
	 * This method initializes protectedCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getProtectedCheck() {
		if (protectedCheck == null) {
			protectedCheck = new JCheckBox();
			protectedCheck.setText("protected");
		}
		return protectedCheck;
	}

	/**
	 * This method initializes publicCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getPublicCheck() {
		if (publicCheck == null) {
			publicCheck = new JCheckBox();
			publicCheck.setText("public");
		}
		return publicCheck;
	}

	/**
	 * This method initializes synchronizedCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSynchronizedCheck() {
		if (synchronizedCheck == null) {
			synchronizedCheck = new JCheckBox();
			synchronizedCheck.setText("synchronized");
		}
		return synchronizedCheck;
	}

	/**
	 * This method initializes privateCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getPrivateCheck() {
		if (privateCheck == null) {
			privateCheck = new JCheckBox();
			privateCheck.setText("private");
		}
		return privateCheck;
	}

	/**
	 * This method initializes abstractCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getAbstractCheck() {
		if (abstractCheck == null) {
			abstractCheck = new JCheckBox();
			abstractCheck.setText("abstract");
		}
		return abstractCheck;
	}

	/**
	 * This method initializes finalCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getFinalCheck() {
		if (finalCheck == null) {
			finalCheck = new JCheckBox();
			finalCheck.setText("final");
		}
		return finalCheck;
	}

	/**
	 * This method initializes signaturePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSignaturePanel() {
		if (signaturePanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 2;
			gridBagConstraints7.gridy = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 1;
			typeLabel = new JLabel();
			typeLabel.setText("Type: ");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 0;
			nameLabel = new JLabel();
			nameLabel.setText("Name: ");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			signaturePanel = new JPanel();
			signaturePanel.setLayout(new GridBagLayout());
			signaturePanel.add(getTypeCombo(), gridBagConstraints2);
			signaturePanel.add(getNameField(), gridBagConstraints4);
			signaturePanel.add(nameLabel, gridBagConstraints5);
			signaturePanel.add(typeLabel, gridBagConstraints6);
			signaturePanel.add(getTypeButton(), gridBagConstraints7);
		}
		return signaturePanel;
	}

	/**
	 * This method initializes nameField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getNameField() {
		if (nameField == null) {
			nameField = new JTextField();
			nameField.setColumns(8);
		}
		return nameField;
	}

	/**
	 * This method initializes typeCombo
	 *
	 * @return javax.swing.JComboBox
	 */
	private JTextField getTypeCombo() {
		if (typeField == null) {
			typeField = new JTextField();
			typeField.setEditable(false);
		}
		return typeField;
	}

	/**
	 * This method initializes buttonPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
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
					FieldEditor.this.cancelled = false;
					FieldEditor.this.setVisible(false);
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
					FieldEditor.this.cancelled = true;
					FieldEditor.this.setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes typeButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTypeButton() {
		if (typeButton == null) {
			typeButton = new JButton();
			typeButton.setText("...");
			typeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
		        	TypeChooseDialog chooser = new TypeChooseDialog(FieldEditor.this);
		        	chooser.invoke(FieldEditor.this.desc.getReturn(), true);
		        	JavaType newRet = chooser.getJavaType();
		        	FieldEditor.this.desc.setReturn(newRet);
		            FieldEditor.this.typeField.setText(newRet.toString());
				}
			});
		}
		return typeButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.dialog.ClassChooseDialog;
import net.sf.rej.java.AccessFlags;
import net.sf.rej.java.Interface;

public class ClassEditor extends JDialog {

	Action superClassChooseAction = new AbstractAction("...") {
		public void actionPerformed(ActionEvent e) {
            ClassIndex cpIndex = SystemFacade.getInstance().getClassIndex();
            ClassChooseDialog ccd = new ClassChooseDialog(ClassEditor.this, cpIndex);
            ccd.invoke();
            ClassLocator cl = ccd.getSelected();
            if (cl != null) {
            	ClassEditor.this.superField.setText(cl.getFullName());
            }
		}

	};

	private JPanel jContentPane = null;
	private JPanel accessorPanel = null;
	private JPanel signaturePanel = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JCheckBox publicCheck = null;
	private JCheckBox staticCheck = null;
	private JCheckBox protectedCheck = null;
	private JCheckBox privateCheck = null;
	private JCheckBox abstractCheck = null;
	private JCheckBox finalCheck = null;
	private JCheckBox interfaceCheck = null;
	private JLabel nameLabel = null;
	JCheckBox superLabel = null;
	private JLabel interfacesLabel = null;
	private JTextField nameField = null;
	JTextField superField = null;
	JButton superChooser = new JButton(this.superClassChooseAction);
	private JScrollPane interfaceScrollPane = null;
	JList interfaceList = null;
	private JButton addButton = null;
	private JButton removeButton = null;

	boolean cancelled = false;
	DefaultListModel interfaceModel = new DefaultListModel();

	/**
	 * This is the default constructor.
	 * @param owner the calling, parent dialog.
	 */
    public ClassEditor(Frame owner) {
        super(owner);
		initialize();
	}

    public void invoke(String className, String superName, int flags, List interfaces) {
        this.nameField.setText(className);
        if (superName == null) {
        	this.superField.setText("");
        	this.superField.setEnabled(false);
        	this.superChooser.setEnabled(false);
        	this.superLabel.setSelected(false);
        } else {
        	this.superField.setText(superName);
        	this.superField.setEnabled(true);
        	this.superChooser.setEnabled(true);
        	this.superLabel.setSelected(true);
        }

        this.publicCheck.setSelected(AccessFlags.isPublic(flags));
        this.staticCheck.setSelected(AccessFlags.isStatic(flags));
        this.protectedCheck.setSelected(AccessFlags.isProtected(flags));
        this.privateCheck.setSelected(AccessFlags.isPrivate(flags));
        this.abstractCheck.setSelected(AccessFlags.isAbstract(flags));
        this.finalCheck.setSelected(AccessFlags.isFinal(flags));
        this.interfaceCheck.setSelected(AccessFlags.isInterface(flags));

        this.interfaceModel.removeAllElements();
        for (int i=0; i < interfaces.size(); i++) {
        	Interface intf = (Interface) interfaces.get(i);
        	this.interfaceModel.addElement(intf);
        }

        pack();
        setModal(true);
		setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    public AccessFlags getFlags() {
        AccessFlags flags = new AccessFlags();
        flags.setAbstract(this.abstractCheck.isSelected());
        flags.setFinal(this.finalCheck.isSelected());
        flags.setInterface(this.interfaceCheck.isSelected());
        flags.setPrivate(this.privateCheck.isSelected());
        flags.setProtected(this.protectedCheck.isSelected());
        flags.setPublic(this.publicCheck.isSelected());
        flags.setStatic(this.staticCheck.isSelected());

        return flags;
    }

    public String getClassName() {
    	return this.nameField.getText();
    }

    public String getSuperClassname() {
    	if (this.superLabel.isSelected()) {
    		return this.superField.getText();
    	} else {
    		return null;
    	}
    }

    /**
     * Returns a list of Strings representing the selected interface names
     * @return List implemented interfaces
     */
    public List<String> getInterfaces() {
    	List<String> list = new ArrayList<String>();
    	for (int i=0; i < this.interfaceModel.size(); i++) {
    		Object obj = this.interfaceModel.get(i);
    		if (obj instanceof String) {
    			list.add((String)obj);
    		} else {
    			Interface intf = (Interface) obj;
    			list.add(intf.getName());
    		}
    	}

    	return list;
    }

    public boolean wasCancelled() {
    	return this.cancelled;
    }

	private void initialize() {
		this.setSize(450, 233);
		this.setTitle("Class Editor");
		this.setContentPane(getJContentPane());

		this.superLabel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (ClassEditor.this.superLabel.isSelected()) {
		        	ClassEditor.this.superField.setEnabled(true);
		        	ClassEditor.this.superChooser.setEnabled(true);
				} else {
		        	ClassEditor.this.superField.setEnabled(false);
		        	ClassEditor.this.superChooser.setEnabled(false);					
				}
			}
		});
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(10, 20, 5, 20);
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getAccessorPanel(), gridBagConstraints);
			jContentPane.add(getSignaturePanel(), gridBagConstraints1);
			jContentPane.add(getButtonPanel(), gridBagConstraints2);
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
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(3);
			accessorPanel = new JPanel();
			accessorPanel.setLayout(gridLayout);
			accessorPanel.add(getPublicCheck(), null);
			accessorPanel.add(getStaticCheck(), null);
			accessorPanel.add(getProtectedCheck(), null);
			accessorPanel.add(getPrivateCheck(), null);
			accessorPanel.add(getAbstractCheck(), null);
			accessorPanel.add(getFinalCheck(), null);
			accessorPanel.add(getInterfaceCheck(), null);
			accessorPanel.setBorder(BorderFactory.createEtchedBorder());
		}
		return accessorPanel;
	}

	/**
	 * This method initializes signaturePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSignaturePanel() {
		if (signaturePanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.gridy = 3;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 2;
			gridBagConstraints9.gridy = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.weighty = 1.0;
			gridBagConstraints8.gridheight = 2;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints7b = new GridBagConstraints();
			gridBagConstraints7b.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7b.gridy = 1;
			gridBagConstraints7b.weightx = 0.0;
			gridBagConstraints7b.gridx = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints5.gridy = 2;
			interfacesLabel = new JLabel();
			interfacesLabel.setText("Implemented Interfaces:");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new java.awt.Insets(5,3,5,5);
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints4.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new java.awt.Insets(5,5,5,2);
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints3.gridx = 0;
			superLabel = new JCheckBox("Superclass: ");
			nameLabel = new JLabel();
			nameLabel.setText("Name:");
			signaturePanel = new JPanel();
			signaturePanel.setLayout(new GridBagLayout());
			signaturePanel.add(superLabel, gridBagConstraints3);
			signaturePanel.add(nameLabel, gridBagConstraints4);
			signaturePanel.add(interfacesLabel, gridBagConstraints5);
			signaturePanel.add(getNameField(), gridBagConstraints6);
			signaturePanel.add(getSuperField(), gridBagConstraints7);
			signaturePanel.add(this.superChooser, gridBagConstraints7b);
			signaturePanel.add(getInterfaceScrollPane(), gridBagConstraints8);
			signaturePanel.add(getAddButton(), gridBagConstraints9);
			signaturePanel.add(getRemoveButton(), gridBagConstraints10);
		}
		return signaturePanel;
	}

	/**
	 * This method initializes buttonPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(1);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(gridLayout1);
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
					ClassEditor.this.cancelled = false;
					ClassEditor.this.setVisible(false);
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
					ClassEditor.this.cancelled = true;
					ClassEditor.this.setVisible(false);
				}
			});
		}
		return cancelButton;
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
	 * This method initializes finalCheck
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getInterfaceCheck() {
		if (interfaceCheck == null) {
			interfaceCheck = new JCheckBox();
			interfaceCheck.setText("interface");
		}
		return interfaceCheck;
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
	 * This method initializes superField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSuperField() {
		if (superField == null) {
			superField = new JTextField();
		}
		return superField;
	}

	/**
	 * This method initializes interfaceScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getInterfaceScrollPane() {
		if (interfaceScrollPane == null) {
			interfaceScrollPane = new JScrollPane();
			interfaceScrollPane.setViewportView(getInterfaceList());
		}
		return interfaceScrollPane;
	}

	/**
	 * This method initializes interfaceList
	 *
	 * @return javax.swing.JList
	 */
	private JList getInterfaceList() {
		if (interfaceList == null) {
			interfaceList = new JList(this.interfaceModel);
		}
		return interfaceList;
	}

	/**
	 * This method initializes addButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("+");
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
		            ClassIndex cpIndex = SystemFacade.getInstance().getClassIndex();
		            ClassChooseDialog ccd = new ClassChooseDialog(MainWindow.getInstance(), cpIndex);
		            ccd.invoke();
		            ClassLocator cl = ccd.getSelected();
		            if (cl != null) {
		                ClassEditor.this.interfaceModel.addElement(cl.getFullName());
		                ClassEditor.this.interfaceList.setSelectedValue(cl.getFullName(), true);
		            }
				}
			});
		}
		return addButton;
	}

	/**
	 * This method initializes removeButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("-");
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// TODO: multiselect
					int selected = ClassEditor.this.interfaceList.getSelectedIndex();
					ClassEditor.this.interfaceModel.remove(selected);
				}
			});
		}
		return removeButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

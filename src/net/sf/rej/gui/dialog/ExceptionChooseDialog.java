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
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.java.JavaType;

public class ExceptionChooseDialog extends JDialog {

	private JPanel jContentPane = null;
	JList paramList = null;
	private JButton moveDownButton = null;
	private JPanel movePanel = null;
	private JButton moveUpButton = null;
	private JPanel addEditRemovePanel = null;
	private JPanel okCancelPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JButton addButton = null;
	private JButton editButton = null;
	private JButton removeButton = null;

	DefaultListModel model = new DefaultListModel();
	boolean cancelled = false;

	/**
	 * This is the default constructor.
	 * @param owner the calling, parent dialog.
	 */
	public ExceptionChooseDialog(Dialog owner) {
		super(owner, true);
		initialize();
	}

	private void initialize() {
		this.setSize(300, 200);
		this.setTitle("Exception Chooser");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(new JScrollPane(getParamList()), gridBagConstraints);
			jContentPane.add(getMovePanel(), gridBagConstraints1);
			jContentPane.add(getAddEditRemovePanel(), gridBagConstraints2);
			jContentPane.add(getOkCancelPanel(), gridBagConstraints3);
		}
		return jContentPane;
	}

	/**
	 * This method initializes paramList
	 *
	 * @return javax.swing.JList
	 */
	private JList getParamList() {
		if (paramList == null) {
			paramList = new JList(this.model);
		}
		return paramList;
	}

	/**
	 * This method initializes moveDownButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getMoveDownButton() {
		if (moveDownButton == null) {
			moveDownButton = new JButton();
			moveDownButton.setText("Move Down");
			moveDownButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JavaType jt = (JavaType)ExceptionChooseDialog.this.paramList.getSelectedValue();
					if (jt != null) {
						int index = ExceptionChooseDialog.this.paramList.getSelectedIndex();
						if (index < (ExceptionChooseDialog.this.model.size()-1)) {
							ExceptionChooseDialog.this.model.remove(index);
							ExceptionChooseDialog.this.model.add(index+1, jt);
							ExceptionChooseDialog.this.paramList.setSelectedIndex(index+1);
						}
					}
				}
			});
		}
		return moveDownButton;
	}

	/**
	 * This method initializes movePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getMovePanel() {
		if (movePanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(2);
			gridLayout.setHgap(2);
			gridLayout.setVgap(5);
			gridLayout.setColumns(1);
			movePanel = new JPanel();
			movePanel.setLayout(gridLayout);
			movePanel.add(getMoveUpButton(), null);
			movePanel.add(getMoveDownButton(), null);
		}
		return movePanel;
	}

	/**
	 * This method initializes moveUpButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getMoveUpButton() {
		if (moveUpButton == null) {
			moveUpButton = new JButton();
			moveUpButton.setText("Move Up");
			moveUpButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JavaType jt = (JavaType)ExceptionChooseDialog.this.paramList.getSelectedValue();
					if (jt != null) {
						int index = ExceptionChooseDialog.this.paramList.getSelectedIndex();
						if (index > 0) {
							ExceptionChooseDialog.this.model.remove(index);
							ExceptionChooseDialog.this.model.add(index-1, jt);
							ExceptionChooseDialog.this.paramList.setSelectedIndex(index-1);						}
					}
				}
			});
		}
		return moveUpButton;
	}

	/**
	 * This method initializes addEditRemovePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getAddEditRemovePanel() {
		if (addEditRemovePanel == null) {
			addEditRemovePanel = new JPanel();
			addEditRemovePanel.add(getAddButton(), null);
			addEditRemovePanel.add(getEditButton(), null);
			addEditRemovePanel.add(getRemoveButton(), null);
		}
		return addEditRemovePanel;
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
					ExceptionChooseDialog.this.cancelled = false;
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
					ExceptionChooseDialog.this.cancelled = true;
					setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes addButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add..");
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
		            ClassIndex cpIndex = SystemFacade.getInstance().getClassIndex();
		            ClassChooseDialog ccd = new ClassChooseDialog(ExceptionChooseDialog.this, cpIndex);
		            ccd.invoke();
		            ClassLocator cl = ccd.getSelected();
		            if (cl != null) {
						ExceptionChooseDialog.this.model.addElement(cl.getFullName());
		            }
				}
			});
		}
		return addButton;
	}

	/**
	 * This method initializes editButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getEditButton() {
		if (editButton == null) {
			editButton = new JButton();
			editButton.setText("Edit..");
			editButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Object obj = ExceptionChooseDialog.this.paramList.getSelectedValue();
					if (obj != null) {
						int index = ExceptionChooseDialog.this.paramList.getSelectedIndex();
			            ClassIndex cpIndex = SystemFacade.getInstance().getClassIndex();
			            ClassChooseDialog ccd = new ClassChooseDialog(ExceptionChooseDialog.this, cpIndex);
			            ccd.invoke();
			            ClassLocator cl = ccd.getSelected();
			            if (cl != null) {
							ExceptionChooseDialog.this.model.remove(index);
							ExceptionChooseDialog.this.model.add(index, cl.getFullName());
			            }
						ExceptionChooseDialog.this.paramList.setSelectedIndex(index);
					}
				}
			});
		}
		return editButton;
	}

	/**
	 * This method initializes removeButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove");
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Object obj = ExceptionChooseDialog.this.paramList.getSelectedValue();
					if (obj != null) {
						ExceptionChooseDialog.this.model.removeElement(obj);
					}
				}
			});
		}
		return removeButton;
	}

	public void invoke(List params) {
		this.model.removeAllElements();
		for (int i=0; i < params.size(); i++) {
			this.model.addElement(params.get(i));
		}
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	public boolean wasCancelled() {
		return this.cancelled;
	}

	public List getExceptions() {
		return Arrays.asList(this.model.toArray());
	}

}

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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.rej.gui.MainWindow;
import net.sf.rej.java.ClassVersion;
import net.sf.rej.util.Wrapper;

public class ClassVersionDialog extends JDialog {

	private JPanel jContentPane = null;
	private JLabel versionLabel = null;
	JComboBox versionCombo = null;
	private JPanel okCancelPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;

	DefaultComboBoxModel versionModel;
	boolean cancelled = false;
	private ClassVersion selectedVersion = null;

	/**
	 * This is the default constructor.
	 * @param owner the calling, parent dialog.
	 */
	public ClassVersionDialog(Dialog owner) {
		super(owner, true);
		initialize();
	}

	/**
	 * This is the default constructor.
	 * @param owner the calling, parent frame.
	 */
	public ClassVersionDialog(Frame owner) {
		super(owner, true);
		initialize();
	}

	private void initialize() {
		this.setSize(300, 150);
		this.setTitle("Class Version Chooser");
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
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			versionLabel = new JLabel();
			versionLabel.setText("Version: ");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(versionLabel, gridBagConstraints);
			jContentPane.add(getVersionCombo(), gridBagConstraints2);
			jContentPane.add(getOkCancelPanel(), gridBagConstraints21);
		}
		return jContentPane;
	}

	/**
	 * This method initializes versionCombo
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getVersionCombo() {
		if (versionCombo == null) {
			this.versionModel = new DefaultComboBoxModel();
			this.versionCombo = new JComboBox(this.versionModel);
			this.versionCombo.setEditable(true);
		}
		return versionCombo;
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
					try {
						selectedVersion = parseVersion();
						cancelled = false;
						setVisible(false);
					} catch (VersionParseException vpe) {
						MainWindow.getInstance().showErrorMessage(vpe.getMessage());
					}
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
					cancelled = true;
					setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	public void invoke(int majorVersion, int minorVersion) {
		this.versionModel.removeAllElements();
		Wrapper<ClassVersion> wrapper49 = new Wrapper<ClassVersion>();
		wrapper49.setContent(new ClassVersion(49, 0));
		wrapper49.setDisplay("49.0 (Java 1.5)");
		this.versionModel.addElement(wrapper49);
		
		Wrapper<ClassVersion> wrapper48 = new Wrapper<ClassVersion>();
		wrapper48.setContent(new ClassVersion(48, 0));
		wrapper48.setDisplay("48.0 (Java 1.4)");
		this.versionModel.addElement(wrapper48);

		
		ClassVersion initial = new ClassVersion(majorVersion, minorVersion);
		if (initial.equals(wrapper49.getContent())) {
			this.versionCombo.setSelectedItem(wrapper49);
		} else if (initial.equals(wrapper48.getContent())) {
			this.versionCombo.setSelectedItem(wrapper48);			
		} else {
			versionModel.addElement(initial);
			this.versionCombo.setSelectedItem(initial);
		}

		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	public boolean wasCancelled() {
		return this.cancelled;
	}
	
	public ClassVersion getVersion() {
		return this.selectedVersion;
	}

	private ClassVersion parseVersion() throws VersionParseException {
		Object item = this.versionCombo.getSelectedItem();
		if (item instanceof String ) {
			ClassVersion version = new ClassVersion();
			String[] strs = item.toString().split("\\.");
			if (strs.length == 1) {
				try {
					version.setMajorVersion(Integer.parseInt(strs[0]));
					version.setMinorVersion(0);
				} catch(NumberFormatException nfe) {
					throw new VersionParseException("Version must be of format number.number");					
				}
			} else if (strs.length == 2) {
				try{
					version.setMajorVersion(Integer.parseInt(strs[0]));
					version.setMinorVersion(Integer.parseInt(strs[1]));				
				} catch(NumberFormatException nfe) {
					throw new VersionParseException("Version must be of format number.number");					
				}
			} else {
				throw new VersionParseException("Version must be of format number.number");					
			}
			
			return version;
		} else if (item instanceof ClassVersion) {
			return (ClassVersion)item;
		} else if (item instanceof Wrapper) {
			@SuppressWarnings("unchecked")
			Wrapper<ClassVersion> wrapper = (Wrapper)item;
			return wrapper.getContent();
		} else {
			throw new AssertionError("Invalid type in combobox: " + item.getClass().getName());
		}
	}
	
	static class VersionParseException extends Exception {
		public VersionParseException(String msg) {
			super(msg);
		}
	}

}

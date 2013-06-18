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
package net.sf.rej.gui.hexeditor;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * A Hex Editor dialog component (<code>JDialog</code> subclass) for
 * viewing and or editing of binary data.
 * 
 * The presentation of the data is more or less following:
 * <br><code>000000: ca fe ba be 30 31 32 33 ....0123</code><br>
 * 
 * The number of bytes displayed on each row can be defined, in that
 * example the width is 8 bytes.
 * 
 * @author Sami Koivu
 */
public class HexEditorDialog extends JDialog {
	
	private HexEditorPanel editor;
	boolean cancelled = true;

	/**
	 * Initializes the dialog, but does not make it visible. A call
	 * to the <code>invoke()</code> makes the dialog visible.
	 * @param owner parent dialog for this modal dialog.
	 * @param dataProvider the source for the data to be displayed
	 * and or edited.
	 * @param width the number of bytes to show on each row.
	 * @param readOnly whether or not this dialog should allow
	 * editing the data or just allow viewing.
	 */
	public HexEditorDialog(Dialog owner, DataProvider dataProvider, int width, boolean readOnly) {
		super(owner);
		init(dataProvider, width, readOnly);
	}

	/**
	 * Initializes the dialog, but does not make it visible. A call
	 * to the <code>invoke()</code> makes the dialog visible.
	 * @param owner parent frame for this modal dialog.
	 * @param dataProvider the source for the data to be displayed
	 * and or edited.
	 * @param width the number of bytes to show on each row.
	 * @param readOnly whether or not this dialog should allow
	 * editing the data or just allow viewing.
	 */
	public HexEditorDialog(Frame owner, DataProvider dataProvider, int width, boolean readOnly) {
		super(owner);
		init(dataProvider, width, readOnly);
	}
	
	private void init(DataProvider dataProvider, int width, boolean readOnly) {
		this.editor = new HexEditorPanel(dataProvider, width, readOnly);
		setModal(true);
		setTitle("Hex Editor");
		setLayout(new BorderLayout());
		getContentPane().add(this.editor, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelled = false;
				setVisible(false);
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
	}
	
	/**
	 * Invokes the editor, making it visible and modal. This method
	 * will return once the user has clicked on the <code>OK</code>
	 * or <code>Cancel</code> -buttons or closed the dialog.
	 * @return true if the dialog was cancelled or closed, false if
	 * the <code>OK</code> -button was clicked.
	 */
	public boolean invoke() {
		setLocationRelativeTo(getParent());
		setVisible(true);
		
		return this.cancelled;
	}
	
	/**
	 * Returns the selection model which can be used to querying
	 * and setting the current selection or cursor position of
	 * the editor.
	 * @return a selection model object.
	 */
	public SelectionModel getSelectionModel() {
		return this.editor.getSelectionModel();
	}
}

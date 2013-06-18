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

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <code>BasicInfoEditor</code> is a GUI editor for basic constantpool types,
 * such as UTF8Text and Constants.
 *
 * @author Sami Koivu
 */

public abstract class BasicInfoEditor extends JDialog {
	private JPanel panel = new JPanel();

	JLabel label = new JLabel("Value: ");

	JTextField value = new JTextField(16);

	JButton doneButton = new JButton("Ok");

	public BasicInfoEditor(Frame frame) {
		super(frame, "", true);
		this.panel.setLayout(new GridBagLayout());
		this.value.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					done();
				}
			}
		});

		this.doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done();
			}
		});
		getContentPane().add(this.panel);
		addCenter(this.label, 0, 0);
		addCenter(this.value, 1, 0);
		addEast(this.doneButton, 1, 1);
		pack();
	}

	private void addCenter(Component comp, int x, int y) {
		this.panel.add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
	}

	private void addEast(Component comp, int x, int y) {
		this.panel.add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
	}

	public void invoke(String title, String previousValue) {
		setTitle(title);
		this.value.setText(previousValue);
		setModal(true);
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

	public void done() {
		try {
			validate(this.value.getText());
			setVisible(false);
		} catch (ValidationException ve) {
			JOptionPane.showMessageDialog(this, ve.getValidationMessage(),
					"Validation error", JOptionPane.OK_OPTION);
		}
	}

	public abstract void validate(String value) throws ValidationException;

	public static class ValidationException extends Exception {
		private String validationMessage;

		public ValidationException(String validationMessage) {
			this.validationMessage = validationMessage;
		}

		public String getValidationMessage() {
			return this.validationMessage;
		}
	}
}
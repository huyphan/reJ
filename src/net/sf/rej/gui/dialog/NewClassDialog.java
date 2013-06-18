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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class NewClassDialog extends JDialog {
    private static final String DEFAULT_PKG_STRING = "[Default]";
    private DefaultComboBoxModel model = null;
    private JComboBox packageCombo = new JComboBox();
    private JTextField classField = new JTextField(20);
    private JTextField fileField = new JTextField(20);
    boolean cancelled = true;

    private Action okAction = new AbstractAction("OK") {
        public void actionPerformed(ActionEvent e) {
            NewClassDialog.this.cancelled = false;
            done();
        }
    };
    private Action cancelAction = new AbstractAction("Cancel") {
        public void actionPerformed(ActionEvent e) {
            NewClassDialog.this.cancelled = true;
            done();
        }
    };

    KeyListener keyListener = new KeyAdapter() {
        @Override
		public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            	NewClassDialog.this.cancelled = false;
                done();
            } else {
                updateFileName();
            }
        }
    };

    ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            updateFileName();
        }
    };

    public NewClassDialog(JFrame parent) {
        super(parent, "Create new class", true);
        this.getContentPane().setLayout(new GridBagLayout());
        this.model = new DefaultComboBoxModel();
        this.model.addElement(DEFAULT_PKG_STRING);
        this.packageCombo.setEditable(true);
        // TODO: get list of packages from ClassIndex
        this.packageCombo.addKeyListener(this.keyListener);
        this.packageCombo.addActionListener(this.actionListener);

        this.classField.addKeyListener(this.keyListener);

        this.packageCombo.setModel(this.model);
        addCenter(new JLabel("Package: "), 0, 0);
        addCenter(this.packageCombo, 1, 0);
        addEast(new JLabel("Class: "), 0, 1);
        addCenter(this.classField, 1, 1);
        addEast(new JLabel("File: "), 0, 2);
        addCenter(this.fileField, 1, 2);
        addCenter(new JButton(this.okAction), 0, 3);
        addCenter(new JButton(this.cancelAction), 1, 3);
        pack();
    }

    private void addCenter(Component comp, int x, int y) {
        this.getContentPane().add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void addEast(Component comp, int x, int y) {
        this.getContentPane().add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    public void invoke() {
    	setLocationRelativeTo(getParent());
        setVisible(true);
    }

    public boolean userCancelled() {
        return this.cancelled;
    }

    public String getSelectedPackage() {
        return (String)this.packageCombo.getSelectedItem();
    }

    public String getFullClassName() {
        String pkg = (String)this.packageCombo.getSelectedItem();
        if (DEFAULT_PKG_STRING.equals(pkg)) {
            return this.classField.getText();
        } else {
            return pkg + "." + this.classField.getText();
        }
    }

    public String getSelectedClass() {
        return this.classField.getText();
    }

    public String getSelectedFile() {
        return this.fileField.getText();
    }

    void done() {
        this.setVisible(false);
    }

    void updateFileName() {
        String pkg = (String)this.packageCombo.getSelectedItem();
        if (DEFAULT_PKG_STRING.equals(pkg)) {
            this.fileField.setText(this.classField.getText() + ".class");
        } else {
            this.fileField.setText(pkg.replace('.', '/') + "/" + this.classField.getText() + ".class");
        }

    }

}

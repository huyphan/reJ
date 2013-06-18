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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.gui.editor.OrderedFilterListModel;

/**
 * A Dialog Window which presents a list of Classes from which the user can
 * select one class by typing a part of the name and/or choosing the class from
 * the list.
 * 
 * @author Sami Koivu
 */
public class ClassChooseDialog extends JDialog {
    private JPanel panel = new JPanel();
    JLabel label = new JLabel("Select class");
    JTextField value = new JTextField(16);
    OrderedFilterListModel model = null;
    JList list = null;
    JLabel packageCaption = new JLabel("Package: ");
    JLabel packageLabel = new JLabel("");
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    ClassLocator selectedValue = null;

    // TODO: add delay to the start of filtering
    public ClassChooseDialog(Frame parent, ClassIndex classPathIndex) {
        super(parent, "Choose Class", true);
        initialize(classPathIndex);
        setLocationRelativeTo(parent);
    }

    public ClassChooseDialog(Dialog parent, ClassIndex classPathIndex) {
        super(parent, "Choose Class", true);
        initialize(classPathIndex);
        setLocationRelativeTo(parent);
    }

	private void initialize(ClassIndex classPathIndex) {
		List cpIndex = classPathIndex.getAll();
        this.model = new OrderedFilterListModel(cpIndex);
        this.model.setFilter(this.value.getText());
        this.model.filter();
        this.list = new JList(this.model);
        this.list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent lse) {
                updatePackage();
            }

        });

        this.panel.setLayout(new GridBagLayout());
        this.value.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    done();
                } else {
                    ClassChooseDialog.this.model.setFilter(ClassChooseDialog.this.value.getText());
                    ClassChooseDialog.this.model.filter();
                    ClassChooseDialog.this.list.setSelectedIndex(0);
                    updatePackage();
                }
            }
        });

        this.list.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    done();
                }
            }
        });

        this.okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                done();
            }
        });

        this.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClassChooseDialog.this.selectedValue = null;
                setVisible(false);
            }
        });

        getContentPane().add(this.panel);
        addCenter(this.label, 0, 0, 2);
        addCenter(this.value, 0, 1, 2);
        addCenter(new JScrollPane(this.list), 0, 2, 2);
        addEast(this.packageCaption, 0, 3);
        addCenter(this.packageLabel, 1, 3);
        addCenter(this.okButton, 0, 4);
        addCenter(this.cancelButton, 1, 4);
        pack();
    }

    private void addCenter(Component comp, int x, int y) {
        this.panel.add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void addCenter(Component comp, int x, int y, int width) {
        this.panel.add(comp, new GridBagConstraints(x, y, width, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void addEast(Component comp, int x, int y) {
        this.panel.add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    public void invoke() {
        setModal(true);
        setVisible(true);
    }

    public void done() {
        this.selectedValue = (ClassLocator)this.list.getSelectedValue();
        if (this.selectedValue != null) {
            setVisible(false);
        }
    }

    public ClassLocator getSelected() {
        return this.selectedValue;
    }

    public void updatePackage() {
        if (this.model.getSize() == 0) {
            this.packageLabel.setText("");
        }
        // TODO: The following line creates exceptions.. probably because of bad design
        ClassLocator cl = (ClassLocator)ClassChooseDialog.this.list.getSelectedValue();
        if(cl != null) {
            this.packageLabel.setText(cl.getPackage());
        } else {
            this.packageLabel.setText("");
        }
    }

}

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
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Field;
import net.sf.rej.util.Wrapper;

public class FieldChooseDialog extends JDialog {
    private Action classChooserAction = new AbstractAction("...") {
        public void actionPerformed(ActionEvent e) {
            ClassIndex cpIndex = SystemFacade.getInstance().getClassIndex();
            ClassChooseDialog ccd = new ClassChooseDialog(MainWindow
                    .getInstance(), cpIndex);
            ccd.invoke();
            ClassLocator cl = ccd.getSelected();
            if (cl != null) {
                FieldChooseDialog.this.clsModel.addElement(cl);
                FieldChooseDialog.this.cls.setSelectedItem(cl);
                updateFieldList();
            }
        }
    };

    private Container panel = this.getContentPane();
    private JLabel label = new JLabel("Select field", SwingConstants.CENTER);
    private JLabel classCaption = new JLabel("Class: ");
    private JLabel methodCaption = new JLabel("Field: ");
    DefaultComboBoxModel clsModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel fieldModel = new DefaultComboBoxModel();
    JComboBox cls = new JComboBox(this.clsModel);
    private JButton clsButton = new JButton(this.classChooserAction);
    private JComboBox field = new JComboBox(this.fieldModel);
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");

    ClassLocator selectedClass = null;
    Field selectedField = null;

    public FieldChooseDialog(Frame frame, ClassLocator defaultClass) {
        super(frame, "Choose Field", true);

        this.clsModel.addElement(defaultClass);
        this.cls.setSelectedItem(defaultClass);
        updateFieldList();

        this.panel.setLayout(new GridBagLayout());
        this.field.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    done();
                }
            }
        });

        this.cls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                updateFieldList();
            }
        });

        this.okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                done();
            }
        });

        this.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FieldChooseDialog.this.selectedClass = null;
                FieldChooseDialog.this.selectedField = null;
                setVisible(false);
            }
        });

        addCenter(this.label, 0, 0, 4);
        addEast(this.classCaption, 0, 1);
        addCenter(this.cls, 1, 1, 2);
        addCenter(this.clsButton, 3, 1, 1);
        addEast(this.methodCaption, 0, 2);
        addCenter(this.field, 1, 2, 3);
        addCenter(this.okButton, 0, 3, 2);
        addCenter(this.cancelButton, 2, 3, 2);
        pack();
    }

    private void addCenter(Component comp, int x, int y, int width) {
        this.panel.add(comp, new GridBagConstraints(x, y, width, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
    }

    private void addEast(Component comp, int x, int y) {
        this.panel.add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
                        2, 2, 2), 0, 0));
    }

    public void invoke() {
        this.setModal(true);
        setLocationRelativeTo(getParent());
        this.setVisible(true);
    }

    public void done() {
        this.selectedClass = (ClassLocator)this.cls.getSelectedItem();
        this.selectedField = null;
        Wrapper wrapper = (Wrapper)this.field.getSelectedItem();
        if(wrapper != null) {
            this.selectedField = (Field) wrapper.getContent();
        }
        this.setVisible(false);
    }

    public ClassLocator getSelectedClass() {
        return this.selectedClass;
    }

    public Field getSelectedField() {
        return this.selectedField;
    }

    void updateFieldList() {
        this.fieldModel.removeAllElements();
        Object obj = this.cls.getSelectedItem();
        try {
            ClassFile cf = null;
            if (obj instanceof String) {
                // TODO: let user type his own class name?
            } else if (obj instanceof ClassLocator) {
                ClassLocator cl = (ClassLocator) obj;
                cf = SystemFacade.getInstance().getClassFile(cl);
            }

            // cf will be null if referenced class is not in project classpath
            if (cf != null) {
                List fields = cf.getFields();
                for (int i = 0; i < fields.size(); i++) {
                    Field field = (Field)fields.get(i);
                    Wrapper<Field> wrapper = new Wrapper<Field>();
                    wrapper.setContent(field);
                    wrapper.setDisplay(field.getSignatureLine());
                    this.fieldModel.addElement(wrapper);
                }
            }

        } catch (Exception e) {
            SystemFacade.getInstance().handleException(e);
        }

        pack();
        setLocationRelativeTo(getParent());
    }

}

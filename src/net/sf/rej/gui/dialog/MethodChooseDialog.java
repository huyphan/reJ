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
import net.sf.rej.java.Method;
import net.sf.rej.util.Wrapper;

public class MethodChooseDialog extends JDialog {
    private Action classChooserAction = new AbstractAction("...") {
        public void actionPerformed(ActionEvent e) {
            ClassIndex cpIndex = SystemFacade.getInstance().getClassIndex();
            ClassChooseDialog ccd = new ClassChooseDialog(MainWindow
                    .getInstance(), cpIndex);
            ccd.invoke();
            ClassLocator cl = ccd.getSelected();
            if (cl != null) {
                MethodChooseDialog.this.clsModel.addElement(cl);
                MethodChooseDialog.this.cls.setSelectedItem(cl);
                updateMethodList();
            }
        }
    };

    private Container panel = this.getContentPane();
    private JLabel label = new JLabel("Select method", SwingConstants.CENTER);
    private JLabel classCaption = new JLabel("Class: ");
    private JLabel methodCaption = new JLabel("Method: ");
    DefaultComboBoxModel clsModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel methodModel = new DefaultComboBoxModel();
    JComboBox cls = new JComboBox(this.clsModel);
    private JButton clsButton = new JButton(this.classChooserAction);
    private JComboBox method = new JComboBox(this.methodModel);
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");

    ClassLocator selectedClass = null;
    Method selectedMethod = null;

    public MethodChooseDialog(Frame frame, ClassLocator defaultClass) {
        super(frame, "Choose Method", true);

        this.clsModel.addElement(defaultClass);
        this.cls.setSelectedItem(defaultClass);
        updateMethodList();

        this.panel.setLayout(new GridBagLayout());
        this.method.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    done();
                }
            }
        });

        this.cls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                updateMethodList();
            }
        });

        this.okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                done();
            }
        });

        this.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MethodChooseDialog.this.selectedClass = null;
                MethodChooseDialog.this.selectedMethod = null;
                setVisible(false);
            }
        });

        addCenter(this.label, 0, 0, 4);
        addEast(this.classCaption, 0, 1);
        addCenter(this.cls, 1, 1, 2);
        addCenter(this.clsButton, 3, 1, 1);
        addEast(this.methodCaption, 0, 2);
        addCenter(this.method, 1, 2, 3);
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
        this.selectedMethod = null;
        Wrapper wrapper = (Wrapper)this.method.getSelectedItem();
        if(wrapper != null) {
            this.selectedMethod = (Method) wrapper.getContent();
        }
        this.setVisible(false);
    }

    public ClassLocator getSelectedClass() {
        return this.selectedClass;
    }

    public Method getSelectedMethod() {
        return this.selectedMethod;
    }

    void updateMethodList() {
        this.methodModel.removeAllElements();
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
                List methods = cf.getMethods();
                for (int i = 0; i < methods.size(); i++) {
                    Method method = (Method) methods.get(i);
                    Wrapper<Method> wrapper = new Wrapper<Method>();
                    wrapper.setContent(method);
                    wrapper.setDisplay(method.getSignatureLine());
                    this.methodModel.addElement(wrapper);
                }
            }

        } catch (Exception e) {
            SystemFacade.getInstance().handleException(e);
        }

        pack();
        setLocationRelativeTo(getParent());
    }

}

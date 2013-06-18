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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.rej.Imports;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.files.MethodLocator;
import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.dialog.MethodChooseDialog;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Method;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.RefInfo;
import net.sf.rej.util.Wrapper;

public class MethodChooser extends JPanel {
    private Action chooserAction = new AbstractAction("...") {
        public void actionPerformed(ActionEvent e) {
            Object obj = MethodChooser.this.combo.getSelectedItem();
            String className = null;
            if (obj instanceof String) {
                className = (String)obj;
            } else if (obj instanceof Wrapper) {
                Wrapper wrapper = (Wrapper)obj;
                if (wrapper.getContent() instanceof RefInfo) {
                    RefInfo ri = (RefInfo)wrapper.getContent();
                    className = ri.getClassName();
                } else {
                    MethodLocator ml = (MethodLocator) wrapper.getContent();
                    className = ml.getClassLocator().getFullName();
                }
            } else {
                // TODO: other types?
            }
            ClassLocator cl = SystemFacade.getInstance().getClassIndex().getLocator(className);
            MethodChooseDialog mcd = new MethodChooseDialog(MainWindow.getInstance(), cl);
            mcd.invoke();
            cl = mcd.getSelectedClass();
            Method method = mcd.getSelectedMethod();
            if(cl != null && method != null) {
                Wrapper<MethodLocator> wrapper = new Wrapper<MethodLocator>();
                wrapper.setContent(new MethodLocator(cl, method));
                wrapper.setDisplay(method.getSignatureLine(cl.getFullName()));
                MethodChooser.this.model.addElement(wrapper);
                MethodChooser.this.combo.setSelectedItem(wrapper);
                if (layoutChangeListener != null) {
                	layoutChangeListener.layoutChanged(MethodChooser.this);
                }
            }
        }
    };

    private JLabel label = new JLabel("Method: ");
    DefaultComboBoxModel model = null;
    JComboBox combo = new JComboBox();
    private JButton button = new JButton(this.chooserAction);

    private ConstantPool cp = null;
    private ClassFile cf = null;
    private Imports imports = null;
    
    private LayoutChangeListener layoutChangeListener = null;

    public MethodChooser(LayoutChangeListener layoutChangeListener) {
        this.setLayout(new GridBagLayout());
        this.add(this.label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(this.combo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(this.button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.layoutChangeListener = layoutChangeListener;
    }

    public void setClassFile(ClassFile cf) {
    	this.cf = cf;
    }

    public void setConstantPool(ConstantPool pool) {
        this.cp = pool;
        List<Wrapper> al = new ArrayList<Wrapper>();
        this.imports = EditorFacade.getInstance().getImports(this.cf);

        for (int i = 0; i < this.cp.size(); i++) {
            ConstantPoolInfo cpi = this.cp.get(i);
            if (cpi != null) {
                if (cpi.getType() == ConstantPoolInfo.INTERFACE_METHOD_REF || cpi.getType() == ConstantPoolInfo.METHOD_REF) {
                    al.add(createWrapper((RefInfo) cpi));
                }
            }
        }
        this.model = new DefaultComboBoxModel(al.toArray());
        this.combo.setModel(this.model);

    }

    public Wrapper<RefInfo> createWrapper(RefInfo ri) {
        Wrapper<RefInfo> wrapper = new Wrapper<RefInfo>();
        wrapper.setContent(ri);
        Descriptor desc = ri.getDescriptor();
        wrapper.setDisplay(EditorFacade.getInstance().getMethodString(this.imports, desc, ri.getClassName(), ri.getTargetName()));
        return wrapper;
    }


    public Object getValue() {
        Object o = this.combo.getSelectedItem();
        int value = 0;
        if (o instanceof String) {
            // parse into ClassInfo, RefInfo NameAndTypeInfo etc
            throw new RuntimeException("Editing not implemented.");
        } else {
            Wrapper wrapper = (Wrapper)o;
            Object content = wrapper.getContent();
            if(content instanceof MethodLocator) {
                MethodLocator ml = (MethodLocator)content;
                return ml;
            } else {
                RefInfo ri = (RefInfo) ( (Wrapper) o).getContent();
                value = this.cp.optionalAdd(ri);
            }
        }

        return Integer.valueOf(value);
    }

    public void setSelected(Object o) {
        this.combo.setSelectedItem(createWrapper((RefInfo) o));
    }

    public void setEditable(boolean b) {
        this.combo.setEditable(b);
    }

    public void setReadOnly() {
        this.combo.setEnabled(false);
    }

}

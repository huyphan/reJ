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
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.util.Wrapper;

public class ConstantpoolConstantChooser extends JPanel {
    JLabel label = new JLabel("Constant: ");
    JComboBox combo = new JComboBox();

    private ConstantPool cp = null;

    public ConstantpoolConstantChooser() {
        this.setLayout(new GridBagLayout());
        this.add(this.label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(this.combo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    public void setConstantPool(ConstantPool pool) {
        this.cp = pool;

        List<Wrapper<ConstantPoolInfo>> al = new ArrayList<Wrapper<ConstantPoolInfo>>();
        for (int i = 0; i < this.cp.size(); i++) {
            ConstantPoolInfo cpi = this.cp.get(i);
            if (cpi != null) {
                switch(cpi.getType()) {
                	case ConstantPoolInfo.DOUBLE:
                	case ConstantPoolInfo.FLOAT:
                	case ConstantPoolInfo.INTEGER:
                	case ConstantPoolInfo.LONG:
                	case ConstantPoolInfo.STRING:
                        al.add(createWrapper(cpi));
                		break;
                	default:
                	    // do nothing with other types
                }
            }
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(al.toArray());
        this.combo.setModel(model);

    }

    public Wrapper<ConstantPoolInfo> createWrapper(ConstantPoolInfo cpi) {
        Wrapper<ConstantPoolInfo> wrapper = new Wrapper<ConstantPoolInfo>();
        wrapper.setContent(cpi);
        wrapper.setDisplay("[" + cpi.getTypeString() + "] " + cpi.getValue());
        return wrapper;
    }

    public Object getValue() {
        Object o = this.combo.getSelectedItem();
        int value = 0;
        if (o instanceof String) {
            // parse into ClassInfo, RefInfo NameAndTypeInfo etc
            throw new RuntimeException("Editing not implemented.");
        } else {
            ConstantPoolInfo cpi = (ConstantPoolInfo) ( (Wrapper) o).getContent();
            value = this.cp.indexOf(cpi);
        }

        return Integer.valueOf(value);
    }

    public void setSelected(Object o) {
        this.combo.setSelectedItem(createWrapper((ConstantPoolInfo) o));
    }

    public void setEditable(boolean b) {
        this.combo.setEditable(b);
    }

    public void setReadOnly() {
        this.combo.setEnabled(false);
    }

}
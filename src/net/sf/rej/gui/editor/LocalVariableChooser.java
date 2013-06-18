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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.attribute.LocalVariableTableAttribute;

public class LocalVariableChooser extends JPanel {
    private JLabel jLabel1 = new JLabel("Local Variable: ");
    private DefaultComboBoxModel model = new DefaultComboBoxModel();
    private JComboBox combo = new JComboBox(model);

    public LocalVariableChooser() {
        this.setLayout(new GridBagLayout());
        this.add(this.jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        0, 0, 0, 0), 0, 0));
        this.add(this.combo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        0, 0, 0, 0), 0, 0));
    }


    public void setLocalVariable(LocalVariableTableAttribute lvAttr) {
    	if (lvAttr == null) {
    		this.model.removeAllElements();
    	} else {
    		for (LocalVariable lv : lvAttr.getLocalVariables()) {
    			this.model.addElement(lv);
    		}
    	}
    }

    public Object getValue() {
    	Object selected = this.combo.getSelectedItem();
    	if (selected == null) {
    		return null;
    	} else if (selected instanceof String) {
    		return Integer.valueOf((String)selected);
    	} else {
    		LocalVariable lv = (LocalVariable) selected;
    		return Integer.valueOf(lv.getIndex());
    	}
    }

    /**
     * Set the currently selected local variable. The parameter is
     * expected to be either a LocalVariable object or a String.
     * The most likely use for this method is for setting the 'old'
     * value of the chooser.
     * 
     * @param lv the value to be selected.
     */
    public void setSelected(Object lv) {
    	int index = this.model.getIndexOf(lv);
    	if (index == -1) {
    		// the object is not in the list yet - add it.
    		this.model.addElement(lv);
    	}
        this.combo.setSelectedItem(lv);
    }

    public void setEditable(boolean b) {
        this.combo.setEditable(b);
    }

    public void setReadOnly() {
        this.combo.setEnabled(false);
    }

}
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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PreferencesDialog extends JDialog {
    private JPanel panel = new JPanel();
    JFileChooser fileChooser = new JFileChooser();


    private Action addAction = new AbstractAction("Add to classpath..") {
        public void actionPerformed(ActionEvent e) {
            PreferencesDialog.this.fileChooser.setMultiSelectionEnabled(true);
            PreferencesDialog.this.fileChooser.showDialog(PreferencesDialog.this, "Add to list");
            File[] files = PreferencesDialog.this.fileChooser.getSelectedFiles();
            for(int i=0; i < files.length; i++) {
                PreferencesDialog.this.model.addElement(files[i]);
            }
        }
    };

    private Action removeAction = new AbstractAction("Remove selected") {
        public void actionPerformed(ActionEvent e) {
        	int index = PreferencesDialog.this.list.getSelectedIndex();
        	if (index != -1) {
        		PreferencesDialog.this.model.remove(index);
        	}
        }
    };

    private Action doneAction = new AbstractAction("Done") {
        public void actionPerformed(ActionEvent e) {
            PreferencesDialog.this.setVisible(false);
        }
    };


    JLabel label = new JLabel("CLASSPATH elements");
    DefaultListModel model = new DefaultListModel();
    JList list = new JList(this.model);
    JButton addButton = new JButton(this.addAction);
    JButton removeButton = new JButton(this.removeAction);
    JButton doneButton = new JButton(this.doneAction);

    public PreferencesDialog(Frame frame) {
        super(frame, "Preferences", true);

        this.panel.setLayout(new GridBagLayout());
                getContentPane().add(this.panel);
        addCenter(this.label, 0, 0);
        addCenter(new JScrollPane(this.list), 1, 0);
        addCenter(this.addButton, 0, 2);
        addCenter(this.removeButton, 1, 2);
        addEast(this.doneButton, 2, 2);
    }

    private void addCenter(Component comp, int x, int y) {
        this.panel.add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void addEast(Component comp, int x, int y) {
        this.panel.add(comp, new GridBagConstraints(x, y, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    public void invoke() {
        setModal(true);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public List<File> getClasspathList() {
        List<File> list = new ArrayList<File>();
        for(int i=0; i < this.model.size(); i++) {
            list.add((File)this.model.get(i));
        }

        return list;
    }

    public void setClasspathList(List<File> list) {
        this.model.clear();
        for(int i=0; i < list.size(); i++) {
            this.model.addElement(list.get(i));
        }
    }
}

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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sf.rej.gui.dialog.ExceptionChooseDialog;
import net.sf.rej.gui.dialog.ParameterChooseDialog;
import net.sf.rej.gui.dialog.TypeChooseDialog;
import net.sf.rej.java.AccessFlags;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.JavaType;

public class MethodEditor extends JDialog {
    private Action returnTypeChooserAction = new AbstractAction("...") {
        public void actionPerformed(ActionEvent e) {
        	TypeChooseDialog chooser = new TypeChooseDialog(MethodEditor.this);
        	chooser.invoke(MethodEditor.this.desc.getReturn(), true);
        	JavaType newRet = chooser.getJavaType();
        	MethodEditor.this.desc.setReturn(newRet);
            MethodEditor.this.returnField.setText(newRet.toString());
        }
    };

    private Action parameterChooserAction = new AbstractAction("...") {
        public void actionPerformed(ActionEvent e) {
        	ParameterChooseDialog chooser = new ParameterChooseDialog(MethodEditor.this);
        	chooser.invoke(MethodEditor.this.desc.getParamList());
        	if (!chooser.wasCancelled()) {
        		MethodEditor.this.desc.setParamList(chooser.getParams());
                MethodEditor.this.parameterField.setText(desc.getParams());
        	}
        }
    };

    private Action exceptionChooserAction = new AbstractAction("...") {
        public void actionPerformed(ActionEvent e) {
        	ExceptionChooseDialog chooser = new ExceptionChooseDialog(MethodEditor.this);
        	chooser.invoke(MethodEditor.this.exceptions);
        	if (!chooser.wasCancelled()) {
        		MethodEditor.this.exceptions = chooser.getExceptions();
                MethodEditor.this.exceptionsField.setText(listToText(MethodEditor.this.exceptions));
        	}
        }
    };

    Descriptor desc = null;
    List exceptions = new ArrayList();

    JCheckBox nativeCheck = new JCheckBox("native");
    JCheckBox publicCheck = new JCheckBox("public");
    JCheckBox staticCheck = new JCheckBox("static");
    JCheckBox synchronizedCheck = new JCheckBox("synchronized");
    JCheckBox protectedCheck = new JCheckBox("protected");
    JCheckBox privateCheck = new JCheckBox("private");
    JCheckBox abstractCheck = new JCheckBox("abstract");
    JCheckBox finalCheck = new JCheckBox("final");
    JLabel jLabel1 = new JLabel("Name: ");
    private DefaultComboBoxModel nameModel = new DefaultComboBoxModel();
    JComboBox nameField = new JComboBox(this.nameModel);
    JLabel jLabel2 = new JLabel();
    JTextField maxStackField = new JTextField();
    JLabel jLabel3 = new JLabel();
    JTextField maxLocalsField = new JTextField();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();
    JLabel jLabel4 = new JLabel();
    JTextField returnField = new JTextField();
    private JButton returnTypeButton = new JButton(this.returnTypeChooserAction);
    JLabel jLabel6 = new JLabel();
    JTextField parameterField = new JTextField();
    private JButton parameterButton = new JButton(this.parameterChooserAction);
    JLabel exceptionsLabel = new JLabel("Exceptions: ");
    JTextField exceptionsField = new JTextField();
    JButton exceptionButton = new JButton(this.exceptionChooserAction);

    private boolean cancelled = false;

    public MethodEditor(Frame owner) {
        super(owner, "Method editor");
        this.getContentPane().setLayout(new GridBagLayout());
        this.nameField.setEditable(true);
        this.returnField.setEditable(false);
        this.exceptionsField.setEditable(false);
        this.nameModel.addElement("<init>");
        this.nameModel.addElement("<clinit>");
        this.jLabel2.setText("Max Stack Size: ");
        this.maxStackField.setText("maxStackField");
        this.maxStackField.setColumns(18);
        this.jLabel3.setText("Max Locals: ");
        this.maxLocalsField.setText("maxLocalsField");
        this.maxLocalsField.setColumns(18);
        this.okButton.setText("Ok");
        this.okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        cancelled = false;
		        setVisible(false);
			}
        });
        this.cancelButton.setText("Cancel");
        this.cancelButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                cancelled = true;
                setVisible(false);
        	}
        });
        this.jLabel4.setRequestFocusEnabled(true);
        this.jLabel4.setText("Return type: ");
        this.jLabel6.setText("Parameters: ");
        this.parameterField.setText("int,java.lang.String");
        this.parameterField.setColumns(18);
        this.parameterField.setEditable(false);
        this.getContentPane().add(this.nativeCheck, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.publicCheck, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.staticCheck, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.synchronizedCheck, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.protectedCheck, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.privateCheck, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.abstractCheck, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.finalCheck, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.jLabel1, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.nameField, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.jLabel2, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.maxStackField, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.jLabel3, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.maxLocalsField, new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.okButton, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.cancelButton, new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.jLabel4, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.returnField, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.returnTypeButton, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.jLabel6, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.parameterField, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.parameterButton, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.exceptionsLabel, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.exceptionsField, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(this.exceptionButton, new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    public static String listToText(List list) {
    	StringBuffer sb = new StringBuffer();
    	for (int i=0; i < list.size(); i++) {
    		if (i > 0) {
    			sb.append(", ");
    		}

    		sb.append(list.get(i));
    	}
    	return sb.toString();
    }

    public void invoke(String name, Descriptor desc, int flags, Integer maxStackSize, Integer maxLocals, List exceptions) {
    	this.exceptions = exceptions;
    	this.exceptionsField.setText(listToText(this.exceptions));
    	this.desc = desc;
    	this.nameModel.removeAllElements();
        this.nameModel.addElement("<init>");
        this.nameModel.addElement("<clinit>");
        this.nameModel.addElement(name);
        this.nameField.setSelectedItem(name);
        String ret = desc.getReturn().toString();
        this.returnField.setText(ret);
        this.parameterField.setText(desc.getParams());
        this.publicCheck.setSelected(AccessFlags.isPublic(flags));
        this.staticCheck.setSelected(AccessFlags.isStatic(flags));
        this.synchronizedCheck.setSelected(AccessFlags.isSynchronized(flags));
        this.nativeCheck.setSelected(AccessFlags.isNative(flags));
        this.protectedCheck.setSelected(AccessFlags.isProtected(flags));
        this.privateCheck.setSelected(AccessFlags.isPrivate(flags));
        this.abstractCheck.setSelected(AccessFlags.isAbstract(flags));
        this.finalCheck.setSelected(AccessFlags.isFinal(flags));
        if (maxLocals != null) {
            this.maxLocalsField.setText(maxLocals.toString());
        } else {
            this.maxLocalsField.setText("");
        }
        if (maxStackSize != null) {
            this.maxStackField.setText(maxStackSize.toString());
        } else {
            this.maxStackField.setText("");
        }

        pack();
        setModal(true);
		setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    public AccessFlags getAccessFlags() {
        AccessFlags flags = new AccessFlags();
        flags.setAbstract(this.abstractCheck.isSelected());
        flags.setFinal(this.finalCheck.isSelected());
        flags.setNative(this.nativeCheck.isSelected());
        flags.setPrivate(this.privateCheck.isSelected());
        flags.setProtected(this.protectedCheck.isSelected());
        flags.setPublic(this.publicCheck.isSelected());
        flags.setStatic(this.staticCheck.isSelected());
        flags.setSynchronized(this.synchronizedCheck.isSelected());

        return flags;
    }

    public int getMaxStack() {
        try {
            int maxStack = Integer.parseInt(this.maxStackField.getText());
            return maxStack;
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }

    public int getMaxLocals() {
        try {
            int maxLocals = Integer.parseInt(this.maxLocalsField.getText());
            return maxLocals;
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }

    public List<String> getExceptions() {
    	/* Return a list of Strings rather than a possible mix of
    	 * strings and ExceptionsAttribute.Exception objects.
    	 * The mixture is due to the fact that new exceptions added
    	 * to the list have not been added to the constant pool at
    	 * this point, because that would break the undo-integrity.
    	 */
    	List<String> list = new ArrayList<String>();
    	for (int i=0; i < this.exceptions.size(); i++) {
    		list.add(this.exceptions.get(i).toString());
    	}

    	return list;
    }

    public String getMethodName() {
        return this.nameField.getSelectedItem().toString();
    }

    public Descriptor getDescriptor() {
        return this.desc;
    }

    public boolean wasCancelled() {
        return this.cancelled;
    }

}

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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import net.sf.rej.gui.InstructionHints;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.attribute.LocalVariableTableAttribute;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.instruction.DecompilationContext;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Parameters;

public class InstructionEditor extends JDialog implements LayoutChangeListener {

    private static final long serialVersionUID = 1L;

    private static final InstructionHints hints = new InstructionHints();
    private Instruction instruction = null;
    private List<Object> choosers = new ArrayList<Object>();
    private InstructionList instructionList = new InstructionList();
    private int position = 0;
    private ConstantPool pool = null;
    private ClassFile cf;
    private LocalVariableTableAttribute lvTable = null;
    private DecompilationContext dc = null;
    private boolean insertMode = false;

    private JPanel content = new JPanel();
    private JPanel instructionInfo = new JPanel();
    private DefaultComboBoxModel model = new DefaultComboBoxModel();
    private JComboBox instructionCombo = new JComboBox(this.model) {
    	@Override
    	public String getToolTipText() {
    		Object obj = this.getSelectedItem();
    		if (obj != null && obj instanceof Instruction) {
    			Instruction inst = (Instruction) obj;
    			return hints.getHint(inst);
    		}
    		
    		return super.getToolTipText();
    	}
    };
    private JButton okButton = new JButton("Ok");
    private JButton cancelButton = new JButton("Cancel");
    private JPanel paramPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    
    boolean cancelled = true;

    public InstructionEditor() {
        super(MainWindow.getInstance(), "Instruction editor", true);
        this.getContentPane().setLayout(new BorderLayout());
        this.content.setLayout(new GridBagLayout());
        this.instructionInfo.setLayout(new GridBagLayout());
        this.okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelled = false;
				setVisible(false);
			}
        });
        this.cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelled = true;
				setVisible(false);
			}        	
        });
        this.paramPanel.setLayout(new GridBagLayout());
        this.instructionInfo.setBorder(BorderFactory.createTitledBorder("Instruction"));
        this.paramPanel.setBorder(BorderFactory.createTitledBorder("Instruction Parameters"));
        this.paramPanel.setDebugGraphicsOptions(0);
        this.getContentPane().add(this.content, BorderLayout.CENTER);
        this.content.add(this.instructionInfo, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.instructionInfo.add(this.instructionCombo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.instructionInfo.add(this.buttonPanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.buttonPanel.add(this.okButton, null);
        this.buttonPanel.add(this.cancelButton, null);
        this.content.add(this.paramPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0), 0, 0));
    }

    /**
     * Invoke instruction editor, with just the instruction
     * set previously by a call to setInstruction available,
     * so that only it's parameters (if any) can be modified.
     */
    public void invokeModify() {
    	this.insertMode = false;

        List<Instruction> instructions = this.instructionList.getList();

        List<Instruction> similarInstructions = new ArrayList<Instruction>();

        for (Instruction inst : instructions) {
            if (this.instruction.isReplaceable(inst, dc)) {
                similarInstructions.add(inst);
            }
        }

        Collections.sort(similarInstructions, new Comparator<Instruction>() {
            public int compare(Instruction a, Instruction b) {
                return a.getMnemonic().compareTo(b.getMnemonic());
            }
        });

        this.model.removeAllElements();
        this.model.addElement(this.instruction);

        for (Instruction inst : similarInstructions) {
            this.model.addElement(inst);
        }

        this.pack();
		setLocationRelativeTo(getOwner());
        ItemListener il = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                instruction = (Instruction) instructionCombo.getSelectedItem();
                updateEditor();
            }
        };
        this.instructionCombo.addItemListener(il);
        this.instructionCombo.setSelectedIndex(0);
		updateEditor();
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(this.instructionCombo);

        super.setVisible(true);
        toolTipManager.unregisterComponent(this.instructionCombo);
    }

    /**
     * Invoke the instruction editor with all available instructions
     */
    public void invokeInsert() {
    	this.insertMode = true;
        List<Instruction> instructions = this.instructionList.getList();
        Collections.sort(instructions, new Comparator<Instruction>() {
			public int compare(Instruction a, Instruction b) {
				return a.getMnemonic().compareTo(b.getMnemonic());
			}
             });
        this.model.removeAllElements();
        for (Instruction inst : instructions) {
        	this.model.addElement(inst);
        }
        this.instruction = (Instruction) this.instructionCombo.getSelectedItem();
        this.pack();
        updateEditor();
		setLocationRelativeTo(getOwner());
		ItemListener il = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
		        instruction = (Instruction) instructionCombo.getSelectedItem();
		        updateEditor();
			}      	
        };
        this.instructionCombo.addItemListener(il);
        updateEditor();
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(this.instructionCombo);
        super.setVisible(true);
        toolTipManager.unregisterComponent(this.instructionCombo);
        this.instructionCombo.removeItemListener(il);
    }

    private void addToPanel(Container parent, Component child, int pos) {
        parent.add(child, new GridBagConstraints(0, pos+1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void updateEditor() {
        this.paramPanel.removeAll();

        Parameters params = this.instruction.getParameters();
        if (params == null) {
        	throw new RuntimeException("Instruction " + this.instruction.getClass().getName() + " fails the instruction contract by returning null on a call to getParameters()");
        }
        this.choosers.clear();
        for (int i = 0; i < params.getCount(); i++) {
            switch (params.getType(i)) {
            case TYPE_LOCAL_VARIABLE_WIDE:
            case TYPE_LOCAL_VARIABLE: {
                LocalVariableChooser chooser = new LocalVariableChooser();
                this.choosers.add(chooser);
               	LocalVariable lv = null;
                if (lvTable != null) {
                	chooser.setLocalVariable(this.lvTable);
               		lv = lvTable.getLocalVariable(params.getInt(i), this.position);
               	}
               	if (lv != null) {
               		chooser.setSelected(lv);
               	} else {
               		chooser.setSelected(String.valueOf(params.getInt(i)));
               	}
                chooser.setEditable(true);
                addToPanel(this.paramPanel, chooser, i);
                break;
            }
            case TYPE_LOCAL_VARIABLE_READONLY: {
                LocalVariableChooser chooser = new LocalVariableChooser();
                this.choosers.add(chooser);
                if (this.lvTable != null) {
                    chooser.setLocalVariable(this.lvTable);
                    LocalVariable lv = this.lvTable.getLocalVariable(params.getInt(i), this.position);
                   	if (lv != null) {
                   		chooser.setSelected(lv);
                   	} else {
                   		chooser.setSelected(String.valueOf(params.getInt(i)));
                   	}
                }
                chooser.setEditable(false);
                chooser.setReadOnly();
                addToPanel(this.paramPanel, chooser, i);
                break;
            }
            case TYPE_CONSTANT_WIDE:
            case TYPE_CONSTANT: {
                ConstantChooser chooser = new ConstantChooser();
                this.choosers.add(chooser);
                chooser.setValue(params.getInt(i));
                addToPanel(this.paramPanel, chooser, i);
                break;
            }
            case TYPE_CONSTANT_POOL_METHOD_REF: {
                MethodChooser chooser = new MethodChooser(this);
                this.choosers.add(chooser);
                chooser.setClassFile(this.cf);
                chooser.setConstantPool(this.pool);
                if (!insertMode) {
                	chooser.setSelected(this.pool.get(params.getInt(i)));
                }
                chooser.setEditable(false);
                addToPanel(this.paramPanel, chooser, i);
                break;
            }
            case TYPE_CONSTANT_POOL_FIELD_REF: {
                FieldChooser chooser = new FieldChooser(this);
                this.choosers.add(chooser);
                chooser.setClassFile(this.cf);
                chooser.setConstantPool(this.pool);
                if (!insertMode) {
                	chooser.setSelected(this.pool.get(params.getInt(i)));
                }
                chooser.setEditable(false);
                addToPanel(this.paramPanel, chooser, i);
                break;
            }
            case TYPE_CONSTANT_POOL_CLASS: {
                ClassChooser chooser = new ClassChooser(this);
                this.choosers.add(chooser);
                chooser.setClassFile(this.cf);
                chooser.setConstantPool(this.pool);
                if (!insertMode) {
                	chooser.setSelected(this.pool.get(params.getInt(i)));
                }
                chooser.setEditable(false);
                addToPanel(this.paramPanel, chooser, i);
                break;
            }
            case TYPE_ARRAYTYPE: {
                ArrayTypeChooser chooser = new ArrayTypeChooser();
                this.choosers.add(chooser);
                if (!insertMode) {
                	chooser.setSelected(params.getInt(i));
                }
                chooser.setEditable(false);
                addToPanel(this.paramPanel, chooser, i);
                break;
            }
            case TYPE_CONSTANT_POOL_CONSTANT: {
                ConstantpoolConstantChooser chooser = new ConstantpoolConstantChooser();
                this.choosers.add(chooser);
                chooser.setConstantPool(this.pool);
                if (!insertMode) {
                	chooser.setSelected(this.pool.get(params.getInt(i)));
                }
                chooser.setEditable(false);
                addToPanel(this.paramPanel, chooser, i);
                break;
            }
            case TYPE_CONSTANT_READONLY:
                // no editor for read-only parameters
                break;
            case TYPE_LABEL:
            	// no editor for labels
            	break;
            case TYPE_SWITCH:
            	// no editor for switches
            	break;
            }
        }
        this.pack();
        this.paramPanel.validate();
        this.paramPanel.repaint();
    }
    
    public boolean wasCancelled() {
    	return this.cancelled;
    }

	public Instruction getInstruction() {
		return this.instruction;
	}

	public List getChoosers() {
		return this.choosers;
	}
	

	public void setPC(int pc) {
		this.position = pc;
	}
	
	public void setClassFile(ClassFile cf) {
		this.cf = cf;
		this.pool = cf.getPool();
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
		this.model.removeAllElements();
		this.model.addElement(instruction);
		this.instructionCombo.setSelectedItem(instruction);
	}

    public void setDecompilationContext(DecompilationContext dc) {
        if (dc != null) {
            this.dc = dc;
            this.lvTable = dc.getLocalVariableTable();
        }
    }

	public void layoutChanged(JComponent c) {
		pack();
	}

}
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
import java.util.ArrayList;
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

import net.sf.rej.gui.editor.FilterListModel;
import net.sf.rej.gui.editor.row.ClassDefRow;
import net.sf.rej.gui.editor.row.EditorRow;
import net.sf.rej.gui.editor.row.FieldDefRow;
import net.sf.rej.gui.editor.row.MethodDefRow;
import net.sf.rej.gui.editor.row.PackageDefRow;
import net.sf.rej.util.Wrapper;

/**
 * A Dialog Window which presents an outline of the class, fields and methods
 * of the open class.
 * 
 * @author Sami Koivu
 */
public class QuickOutlineDialog extends JDialog {
    private JPanel panel = new JPanel();
    JTextField value = new JTextField(16);
    FilterListModel model = null;
    JList list = null;
    JLabel typeCaption = new JLabel("Type: ");
    JLabel typeLabel = new JLabel("");
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    EditorRow selectedValue = null;

    // TODO: delay to the start of filtering
    public QuickOutlineDialog(Frame parent, List<EditorRow> lines) {
        super(parent, "Navigate to..", true);
        initialize(lines);
        setLocationRelativeTo(parent);
    }

    public QuickOutlineDialog(Dialog parent, List<EditorRow> lines) {
        super(parent, "Navigate to..", true);
        initialize(lines);
        setLocationRelativeTo(parent);
    }

	private void initialize(List<EditorRow> lines) {
		List<Object> wrapperItems = new ArrayList<Object>();
		for (EditorRow er : lines) {
			Wrapper<EditorRow> wrapper = new Wrapper<EditorRow>();
			wrapperItems.add(wrapper);
			wrapper.setContent(er);
			if (er instanceof PackageDefRow) {
				PackageDefRow pdr = (PackageDefRow)er;
				wrapper.setDisplay(pdr.getPackage());
			} else if (er instanceof ClassDefRow) {
				ClassDefRow cdr = (ClassDefRow)er;
				wrapper.setDisplay(cdr.getClassFile().getShortClassName());
			} else if (er instanceof FieldDefRow) {
				FieldDefRow fdr = (FieldDefRow)er;
				wrapper.setDisplay(fdr.getField().getName());
			} else if (er instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow)er;
				wrapper.setDisplay(mdr.getMethod().getName());
			} else {
	        	throw new AssertionError("Invalid object type: " + er.getClass());
			}
		}
		
        this.model = new FilterListModel(wrapperItems);
        this.model.setFilter(this.value.getText());
        this.model.filter();
        this.list = new JList(this.model);
        this.list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent lse) {
                updateType();
            }

        });

        this.panel.setLayout(new GridBagLayout());
        this.value.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    done();
                } else {
                    model.setFilter(value.getText());
                    model.filter();
                    list.setSelectedIndex(0);
                    updateType();
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
                selectedValue = null;
                setVisible(false);
            }
        });

        getContentPane().add(this.panel);
        addCenter(this.value, 0, 1, 2);
        addCenter(new JScrollPane(this.list), 0, 2, 2);
        addEast(this.typeCaption, 0, 3);
        addCenter(this.typeLabel, 1, 3);
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
		@SuppressWarnings("unchecked")
    	Wrapper<EditorRow> wrapper = (Wrapper)this.list.getSelectedValue();
        if (wrapper != null) {
            this.selectedValue = wrapper.getContent();
            setVisible(false);
        }
    }

    public EditorRow getSelected() {
        return this.selectedValue;
    }

	public void updateType() {
        if (this.model.getSize() == 0) {
            this.typeLabel.setText("");
        }

        EditorRow er = null;
        @SuppressWarnings("unchecked")
        Wrapper<EditorRow> wrapper = (Wrapper) list.getSelectedValue();
        if (wrapper != null) {
        	er = wrapper.getContent();
        }
        if(er == null) {
            this.typeLabel.setText("");
        } else if (er instanceof PackageDefRow) {
            this.typeLabel.setText("Package Definition");        	
        } else if (er instanceof ClassDefRow) {
            this.typeLabel.setText("Class Definition");        	
        } else if (er instanceof FieldDefRow) {
            this.typeLabel.setText("Field Definition");        	
        } else if (er instanceof MethodDefRow) {
            this.typeLabel.setText("Method Definition");        	
        } else {
        	throw new AssertionError("Invalid object type: " + er.getClass());
        }
    }

}

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
package net.sf.rej.gui.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sf.rej.files.FileSet;
import net.sf.rej.files.Project;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.compare.ComparePanel;
import net.sf.rej.gui.compare.CompareThread;
import net.sf.rej.gui.compare.FileItem;
import net.sf.rej.gui.compare.PackageItem;
import net.sf.rej.gui.compare.Style;
import net.sf.rej.gui.editor.CaseInsensitiveMatcher;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Disassembler;

public class CompareTab extends JPanel implements Tabbable, EventObserver {
	Icon folderPlain;
	Icon folderRed;
	Icon folderYellow;
	Icon folderMixed;
	Icon leafPlain;
	Icon leafRed;
	Icon leafYellow;
	Icon leafMixed;
	
    private static final long serialVersionUID = 1L;
    
    private Action showDiffAction = new AbstractAction("Show Differences") {
    	public void actionPerformed(ActionEvent e) {
            try {
                filesetA = SystemFacade.getInstance().getFileSet(
                        new File(filenameField1.getText()));
                filesetB = SystemFacade.getInstance().getFileSet(
                        new File(filenameField2.getText()));

                tabbedPane.setSelectedComponent(treeScrollPane);
                root.removeAllChildren();
                root = new DefaultMutableTreeNode("Comparison", true);
                model.setRoot(root);
                Thread thread = new Thread(new CompareThread(filesetA, filesetB, tree, root));
                thread.start();
            } catch (Exception ex) {
                SystemFacade.getInstance().handleException(ex);
            }
    	}
    };

    private Action setSet1Action = new AbstractAction("Set File/Folder/Archive..") {
		public void actionPerformed(ActionEvent e) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setDialogTitle("Select file set 1");
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int i = fileChooser.showDialog(CompareTab.this, "Select");
			if (i == JFileChooser.APPROVE_OPTION) {
				filenameField1.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	};

    private Action setSet2Action = new AbstractAction("Set File/Folder/Archive..") {
    	public void actionPerformed(ActionEvent e) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setDialogTitle("Select file set 2");
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int i = fileChooser.showDialog(CompareTab.this, "Select");
			if (i == JFileChooser.APPROVE_OPTION) {
				filenameField2.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
    	}
    };

    private ComparePanel comparePanel = new ComparePanel();
    private JPanel jPanel1 = new JPanel();
    private JPanel jPanel2 = new JPanel();
    private GridBagLayout gridBagLayout2 = new GridBagLayout();
    private GridBagLayout gridBagLayout3 = new GridBagLayout();
    private JLabel set1Label = new JLabel();
    private JTextField filenameField1 = new JTextField();
    private JButton setFileButton1 = new JButton(setSet1Action);
    private JLabel set2Label = new JLabel();
    private JTextField filenameField2 = new JTextField();
    private JButton setFileButton2 = new JButton(setSet2Action);
    private JButton showDiffButton = new JButton(this.showDiffAction);
 
    private JFileChooser fileChooser = new JFileChooser();
    private JScrollPane treeScrollPane = new JScrollPane();
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Comparison", true);
    private DefaultTreeModel model = new DefaultTreeModel(this.root);
    private JTree tree = new JTree(this.model);
    private JTabbedPane tabbedPane = new JTabbedPane();

    private FileSet filesetA;
    private FileSet filesetB;
    
    private FileItem selectedItem = null;
    private String lastQueryString = "";
    private CaseInsensitiveMatcher lastSearch = null;
    
    public CompareTab() {
    	// Load the icons for the tree renderer
    	try {
    		this.folderPlain = new ImageIcon(getClass().getResource("/img/folder-plain.png"));
    		this.folderRed = new ImageIcon(getClass().getResource("/img/folder-red.png"));
    		this.folderYellow = new ImageIcon(getClass().getResource("/img/folder-yellow.png"));
    		this.folderMixed = new ImageIcon(getClass().getResource("/img/folder-mixed.png"));
    		this.leafPlain = new ImageIcon(getClass().getResource("/img/leaf-plain.png"));
    		this.leafRed = new ImageIcon(getClass().getResource("/img/leaf-red.png"));
    		this.leafYellow = new ImageIcon(getClass().getResource("/img/leaf-yellow.png"));
    		this.leafMixed = new ImageIcon(getClass().getResource("/img/leaf-mixed.png"));
    	} catch(NullPointerException npe) {
    		this.folderPlain = new ImageIcon("img/folder-plain.png");
    		this.folderRed = new ImageIcon("img/folder-red.png");
    		this.folderYellow = new ImageIcon("img/folder-yellow.png");
    		this.folderMixed = new ImageIcon("img/folder-mixed.png");
    		this.leafPlain = new ImageIcon("img/leaf-plain.png");
    		this.leafRed = new ImageIcon("img/leaf-red.png");
    		this.leafYellow = new ImageIcon("img/leaf-yellow.png");
    		this.leafMixed = new ImageIcon("img/leaf-mixed.png");    		
    	}

    	
        this.setLayout(new GridBagLayout());
        this.jPanel1.setLayout(this.gridBagLayout2);
        this.jPanel2.setLayout(this.gridBagLayout3);
        this.set1Label.setText("Set 1");
        this.filenameField1.setBackground(new Color(255, 100, 100));
        this.filenameField1.setText("");
        this.set2Label.setText("Set 2");
        this.filenameField2.setBackground(new Color(255, 255, 100));
        this.filenameField2.setText("");
        this.jPanel1.setBorder(BorderFactory.createEtchedBorder());
        this.jPanel2.setBorder(BorderFactory.createEtchedBorder());
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(1, 2));
        grid.add(this.jPanel1);
        grid.add(this.jPanel2);
        this.tree.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		treeClicked(e);
        	}
        });
        this.add(grid, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        this.jPanel1.add(this.set1Label, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        0, 0, 0, 0), 0, 0));
        this.jPanel1.add(this.filenameField1, new GridBagConstraints(1, 1, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        this.jPanel1.add(this.setFileButton1, new GridBagConstraints(0, 1, 1, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        this.jPanel2.add(this.set2Label, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        0, 0, 0, 0), 0, 0));
        this.jPanel2.add(this.filenameField2, new GridBagConstraints(1, 1, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        this.jPanel2.add(this.setFileButton2, new GridBagConstraints(0, 1, 1, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(this.showDiffButton, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        0, 0, 0, 0), 0, 0));
        this.add(this.tabbedPane, new GridBagConstraints(0, 2, 2, 1, 1.0, 2.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));
        this.tabbedPane.addTab("Files", treeScrollPane);
        this.tabbedPane.addTab("Compare", this.comparePanel);
        this.treeScrollPane.getViewport().add(this.tree, null);

        this.tree.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
			public Component getTreeCellRendererComponent(JTree tree,
                    Object value, boolean sel, boolean expanded, boolean leaf,
                    int row, boolean hasFocus) {
                // customize color
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object obj = node.getUserObject();
                if (obj instanceof FileItem) {
                	FileItem fileItem = (FileItem) obj;
                	switch (fileItem.getStyle()) {
    				case PLAIN:
    					this.leafIcon = leafPlain;
    					break;    					
    				case RED:
    					this.leafIcon = leafRed;
    					break;    					
    				case YELLOW:
    					this.leafIcon = leafYellow;
    					break;    					
    				case RED_AND_YELLOW:
    					this.leafIcon = leafMixed;
    					break;    					
    				}
                } else if (obj instanceof PackageItem) {
                	PackageItem pkgItem = (PackageItem) obj;
                	switch (pkgItem.getStyle()) {
    				case PLAIN:
    					this.closedIcon = folderPlain;
    					this.openIcon = folderPlain;
    					break;    					
    				case RED:
    					this.closedIcon = folderRed;
    					this.openIcon = folderRed;
    					break;    					
    				case YELLOW:
    					this.closedIcon = folderYellow;
    					this.openIcon = folderYellow;
    					break;    					
    				case RED_AND_YELLOW:
    					this.closedIcon = folderMixed;
    					this.openIcon = folderMixed;
    					break;    					
    				}
                } else {
					this.closedIcon = folderPlain;
					this.openIcon = folderPlain;                	
                }
                
                if (obj == selectedItem) {
                	setFont(getFont().deriveFont(Font.BOLD));
                } else if (getFont() != null) {
                	setFont(getFont().deriveFont(Font.PLAIN));
                }

                return super.getTreeCellRendererComponent(tree, value, sel,
                        expanded, leaf, row, hasFocus);
            }
            
        });
    }

	public void processEvent(Event event) {
		if (event.getType() == EventType.PROJECT_UPDATE) {
			Project p = event.getProject();
			if (p != null) {
				this.filenameField1.setText(p.getFile().getAbsolutePath());
			} else {
				this.filenameField1.setText("");
			}
		}
    }

    private void treeClicked(MouseEvent e) {
        TreePath selPath = this.tree.getPathForLocation(e.getX(), e.getY());
        if (selPath == null)
            return;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
                .getLastPathComponent();
        Object userObj = node.getUserObject();

        if (e.getClickCount() == 2
                && e.getModifiers() == InputEvent.BUTTON1_MASK) {
            if (userObj instanceof FileItem) {
            	FileItem ca =(FileItem)userObj;
            	if (ca.getFullNameA().endsWith(".class")
            	 && ca.getFullNameB().endsWith(".class")
            	 && ca.getStyle() != Style.RED
            	 && ca.getStyle() != Style.YELLOW) {
            		this.selectedItem = ca;
            		try {
            			byte[] dataA = this.filesetA.getData(ca.getFullNameA());
            			byte[] dataB = this.filesetB.getData(ca.getFullNameB());

            			ClassFile cfA = Disassembler.readClass(dataA);
            			ClassFile cfB = Disassembler.readClass(dataB);
            			this.comparePanel.setClassFiles(cfA, cfB);
            			this.tabbedPane.setSelectedComponent(this.comparePanel);
            		} catch (Exception ex) {
            			SystemFacade.getInstance().handleException(ex);
            		}
            	}
            }
        }
    }

    public void redo() {
        // not applicable for this tab
    }

    public void undo() {
        // not applicable for this tab
    }

    public void insert() {
        // not applicable for this tab
    }

    public void remove() {
        // not applicable for this tab
    }

    public void goTo(Link link) {
        // not applicable for this tab
    }

    public void find() {
    	if (this.tabbedPane.getSelectedComponent() == this.treeScrollPane) {
    		String query = (String)JOptionPane.showInputDialog(this, "Search for..", "Search", JOptionPane.QUESTION_MESSAGE, null, null, this.lastQueryString);
    		if (query == null)
    			return; // early return

    		this.lastQueryString = query;
    		this.lastSearch = new CaseInsensitiveMatcher(query);
    		Enumeration en = root.breadthFirstEnumeration();
    		while (en.hasMoreElements()) {
    			DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
    			Object obj = node.getUserObject();
    			if (obj instanceof FileItem) {
    				FileItem fileItem = (FileItem) obj;
    				String filename = fileItem.getFullNameA();
    				if (this.lastSearch.matches(filename)) {
    					Object[] path = { root, node.getParent(), node };
    					TreePath tp = new TreePath(path);
    					tree.setSelectionPath(tp);
    					tree.startEditingAtPath(tp);
    					SystemFacade.getInstance().setStatus("Found '" + query + "'.");
    					return; // early return
    				}
    			}
    		}

    		this.lastSearch = null;
    		SystemFacade.getInstance().setStatus("No occurances of '" + query + "' found.");		
    	} else {
    		this.comparePanel.find();
    	}
    }

    public void findNext() {
    	if (this.tabbedPane.getSelectedComponent() == this.treeScrollPane) {
    		if (this.lastSearch == null) {
    			find();
    		} else {
    			Enumeration en = root.breadthFirstEnumeration();
    			boolean startSearching = false;
    			while (en.hasMoreElements()) {
    				DefaultMutableTreeNode node = (DefaultMutableTreeNode)en.nextElement();
    				if (!startSearching) {
    					if (node.equals(tree.getSelectionPath().getLastPathComponent())) {
    						startSearching = true;
    					}
    					continue;
    				}
    				Object obj = node.getUserObject();
    				if (obj instanceof FileItem) {
    					FileItem fileItem = (FileItem) obj;
    					String filename = fileItem.getFullNameA();
    					if (this.lastSearch.matches(filename)) {
    						Object[] path = {this.root, node.getParent(), node};
    						TreePath tp = new TreePath(path);
    						tree.setSelectionPath(tp);
    						tree.startEditingAtPath(tp);
    						SystemFacade.getInstance().setStatus("Found '" + this.lastQueryString + "'.");
    						return; // early return
    					}
    				}
    			}
    			SystemFacade.getInstance().setStatus("No more occurances of '" + this.lastQueryString + "' found.");		
    		}
    	} else {
    		this.comparePanel.findNext();
    	}
    }

	public void outline() {
		if (this.tabbedPane.getSelectedComponent() == this.comparePanel) {
			this.comparePanel.outline();
		}
	}

	public void leavingTab() {
	}
	
	public String getTabTitle() {
		return "Compare";
	}

	public void enteringTab() {
	}

}
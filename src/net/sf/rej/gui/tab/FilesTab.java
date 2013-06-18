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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.sf.rej.files.FileSet;
import net.sf.rej.files.Project;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.editor.CaseInsensitiveMatcher;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.java.ClassFile;
import net.sf.rej.util.Wrapper;

/**
 * <code>FilesTab</code> is a GUI tab for displaying the contents of a
 * fileset.
 * 
 * @author Sami Koivu
 */

public class FilesTab extends JPanel implements Tabbable, EventObserver  {
	DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	DefaultTreeModel model = new DefaultTreeModel(root);
	private Project project;
	private String openFile;
	
	private Cursor normalCursor = null;
	private Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);

    JTree contentsTree = new JTree(model);
 
    TreeCellRenderer renderer = new DefaultTreeCellRenderer() {
		@Override
    	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Font f = getFont();
    		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    		Object obj = node.getUserObject();
    		if (obj instanceof Wrapper) {
    	    	@SuppressWarnings("unchecked")
    			Wrapper<String> wrapper = (Wrapper<String>)obj;
    			if (wrapper.getContent().equals(openFile)) {
    				this.setFont(f.deriveFont(Font.BOLD));
    			} else {
    				this.setFont(f.deriveFont(Font.PLAIN));    				
    			}
    		} else {
    			if (f != null) {
    				this.setFont(f.deriveFont(Font.PLAIN));
    			}
    		}
    		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
    				row, hasFocus);
    	}
    };

	JLabel statusLabel = new JLabel();
	JLabel noFileOpenLabel = new JLabel("No file open.", SwingConstants.CENTER);

	FileSet fileSet = null;

	CaseInsensitiveMatcher lastSearch = null;
	private String lastQueryString = "";
	EventDispatcher dispatcher;

	public FilesTab() {
		try {
			this.contentsTree.setCellRenderer(renderer);
			this.noFileOpenLabel.setFont(this.noFileOpenLabel.getFont().deriveFont(16.0f));
			this.setLayout(new BorderLayout());
			this.contentsTree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						selectFile();
					}
				}
			});
			
			this.contentsTree.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						selectFile();
					}
				}
			});
			this.statusLabel.setText("");
			this.add(new JScrollPane(this.contentsTree), BorderLayout.CENTER);
			this.add(this.statusLabel, BorderLayout.NORTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTabTitle() {
		return "Files";
	}

	public void processEvent(Event event) {
		switch (event.getType()) {
		case INIT:
			this.dispatcher = event.getDispatcher();
			break;
		case PROJECT_UPDATE:
			this.project = event.getProject();
			break;
		case CLASS_OPEN:
			this.openFile = event.getFile();
			repaint();
			break;
		case CLASS_UPDATE:
		case CLASS_REPARSE:
		case CLASS_PARSE_ERROR:
		case DISPLAY_PARAMETER_UPDATE:
		case DEBUG_ATTACH:
		case DEBUG_DETACH:
		case DEBUG_RESUMED:
		case DEBUG_SUSPENDED:
		case DEBUG_THREAD_CHANGE_REQUESTED:
		case DEBUG_STEP_INTO_REQUESTED:
		case DEBUG_STEP_OUT_REQUESTED:
		case DEBUG_STEP_OVER_REQUESTED:
		case DEBUG_RESUME_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGE_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGED:
		case DEBUG_SUSPEND_REQUESTED:
		case DEBUG_THREAD_CHANGED:
			// do nothing
			break;
		}

		if (event.getType() == EventType.CLASS_OPEN) {
			String file = event.getFile();
			if (file == null) {
				this.statusLabel.setText("No file selected.");
			} else {
				this.statusLabel.setText("Selected file: " + file);
			}
		}
		
		if (event.getType() == EventType.PROJECT_UPDATE && this.project != null) {	
			this.fileSet = this.project.getFileSet();
			List<String> list = this.fileSet.getContentsList();
			
			this.root = new DefaultMutableTreeNode(this.fileSet.getName());
			this.model.setRoot(this.root);
			
			// get a list of all the packages in the fileset
			Set<String> pkgs = new TreeSet<String>();
			for (String contentFile : list) {
				int index = contentFile.lastIndexOf('/');
				if (index == -1) {
					pkgs.add("");
				} else {
					pkgs.add(contentFile.substring(0, index));
				}
				
			}
			
			// associate each package name with a tree node
			Map<String, DefaultMutableTreeNode> map = new HashMap<String, DefaultMutableTreeNode>(); 
			for (String pkg : pkgs) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(pkg);
				root.add(node);
				map.put(pkg, node);
			}

			// sort
			Set<String> contentFileSet = new TreeSet<String>(list);

			// add each file to the corresponding node
			for (String contentFile : contentFileSet) {
				int index = contentFile.lastIndexOf('/');
				String pkg = "";
				if (index != -1) {
					pkg = contentFile.substring(0, index);
				}
				DefaultMutableTreeNode pkgNode = map.get(pkg);
				Wrapper<String> wrapper = new Wrapper<String>();
				wrapper.setContent(contentFile);
				if (index == -1) {
					wrapper.setDisplay(contentFile);					
				} else {
					wrapper.setDisplay(contentFile.substring(pkg.length()+1));
				}
				DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(wrapper);
				pkgNode.add(fileNode);
			}
			
			// expand the root
			this.contentsTree.expandPath(this.contentsTree.getPathForRow(0));				
			if (pkgs.size() == 1) {
				// only one package, expand it
				this.contentsTree.expandPath(this.contentsTree.getPathForRow(1));
			}
		}

	}
	
	public void selectFile() {
		try {
			this.normalCursor = getCursor();
			setCursor(this.busyCursor);
			TreePath path = this.contentsTree.getSelectionPath();
			DefaultMutableTreeNode node = null;
			if (path != null) {
				node = (DefaultMutableTreeNode) path.getLastPathComponent();
			}
			if (node != null && node.getUserObject() instanceof Wrapper) {
				@SuppressWarnings("unchecked")
				Wrapper<String> wrapper = (Wrapper)node.getUserObject();
				String file = wrapper.getContent();
				try {
					Event event = new Event(EventType.CLASS_OPEN);
					ClassFile cf = this.project.getClassFile(file);
					event.setClassFile(cf);
					event.setFile(file);
					this.dispatcher.notifyObservers(event);
				} catch(Exception ex) {
					SystemFacade.getInstance().handleException(ex);
					this.dispatcher.notifyObservers(new Event(EventType.CLASS_PARSE_ERROR));
				}
			}	
		} finally {
			setCursor(this.normalCursor);
		}
	}

	public void redo() {
		SystemFacade.getInstance().performProjectRedo();
	}

	public void undo() {
		SystemFacade.getInstance().performProjectUndo();
	}

	public void insert() {
	}

	@SuppressWarnings("unchecked")
	public void remove() {
		TreePath[] tps = this.contentsTree.getSelectionPaths();
		if (tps != null) {
			List<String> fileList = new ArrayList<String>();
			for (TreePath path : tps) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				Object obj = node.getUserObject();
				if (obj instanceof String) {
					Enumeration en = node.children();
					while (en.hasMoreElements()) {
						DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
						obj = child.getUserObject();
						if (obj instanceof Wrapper) {
							Wrapper<String> wrapper = (Wrapper<String>)obj;
							fileList.add(wrapper.getContent());
						}
					}
				} else {
					Wrapper<String> wrapper = (Wrapper<String>)obj;
					fileList.add(wrapper.getContent());
				}
				
			}
			
			if (fileList.size() > 0) {
				SystemFacade.getInstance().removeFile(fileList);
			}
		}
	}

	public void goTo(Link link) {
	}

	public void find() {
		String query = (String)JOptionPane.showInputDialog(this, "Search for..", "Search", JOptionPane.QUESTION_MESSAGE, null, null, this.lastQueryString);
		if (query == null)
			return; // early return

		this.lastQueryString = query;
		this.lastSearch = new CaseInsensitiveMatcher(query);
		Enumeration en = this.root.breadthFirstEnumeration();
		while (en.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
			Object obj = node.getUserObject();
			if (obj instanceof Wrapper) {
				@SuppressWarnings("unchecked")
				Wrapper<String> wrapper = (Wrapper<String>) obj;
				String filename = wrapper.getContent();
				if (this.lastSearch.matches(filename)) {
					Object[] path = { FilesTab.this.root, node.getParent(),
							node };
					TreePath tp = new TreePath(path);
					contentsTree.setSelectionPath(tp);
					contentsTree.startEditingAtPath(tp);
					SystemFacade.getInstance().setStatus("Found '" + query + "'.");
					return; // early return
				}
			}
		}

		this.lastSearch = null;
		SystemFacade.getInstance().setStatus("No occurances of '" + query + "' found.");		
	}

	public void findNext() {
		if (this.lastSearch == null) {
			find();
		} else {
			Enumeration en = this.root.breadthFirstEnumeration();
			boolean startSearching = false;
			while (en.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)en.nextElement();
				if (!startSearching) {
					if (node.equals(this.contentsTree.getSelectionPath().getLastPathComponent())) {
						startSearching = true;
					}
					continue;
				}
				Object obj = node.getUserObject();
				if (obj instanceof Wrapper) {
					@SuppressWarnings("unchecked")
					Wrapper<String> wrapper = (Wrapper<String>)obj;
					String filename = wrapper.getContent();
					if (this.lastSearch.matches(filename)) {
						Object[] path = {this.root, node.getParent(), node};
						TreePath tp = new TreePath(path);
						this.contentsTree.setSelectionPath(tp);
						this.contentsTree.startEditingAtPath(tp);
						SystemFacade.getInstance().setStatus("Found '" + this.lastQueryString + "'.");
						return; // early return
					}
				}
			}
			SystemFacade.getInstance().setStatus("No more occurances of '" + this.lastQueryString + "' found.");		
		}
	}

	public void outline() {
	}

	public void leavingTab() {
		// TODO Auto-generated method stub
		
	}

	public void enteringTab() {
	}

}
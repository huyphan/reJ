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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.split.StructureSplitSynchronizer;
import net.sf.rej.gui.structure.ClassFileNode;
import net.sf.rej.gui.structure.StructureNode;
import net.sf.rej.java.ClassFile;
import net.sf.rej.util.Range;

public class StructureTab extends JPanel implements Tabbable, EventObserver {
    private JTree tree = new JTree();
    private ClassFile cf = null;
    private boolean isOpen = false;
    private boolean upToDate = false;
	private StructureSplitSynchronizer sync;
	private Map<Object, Range> offsets;

    public StructureTab() {
        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(this.tree), BorderLayout.CENTER);
        
        this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				splitSynchronize();
			}
        });

        this.tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON3) {
					// right click
					TreePath path = tree.getPathForLocation(me.getX(), me.getY());
					if (path == null) {
						return; // early return
					}
					
					Object obj = path.getLastPathComponent();
					if (obj instanceof StructureNode) {
						StructureNode sn = (StructureNode) obj;
						JPopupMenu menu = sn.getContextMenu();
						if (menu != null) {
							menu.show(tree, me.getX(), me.getY());
						}
					}
				}
			}
		});

    }
    
    public void refresh() {
		if (this.cf != null) {
			StructureNode root = new ClassFileNode(this.cf);
			this.tree.setModel(new DefaultTreeModel(root));
			this.offsets = this.cf.getOffsetMap();
			if (this.sync != null) {
				this.sync.setOffsets(this.offsets);
				splitSynchronize();
			}
		}
		this.upToDate = true;
    }

    public void redo() {
        EditorFacade.getInstance().performRedo();
    }

    public void undo() {
        EditorFacade.getInstance().performUndo();
    }

    public void insert() {
    }

    public void remove() {
    }

    public void goTo(Link link) {
    }

    public void find() {
    }

    public void findNext() {
    }

	public void processEvent(Event event) {
        try {
        	if (event.getType() == EventType.CLASS_OPEN || event.getType() == EventType.CLASS_REPARSE) {
        		this.cf = event.getClassFile();
        	}
        	
        	if (event.getType() == EventType.CLASS_PARSE_ERROR) {
    			this.tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Class parse error.")));
        	}
        	
        	if (event.getType() == EventType.CLASS_OPEN || event.getType() == EventType.CLASS_UPDATE || event.getType() == EventType.CLASS_REPARSE) {
        		this.upToDate = false;
        		if (this.isOpen) {
        			refresh();
        		}
            }
        } catch(Exception e) {
            SystemFacade.getInstance().handleException(e);
        }
    }

	public void outline() {
	}

	public void leavingTab() {
		this.isOpen = false;
	}

	public String getTabTitle() {
		return "Structure";
	}

	public void enteringTab() {
		this.isOpen = true;
		if (!this.upToDate) {
			refresh();
		}
		splitSynchronize();
	}

	public void setSplitSynchronizer(StructureSplitSynchronizer sync) {
		this.sync = sync;
		this.sync.setOffsets(this.offsets);
		splitSynchronize();
	}
	
	private void splitSynchronize() {
		if (this.sync != null && isOpen) {
			TreePath path = this.tree.getSelectionPath();
			if (path != null) {
				StructureNode node = (StructureNode) path.getLastPathComponent();
				this.sync.sync(node);
			}
		}
	}

}

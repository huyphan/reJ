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

import javax.swing.JPanel;

import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.hexeditor.ByteArrayDataProvider;
import net.sf.rej.gui.hexeditor.HexEditorPanel;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Disassembler;

public class HexEditorTab extends JPanel implements Tabbable, EventObserver {
    private ClassFile cf = null;
    private byte[] data = null;
    private HexEditorPanel hexEditor = null;
    private EventDispatcher dispatcher = null;
    boolean modified;
    private boolean isOpen = false;
    private boolean upToDate = false;

    public HexEditorTab() {
        this.setLayout(new BorderLayout());
    }
    
    public HexEditorPanel getHexEditor() {
    	return this.hexEditor;
    }

    public void redo() {
        EditorFacade.getInstance().performUndo();
    }

    public void undo() {
        EditorFacade.getInstance().performRedo();
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

    public String getTabTitle() {
		return "Hex Editor";
	}

	public void processEvent(Event event) {
        try {
        	if (event.getType() == EventType.INIT) {
        		this.dispatcher = event.getDispatcher();
        	}
        	
        	if (event.getType() == EventType.CLASS_OPEN) {
        		this.cf = event.getClassFile();
        	}
        	
        	if (event.getType() == EventType.CLASS_OPEN || event.getType() == EventType.CLASS_UPDATE) {
        		this.upToDate = false;
        		if (isOpen) {
        			refresh();
        		}
            }
        } catch(Exception e) {
            SystemFacade.getInstance().handleException(e);
        }
    }
	
	public void refresh() {
		if (this.cf != null) {
			// TODO: update the hex panel instead of creating a new one
			if (this.hexEditor != null) {
				this.remove(this.hexEditor);
			}
			this.data = this.cf.getData(); 
			ByteArrayDataProvider badp = new ByteArrayDataProvider(this.data) {
				@Override
				public void set(int index, byte value) {
					// TODO: this should be made an undoable event
					super.set(index, value);
					HexEditorTab.this.modified = true;
				}
			};
			this.modified = false;
			this.hexEditor = new HexEditorPanel(badp, 16, false);
			this.add(this.hexEditor, BorderLayout.CENTER);
			this.validate();
		}
		
		this.upToDate = true;
	}

	public void outline() {
	}

	public void leavingTab() {
		this.isOpen = false;
		
		if (this.modified) {
			this.modified = false;
			// TODO: recover from a parsing error - invalid class
			try {
				this.cf = Disassembler.readClass(this.data);
				Event event = new Event(EventType.CLASS_REPARSE);
				event.setClassFile(this.cf);
				this.dispatcher.notifyObservers(event);
			} catch (Exception e) {
				SystemFacade.getInstance().handleException(e);
				this.dispatcher.notifyObservers(new Event(EventType.CLASS_PARSE_ERROR));
			}
		}	
	}

	public void enteringTab() {
		this.isOpen = true;
		
		if (!this.upToDate) {
			refresh();
		}
	}

}

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
package net.sf.rej.gui.split;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.hexeditor.ByteArrayDataProvider;
import net.sf.rej.gui.hexeditor.HexEditorPanel;
import net.sf.rej.java.ClassFile;

public class HexSplit extends JPanel implements EventObserver {
	
	private boolean active = false;
	private ClassFile cf = null;
	private HexEditorPanel hexEditor = null;
	
	public HexSplit() {
		setLayout(new BorderLayout());
	}

	public void processEvent(Event event) {
        try {
        	if (event.getType() == EventType.CLASS_OPEN) {
        		this.cf = event.getClassFile();
        	}
        	
        	if (event.getType() == EventType.CLASS_OPEN || event.getType() == EventType.CLASS_UPDATE) {
        		if (this.active) {
        			refresh();
        		}
            }
        } catch(Exception e) {
            SystemFacade.getInstance().handleException(e);
        }
	}
	
	public void setActive(boolean active) {
		this.active = active;
		if (active) {
			refresh();
		}
	}

	public void refresh() {
		if (this.cf != null) {
			// TODO: update the hex panel instead of creating a new one
			if (this.hexEditor != null) {
				this.remove(this.hexEditor);
			}
			final byte[] data = this.cf.getData(); 
			ByteArrayDataProvider badp = new ByteArrayDataProvider(data);
			this.hexEditor = new HexEditorPanel(badp, 16, true);
			this.add(this.hexEditor, BorderLayout.CENTER);
			this.validate();
		}
	}

	public HexEditorPanel getHexEditor() {
		return this.hexEditor;
	}

}

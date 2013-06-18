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
package net.sf.rej.gui.hexeditor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

/**
 * A MouseListener component for the HexEditor which translates user
 * actions into the changes in the selection.
 * 
 * @author Sami Koivu
 */
public class HexEditorMouseListener implements MouseListener {
	
	private SelectionModel selectionModel;
	private HexEditorCellRenderer renderer;
	private int width;

	public HexEditorMouseListener(SelectionModel selectionModel, HexEditorCellRenderer renderer) {
		this.selectionModel = selectionModel;
		this.renderer = renderer;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		JList list = (JList) e.getComponent();
		int clickedIndex = list.locationToIndex(e.getPoint());
		int index = this.renderer.getIndex(clickedIndex, e);
		this.selectionModel.setSelectedIndex(index);
		list.setSelectedIndex(index / this.width);
		list.ensureIndexIsVisible(index / this.width);
		list.repaint();
		e.consume();		
	}

	public void mouseReleased(MouseEvent e) {
		JList list = (JList) e.getComponent();
		int clickedIndex = list.locationToIndex(e.getPoint());
		int index = this.renderer.getIndex(clickedIndex, e);
		this.selectionModel.setSelectedIndex(index);
		list.setSelectedIndex(index / this.width);
		list.ensureIndexIsVisible(index / this.width);
		list.repaint();
		e.consume();		
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

}

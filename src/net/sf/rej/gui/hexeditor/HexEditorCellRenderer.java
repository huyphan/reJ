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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * The custom list cell renderer which is the core of the Hex Editor.
 * The overridden paint method uses the drawer to output the graphics
 * according ot the DataRow objects contained in the list.
 * 
 * @author Sami Koivu
 */
public class HexEditorCellRenderer extends DefaultListCellRenderer {
	
	private DataRow row = null;
	private SelectionModel selectionModel = null;
	private int width = 0;
	private int hexStart = 0;
	private int asciiStart = 0;
	private int charWidth = 0;
	private String contentLengthString = " ";

	public HexEditorCellRenderer(SelectionModel selectionModel) {
		this.selectionModel = selectionModel;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		this.row = (DataRow) value;
		return super.getListCellRendererComponent(list, contentLengthString, index, isSelected,
				cellHasFocus);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Drawer hd = new Drawer(g, getSize());

		hd.draw(this.row.getOffsetString());
		
		hd.draw(": ");
		this.hexStart = hd.getOffset(); 
		for (int i=0; i < width; i++) {
			if (row.isDataAvailable(i)) {
				hd.draw(row.getHexData(i), this.selectionModel.isSelected(row.getAbsolute(i)), this.selectionModel.getHexSelection());
				hd.draw(" ");
			} else {
				hd.draw("   ");
			}
		}
		
		this.asciiStart = hd.getOffset();
		this.charWidth = (this.asciiStart - this.hexStart) / (this.width * 3);
		for (int i=0; i < width; i++) {
			if (row.isDataAvailable(i)) {
				hd.draw(String.valueOf(row.getAsciiData(i)), this.selectionModel.isSelected(row.getAbsolute(i)), !this.selectionModel.getHexSelection());
			} else {
				hd.draw(" ");
			}
		}
	}

	public void setWidth(int width) {
		this.width = width;
		StringBuilder sb = new StringBuilder();
		sb.append("        ");
		for (int i=0; i < width; i++) {
			sb.append("   ");
		}
		for (int i=0; i < width; i++) {
			sb.append(" ");
		}
		this.contentLengthString = sb.toString();
	}

	public int getIndex(int clickedIndex, MouseEvent e) {
		if (clickedIndex < 0 ) {
			return -1; // early return
		}
		
		int base = clickedIndex * this.width;
		if (e.getPoint().x < this.hexStart) {
			return base;
		} else if (e.getPoint().x < this.asciiStart) {
			int index = (e.getPoint().x - this.hexStart) / (this.charWidth * 3);
			return base + index;
		} else {
			int index = (e.getPoint().x - this.asciiStart) / this.charWidth;
			if (index < this.width) {
				return base + index;
			}
		}
		
		return -1;
	}
}

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

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A Hex Editor component (<code>JPanel</code> subclass) for
 * viewing and or editing binary data.
 * 
 * The presentation of the data is more or less following:
 * <code>000000: ca fe ba be 30 31 32 33 ....0123</code>
 * 
 * The number of bytes displayed on each row can be defined, in that
 * example the width is 8 bytes.
 * 
 * @author Sami Koivu
 */
public class HexEditorPanel extends JPanel {
	
	private DefaultListModel model = new DefaultListModel();
	private JList list = new JList(model);
	private DataProvider data;
	private SelectionModel selectionModel = new SelectionModel();
	private HexEditorCellRenderer renderer = new HexEditorCellRenderer(selectionModel);
	private HexEditorKeyListener keyListener = new HexEditorKeyListener(selectionModel);
	private HexEditorMouseListener mouseListener = new HexEditorMouseListener(selectionModel, renderer);
	private boolean readOnly;
	private int width;

	/**
	 * Initializes the EditorPanel with the given byte array as the
	 * data, setting the row width (in bytes) and readonly-mode.
	 * @param data the data to be displayed and or edited.
	 * @param width how many bytes to display on a row.
	 * @param readOnly true to just display the values and not allow
	 * editing.
	 */
	public HexEditorPanel(final byte[] data, int width, boolean readOnly) {
		this(new ByteArrayDataProvider(data), width, readOnly);
	}
	
	/**
	 * Initializes the EditorPanel with the given <codeDataProvider
	 * </code> as the data, setting the row width (in bytes) and
	 * readonly-mode.
	 * @param data the data to be displayed and or edited.
	 * @param width how many bytes to display on a row.
	 * @param readOnly true to just display the values and not allow
	 * editing.
	 */
	public HexEditorPanel(DataProvider data, int width, boolean readOnly) {
		super();
		this.list.setFont(Drawer.PLAIN);
		this.readOnly = readOnly;
		this.width = width;
		
		this.selectionModel.setSize(data.getSize());
		
		this.renderer.setWidth(width);
		this.list.setCellRenderer(this.renderer);
		
		this.data = data;
		
		setLayout(new BorderLayout());
		add(new JScrollPane(this.list), BorderLayout.CENTER);
		
		for (int i=0; i < this.data.getSize(); i += width) {
			DataRow row = new DataRow(this.data, i);
			this.model.addElement(row);
		}

		// Needed to be able to abuse the TAB key
		this.list.setFocusTraversalKeysEnabled(false); 
		this.keyListener.setDataProvider(this.data);
		this.keyListener.setReadOnly(this.readOnly);
		this.keyListener.setWidth(width);
		this.list.addKeyListener(this.keyListener);
		this.mouseListener.setWidth(width);
		this.list.addMouseListener(this.mouseListener);
	}
	
	/**
	 * Returns the selection model which can be used to querying
	 * and setting the current selection or cursor position of
	 * the editor.
	 * @return a selection model object.
	 */
	public SelectionModel getSelectionModel() {
		return this.selectionModel;
	}
	
	/**
	 * Scrolls the viewport to make the first selected index completely visible.
	 */
	public void ensureSelectionIsVisible() {
		int index = this.selectionModel.getMinimumIndex();
		
		if (index != -1) {
			this.list.ensureIndexIsVisible(this.selectionModel.getMaximumIndex() / this.width);
			this.list.ensureIndexIsVisible(index / this.width);
		}
	}

}

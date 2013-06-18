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
package net.sf.rej.gui.editor.transfer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class BytecodeEditorTransferHandler extends TransferHandler {
	DataFlavor plainFlavor;
	DataFlavor htmlFlavor;
	DataFlavor objectFlavor;
	DataFlavor[] flavors = null;

	TransferComponent transferComponent;
	
	String plainSelection = "";
	String htmlSelection = "";
	Object data;
	
	private Transferable transferable = new Transferable() {

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (flavor.equals(plainFlavor)) {
				return new ByteArrayInputStream(plainSelection.getBytes(flavor.getParameter("charset")));				
			} else if (flavor.equals(htmlFlavor)) {
				return htmlSelection;
			} else if (flavor.equals(objectFlavor)) {
				return data;
			} else {
				throw new AssertionError("Invalid flavor: " + flavor);
			}
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			for (DataFlavor df : flavors) {
				if (df.equals(flavor)) {
					return true;
				}
			}
			
			return false;
		}
		
	};

	public BytecodeEditorTransferHandler(TransferComponent transferComponent) {
		this.transferComponent = transferComponent;
		try {
			plainFlavor = DataFlavor.getTextPlainUnicodeFlavor();
			htmlFlavor = new DataFlavor("text/html; class=java.lang.String", "Rich text (HTML");
			objectFlavor = new DataFlavor(Object.class, "Special transfer object(s)");
			this.flavors = new DataFlavor[] {plainFlavor, htmlFlavor, objectFlavor};
		} catch(Exception cnfe) {
			cnfe.printStackTrace();
		}
	}
	
	@Override
	public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
		this.plainSelection = transferComponent.getSelectionPlainText();
		this.htmlSelection = transferComponent.getSelectionHTML();
		this.data = transferComponent.getSelectionObject();
		clip.setContents(this.transferable, null);
	}
	
	@Override
	public boolean importData(JComponent comp, Transferable t) {
		try {
			Object data = t.getTransferData(objectFlavor);
			if (data != null) {
				this.transferComponent.pasteRows(data);
				return true;
			}
		} catch(UnsupportedFlavorException e) {
			// do nothing
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}

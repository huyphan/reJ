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

import java.util.Map;

import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.util.Range;

public class ConstantPoolToHexSync implements ConstantPoolSplitSynchronizer {
	
	private HexSplit hexEditor;
	private Map<Object, Range> offsets = null;

	public ConstantPoolToHexSync(HexSplit hexSplit) {
		this.hexEditor = hexSplit;
	}

	public void setOffsets(Map<Object, Range> offsets) {
		this.offsets = offsets;
	}

	public void sync(ConstantPoolInfo cpi) {
		if (cpi == null) {
			this.hexEditor.getHexEditor().getSelectionModel().clearSelection();			
		} else {
			Range range = this.offsets.get(cpi);
			this.hexEditor.getHexEditor().getSelectionModel().setSelectedInverval(range.getOffset(), range.getOffset() + range.getSize());			
		}
		this.hexEditor.repaint();
		this.hexEditor.getHexEditor().ensureSelectionIsVisible();
	}

}

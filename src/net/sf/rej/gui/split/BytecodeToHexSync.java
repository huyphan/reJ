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

import java.util.HashMap;
import java.util.Map;

import net.sf.rej.gui.editor.row.CodeRow;
import net.sf.rej.gui.editor.row.EditorRow;
import net.sf.rej.gui.editor.row.FieldDefRow;
import net.sf.rej.gui.editor.row.MethodDefRow;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.java.instruction.DecompilationContext;
import net.sf.rej.util.Range;

public class BytecodeToHexSync implements BytecodeSplitSynchronizer {
	HexSplit hexEditor = null;
	private Map<Object, Range> offsets = null;
	private Map<Method, Map<Object, Range>> methodOffsets = null;
	
	public BytecodeToHexSync(HexSplit hexEditor) {
		this.hexEditor = hexEditor;
	}
	
	public void setOffsets(Map<Object, Range> offsets) {
		this.offsets = offsets;
		this.methodOffsets = new HashMap<Method, Map<Object, Range>>();
	}

	public void sync(EditorRow er) {
		if (er == null) {
			this.hexEditor.getHexEditor().getSelectionModel().clearSelection();
		} else {
			if (er instanceof FieldDefRow) {
				FieldDefRow fdr = (FieldDefRow) er;
				Range offset = this.offsets.get(fdr.getField());
				this.hexEditor.getHexEditor().getSelectionModel().setSelectedInverval(offset.getOffset(), offset.getOffset() + offset.getSize());
			} else if (er instanceof MethodDefRow) {
				MethodDefRow mdr = (MethodDefRow) er;
				Range offset = this.offsets.get(mdr.getMethod());
				this.hexEditor.getHexEditor().getSelectionModel().setSelectedInverval(offset.getOffset(), offset.getOffset() + offset.getSize());					
			} else if (er instanceof CodeRow) {
				CodeRow cr = (CodeRow) er;
				Method m = cr.getEnclosingMethodDef().getMethod();
				CodeAttribute ca = m.getAttributes().getCode();
				// "cache" method offset maps
				Map<Object, Range> methodOffsetMap = this.methodOffsets.get(m);
				if (methodOffsetMap == null) {
					methodOffsetMap = m.getOffsetMap();
					this.methodOffsets.put(m, methodOffsetMap);
				}
				Range range = methodOffsetMap.get(ca);
				int offset = this.offsets.get(m).getOffset() +  range.getOffset() + 14 + cr.getPosition();
				DecompilationContext dc = ca.getCode().createDecompilationContext();
				dc.setPosition(cr.getPosition());
				int size = cr.getInstruction().getSize(dc);
				
				this.hexEditor.getHexEditor().getSelectionModel().setSelectedInverval(offset, offset + size);					
			} else {
				this.hexEditor.getHexEditor().getSelectionModel().clearSelection();
			}
		}
			
		this.hexEditor.repaint();
		this.hexEditor.getHexEditor().ensureSelectionIsVisible();
	}
}

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
package net.sf.rej.gui.editor.row;

import net.sf.rej.java.JavaType;
import net.sf.rej.java.LocalVariable;

/**
 * A byte code editor row for local variable definitions.
 * 
 * @author Sami Koivu
 */
public class LocalVariableDefRow implements EditorRow {

	private LocalVariable lv;
	private MethodDefRow method;

	public LocalVariableDefRow(LocalVariable lv, MethodDefRow enclosingMethod) {
		this.lv = lv;
		this.method = enclosingMethod;
	}

	public LocalVariable getLocalVariable() {
		return this.lv;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
        JavaType ret = this.lv.getDescriptor().getReturn();
        sb.append("        ");
        sb.append(ret.getType());
        sb.append(ret.getDimensions() + " " + lv.getName() + " (#" + lv.getIndex() + " " + lv.getStartPc() + " - " + lv.getEndPc() + ")");
        
        return sb.toString();
	}

    public MethodDefRow getEnclosingMethodDef() {
        return this.method;
    }

}

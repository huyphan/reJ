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
package net.sf.rej.gui.action;

import net.sf.rej.gui.Undoable;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;

public class RemoveLastConstantPoolInfo implements Undoable {
	
	private ConstantPool cp;
	private ConstantPoolInfo cpi = null;
	
	public RemoveLastConstantPoolInfo(ConstantPool cp) {
		this.cp = cp;
		this.cpi = cp.get(cp.size() - 1);
	}

	public void execute() {
		this.cp.removeLast();
	}

	public void undo() {
		this.cp.forceAdd(this.cpi);
	}

}

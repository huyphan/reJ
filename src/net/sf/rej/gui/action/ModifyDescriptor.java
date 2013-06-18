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
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.DescriptorEnabled;

public class ModifyDescriptor implements Undoable {
	private ConstantPool cp;
	private Descriptor desc;
	private DescriptorEnabled descEnabled;
	
	private int oldIndex;
	
	private int createdIndex = -1;
	
	public ModifyDescriptor(ConstantPool cp, Descriptor desc, DescriptorEnabled descEnabled) {
		this.cp = cp;
		this.desc = desc;
		this.descEnabled = descEnabled;
		
		this.oldIndex = this.descEnabled.getDescriptorIndex();
	}

	public void execute() {
		String raw = this.desc.getRawDesc();
		int descIndex = this.cp.indexOfUtf8(raw);
		if (descIndex == -1) {
			descIndex = this.cp.optionalAddUtf8(raw);
			this.createdIndex = descIndex;
		}
		this.descEnabled.setDescriptorIndex(descIndex);
	}

	public void undo() {
		if (this.createdIndex != -1) {
			this.cp.removeLast();
		}
		
		this.descEnabled.setDescriptorIndex(this.oldIndex);
	}

}

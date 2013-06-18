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
package net.sf.rej.java;

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;

public class Interface {

	private int nameIndex;
	private ConstantPool cp;

	public Interface(int nameIndex, ConstantPool cp) {
		this.nameIndex = nameIndex;
		this.cp = cp;
	}

	public int getNameIndex() {
		return this.nameIndex;
	}

	public String getName() {
		ConstantPoolInfo cpi = this.cp.get(this.nameIndex);

		return cpi.getValue();
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Interface) {
			Interface iObj = (Interface)obj;
			return iObj.getName().equals(getName());
		}
		return false; 
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}

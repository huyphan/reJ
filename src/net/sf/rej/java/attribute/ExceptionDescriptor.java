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
package net.sf.rej.java.attribute;

import net.sf.rej.java.constantpool.ConstantPool;

public class ExceptionDescriptor {
	private int index;
	private ConstantPool cp;

	public ExceptionDescriptor(ConstantPool pool, int index) {
		this.cp = pool;
		this.index = index;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return this.cp.get(this.index).getValue();
	}

	public int getIndex() {
		return this.index;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExceptionDescriptor)) {
			return false;
		}
		ExceptionDescriptor ex = (ExceptionDescriptor) obj; 
		return ex.getName().equals(getName());
	}
}
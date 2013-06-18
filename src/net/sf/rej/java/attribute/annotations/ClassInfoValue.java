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
package net.sf.rej.java.attribute.annotations;

import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPool;

public class ClassInfoValue implements ElementValue {
	
	private int classInfoIndex;
	private ConstantPool pool;

	public ClassInfoValue(int classInfoIndex, ConstantPool pool) {
		this.classInfoIndex = classInfoIndex;
		this.pool = pool;
	}
	
	public String getValue() {
		ClassInfo ci = (ClassInfo)this.pool.get(this.classInfoIndex);
		return ci.getName();
	}

}

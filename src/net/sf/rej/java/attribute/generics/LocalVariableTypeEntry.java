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
package net.sf.rej.java.attribute.generics;

import net.sf.rej.java.constantpool.ConstantPool;

public class LocalVariableTypeEntry {
	
	private ConstantPool pool;
	private int startPc;
	private int length;
	private int nameIndex;
	private int signatureIndex;
	private int index;

	public LocalVariableTypeEntry(ConstantPool pool, int startPc, int length, int nameIndex, int signatureIndex, int index) {
		this.pool = pool;
		this.startPc = startPc;
		this.length = length;
		this.nameIndex = nameIndex;
		this.signatureIndex = signatureIndex;
		this.index = index;
	}
	
	public String getSignatureString() {
		return this.pool.get(this.signatureIndex).getValue();
	}
	
	public int getStartPc() {
		return this.startPc;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public String getName() {
		return this.pool.get(this.nameIndex).getValue();
	}
	
	public int getIndex() {
		return this.index;
	}

}

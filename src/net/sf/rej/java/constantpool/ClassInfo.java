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
package net.sf.rej.java.constantpool;

import net.sf.rej.java.Descriptor;
import net.sf.rej.util.ByteSerializer;

public class ClassInfo extends ConstantPoolInfo {

	/**
	 * Pointer to a UTF8Info entry in the constant pool. The package elements of the class name are separated with '/' instead of '.'
	 */
    private int nameIndex;

    public ClassInfo(int nameIndex, ConstantPool pool) {
        super(CLASS, pool);
        this.nameIndex = nameIndex;
    }

    @Override
	public String toString() {
        return "(class) nameindex " + this.nameIndex + "("
                + this.pool.get(this.nameIndex) + ")";
    }

    public String getName() {
        String name = this.pool.get(this.nameIndex).getValue()
                .replace('/', '.');
        if (name.startsWith("[")) {
            Descriptor desc = new Descriptor(name);
            return desc.getReturn().toString();
        } else {
            return name;
        }
    }
    
    public int getNameIndex() {
    	return this.nameIndex;
    }
    
    public void setNameIndex(int index) {
    	this.nameIndex = index;
    }
    
    @Override
	public byte[] getData() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addByte(getType());
        ser.addShort(this.nameIndex);

        return ser.getBytes();
    }

    @Override
	public int hashCode() {
        return getName().hashCode();
    }

    @Override
	public String getValue() {
        return getName();
    }

    @Override
	public boolean equals(Object other) {
        if (other == null) return false;
        try {
            return getName().equals(((ClassInfo) other).getName());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
	public String getTypeString() {
        return "Class reference";
    }
}

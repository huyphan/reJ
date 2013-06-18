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

import net.sf.rej.util.ByteSerializer;

/**
 * <code>IntegerInfo</code> class models an integer constant item in the constant pool.
 * 
 * @author Sami Koivu
 */
public class IntegerInfo extends ConstantPoolInfo {
    private int value;

    public IntegerInfo(int value, ConstantPool pool) {
        super(INTEGER, pool);
        this.value = value;
    }

    @Override
	public String toString() {
        return "(int) " + String.valueOf(this.value);
    }

    @Override
	public byte[] getData() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addByte(getType());
        ser.addInt(this.value);

        return ser.getBytes();
    }

    @Override
	public int hashCode() {
        return this.value;
    }

    @Override
	public boolean equals(Object other) {
        if (other == null) return false;

        try {
            return this.value == ((IntegerInfo) other).value;
        } catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
	public String getTypeString() {
        return "Integer constant";
    }

    public int getIntValue() {
        return this.value;
    }

    public void setIntValue(int value) {
        this.value = value;
    }

}

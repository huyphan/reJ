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

public class FloatInfo extends ConstantPoolInfo {
    private int bytes;

    public FloatInfo(int bytes, ConstantPool pool) {
        super(FLOAT, pool);
        this.bytes = bytes;
    }
    
    public FloatInfo(float value, ConstantPool pool) {
        super(FLOAT, pool);
        setFloatValue(value);
	}

    @Override
	public String toString() {
        return "(float) " + String.valueOf(this.getFloatValue());
    }

    @Override
	public byte[] getData() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addByte(getType());
        ser.addInt(this.bytes);

        return ser.getBytes();
    }

    @Override
	public int hashCode() {
        return this.bytes;
    }

    @Override
	public boolean equals(Object other) {
        if (other == null) return false;

        try {
            return this.bytes == ((FloatInfo) other).bytes;
        } catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
	public String getTypeString() {
        return "Float constant";
    }

    public float getFloatValue() {
    	return Float.intBitsToFloat(this.bytes);
    	/* old, 'manual' implementation - has some slight problems
        int s = ((bytes >> 31) == 0) ? 1 : -1;
        int e = ((bytes >> 23) & 0xff);
        int m = (e == 0) ? (bytes & 0x7fffff) << 1 : (bytes & 0x7fffff) | 0x800000;

        // value = s x m x 2pow(e-150)
        float value = (s * m) * (float) Math.pow(2f, e - 150f);
        return value;
         */
    }

    public void setFloatValue(float value) {
    	this.bytes = Float.floatToRawIntBits(value);
    }
    
    public int getBytes() {
    	return this.bytes;
    }

}
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
 * <code>DoubleInfo</code> objects are double info entries in the constant
 * pool.
 * 
 * @author Sami Koivu
 */

public class DoubleInfo extends ConstantPoolInfo {
	private long highBytes;

	private long lowBytes;

	public DoubleInfo(long highBytes, long lowBytes, ConstantPool pool) {
		super(DOUBLE, pool);
		this.highBytes = highBytes;
		this.lowBytes = lowBytes;
	}
	
	public DoubleInfo(double value, ConstantPool pool) {
		super(DOUBLE, pool);
		setDoubleValue(value);
	}

	@Override
	public String toString() {
		return "(double) " + String.valueOf(getDoubleValue());
	}

	@Override
	public byte[] getData() {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(getType());
		ser.addInt(this.highBytes);
		ser.addInt(this.lowBytes);

		return ser.getBytes();
	}

	@Override
	public int hashCode() {
		return (int) this.highBytes +  (int) this.lowBytes;
	}

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof DoubleInfo) {
			return this.lowBytes == ((DoubleInfo) other).lowBytes && this.highBytes == ((DoubleInfo)other).highBytes;
		} else {
			return false;
		}
	}

	@Override
	public String getTypeString() {
		return "Double constant";
	}

	public double getDoubleValue() {
		long bits = (highBytes << 32l) + lowBytes;
		return Double.longBitsToDouble(bits);
		/* old, 'manual' implementation - has some problems
		long s = ((bits >> 63) == 0) ? 1 : -1;
		long e = (int) ((bits >> 52) & 0x7ffL);
		long m = (e == 0) ? (bits & 0xfffffffffffffL) << 1
				: (bits & 0xfffffffffffffL) | 0x10000000000000L;

		// value = s x m x 2 pow (1075-e)
		double value = (s * m) * Math.pow(2, e - 1075);
		
		return value;
		 */
	}

	public void setDoubleValue(double newValue) {
		long raw = Double.doubleToRawLongBits(newValue);
		this.lowBytes = raw & 0xFFFFFFFFl;
		this.highBytes = raw >> 32;
	}
	
	public long getLowBytes() {
		return this.lowBytes;
	}
	
	public long getHighBytes() {
		return this.highBytes;
	}

}
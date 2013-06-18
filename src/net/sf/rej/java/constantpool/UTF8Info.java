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

import java.io.UnsupportedEncodingException;

import net.sf.rej.util.ByteSerializer;

/**
 * This class models UTF8 Info Entries in a constant pool section of a class
 * defition.
 * 
 * @author Sami Koivu
 */

public class UTF8Info extends ConstantPoolInfo {

	private String value;

	public UTF8Info(String string, ConstantPool pool) {
		super(UTF8, pool);
		this.value = string;
	}

	public UTF8Info(byte[] bytes, ConstantPool pool) {
		super(UTF8, pool);
		try {
			this.value = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "(utf-8) " + this.value;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	/**
	 * Update the value of this instance.
	 * 
	 * @param newValue
	 */
	public void setString(String newValue) {
		this.value = newValue;
	}

	@Override
	public byte[] getData() {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(getType());
		try {
			byte[] data = this.value.getBytes("UTF-8");
			ser.addShort(data.length);
			ser.addBytes(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ser.getBytes();
	}

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;

		try {
			return this.value.equals(((UTF8Info) other).value);
		} catch (ClassCastException cce) {
			return false;
		}
	}

	@Override
	public String getTypeString() {
		return "UTF-8 Text";
	}

}
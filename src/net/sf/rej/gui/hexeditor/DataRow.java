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
package net.sf.rej.gui.hexeditor;

import net.sf.rej.util.ByteToolkit;

/**
 * Stores the data of the hex editor. Each object of this class
 * is a row in a <code>JList</code> and the custom renderer renders
 * the data contained in these objects. Each row shares the <code>
 * DataProvider</code> object.
 * 
 * @author Sami Koivu
 */
public class DataRow {
	
	private DataProvider data;
	private int offset;
	
	/**
	 * Initializes this object with the given data and an offset from
	 * which to start reading the data.
	 * @param data the data contained in this row.
	 * @param offset the offset from which to read the data.
	 */
	public DataRow(DataProvider data, int offset) {
		this.data = data;
		this.offset = offset;
	}
	
	/**
	 * Checks availability of data in the given index.
	 * @param index an index between 0 and row-width inclusive.
	 * @return whether or not that data is available. <code>false
	 * </code> indicates that the index is after the end of the data.
	 */
	public boolean isDataAvailable(int index) {
		return offset + index < data.getSize();
	}
	
	/**
	 * Returns a 6 digit hexadecimal number indicating the beginning
	 * offset of this row.
	 * @return hexadecimal offset String.
	 */
	public String getOffsetString() {
		return ByteToolkit.getHexString(this.offset, 6);
	}
	
	/**
	 * Returns a 2 digit hexadecimal value of the byte in the given
	 * index of this row.
	 * @param index the index of the byte queried.
	 * @return a hexadecimal value String, such as "ca".
	 */
	public String getHexData(int index) {
		return ByteToolkit.getHexString(data.get(offset + index), 2);
	}
	
	/**
	 * Returns the byte in the given index as a char value. The
	 * following operations are performed on the char. If it's 
	 * ascii value is lower than 0x20 (space character) it is replaced
	 * by a dot. If it's value is higher than 0xe0 it is replaced by
	 * a dot.
	 * @param index the inded of the queried ascii character.
	 * @return a char of the byte in the given position.
	 */
	public char getAsciiData(int index) {
		char c = (char)data.get(offset + index);
		if (c < 32) c = '.';
		if (c > 0xe0) c= '.';
		return c;
	}
	
	/**
	 * Returns the absolute position of the given index of this row.
	 * @param index an index into this row.
	 * @return the absolute offset value. For example, of this <code>
	 * DataRow</code> was initialized with the offset of 200 and the
	 * the absolute offset of the index 7 is queried, 207 will be
	 * returned.
	 */
	public int getAbsolute(int index) {
		return this.offset + index;
	}

}

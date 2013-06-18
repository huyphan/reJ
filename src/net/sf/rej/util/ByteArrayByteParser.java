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
package net.sf.rej.util;

/**
 * Class that wraps a byte array and offers tools for reading
 * the array.
 * 
 * @author Sami Koivu
 */

public class ByteArrayByteParser implements ByteParser{

	/**
	 * The byte array used as the data source for this parser.
	 */
	protected byte[] data;

	/**
	 * The current position in the byte array. The next byte
	 * that will be read from the array will be data[pos].
	 */
	protected int pos = 0;

	/**
	 * The big-endian mode of this parser.
	 */
	protected boolean bigEndian = false;

	/**
	 * Initializes this parser with the given byte array, starting
	 * the parsing from the beginning of the array.
	 * @param data a byte array to parse.
	 */
	public ByteArrayByteParser(byte[] data) {
		this.data = data;
	}

	/**
	 * Initializes this parse with the given byte array, starting
	 * the parsing from the position indicated by the parameter
	 * <code>pos</code>.
	 * @param data a byte array to parse.
	 * @param pos the position to start parsing from.
	 */
	public ByteArrayByteParser(byte[] data, int pos) {
		this.data = data;
		this.pos = pos;
	}

	public byte getByte() {
		return this.data[this.pos++];
	}

	public byte[] getBytes(int count) {
		byte[] bytes = ByteToolkit.getBytes(this.data, this.pos, count);
		this.pos += count;
		return bytes;
	}

	public int getByteAsInt() {
		return ByteToolkit.getByte(this.data[this.pos++]);
	}

	public int getShortAsInt() {
		int i = (int) ByteToolkit.getLong(this.data, this.pos, 2,
				this.bigEndian);
		this.pos += 2;

		return i;
	}

	// TODO: misleading name - this returns the long value of 4 next bytes in the
	// array
	public long getInt() {
		long l = ByteToolkit.getLong(this.data, this.pos, 4, this.bigEndian);
		this.pos += 4;

		return l;
	}

	@Deprecated
	public ByteParser getNewParser() {
		ByteParser parser = new ByteArrayByteParser(this.data, this.pos);
		parser.setBigEndian(this.bigEndian);
		return parser;
	}

	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public boolean hasMore() {
		return this.pos < (this.data.length);
	}

	public int getPosition() {
		return this.pos;
	}

	public int peekByte() {
		return ByteToolkit.getByte(this.data[this.pos]);
	}

}

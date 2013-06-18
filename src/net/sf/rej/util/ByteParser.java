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
 * Interface for reading binary data.
 * 
 * @author Sami Koivu
 */

public interface ByteParser {

	/**
	 * Returns the next byte available.
	 * @return next byte in the parser.
	 */
	public byte getByte();

	/**
	 * Returns an array of the next <code>count</code> bytes.
	 * @param count the number of bytes to read.
	 * @return a byte array of the data read.
	 */
	public byte[] getBytes(int count);

	/**
	 * Returns the next unsigned byte available as an <code>int</code>.
	 * @return the unsigned value of the next available byte.
	 */
	public int getByteAsInt();
	
	/**
	 * Returns the unsigned value of the next two bytes available
	 * in the parser as an <code>int</code>.
	 * @return the next available unsigned short value.
	 */
	public int getShortAsInt();

	// TODO: see if the type could be changed to int without side-effects
	
	/**
	 * Returns the unsigned value of the next 4 bytes as a
	 * <code>long</code>.
	 * @return long value of the next 4 bytes.
	 */
	public long getInt();

	/**
	 * Creates a new parser starting from the current position
	 * independent of this parser.
	 * @return a new parser starting from current position.
	 * @deprecated This feature is not applicable
	 * to streams, so it should not be used. Instead either a
	 * mark / reset along with a byte array read approach should
	 * be used instead.
	 */
	@Deprecated
	public ByteParser getNewParser();

	/**
	 * Sets the big-endian mode of this parser.
	 * @param bigEndian a boolean value true for big-endian mode, false
	 * for non-big-endian mode.
	 */
	public void setBigEndian(boolean bigEndian);

	/**
	 * Returns true if the end of the underlying data source has not
	 * been reached yet. Not that the return of this method in no way
	 * indicates that data is available for immediate reading, for
	 * example of the underlying data source is a socket. 
	 * @return boolean true if there is still more data available to
	 * be read.
	 */
	public boolean hasMore();

	/**
	 * Returns the current position of this parser. In other words,
	 * the number of bytes read from this parser.
	 * @return the position of this parser.
	 */
	public int getPosition();

	/**
	 * Returns an unsigned value of the next byte in this parser,
	 * without advancing the position of the parser. Thus, if this
	 * method is called n times in succession without calling the
	 * other methods of this class, a value i will be consistently
	 * returned every time. This method is identical to the method
	 * {@link net.sf.rej.util.ByteParser#getByteAsInt()} other
	 * than for the fact that the position is not advanced. 
	 * @return the unsigned value of the next available byte.
	 */
	public int peekByte();

}

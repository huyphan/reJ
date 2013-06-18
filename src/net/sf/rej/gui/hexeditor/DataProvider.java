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

/**
 * An abstraction of the data used by the Hex Editor.
 * 
 * @author Sami Koivu
 */
public interface DataProvider {
	/**
	 * Returns the byte in the given position.
	 * @param index the queried position.
	 * @return the byte in that position.
	 */
	public byte get(int index);
	
	/**
	 * Sets the byte in the given position.
	 * @param index the position to set.
	 * @param value new value for the byte.
	 */
	public void set(int index, byte value);
	
	/**
	 * Returns the size of the data in bytes.
	 * @return the size of the data abstracted by this interface.
	 */
	public int getSize();
}

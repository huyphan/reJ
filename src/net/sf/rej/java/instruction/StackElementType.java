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
package net.sf.rej.java.instruction;

/**
 * An enumeration which identifies the type of a stack element.
 * 
 * @author Sami Koivu
 */
public enum StackElementType {
	/**
	 * A type for an int value. Other than java ints this type is also used
	 * by booleans, bytes, shorts and chars.
	 */
	INT,
	
	/**
	 * A type for long values.
	 */
	LONG,
	
	/**
	 * A type for object references (including arrays an exceptions).
	 */
	REF,
	
	/**
	 * A type for double values.
	 */
	DOUBLE,
	
	/**
	 * A type for float values.
	 */
	FLOAT,
	
	/**
	 * A type for type 1 computational values, that is:
	 * booleans, bytes, chars, shorts, ints, floats, references, and return
	 * addresses.
	 *
	 * A set of two of these might also be a type 2 computational value, that is:
	 * long or double.
	 */
	ANY,
	CAT2,
	ADDRESS;
}

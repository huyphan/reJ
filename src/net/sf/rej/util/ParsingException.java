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
 * An unchecked exception indicating a parsing error during the
 * parsing of a classfile. Used to indicate an IO problem when
 * parsing a classfile from a stream (as opposed to a byte array).
 * 
 * @author Sami Koivu
 */
public class ParsingException extends RuntimeException {

	/**
	 * Instanciate this object with the given <code>Exception</code>.
	 * @param wrapped an exception that is wrapped by this
	 * object.
	 */
    public ParsingException(Exception wrapped) {
        super(wrapped);
    }

    /**
     * Instanciate this object with the given message.
     * @param message exception message.
     */
    public ParsingException(String message) {
        super(message);
    }

}

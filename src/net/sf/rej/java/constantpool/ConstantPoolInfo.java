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

import java.util.logging.Logger;

import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteToolkit;
import net.sf.rej.util.ParsingException;

/**
 * <code>ConstantPoolInfo</code> is the superclass for all constant pool items.
 *
 * @author Sami Koivu
 */

public abstract class ConstantPoolInfo {
	
	private static final Logger logger = Logger.getLogger(ConstantPoolInfo.class.getName());
	
    public static final int CLASS = 7;
    public static final int FIELD_REF = 9;
    public static final int METHOD_REF = 10;
    public static final int INTERFACE_METHOD_REF = 11;
    public static final int STRING = 8;
    public static final int INTEGER = 3;
    public static final int FLOAT = 4;
    public static final int LONG = 5;
    public static final int DOUBLE = 6;
    public static final int NAME_AND_TYPE = 12;
    public static final int UTF8 = 1;

    private int tag;

    protected ConstantPool pool;

    protected ConstantPoolInfo(int tag, ConstantPool pool) {
        this.tag = tag;
        this.pool = pool;
    }

    /**
     * Parse a constant pool info item from a byte parser.
     * @param parser ByteParser providing the data.
     * @param pool ConstantPool to be passed on the the ConstantPoolInfo object.
     * @return A subclass of ConstantPoolInfo of the correct type.
     */
    public static ConstantPoolInfo getCPI(ByteParser parser, ConstantPool pool) {
        int tag = parser.getByteAsInt();
        switch (tag) {
        case CLASS:
            int nameIndex = parser.getShortAsInt();
            return new ClassInfo(nameIndex, pool);
        case FIELD_REF:
        case METHOD_REF:
        case INTERFACE_METHOD_REF:
            int classIndex = parser.getShortAsInt();
            int nameAndTypeIndex = parser.getShortAsInt();
            return new RefInfo(tag, classIndex, nameAndTypeIndex, pool);
        case STRING:
            int stringIndex = parser.getShortAsInt();
            return new StringInfo(stringIndex, pool);
        case INTEGER:
            int i = (int) parser.getInt();
            return new IntegerInfo(i, pool);
        case FLOAT:
            int f = (int) parser.getInt();
            return new FloatInfo(f, pool);
        case LONG:
            long highBytes = parser.getInt();
            long lowBytes = parser.getInt();
            return new LongInfo(highBytes, lowBytes, pool);
        case DOUBLE:
            highBytes = parser.getInt();
            lowBytes = parser.getInt();
            return new DoubleInfo(highBytes, lowBytes, pool);
        case NAME_AND_TYPE:
            nameIndex = parser.getShortAsInt();
            int descriptorIndex = parser.getShortAsInt();
            return new NameAndTypeInfo(nameIndex, descriptorIndex, pool);
        case UTF8:
            int length = parser.getShortAsInt();
            byte[] bytes = parser.getBytes(length);
            return new UTF8Info(bytes, pool);
        default:
            byte[] asdf = parser.getBytes(32);
        	logger.warning("Unsupported/invalid constantpool entry: " + tag);
            logger.warning(ByteToolkit.byteArrayToDebugString(asdf));
        }
        throw new ParsingException("Unsupported contstantpool entry: " + tag);
    }

    /**
     * Return the type of this ConstantPoolInfo instance. The int value returned corresponds to the public constants defined in this class
     * and also to the byte value that is written to the class file.
     * @return int type value of this instance.
     */
    public int getType() {
        return this.tag;
    }

    /**
     * Return a String representation of the value of this constant pool entry. Each subclass
     * overwrites this method and returns a value that makes sense for it's context.
     * @return String value of this entry
     */
    public String getValue() {
        return toString();
    }

    /**
     * Return the data of this entry in the form that it can be written to a class file.
     * @return byte[] array with data
     */
    public abstract byte[] getData();

    /**
     * Return a String describing the type of this instance.
     * @return The type of this entry as a String.
     */
    public abstract String getTypeString();
}
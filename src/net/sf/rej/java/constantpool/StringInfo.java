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
 * Represents a String Info entry in the constant pool.
 * 
 * @author Sami Koivu
 */

public class StringInfo extends ConstantPoolInfo {

    private int stringIndex;

    public StringInfo(int stringIndex, ConstantPool pool) {
        super(STRING, pool);
        this.stringIndex = stringIndex;
    }

    @Override
	public String toString() {
        return "(string) index " + this.stringIndex + "("
                + this.pool.get(this.stringIndex) + ")";
    }

    @Override
	public byte[] getData() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addByte(getType());
        ser.addShort(this.stringIndex);

        return ser.getBytes();
    }

    public String getString() {
        return ((UTF8Info) this.pool.get(this.stringIndex)).getValue();
    }

    @Override
	public String getValue() {
    	ConstantPoolInfo cpi = this.pool.get(this.stringIndex);
    	if (cpi == null) {
    		return "String Info refers to a null item on the constant pool.";
    	} else if (!(cpi instanceof UTF8Info)) {
    		return "String Info refers to an item of wrong type (" + cpi.getTypeString() + ").";    		
    	} else {
    		return "\"" + getString() + "\"";
    	}
    }

    @Override
	public int hashCode() {
        return getString().hashCode();
    }

    @Override
	public boolean equals(Object other) {
        if (other == null) return false;

        try {
            return getString().equals(((StringInfo) other).getString());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
	public String getTypeString() {
        return "String Constant";
    }

    public String getStringValue() {
        UTF8Info info = (UTF8Info) super.pool.get(this.stringIndex);
        return info.getValue();
    }

    public UTF8Info getUTF8Info() {
        return (UTF8Info)this.pool.get(this.stringIndex);
    }

    public int getUTF8Index() {
        return this.stringIndex;
    }

    public void createNewUTF8String(String newString) {
        this.stringIndex = this.pool.optionalAddUtf8(newString);
    }

    public void setUTF8Index(int index) {
        this.stringIndex = index;
    }
}

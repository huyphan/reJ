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
package net.sf.rej.java.attribute;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

public class ExceptionsAttribute extends Attribute {

    private List<ExceptionDescriptor> exceptions = new ArrayList<ExceptionDescriptor>();

    public ExceptionsAttribute(int nameIndex, ConstantPool pool) {
        super(nameIndex, pool);
    }

    public ExceptionsAttribute(int nameIndex, ConstantPool pool, List<ExceptionDescriptor> exceptions) {
    	super(nameIndex, pool);
    	this.exceptions.addAll(exceptions);
    }

    @Override
	public byte[] getPayload() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(this.exceptions.size());
    	for (ExceptionDescriptor ex : this.exceptions) {
    		ser.addShort(ex.getIndex());
    	}

        return ser.getBytes();
    }
    
    @Override
    public void setPayload(byte[] data) {
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        int numberOfExceptions = parser.getShortAsInt();
        for (int i = 0; i < numberOfExceptions; i++) {
        	int index = parser.getShortAsInt();
            this.exceptions.add(new ExceptionDescriptor(pool, index));
        }
    }

    public List<ExceptionDescriptor> getExceptions() {
        List<ExceptionDescriptor> list = new ArrayList<ExceptionDescriptor>();
        list.addAll(this.exceptions);
        return list;
    }

    @Override
	public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("Exceptions (");
    	for (ExceptionDescriptor ex : this.exceptions) {
    		if (sb.length() > 0) {
    			sb.append(", ");
    		}
    		sb.append(ex);
    	}
    	sb.append(")");

    	return sb.toString();
    }

}

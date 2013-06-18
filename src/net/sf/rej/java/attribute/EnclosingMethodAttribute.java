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

import net.sf.rej.java.Descriptor;
import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.NameAndTypeInfo;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

public class EnclosingMethodAttribute extends Attribute {
	
	private int classIndex = 0;
	private int methodIndex = 0;

	public EnclosingMethodAttribute(int nameIndex, ConstantPool pool) {
		super(nameIndex, pool);
	}
	
	@Override
	public void setPayload(byte[] data) {
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        this.classIndex = parser.getShortAsInt();
        this.methodIndex = parser.getShortAsInt();
	}
	
	@Override
	public byte[] getPayload() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(this.classIndex);
        ser.addShort(this.methodIndex);
        
        return ser.getBytes();
	}
	
	public boolean hasEnclosingMethod() {
		return methodIndex != 0;
	}
	
	public ClassInfo getEnclosingClass() {
		return (ClassInfo) this.pool.get(this.classIndex);
	}
	
	public NameAndTypeInfo getEnclosingMethod() {
		if (hasEnclosingMethod()) {
			return (NameAndTypeInfo) this.pool.get(this.methodIndex);
		} else {
			return null;
		}
 	}
	
	public String getEnclosingClassName() {
		return getEnclosingClass().getName();
	}
	
	public String getEnclosingMethodName() {
		if (hasEnclosingMethod()) {
			return this.getEnclosingMethod().getName();
		} else {
			return null;
		}
	}
	
	public Descriptor getEnclosingMethodDesc() {
		if (hasEnclosingMethod()) {
			return this.getEnclosingMethod().getDescriptor();
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Enclosing class = <");
		sb.append(getEnclosingClassName());
		sb.append(">");
		if (hasEnclosingMethod()) {
			sb.append(" Enclosing method = <");
			Descriptor desc = getEnclosingMethodDesc();
			String name = getEnclosingMethodName();
			sb.append(desc.getReturn());
			sb.append(" ");
			sb.append(name);
			sb.append("(");
			sb.append(desc.getParams());
			sb.append(")>");
		}
		return sb.toString();
	}

}

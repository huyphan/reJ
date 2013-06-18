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

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.ByteToolkit;

public class Attribute {

	protected int nameIndex;
	private byte[] info;
	protected ConstantPool pool;

	protected Attribute(int nameIndex, ConstantPool pool) {
		this.pool = pool;
		this.nameIndex = nameIndex;
	}

	public static Attribute getAttribute(ByteParser parser, ConstantPool pool) {
		int nameIndex = parser.getShortAsInt();
		int count = (int) parser.getInt();
		byte[] info = parser.getBytes(count);
		String name = pool.get(nameIndex).getValue();
		if ("Code".equals(name)) {
			Attribute attr = new CodeAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("LocalVariableTable".equals(name)) {
			Attribute attr = new LocalVariableTableAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("LineNumberTable".equals(name)) {
			Attribute attr = new LineNumberTableAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("Exceptions".equals(name)) {
			Attribute attr = new ExceptionsAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("ConstantValue".equals(name)) {
			Attribute attr = new ConstantValueAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("InnerClasses".equals(name)) {
			Attribute attr = new InnerClassesAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("Synthetic".equals(name)) {
			Attribute attr =  new SyntheticAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("SourceFile".equals(name)) {
			Attribute attr = new SourceFileAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("Deprecated".equals(name)) {
			Attribute attr = new DeprecatedAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("RuntimeVisibleAnnotations".equals(name)) {
			Attribute attr = new RuntimeVisibleAnnotationsAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("RuntimeInvisibleAnnotations".equals(name)) {
			Attribute attr = new RuntimeInvisibleAnnotationsAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("EnclosingMethod".equals(name)) {
			Attribute attr = new EnclosingMethodAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("Signature".equals(name)) {
			Attribute attr = new SignatureAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else if ("LocalVariableTypeTable".equals(name)) {
			Attribute attr = new LocalVariableTypeTableAttribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		} else {
			Attribute attr = new Attribute(nameIndex, pool);
			attr.setPayload(info);
			return attr;
		}

	}

	@Override
	public String toString() {
		return "Attribute: nameIndex " + this.nameIndex + "("
				+ this.pool.get(this.nameIndex) + ") info "
				+ ByteToolkit.byteArrayToDebugString(this.info);
	}

	public String getName() {
		return this.pool.get(this.nameIndex).getValue();
	}

	public final byte[] getData() {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addShort(this.nameIndex);
		byte[] payload = getPayload();
		ser.addInt(payload.length);
		ser.addBytes(payload);

		return ser.getBytes();
	}
	
	public byte[] getPayload() {
		return this.info;
	}

	public void setPayload(byte[] data) {
		this.info = data;
	}

}

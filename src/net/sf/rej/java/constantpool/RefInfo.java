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

import net.sf.rej.java.Descriptor;
import net.sf.rej.util.ByteSerializer;

// TODO: Reduce coupling between ConstantPoolInfo items and the ConstantPool.
// The items maybe shouldn't know what pool they are a part of

public class RefInfo extends ConstantPoolInfo {

	private int tag;

	/**
	 * Pointer to a <code>ClassInfo</code> entry in the constant pool.
	 */
	private int classIndex;

	/**
	 * Pointer to a <code>NameAndTypeInfo</code> entry in the constant pool.
	 */
	private int nameAndTypeIndex;

	public RefInfo(int tag, int classIndex, int nameAndTypeIndex,
			ConstantPool pool) {
		super(tag, pool);
		this.tag = tag;
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	@Override
	public String toString() {
		Descriptor desc = getDescriptor();
		return desc.getReturn() + " " + getClassName() + "." + getTargetName()
				+ "(" + desc.getParams() + ")";
	}

	public String getClassName() {

		ClassInfo ci = (ClassInfo) this.pool.get(this.classIndex);
		return ci.getName();
	}

	/**
	 * Returns the method or field name this Ref
	 * 
	 * @return String
	 */
	public String getTargetName() {
		NameAndTypeInfo info = (NameAndTypeInfo) this.pool
				.get(this.nameAndTypeIndex);
		return info.getName();
	}

	public String getMethodType() {
		NameAndTypeInfo info = (NameAndTypeInfo) this.pool
				.get(this.nameAndTypeIndex);
		return info.getDescriptorString();
	}

	public Descriptor getDescriptor() {
		NameAndTypeInfo info = (NameAndTypeInfo) this.pool
				.get(this.nameAndTypeIndex);
		return info.getDescriptor();
	}

	@Override
	public byte[] getData() {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(getType());
		ser.addShort(this.classIndex);
		ser.addShort(this.nameAndTypeIndex);

		return ser.getBytes();
	}

	public NameAndTypeInfo getNameAndTypeInfo() {
		NameAndTypeInfo info = (NameAndTypeInfo) this.pool
				.get(this.nameAndTypeIndex);
		return info;
	}

	@Override
	public int hashCode() {
		int i = getClassName().hashCode();
		i += getTargetName().hashCode();
		i += this.tag;
		i += getMethodType().hashCode();

		return i;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;

		try {
			RefInfo ref = (RefInfo) other;
			if (this.tag != ref.tag)
				return false;

			if (!getClassName().equals(ref.getClassName()))
				return false;

			if (!getTargetName().equals(ref.getTargetName()))
				return false;

			if (!getMethodType().equals(ref.getMethodType()))
				return false;

			return true;
		} catch (ClassCastException cce) {
			return false;
		}
	}

	@Override
	public String getTypeString() {
		switch (this.tag) {
		case FIELD_REF:
			return "Fieldref";
		case INTERFACE_METHOD_REF:
			return "InterfaceMethodref";
		case METHOD_REF:
			return "Methodref";
		default:
			throw new RuntimeException("Internal error, undefined Ref type = "
					+ this.tag);
		}
	}

}

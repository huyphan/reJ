package net.sf.rej.java.constantpool;

import net.sf.rej.java.Descriptor;
import net.sf.rej.util.ByteSerializer;

/**
 * Models a NameAndTypeInfo entry in the constant pool of a class. It is a
 * reference to the name and type of a method or field. It points to a name
 * entry, and a descriptor entry which has a return type and a possible type for
 * parameters.
 * 
 * @author Sami Koivu
 */

public class NameAndTypeInfo extends ConstantPoolInfo implements DescriptorEnabled {

	private int nameIndex;

	private int descriptorIndex;

	public NameAndTypeInfo(int nameIndex, int descriptorIndex, ConstantPool pool) {
		super(NAME_AND_TYPE, pool);
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	@Override
	public String toString() {
		Descriptor desc = getDescriptor();
		return desc.getReturn() + " " + getName() + "(" + desc.getParams()
				+ ")";
	}

	public String getName() {
		return this.pool.get(this.nameIndex).getValue();
	}
	
	public UTF8Info getNameInfo() {
		return (UTF8Info)this.pool.get(this.nameIndex);		
	}

	public String getDescriptorString() {
		return this.pool.get(this.descriptorIndex).getValue();
	}

	@Override
	public byte[] getData() {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(getType());
		ser.addShort(this.nameIndex);
		ser.addShort(this.descriptorIndex);

		return ser.getBytes();
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + getDescriptorString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;

		try {
			NameAndTypeInfo info = (NameAndTypeInfo) other;

			if (!getName().equals(info.getName()))
				return false;

			if (!getDescriptorString().equals(info.getDescriptorString()))
				return false;

			return true;
		} catch (ClassCastException cce) {
			return false;
		}
	}

	public Descriptor getDescriptor() {
		return new Descriptor(getDescriptorString());
	}

	public void setDescriptorIndex(int index) {
		this.descriptorIndex = index;
	}

	@Override
	public String getTypeString() {
		return "Name and type";
	}

	public void setNameIndex(int nameIndex) {
		this.nameIndex = nameIndex;
	}
	
	public int getNameIndex() {
		return this.nameIndex;
	}
	
	public int getDescriptorIndex() {
		return this.descriptorIndex;
	}

}
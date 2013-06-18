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
package net.sf.rej.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.DescriptorEnabled;
import net.sf.rej.java.constantpool.UTF8Info;
import net.sf.rej.java.instruction.Label;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

/**
 * Local variable. An entity that binds a local variable to a scope and a name
 * in the constant pool.
 * 
 * @author Sami Koivu
 */

public class LocalVariable implements DescriptorEnabled {

	private Label startLabel;
	private Label endLabel;
	private int nameIndex;
	private int descriptorIndex;
	private int index;
	private ConstantPool pool;

	public LocalVariable(ByteParser parser, ConstantPool pool) {
		this.pool = pool;
		int startPc = parser.getShortAsInt();
		int length = parser.getShortAsInt();
		this.nameIndex = parser.getShortAsInt();
		this.descriptorIndex = parser.getShortAsInt();
		this.index = parser.getShortAsInt();
		this.startLabel = new Label(startPc, getName() + "_start");
		this.endLabel = new Label(startPc + length, getName() + "_end");
	}

	public byte[] getData() {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addShort(getStartPc());
		ser.addShort(getEndPc() - getStartPc());
		ser.addShort(this.nameIndex);
		ser.addShort(this.descriptorIndex);
		ser.addShort(this.index);
		return ser.getBytes();
	}

	@Override
	public String toString() {
		return this.index + " - " + this.getName();
	}

	public String getName() {
		ConstantPoolInfo cpi = this.pool.get(this.nameIndex);
		return cpi.getValue();
	}

	public boolean isInRange(int pc) {
		return (pc >= getStartPc() && pc <= getEndPc());
	}

	public int getIndex() {
		return this.index;
	}

	public int getDescriptorIndex() {
		return this.descriptorIndex;
	}

	public void setDescriptorIndex(int index) {
		this.descriptorIndex = index;
	}

	public Descriptor getDescriptor() {
		UTF8Info info = (UTF8Info) this.pool.get(this.descriptorIndex);
		return new Descriptor(info.getValue());
	}

	public int getStartPc() {
		return this.startLabel.getPosition();
	}

	public int getEndPc() {
		return this.endLabel.getPosition();
	}

	public List<Label> getLabels() {
		List<Label> al = new ArrayList<Label>();
		al.add(this.startLabel);
		al.add(this.endLabel);

		return al;
	}

	public String getSignatureLine() {
		return getDescriptor().getReturn() + " " + getName();
	}

}

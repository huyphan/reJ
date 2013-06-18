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

public class InnerClassesAttribute extends Attribute {

	/* TODO: setters, getters */
	private List<InnerClassesAttribute.InnerClass> innerClasses = new ArrayList<InnerClassesAttribute.InnerClass>();

	public InnerClassesAttribute(int nameIndex, ConstantPool pool) {
		super(nameIndex, pool);
	}
	
	@Override
	public void setPayload(byte[] data) {
		super.setPayload(data); // TODO: ser / deser
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        int numberOfExceptions = parser.getShortAsInt();
        for (int i = 0; i < numberOfExceptions; i++) {
        	int innerClassInfoIndex = parser.getShortAsInt();
        	int outerClassInfoIndex = parser.getShortAsInt();
        	int innerClassNameIndex = parser.getShortAsInt();
        	int innerClassAccessFlags = parser.getShortAsInt();
        	InnerClass ic = new InnerClass(innerClassInfoIndex, outerClassInfoIndex, innerClassNameIndex, innerClassAccessFlags);
        	this.innerClasses.add(ic);
        }
	}
	
	@Override
	public String toString() {
		return "InnerClasses (" + this.innerClasses.size() + " entries)";
	}
	
	public class InnerClass {
		private int innerClassInfoIndex;
		private int outerClassInfoIndex;
		private int innerClassNameIndex;
		private int innerClassAccessFlags;
		
		public InnerClass(int innerClassInfoIndex, int outerClassInfoIndex, int innerNameIndex, int innerFlags) {
			this.innerClassInfoIndex = innerClassInfoIndex;
			this.outerClassInfoIndex = outerClassInfoIndex;
			this.innerClassNameIndex = innerNameIndex;
			this.innerClassAccessFlags = innerFlags;
		}

		public int getInnerClassAccessFlags() {
			return innerClassAccessFlags;
		}

		public void setInnerClassAccessFlags(int innerClassAccessFlags) {
			this.innerClassAccessFlags = innerClassAccessFlags;
		}

		public int getInnerClassInfoIndex() {
			return innerClassInfoIndex;
		}

		public void setInnerClassInfoIndex(int innerClassInfoIndex) {
			this.innerClassInfoIndex = innerClassInfoIndex;
		}

		public int getInnerClassNameIndex() {
			return innerClassNameIndex;
		}

		public void setInnerClassNameIndex(int innerClassNameIndex) {
			this.innerClassNameIndex = innerClassNameIndex;
		}

		public int getOuterClassInfoIndex() {
			return outerClassInfoIndex;
		}

		public void setOuterClassInfoIndex(int outerClassInfoIndex) {
			this.outerClassInfoIndex = outerClassInfoIndex;
		}
	}

}

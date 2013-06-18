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

import net.sf.rej.java.attribute.generics.LocalVariableTypeEntry;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;

public class LocalVariableTypeTableAttribute extends Attribute {
	
	private List<LocalVariableTypeEntry> entries = null; 

	public LocalVariableTypeTableAttribute(int nameIndex, ConstantPool pool) {
		super(nameIndex, pool);
	}

	@Override
	public void setPayload(byte[] data) {
		super.setPayload(data);
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        int count = parser.getShortAsInt();
        this.entries = new ArrayList<LocalVariableTypeEntry>(count);
        for (int i=0; i < count; i++) {
        	int startPc = parser.getShortAsInt();
        	int length = parser.getShortAsInt();
        	int nameIndex = parser.getShortAsInt();
        	int signatureIndex = parser.getShortAsInt();
        	int index = parser.getShortAsInt();
        	LocalVariableTypeEntry entry = new LocalVariableTypeEntry(this.pool, startPc, length, nameIndex, signatureIndex, index);
        	entries.add(entry);
        }
	}
	
	@Override
	public byte[] getPayload() {
        return super.getPayload();
	}
	
	@Override
	public String toString() {
		return "LocalVariableTypeTable (" + this.entries.size() + " entries)";
	}
	
	public List<LocalVariableTypeEntry> getEntries() {
		List<LocalVariableTypeEntry> retList = new ArrayList<LocalVariableTypeEntry>(this.entries.size());
		retList.addAll(this.entries);
		return retList;
	}

			
}

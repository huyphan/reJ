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
package net.sf.rej.java.attribute.annotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.rej.java.Descriptor;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.UTF8Info;
import net.sf.rej.util.ByteParser;

public class Annotation {
	
	private ConstantPool pool;
	private int typeIndex;
	private Map<Integer, ElementValue> elementValues;

	public Annotation(ByteParser parser, ConstantPool pool) {
		this.pool = pool;
		this.typeIndex = parser.getShortAsInt();
		int elementValuePairs = parser.getShortAsInt();
		this.elementValues = new HashMap<Integer, ElementValue>(elementValuePairs);
		for (int j=0; j < elementValuePairs; j++) {
			int elementNameIndex = parser.getShortAsInt();
			ElementValue ev = parseElementValue(parser);
			this.elementValues.put(elementNameIndex, ev);
		}
	}

	private ElementValue parseElementValue(ByteParser parser) {
		ElementValue elementValue = null;

		int tag = parser.getByteAsInt();
		switch (tag) {
		case 'B':
		case 'C':
		case 'D':
		case 'F':
		case 'I':
		case 'J':
		case 'S':
		case 'Z':
		case 's':
			// const value index
			int constValueIndex = parser.getShortAsInt();
			elementValue = new ConstantValue(tag, constValueIndex, this.pool);
			break;
		case 'e':
			// enum const value
			int typeNameIndex = parser.getShortAsInt();
			int constNameIndex = parser.getShortAsInt();
			elementValue = new EnumValue(typeNameIndex, constNameIndex, this.pool);
			break;
		case 'c':
			int classInfoIndex = parser.getShortAsInt();
			elementValue = new ClassInfoValue(classInfoIndex, this.pool);
			break;
		case '@':
			elementValue = new NestedAnnotationValue(new Annotation(parser, this.pool));
			break;
		case '[':
			int numValues = parser.getShortAsInt();
			List<ElementValue> array = new ArrayList<ElementValue>(numValues);
			for (int i=0; i < numValues; i++) {
				array.add(parseElementValue(parser));
			}
			
			elementValue = new ArrayValue(array);
			break;
		default:
			throw new RuntimeException("Invalid annotation tag: " + tag + " / " + (char)tag);
		}

		return elementValue;
	}
	
	public String getName() {
		UTF8Info info = (UTF8Info)this.pool.get(this.typeIndex);
		Descriptor desc = new Descriptor(info.getValue());
		return desc.getReturn().toString();
	}
	
	public int getElementValueCount() {
		return this.elementValues.size();
	}
	
	public Map<String, ElementValue> getElementValues() {
		Map<String, ElementValue> result = new HashMap<String, ElementValue>();
		for (Entry<Integer, ElementValue> entry: this.elementValues.entrySet()) {
			int key = entry.getKey();
			ConstantPoolInfo nameInfo = this.pool.get(key);
			String name = nameInfo.getValue();
			result.put(name, entry.getValue());
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, ElementValue> entry : this.elementValues.entrySet()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			int key = entry.getKey();
			ConstantPoolInfo nameInfo = this.pool.get(key);
			String name = nameInfo.getValue();
			ElementValue ev = entry.getValue();
			sb.append(name + " = " + ev.getValue());
		}
		String pairs = sb.toString();
		sb = new StringBuilder();
		UTF8Info info = (UTF8Info)this.pool.get(this.typeIndex);
		Descriptor desc = new Descriptor(info.getValue());
		sb.append(desc.getReturn());
		if (pairs.length() > 0) {
			sb.append("(");
			sb.append(pairs);
			sb.append(")");
		}
		return sb.toString();
	}
}

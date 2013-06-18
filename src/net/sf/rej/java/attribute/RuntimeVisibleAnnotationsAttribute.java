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

import net.sf.rej.java.attribute.annotations.Annotation;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;

public class RuntimeVisibleAnnotationsAttribute extends Attribute {
	// TODO: make mutable, implement getData, accessors, etc
	
	private List<Annotation> annotations = new ArrayList<Annotation>();
	
	public RuntimeVisibleAnnotationsAttribute(int nameIndex, ConstantPool pool) {
		super(nameIndex, pool);
	}
	
	@Override
	public void setPayload(byte[] data) {
		super.setPayload(data); // TODO: ser / de-ser
		ByteParser parser = new ByteArrayByteParser(data);
		parser.setBigEndian(true);
		int numAnnotations = parser.getShortAsInt();
		for (int i=0; i < numAnnotations; i++) {
			this.annotations.add(new Annotation(parser, this.pool));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Annotation annotation : this.annotations) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(annotation.toString());
		}
		sb.insert(0, "RuntimeVisibleAnnotations: ");
		return sb.toString();
	}
	
	public List<Annotation> getAnnotations() {
		List<Annotation> list = new ArrayList<Annotation>(this.annotations.size());
		list.addAll(this.annotations);
		return list;
	}

}

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
import java.util.List;

public class ArrayValue implements ElementValue {
	
	private List<ElementValue> array;

	public ArrayValue(List<ElementValue> array) {
		this.array = array;
	}
	
	public String getValue() {
		StringBuilder sb = new StringBuilder();
		for (ElementValue av : this.array) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(av.getValue());
		}
		
		if (this.array.size() > 1) {
			sb.insert(0, "{");
			sb.append("}");
		}
		
		return sb.toString();
	}
	
	public List<ElementValue> getArray() {
		List<ElementValue> list = new ArrayList<ElementValue>(this.array.size());
		list.addAll(this.array);
		return list;
	}

}

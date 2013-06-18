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
package net.sf.rej.java.attribute.generics;

import java.util.ArrayList;
import java.util.List;

public class Signature {
	
	private List<FormalTypeParameter> typeParams = null;
	private List<GenericJavaType> methodParams = null;
	private List<GenericJavaType> types = new ArrayList<GenericJavaType>();

	public void setFormalTypeParameters(List<FormalTypeParameter> typeParams) {
		this.typeParams = typeParams;
	}

	public void setParameters(List<GenericJavaType> params) {
		this.methodParams = params;
	}

	public void addGenericType(GenericJavaType type) {
		this.types.add(type);
	}
	
	public List<FormalTypeParameter> getFormalTypeParameters() {
		return this.typeParams;
	}
	
	public List<GenericJavaType> getMethodParameters() {
		return this.methodParams;
	}
	
	public List<GenericJavaType> getTypes() {
		List<GenericJavaType> retList = new ArrayList<GenericJavaType>(this.types.size());
		retList.addAll(this.types);
		return retList;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.typeParams != null) {
			sb.append("<");
			boolean first = true;			
			for (FormalTypeParameter typeParam : this.typeParams) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(typeParam.toString());
			}
			sb.append("> ");
		}
		
		if (this.methodParams != null) {
			sb.append("(");
			boolean first = true;
			for (GenericJavaType param : this.methodParams) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(param.toString());
			}
			sb.append(") ");
		}
		
		for (GenericJavaType type : this.types) {
			sb.append(type.toString());
			sb.append(" ");
		}

		return sb.toString().trim();
	}

}

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

import net.sf.rej.java.JavaType;

/**
 * This class models a concept which is broader than what is modeled by a
 * <code>JavaType</code> class. Basically it can be a primitive type, a type
 * parameter identifier or a reference type. It may contain type arguments
 * (which in turn may contain type arguments, recursively).
 * 
 * @author Sami Koivu
 */
public class GenericJavaType implements TypeArgument {
	
	private Types type;
	private JavaType baseType;
	private List<TypeArgument> typeArguments = new ArrayList<TypeArgument>();

	public GenericJavaType() {
	}
	
	public void addTypeArgument(TypeArgument arg) {
		this.typeArguments.add(arg);
	}

	public void setType(Types type, int dimensions, String typeStr) {
		this.type = type;
		this.baseType = new JavaType(typeStr, dimensions);
	}
	
	public JavaType getBaseType() {
		return this.baseType;
	}
	
	public Types getType() {
		return this.type;
	}
	
	public List<TypeArgument> getTypeArguments() {
		List<TypeArgument> retList = new ArrayList<TypeArgument>(this.typeArguments.size());
		retList.addAll(this.typeArguments);
		return retList;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseType());
		if (this.typeArguments.size() > 0) {
			sb.append("<");
			boolean first = true;
			for(TypeArgument param : this.typeArguments) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(param);
			}
			sb.append(">");
		}
		
		return sb.toString();
	}

}

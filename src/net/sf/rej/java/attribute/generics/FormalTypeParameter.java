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

/**
 * Models a formal type parameter. A type parameter consists of an identifier
 * and an optional class bound and optional interface bounds.
 *  
 * @author Sami Koivu
 */
public class FormalTypeParameter {
	
	private String identifier;
	private GenericJavaType classBound;
	private List<GenericJavaType> interfaceBounds = new ArrayList<GenericJavaType>();

	public FormalTypeParameter(String identifier) {
		this.identifier = identifier;
	}

	public void setClassBound(GenericJavaType type) {
		this.classBound = type;
	}

	public void addInterfaceBound(GenericJavaType type) {
		this.interfaceBounds.add(type);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.identifier);
		sb.append(" extends ");
		boolean first = true;
		for (GenericJavaType intf : getTypeUnion()) {
			if (first) {
				first = false;
			} else {
				sb.append(" & ");
			}
			sb.append(intf);
		}
		
		return sb.toString();
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public GenericJavaType getClassBound() {
		return this.classBound;
	}
	
	public List<GenericJavaType> getInterfaceBounds() {
		List<GenericJavaType> retList = new ArrayList<GenericJavaType>(this.interfaceBounds.size());
		retList.addAll(this.interfaceBounds);
		return retList;
	}
	
	public List<GenericJavaType> getTypeUnion() {
		List<GenericJavaType> retList = new ArrayList<GenericJavaType>(this.interfaceBounds.size() + 1);
		if (this.classBound != null) {
			retList.add(this.classBound);
		}
		retList.addAll(this.interfaceBounds);
		
		return retList;
	}

}

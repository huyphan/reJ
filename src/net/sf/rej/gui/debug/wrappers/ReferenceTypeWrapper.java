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
package net.sf.rej.gui.debug.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Field;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

public class ReferenceTypeWrapper implements IReferenceType {
	private ReferenceType type;
	
	public ReferenceTypeWrapper(ReferenceType type) {
		this.type = type;
	}

	public String getSuperClassName() {
		// classObject().getClass().getSuperclass().getName();
		return this.type.getClass().getSuperclass().getName();
	}

	public String name() {
		return this.type.name();
	}

	public List<IField> visibleFields() {
		List<IField> fields = new ArrayList<IField>();
		for (Field field : this.type.visibleFields()) {
			fields.add(new FieldWrapper(field));
		}
		
		return fields;
	}

	public List<IMethod> visibleMethods() {
		List<IMethod> methods = new ArrayList<IMethod>();
		for (Method method : this.type.visibleMethods()) {
			methods.add(new MethodWrapper(method));
		}
		
		return methods;
	}

}

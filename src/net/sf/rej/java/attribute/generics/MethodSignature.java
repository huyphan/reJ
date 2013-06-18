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

import java.util.List;

/**
 * Models a method signature for methods with formal type parameters or
 * type arguments for the return type or the parameters.
 * 
 * @author Sami Koivu
 */
public class MethodSignature {
	
	private List<FormalTypeParameter> formalTypeParameters = null;
	private GenericJavaType returnType = null;
	private List<GenericJavaType> parameterTypes = null;
	
	public MethodSignature() {
	}

	public void setReturnType(GenericJavaType returnType) {
		this.returnType = returnType;
	}
	
	public void setParameters(List<GenericJavaType> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	
	public GenericJavaType getReturnType() {
		return this.returnType;
	}
	
	public List<GenericJavaType> getParameters() {
		return this.parameterTypes;
	}

	public void setFormalTypeParameters(List<FormalTypeParameter> typeParams) {
		this.formalTypeParameters = typeParams;
	}
	
	public List<FormalTypeParameter> getFormalTypeParameters() {
		return this.formalTypeParameters;
	}

}

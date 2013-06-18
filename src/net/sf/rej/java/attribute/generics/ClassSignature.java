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
 * Models a class signature for classes with formal type parameters or
 * type arguments for the superclass or interfaces.
 * 
 * @author Sami Koivu
 */
public class ClassSignature {
	private List<FormalTypeParameter> formalTypeParams = null;
	private GenericJavaType superClassSignature = null;
	private List<GenericJavaType> superInterfaceSignatures = new ArrayList<GenericJavaType>();

	public void setSuperClassSignature(GenericJavaType superClassSignature) {
		this.superClassSignature = superClassSignature;
	}

	public void addSuperInterfaceSignature(GenericJavaType intf) {
		this.superInterfaceSignatures.add(intf);
	}

	public void setFormalTypeParameters(List<FormalTypeParameter> formalTypeParameters) {
		this.formalTypeParams = formalTypeParameters;
	}

	public GenericJavaType getSuperClassSignature() {
		return this.superClassSignature;
	}
	
	public List<GenericJavaType> getSuperInterfaceSignatures() {
		List<GenericJavaType> retList = new ArrayList<GenericJavaType>();
		retList.addAll(this.superInterfaceSignatures);
		return retList;
	}
	
	public List<FormalTypeParameter> getFormalTypeParameters() {
		return this.formalTypeParams;
	}

}

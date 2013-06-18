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
 * A facade class for getting parsed signature classes out of strings. Since
 * it is impossible to determine whether a given signature is a class-, field-
 * or a method signature the choice is delegated to the user of the class.
 * 
 * @author Sami Koivu
 */
public class Signatures {

	public static ClassSignature getClassSignature(String signatureString) {
		SignatureParser parser = new SignatureParser(signatureString);
		ClassSignature signature = new ClassSignature();
		if (parser.peekNextType() == SignatureParser.ElementType.START_DEF) {
			// set formal type param
			List<FormalTypeParameter> typeParams = parser.getFormalTypeParameters();
			signature.setFormalTypeParameters(typeParams);
		}
		// superclass
		signature.setSuperClassSignature(parser.getGenericType());
		
		// interfaces (if any)
		while (parser.hasMore()) {
			signature.addSuperInterfaceSignature(parser.getGenericType());
		}
		
		return signature;
	}
	
	public static FieldSignature getFieldSignature(String signatureString) {
		SignatureParser parser = new SignatureParser(signatureString);
		FieldSignature signature = new FieldSignature(parser.getGenericType());
		return signature;
	}
	
	public static MethodSignature getMethodSignature(String signatureString) {
		SignatureParser parser = new SignatureParser(signatureString);
		MethodSignature signature = new MethodSignature();
		if (parser.peekNextType() == SignatureParser.ElementType.START_DEF) {
			// set formal type param
			List<FormalTypeParameter> typeParams = parser.getFormalTypeParameters();
			signature.setFormalTypeParameters(typeParams);
		}
		List<GenericJavaType> params = parser.getMethodParameters();
		signature.setParameters(params);
		GenericJavaType returnType = parser.getGenericType();
		signature.setReturnType(returnType);
		return signature;
	}

	/**
	 * Parses the signature string and returns a Signature object. This method
	 * may be used when the type of the signature (class signature, method
	 * signature, field signature, local variable signature) isn't known.
	 * @param signatureString
	 * @return A parsed signature object.
	 */
	public static Signature getSignature(String signatureString) {
		SignatureParser parser = new SignatureParser(signatureString);
		Signature signature = new Signature();
		if (parser.peekNextType() == SignatureParser.ElementType.START_DEF) {
			// set formal type param
			List<FormalTypeParameter> typeParams = parser.getFormalTypeParameters();
			signature.setFormalTypeParameters(typeParams);
		}
		if (parser.peekNextType() == SignatureParser.ElementType.START_METHOD_PARAMS) {
			// method params
			List<GenericJavaType> params = parser.getMethodParameters();
			signature.setParameters(params);
		}
		
		while (parser.hasMore()) {
			GenericJavaType type = parser.getGenericType();
			signature.addGenericType(type);
		}
		return signature;
		
	}

}

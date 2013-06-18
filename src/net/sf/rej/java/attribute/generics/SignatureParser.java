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
 * Utility class that performs parsing of signatures strings.
 * 
 * @author Sami Koivu
 */
public class SignatureParser {
	
	static enum ElementType {
		OTHER, // primitive types
		REFERENCE_TYPE, // Lxxx;
		TYPE_PARAMETER, // Txxx;
		START_DEF, // <
		END_DEF, // >
		DELIM_DEF, // :
        // or <Ljava/lang/String;>
		WILD_CARD, // ?
		BOUND, // + or -
		START_METHOD_PARAMS, // (
		END_METHOD_PARAMS, // )
		ARRAY, // [Ljava/lang/String;
		PRIMITIVE // V or I or B or C or D or F etc 
	}
	
	private String signature;
	private int pos = 0;
	
	public SignatureParser(String signature) {
		this.signature = signature;
	}
	
	public ElementType peekNextType() {
		switch (signature.charAt(this.pos)) {
		case 'L':
			return ElementType.REFERENCE_TYPE;
		case 'T':
			return ElementType.TYPE_PARAMETER;
		case '<':
			return ElementType.START_DEF;
		case '>':
			return ElementType.END_DEF;
		case ':':
			return ElementType.DELIM_DEF;
		case '*':
			return ElementType.WILD_CARD;
		case '+':
		case '-':
			return ElementType.BOUND;
		case '(':
			return ElementType.START_METHOD_PARAMS;
		case ')':
			return ElementType.END_METHOD_PARAMS;
		case '[':
			return ElementType.ARRAY;
		case 'B':
		case 'C':
		case 'D':
		case 'F':
		case 'I':
		case 'J':
		case 'S':
		case 'Z':
		case 'V':
			return ElementType.PRIMITIVE;
		default:
			return ElementType.OTHER;
		}
	}
	
	public boolean hasMore() {
		return this.pos < this.signature.length();
	}
	
	public int getDimensionCount() {
		int count = 0;
		while (peekNextType() == ElementType.ARRAY) {
			count++;
			this.pos++;
		}
		
		return count;
	}

	public GenericJavaType getGenericType() {
		GenericJavaType type = new GenericJavaType();
		int dimensions = getDimensionCount();
		ElementType et = peekNextType();
		this.pos++;
		if (et == ElementType.REFERENCE_TYPE) {
			int next = getFirstIndex("<", ";");
			String typeStr = this.signature.substring(this.pos, next);
			typeStr = typeStr.replace('/', '.');
			type.setType(Types.REFERENCE_TYPE, dimensions, typeStr);
			
			if (this.signature.charAt(next) == '<') {
				// type parameters
				this.pos = next + 1;
				while (this.signature.charAt(this.pos) != '>') {
					// deal with wildcards and bounds and whatnot
					ElementType typeParamType = peekNextType();
					if (typeParamType == ElementType.WILD_CARD) {
						type.addTypeArgument(new Any());
						this.pos++;
					} else if (typeParamType == ElementType.BOUND) {
						char bound = this.signature.charAt(this.pos);
						this.pos++;
						GenericJavaType paramType = getGenericType();
						type.addTypeArgument(new BoundTypeArgument(paramType, bound));
					} else {
						// straight type
						GenericJavaType paramType = getGenericType();
						type.addTypeArgument(paramType);
					}
				}
				this.pos += 2; // skip past > and ;
			} else {
				this.pos = next + 1;
			}
		} else if (et == ElementType.TYPE_PARAMETER) {
			int next = this.signature.indexOf(";", this.pos);
			String typeStr = this.signature.substring(this.pos, next);
			this.pos = next + 1;
			type.setType(Types.TYPE_PARAMETER_IDENTIFIER, dimensions, typeStr);
		} else if (et == ElementType.PRIMITIVE) {
			// primitive types
			type.setType(Types.PRIMITIVE_TYPE, dimensions, getPrimitiveName(this.signature.charAt(this.pos-1)));
		} else {
			throw new RuntimeException("Invalid type: " + et + " - " + this.signature.charAt(this.pos));
		}
		return type;
	}
	
    public static String getPrimitiveName(char c) {
        switch (c) {
        case 'B':
            return "byte";
        case 'C':
            return "char";
        case 'D':
            return "double";
        case 'F':
            return "float";
        case 'I':
            return "int";
        case 'J':
            return "long";
        case 'S':
            return "short";
        case 'Z':
            return "boolean";
        case 'V':
            return "void";
        }
        
        return null;
    }


	public int getFirstIndex(String ... targets) {
		int min = -1;
		for (String target : targets) {
			int index = this.signature.indexOf(target, this.pos);
			if (index != -1) {
				if (min == -1 || index < min) {
					min = index;
				}
			}
		}
		return min;
	}

	public String getTypeParameterIdentifier() {
		int index = getFirstIndex(":");
		String name =  this.signature.substring(this.pos, index);
		this.pos = index + 1;
		return name;
	}

	public List<FormalTypeParameter> getFormalTypeParameters() {
		if (peekNextType() != ElementType.START_DEF) {
			throw new RuntimeException("Invalid type parameter start char: " + this.signature.charAt(this.pos));
		}
		List<FormalTypeParameter> typeParams = new ArrayList<FormalTypeParameter>();
		this.pos++; // skip over start def
		while (peekNextType() != SignatureParser.ElementType.END_DEF) {
			typeParams.add(getFormalTypeParameter());			
		}
		this.pos++; // skip over end def
		
		return typeParams;
	}
	
	private FormalTypeParameter getFormalTypeParameter() {
		String name = getTypeParameterIdentifier();
		FormalTypeParameter typeParam = new FormalTypeParameter(name);
		ElementType et = peekNextType();
		if (et == SignatureParser.ElementType.DELIM_DEF) {
			// empty superclass
		} else if (et != SignatureParser.ElementType.END_DEF) {
			// get superclass
			typeParam.setClassBound(getGenericType());
		}
		while (peekNextType() == SignatureParser.ElementType.DELIM_DEF) {
			this.pos++; // skip over delim def
			typeParam.addInterfaceBound(getGenericType());
			
		}
		
		return typeParam;
	}

	public List<GenericJavaType> getMethodParameters() {
		ElementType openParenthesis = peekNextType();
		if (openParenthesis != ElementType.START_METHOD_PARAMS) {
			throw new RuntimeException("Invalid method params start: " + this.signature.charAt(this.pos));
		}
		this.pos++; // move past the opening parenthesis
		List<GenericJavaType> params = new ArrayList<GenericJavaType>();
		while (peekNextType() != ElementType.END_METHOD_PARAMS) {
			params.add(getGenericType());
		}
		this.pos++; // move past the closing parenthesis
		return params;
	}
}

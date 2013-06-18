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
package net.sf.rej.java;

/**
 * The <code>JavaType</code> class represents a java type that is either
 * primitive (int, long, char, double, etc) or a class/interface type. In
 * either case the type may possibly contain several dimensions. 
 * 
 * @author Sami Koivu
 */
public class JavaType {
	
	public static final class ImmutableJavaType extends JavaType {
	    
		public ImmutableJavaType(String typeName, int dimensions) {
			super(typeName, dimensions);
	    }

	    @Override
		public void dropDimension() {
			throw new RuntimeException("This instance is immutable.");
		}
	}

	/**
	 * An immutable constant for the primitive type int (with no dimensions)
	 */
	public static final JavaType INT = new ImmutableJavaType("int", 0);
	
	/**
	 * An immutable constant for the type java.lang.Object (with no dimensions)
	 */
	public static final JavaType JAVA_LANG_OBJECT = new ImmutableJavaType("java.lang.Object", 0);

	/**
	 * The name of this type. Either the primitive name (int, double, etc) or
	 * the name of a class/interface type (java.lang.String).
	 */
    private String type;
    
    /**
     * The number of dimensions of this type.
     */
    private int dimensions = 0;

    /**
     * Instantiates a new JavaType from the given definition String. The
     * parameter is expected to be a FQN in the Java source syntax. For
     * example:<blockquote>java.lang.String</blockquote>
     * or:
     * <blockquote>byte[]</blockquote>
     * @param def a string defining the type.
     */
    public JavaType(String def) {
        int i = def.indexOf("[");
        if (i == -1) {
            this.type = def;
            dimensions = 0;
        } else {
            this.type = def.substring(0, i);
            // TODO: tidy up. (length -i) /2 ? Verify.
            this.dimensions = def.substring(i).length()/2;
        }
    }

    /**
     * Instantiates a new <code>JavaType</code> specifying explicitly the
     * name and the number of dimensions.
     * @param typeName the name of the type (without dimensions).
     * @param dimensions the number of dimensions.
     */
    public JavaType(String typeName, int dimensions) {
    	this.type = typeName;
    	this.dimensions = dimensions;
    }

    /**
     * Returns the type of the instance. For a string array this method would
     * return "java.lang.String".
     * @return the type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the dimension part of a type. For a type with no dimensions
     * this method will return an empty String, for a type with one dimension
     * it will return "[]" and so on.
     * @return the dimension part of this type.
     */
    public String getDimensions() {
    	StringBuffer sb = new StringBuffer(this.dimensions*2);
    	for (int i=0; i < this.dimensions; i++) {
    		sb.append("[]");
    	}
        return sb.toString();
    }

    /**
     * Returns true if the type denoted by this instance if a primitive type.
     * @return true if type is primitive.
     */
    public boolean isPrimitive() {
        if (this.type.equals("void")) {
            return true;
        } else if (this.type.equals("byte")) {
            return true;
        } else if (this.type.equals("char")) {
            return true;
        } else if (this.type.equals("double")) {
            return true;
        } else if (this.type.equals("float")) {
            return true;
        } else if (this.type.equals("int")) {
            return true;
        } else if (this.type.equals("long")) {
            return true;
        } else if (this.type.equals("short")) {
            return true;
        } else if (this.type.equals("boolean")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
	public String toString() {
        return this.type + this.getDimensions();
    }

    /**
     * Returns the number of dimensions. Returns 0 if the type has no
     * dimensions.
     * @return number of dimensions.
     */
    public int getDimensionCount() {
    	return this.dimensions;
    }

    /**
     * Returns a raw representation of this type, ie.
     * the way the types are written in .class files.
     * 
     * Primitive types become a one character corresponding code
     * and class references are encoded into, for example in the case of
     * java.lang.String:
     * <blockquote>Ljava/lang/String;</blockquote>
     * @return A "raw" string reprentation of this type.
     */
    public String getRaw() {
    	StringBuffer sb = new StringBuffer();

    	// dimensions
    	for (int i=0; i < this.dimensions; i++) {
    		sb.append("[");
    	}

    	// primitive types
        if (this.type.equals("void")) {
            sb.append("V");
        } else if (this.type.equals("byte")) {
            sb.append("B");
        } else if (this.type.equals("char")) {
            sb.append("C");
        } else if (this.type.equals("double")) {
            sb.append("D");
        } else if (this.type.equals("float")) {
            sb.append("F");
        } else if (this.type.equals("int")) {
            sb.append("I");
        } else if (this.type.equals("long")) {
            sb.append("J");
        } else if (this.type.equals("short")) {
            sb.append("S");
        } else if (this.type.equals("boolean")) {
            sb.append("Z");
        } else {
        	// complex type
            sb.append("L" + this.type.replace('.', '/') + ";");
        }

        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof JavaType) {
    		JavaType other = (JavaType) obj;
    		return other.type.equals(this.type) && other.dimensions == this.dimensions;
    	} else {
    		return false;
    	}
    }
    
    @Override
    public int hashCode() {
    	return this.type.hashCode() + this.dimensions*63;
    }

    /**
     * Decreases the dimension count by one.
     */
	public void dropDimension() {
		this.dimensions--;
	}

}

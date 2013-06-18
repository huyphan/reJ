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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.ByteToolkit;
import net.sf.rej.util.Range;

/**
 * <code>ClassFile</code> objects represent .class files. They're constructed
 * either by parsing a byte array or a stream with <code>net.sf.rej.java.Disassembler
 * </code>, using the Constructor or using the <code>net.sf.rej.java.ClassFactory</code>
 * class.
 * 
 * @author Sami Koivu
 */
public class ClassFile {
	
	/**
	 * The default magic for comparison.
	 */
    private static final byte[] magic = { (byte) 0xCa, (byte) 0xfe, (byte) 0xBa, (byte) 0xbe};

    /**
     * Class-file version.
     */
    private ClassVersion version = new ClassVersion();

    /**
     * The constant pool of this class file.
     */
    private ConstantPool pool;

    /**
     * The class-level access-flags of this class (public, protected, etc) 
     */
    private int accessFlags;
    
    /**
     * An index to a ClassRef in the constant pool, defining this class.
     */
    private int thisClass;
    
    /**
     * An index to a ClassRef in the constant pool, defining this class's
     * parent.
     */    
    private int superClass;
    
    /**
     * A list of interfaces implemented by this class.
     */
    private List<Interface> interfaces = new ArrayList<Interface>();

    /**
     * A list of the fields of this class.
     */
    private List<Field> fields = new ArrayList<Field>();

    /**
     * A list of methods of this class, including any static code blocks and
     * constructors.
     */
    private List<Method> methods = new ArrayList<Method>();

    /**
     * An <code>Attributes</code> object containing all the class-level
     * attributes of this class (if any), such as SourceFile.
     */
    private Attributes attributes;

    /**
     * Initializes this <code>ClassFile</code> object. Note that
     * this object is not yet valid for deserialization until several
     * attributes of this class have been set.
     */
    public ClassFile() {
        // default constructor
    }

    /**
     * Returns the class-level access flags of this class. The int value is the
     * result of a bit-wise AND operation of all the modifiers of this class.
     * For a more thorough description and programmatically handling this value
     * see <code>net.sf.rej.java.AccessFlags</code>.
     * @return class-level access-flags.
     */
    public int getAccessFlags() {
        return this.accessFlags;
    }

    /**
     * Returns the major version of this class. For a more thorough description
     * of how the version maps into different Java compability settings,
     * see the <code>net.sf.rej.java.ClassVersion</code> class.
     * @return the major version of this class.
     */
    public int getMajorVersion() {
        return this.version.getMajorVersion();
    }

    /**
     * Returns the minor version of this class. For a more thorough description
     * of how the version maps into different Java compability settings,
     * see the <code>net.sf.rej.java.ClassVersion</code> class.
     * @return the minor version of this class.
     */
    public int getMinorVersion() {
        return this.version.getMinorVersion();
    }

    /**
     * Sets the major version of this class. For a more thorough description
     * of how the version maps into different Java compability settings,
     * see the <code>net.sf.rej.java.ClassVersion</code> class.
     * @param majorVersion the new major version for this class.
     */
    public void setMajorVersion(int majorVersion) {
    	this.version.setMajorVersion(majorVersion);
    }

    /**
     * Sets the minor version of this class. For a more thorough description
     * of how the version maps into different Java compability settings,
     * see the <code>net.sf.rej.java.ClassVersion</code> class.
     * @param minorVersion the new minor version for this class.
     */
    public void setMinorVersion(int minorVersion) {
    	this.version.setMinorVersion(minorVersion);
    }

    /**
     * Returns the major and minor version of this class in a <code>ClassVersion
     * </code> object. For a more thorough description
     * of how the version maps into different Java compability settings,
     * see the <code>net.sf.rej.java.ClassVersion</code> class.
     * @return the major and minor versions of this class.
     */
    public ClassVersion getVersion() {
    	return this.version;
    }
    
    /**
     * Sets the major and minor versions of this class.
     * @param version
     */
    public void setVersion(ClassVersion version) {
    	this.version = version;
    }

    /**
     * Returns a read-only list of the methods in this class. The methods are
     * returned as self-contained <code>net.sf.rej.java.Method</code> objects.
     * Modifying this list does not affect this class.
     * Modifying the methods contained in the list does affect this class.
     * @return a list of Method objects describing the methods of this class.
     */
    public List<Method> getMethods() {
        List<Method> list = new ArrayList<Method>();
        list.addAll(this.methods);
        return list;
    }

    /**
     * Adds a method to this class. For creating <code>net.sf.rej.java.Method</code>
     * objects, see <code>net.sf.rej.java.MethodFactory</code> class.
     * @param method a method (or static code block, or constructor)
     * to add to this class.
     */
    public void add(Method method) {
        this.methods.add(method);
    }

    /**
     * Adds a field to this class. For creating <code>net.sf.rej.java.Field</code>
     * objects, see <code>net.sf.rej.java.FieldFactory</code> class.
     * @param field a field to add to this class.
     */
    public void add(Field field) {
    	this.fields.add(field);
    }
    
    /**
     * Returns a read-only list of the interfaces implemented by this class.
     * The interfaces are returned as self-contained <code>net.sf.rej.java.Interface
     * </code> objects. Modifying this list does not affect this class. Modifying
     * the <code>Interface</code> objects contained in the list does affect this
     * class.
     * @return a list of Interface objects describing the interfaces implemented
     * by this class.
     */
    public List<Interface> getInterfaces() {
        List<Interface> list = new ArrayList<Interface>();
        list.addAll(this.interfaces);
        return list;
    }

    /**
     * Returns the index to the constant pool ClassRef item which describes
     * the class represented by this <code>ClassFile</code> object.
     * @return an int value index to the constant pool.
     */
    public int getThisClass() {
        return this.thisClass;
    }

    /**
     * Returns an <code>Attributes</code> object, containing the class-level attributes of this
     * class - if it has any. The object returned is not a copy and any modifications
     * to it or the attributes contained in it will affect this class.
     * @return an Attributes object with the class-level attributes of this class.
     */
    public Attributes getAttributes() {
        return this.attributes;
    }

    /**
     * Returns the index to the constant pool ClassRef item which describes
     * the parent class of the class represented by this <code>ClassFile</code>
     * object.
     * @return an int value index to the constant pool.
     */
    public int getSuperClass() {
        return this.superClass;
    }

    /**
     * Returns a read-only list of the fields in this class. The fields are
     * returned as self-contained <code>net.sf.rej.java.Field</code> objects.
     * Modifying the list returned will not affect this class.
     * Modifying the fields contained in the list will affect this class.
     * @return a list of Field objects describing the fields of this class.
     */
    public List<Field> getFields() {
        List<Field> list = new ArrayList<Field>();
        list.addAll(this.fields);
        return list;
    }

    /**
     * Returns the ConstantPool object which models the constant pool associated
     * with this <code>ClassFile</code> object. The object returned is not a copy
     * and any modification to it will modify this class file as well.
     * @return the constant pool.
     */
    public ConstantPool getPool() {
        return this.pool;
    }

    /**
     * Returns the default magic for Java class files. In other words,
     * <code>0xcafebabe</code>
     * @return a byte array with the default class file magic.
     */
    public byte[] getMagic() {
        return ClassFile.magic;
    }

    /**
     * Sets the class-level access flags of this class. The int value is the
     * result of a bit-wise AND operation of all the modifiers of this class.
     * For a more thorough description and programmatically handling this value
     * see <code>net.sf.rej.java.AccessFlags</code>.
     * @param accessFlags the class-level access-flags.
     */
    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    /**
     * Sets all the methods for this class. Any methods existing
     * in this class prior to calling this method are discarded (provided,
     * naturally, that they are not in the list and thus added again).
     * @param methods the list of methods (or static code block, or constructor)
     * for this class.
     */
    public void setMethods(List<Method> methods) {
        this.methods.clear();
        this.methods.addAll(methods);
    }

    /**
     * Sets all the interfaces for this class. Any interfaces existing
     * in this class prior to calling this method are discarded (provided,
     * naturally, that they are not in the list and thus added again).
     * @param interfaces the list of interfaces for this class.
     */
    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces.clear();
        this.interfaces.addAll(interfaces);
    }

    /**
     * Sets the index to the constant pool ClassRef item which defines the
     * class modeled by this <code>ClassFile</code> object.
     * @param thisClass index to the constant pool.
     */
    public void setThisClass(int thisClass) {
        this.thisClass = thisClass;
    }

    /**
     * Sets the attributes object for this class, discarding the old one.
     * @param attributes an attributes object containing 0-n attributes.
     */
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Sets the index to the constant pool ClassRef item which defines the
     * parent class of the class modeled by this <code>ClassFile</code> object.
     * @param superClass index to the constant pool.
     */
    public void setSuperClass(int superClass) {
        this.superClass = superClass;
    }

    /**
     * Sets all the fields for this class. Any fields existing
     * in this class prior to calling this method are discarded (provided,
     * naturally, that they are not in the list and thus added again).
     * @param fields the list of fields for this class.
     */
    public void setFields(List<Field> fields) {
        this.fields.clear();
        this.fields.addAll(fields);
    }

    /**
     * Sets the constant pool for this class. The old constant pool is discarded.
     * @param pool new constant pool.
     */
    public void setPool(ConstantPool pool) {
        this.pool = pool;
    }

    /**
     * Validates the magic given as a parameter. If the given magic is not the
     * standard 0xcafebabe a <code>RuntimeException</code> is thrown.
     * @param magic the magic to validate.
     * @throws RuntimeException the validation failed.
     */
    public void validateMagic(byte[] magic) throws RuntimeException {
        if (!Arrays.equals(magic, ClassFile.magic)) {
        	throw new RuntimeException("Invalid magic: " + ByteToolkit.getHexString(magic));
        }
    }

    /**
     * Serializes this class. The produced byte array is in the class file format.
     * It may be written to a .class file and it may be loaded by a <code>ClassLoader</code>.
     * @return the class data.
     */
    public byte[] getData() {
        ByteSerializer ser = new ByteSerializer(true);
        // 0:
        ser.addBytes(ClassFile.magic);

        // 4:
        ser.addShort(getMinorVersion());
        ser.addShort(getMajorVersion());
        
        // 8:
        ser.addBytes(this.pool.getData());

        // 8+pooldata:
        ser.addShort(this.accessFlags);
        ser.addShort(this.thisClass);
        ser.addShort(this.superClass);

        // 14+pooldata:
        ser.addShort(this.interfaces.size());
        // 16+pooldata:
        for (Interface anInterface : this.interfaces) {
            ser.addShort(anInterface.getNameIndex());
        }

        // 16+pooldata+interfaces*2
        ser.addShort(this.fields.size());
        // 18+pooldata+interfaces*2
        for (Field field : this.fields) {
            ser.addBytes(field.getData());
        }

        // 18+pooldata+interfaces*2+fielddata
        ser.addShort(this.methods.size());
        // 20+pooldata+interfaces*2+fielddata
        for (Method method : this.methods) {
            ser.addBytes(method.getData());
        }

        // 20+pooldata+interfaces*2+fielddata+methoddata
        ser.addBytes(this.attributes.getData());
        return ser.getBytes();
    }
    
    public static enum OffsetTag {MAGIC, VERSION, MINOR_VERSION, MAJOR_VERSION,
    	CONSTANT_POOL, ACCESS_FLAGS, THIS_CLASS, SUPER_CLASS,
    	INTERFACE_COUNT, INTERFACE_DATA, FIELD_COUNT, FIELD_DATA,
    	METHOD_COUNT, METHOD_DATA, ATTRIBUTES}
     
    /**
     * Returns a map of offsets of each significant element of the class file.
     * The offsets returned by this method are only valid until this
     * <code>ClassFile</code> object is modified. The keys in the map are
     * of type <code>OffsetTag</code>, <code>Interface</code>, 
     * <code>Field</code> and <code>Method</code>.
     * 
     * @return a map of element offsets in class file data.
     */
    public Map<Object, Range> getOffsetMap() {
    	Map<Object, Range> map = new HashMap<Object, Range>();
    	int offset = 0;
    	map.put(OffsetTag.MAGIC, new Range(offset, 4));
    	offset += 4;
    	map.put(OffsetTag.VERSION, new Range(offset, 4));
    	map.put(OffsetTag.MINOR_VERSION, new Range(offset, 2));
    	offset += 2;
    	map.put(OffsetTag.MAJOR_VERSION, new Range(offset, 2));
    	offset += 2;
    	int poolSize = this.pool.getData().length;
    	map.put(OffsetTag.CONSTANT_POOL, new Range(offset, poolSize));
    	// TODO: offsets for each pool item?
    	offset += poolSize;
    	map.put(OffsetTag.ACCESS_FLAGS, new Range(offset, 2));
    	offset += 2;
    	map.put(OffsetTag.THIS_CLASS, new Range(offset, 2));
    	offset += 2;
    	map.put(OffsetTag.SUPER_CLASS, new Range(offset, 2));
    	offset += 2;
    	map.put(OffsetTag.INTERFACE_COUNT, new Range(offset, 2));
    	offset += 2;
    	map.put(OffsetTag.INTERFACE_DATA, new Range(offset, this.interfaces.size()*2));
    	{
    		for (Interface anInterface : this.interfaces) {
    			map.put(anInterface, new Range(offset, 2));
    			offset += 2;
    		}
    	}
    	map.put(OffsetTag.FIELD_COUNT, new Range(offset, 2));
    	offset += 2;
    	int fieldDataTotalSize = 0;
    	Range fieldDataOffset = new Range(offset, 0);
    	map.put(OffsetTag.FIELD_DATA, fieldDataOffset);
        for (Field field : this.fields) {
        	int fieldSize = field.getData().length;
        	map.put(field, new Range(offset, fieldSize));
        	offset += fieldSize;
        	fieldDataTotalSize += fieldSize;
        }
        fieldDataOffset.setSize(fieldDataTotalSize);
        
    	map.put(OffsetTag.METHOD_COUNT, new Range(offset, 2));
    	offset += 2;
    	int methodDataTotalSize = 0;
    	Range methodDataOffset = new Range(offset, 0);
    	map.put(OffsetTag.METHOD_DATA, methodDataOffset);
        for (Method method : this.methods) {
        	int methodSize = method.getData().length;
        	map.put(method, new Range(offset, methodSize));
        	offset += methodSize;
        	methodDataTotalSize += methodSize;
        }
        methodDataOffset.setSize(methodDataTotalSize);
        
    	map.put(OffsetTag.ATTRIBUTES, new Range(offset, this.attributes.getData().length));
 
    	return map;
    }

    /**
     * Returns the short name of this class, in other words, the name without
     * the package definition part. For example, for the class <code>java.lang.String</code>
     * this method returns "String".
     * 
     * @return the short name of the class.
     */
    public String getShortClassName() {
        String cn = getFullClassName();
        return cn.substring(cn.lastIndexOf(".") + 1);
    }

    /**
     * Returns the full class name, including the package definition part. For
     * example <code>"net.sf.rej.java.ClassFile"</code>.
     * @return the full name of this class.
     */
    public String getFullClassName() {
        ConstantPool cp = this.pool;
        ConstantPoolInfo cpi = cp.get(this.getThisClass());
        return cpi.getValue();
    }

    /**
     * Returns the full class name of the parent class of the class modeled by
     * this object. For example, for a <code>ClassFile</code> object modeling
     * the class <code>java.lang.String</code> this method will return
     * <code>"java.lang.Object"</code>.
     * 
     * @return the full name of the super class of this class.
     */
    public String getSuperClassName() {
    	if (this.getSuperClass() == 0) return null;
    	
        ConstantPoolInfo cpi = this.pool.get(this.getSuperClass());
        return cpi.getValue();
    }

    /**
     * Returns the class-level access modifiers of the class modeled by this
     * object as a String. For example "public final".
     * 
     * @return the class-level access modifers.
     */
    public String getAccessString() {
        StringBuffer sb = new StringBuffer();
        if (AccessFlags.isPublic(this.accessFlags)) sb.append("public ");
        if (AccessFlags.isPrivate(this.accessFlags)) sb.append("private ");
        if (AccessFlags.isProtected(this.accessFlags)) sb.append("protected ");
        if (AccessFlags.isAbstract(this.accessFlags)) sb.append("abstract ");
        if (AccessFlags.isStatic(this.accessFlags)) sb.append("static ");
        //if(AccessFlags.isSuper(this.accessFlags)) sb.append("super ");
        if (AccessFlags.isFinal(this.accessFlags)) sb.append("final ");
        if (AccessFlags.isNative(this.accessFlags)) sb.append("native ");

        return sb.toString().trim();
    }


    /**
     * Returns the name of the package defined for the class presented by this
     * object. In the case where the class is in the default package, an empty
     * <code>String</code> is returned.
     * @return package.
     */
    public String getPackageName() {
        String cn = getFullClassName();
        int lastDot = cn.lastIndexOf(".");
        if(lastDot == -1) {
            return ""; /* empty string for no package is a bit of a hack */
        } else {
            return cn.substring(0, lastDot);
        }
    }  

    /**
     * Removes the given method from this class. Since no equals method has
     * been defined for the <code>Method</code> class the removal only works
     * if the <code>Method</code> object given as parameter is one of the
     * <code>Method</code> objects stored in this class's list of methods.
     * @param method the method to remove.
     */
    public void remove(Method method) {
        this.methods.remove(method);
    }

    /**
     * Removes the given field from this class. Since no equals method has
     * been defined for the <code>Field</code> class the removal only works
     * if the <code>Field</code> object given as parameter is one of the
     * <code>Field</code> objects stored in this class's list of fields.
     * @param field the field to remove.
     */
    public void remove(Field field) {
        this.fields.remove(field);
    }

    /**
     * Returns a String describing the Java version compability of this class.
     * The value is determined from the major/minor version of the class file
     * using the following specification:
     * <blockquote>
	 * The Java virtual machine implementation of Sun’s JDK release 1.0.2 supports
	 * class file format versions 45.0 through 45.3 inclusive. Sun’s JDK releases
	 * 1.1.X can support class file formats of versions in the range 45.0 through
	 * 45.65535 inclusive. For implementations of version 1.k of the Java 2 platform
	 * can support class file formats of versions in the range 45.0 through 44+k.0
	 * inclusive.
     * </blockquote>
     *  
     * @return java version compability <code>String</code> such as "1.5".
     */
	public String getJavaVersionCompabilityString() {
		return this.version.getJavaVersionCompabilityString();
	}
	
	/**
	 * Checks for equality. At this moment the equality is checked by comparing
	 * the fully qualified names of the two <code>ClassFile</code> objects.
	 * 
	 * @param obj the object to compare to this object.
	 * @return true if the class files refer to the same FQN.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ClassFile)) {
			return false;
		}
		ClassFile cf = (ClassFile)obj;
		
		/* TODO: Other than just checking the FQN, equals should be called
		 * on each of the methods, fields and attributes as well.
		 */
		return cf.getFullClassName().equals(getFullClassName());
	}

	@Override
	public int hashCode() {
		return getFullClassName().hashCode();
	}

}

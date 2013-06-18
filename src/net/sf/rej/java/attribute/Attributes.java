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
package net.sf.rej.java.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.Range;

public class Attributes {

    private List<Attribute> attributes = new ArrayList<Attribute>();

    public Attributes() {
    }

    public Attributes(ByteParser parser, ConstantPool pool) {
        int count = parser.getShortAsInt();
        for (int i = 0; i < count; i++) {
            Attribute a = Attribute.getAttribute(parser, pool);
            this.attributes.add(a);
        }

    }

    public List<Attribute> getAttributes() {
    	List <Attribute> attrs = new ArrayList<Attribute>();
    	attrs.addAll(this.attributes);
        return attrs;
    }

    public LineNumberTableAttribute getLineNumberTable() {
        return (LineNumberTableAttribute) getNamedAttribute("LineNumberTable");
    }

    public LocalVariableTableAttribute getLocalVariableTable() {
        return (LocalVariableTableAttribute) getNamedAttribute("LocalVariableTable");
    }

    public SyntheticAttribute getSyntheticAttribute() {
        return (SyntheticAttribute) getNamedAttribute("Synthetic");
    }
    
    public DeprecatedAttribute getDeprecatedAttribute() {
        return (DeprecatedAttribute) getNamedAttribute("Deprecated");
    }
    
    public SourceFileAttribute getSourceFileAttribute() {
    	return (SourceFileAttribute) getNamedAttribute("SourceFile");
    }

    public ExceptionsAttribute getExceptionsAttribute() {
        return (ExceptionsAttribute) getNamedAttribute("Exceptions");
    }

    public SignatureAttribute getSignatureAttribute() {
        return (SignatureAttribute) getNamedAttribute("Signature");
    }

    public LocalVariableTypeTableAttribute getLocalVariableTypeTable() {
		return (LocalVariableTypeTableAttribute) getNamedAttribute("LocalVariableTypeTable");
	}

    public RuntimeVisibleAnnotationsAttribute getRuntimeVisibleAnnotationsAttribute() {
    	return (RuntimeVisibleAnnotationsAttribute) getNamedAttribute("RuntimeVisibleAnnotations");
    }

    public RuntimeInvisibleAnnotationsAttribute getRuntimeInvisibleAnnotationsAttribute() {
    	return (RuntimeInvisibleAnnotationsAttribute) getNamedAttribute("RuntimeInvisibleAnnotations");
    }

    public CodeAttribute getCode() {
        return (CodeAttribute) getNamedAttribute("Code");
    }

    public ConstantValueAttribute getConstantValueAttribute() {
        return (ConstantValueAttribute) getNamedAttribute("ConstantValue");
    }

    public Attribute getNamedAttribute(String name) {
        for (int i = 0; i < this.attributes.size(); i++) {
            Attribute attr = this.attributes.get(i);
            if (name.equals(attr.getName())) {
                return attr;
            }
        }
        return null;
    }

    public byte[] getData() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(this.attributes.size());
        for (Attribute attr : this.attributes) {
            ser.addBytes(attr.getData());
        }

        return ser.getBytes();
    }
    
    /**
     * Returns a map of offsets of each significant element of this method.
     * The offsets returned by this method are only valid until this
     * object is modified. The keys in the map are
     * of type <code>OffsetTag</code>, <code>Interface</code>, 
     * <code>Field</code> and <code>Method</code>.
     * @param initialOffset the initial offset (this is added to each offset value).
     * @return a map of element offsets in class file data.
     */
    public Map<Object, Range> getOffsetMap(int initialOffset) {
    	Map<Object, Range> map = new HashMap<Object, Range>();
    	int offset = initialOffset;
    	offset += 2;
        for (Attribute attr : this.attributes) {
        	int size = attr.getData().length;
        	map.put(attr, new Range(offset, size));
        	offset += size;
        }

    	return map;
    }


    public void removeAttribute(Attribute attr) {
        this.attributes.remove(attr);
    }

    public void addAttribute(Attribute attr) {
        this.attributes.add(attr);
    }

}

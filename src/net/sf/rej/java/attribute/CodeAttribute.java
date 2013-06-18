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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.rej.java.Code;
import net.sf.rej.java.Exceptions;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.Range;

public class CodeAttribute extends Attribute {

    private int maxStack;
    private int maxLocals;
    private Code code;
    private Exceptions exceptions;

    private Attributes attributes;

    public CodeAttribute(int nameIndex, ConstantPool pool) {
        super(nameIndex, pool);
    }

    public CodeAttribute(int nameIndex, ConstantPool cp, int maxStack, int maxLocals) {
        super(nameIndex, cp);
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.code = new Code(cp);
        this.exceptions = new Exceptions();
        this.attributes = new Attributes();
    }

    @Override
	public byte[] getPayload() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(this.maxStack);
        ser.addShort(this.maxLocals);
        byte[] data = this.code.getData();
        ser.addInt(data.length);
        ser.addBytes(data);
        ser.addBytes(this.exceptions.getData());
        ser.addBytes(this.attributes.getData());

        return ser.getBytes();
    }
    
    public static enum OffsetTag {MAX_STACK, MAX_LOCALS, CODE_LENGTH, CODE, EXCEPTIONS, ATTRIBUTES}

    /**
     * Returns a map of offsets of each significant element of this method.
     * The offsets returned by this method are only valid until this
     * object is modified. The keys in the map are
     * of type <code>OffsetTag</code>, <code>Attribute</code>. 
     * 
     * @return a map of element offsets in class file data.
     */
    public Map<Object, Range> getOffsetMap() {
    	Map<Object, Range> map = new HashMap<Object, Range>();
    	int offset = 6; // 6 bytes of Attribute serialization data.
    	map.put(OffsetTag.MAX_STACK, new Range(offset, 2));
    	offset += 2;
    	map.put(OffsetTag.MAX_LOCALS, new Range(offset, 2));
    	offset += 2;
    	map.put(OffsetTag.CODE_LENGTH, new Range(offset, 4));
    	offset += 4;
    	int codeLength = this.code.getData().length;
    	map.put(OffsetTag.CODE, new Range(offset, codeLength));
    	offset += codeLength;
    	int exceptionsLength = this.exceptions.getData().length;
    	map.put(OffsetTag.EXCEPTIONS, new Range(offset, exceptionsLength));
    	offset += exceptionsLength;
    	map.put(OffsetTag.ATTRIBUTES, new Range(offset, this.attributes.getData().length));
    	
    	// each attribute
    	map.putAll(this.attributes.getOffsetMap(offset));

    	return map;
    }
    
    @Override
    public void setPayload(byte[] data) {
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        this.maxStack = parser.getShortAsInt();
        this.maxLocals = parser.getShortAsInt();
        int codeLength = (int) parser.getInt();
        byte[] codeData = parser.getBytes(codeLength);
        ByteParser newParser = new ByteArrayByteParser(codeData);
        newParser.setBigEndian(true);
        this.code = new Code(newParser, pool);
        this.exceptions = new Exceptions(parser, pool);
        this.attributes = new Attributes(parser, pool);
        LineNumberTableAttribute lnAttr = this.attributes.getLineNumberTable();
        if (lnAttr != null) {
        	lnAttr.setCode(this.code);
        	this.code.setLineNumberTable(lnAttr);
        }
        this.code.insertLabels(this.exceptions.getLabels());
        LocalVariableTableAttribute lvta = this.attributes.getLocalVariableTable();
        this.code.setLocalVariableTable(lvta);
        this.code.setExceptions(this.exceptions);
        if (lvta != null) {
            this.code.insertLabels(lvta.getVariableLabels());
        }
    }

    public Code getCode() {
        return this.code;
    }

    public int getMaxStackSize() {
        return this.maxStack;
    }

    public void setStackSize(int maxStack) {
        this.maxStack = maxStack;
    }

    public void dump(PrintStream out) {
        this.code.printCode(out, this.attributes.getLineNumberTable(), this.attributes.getLocalVariableTable());
    }

    public Attributes getAttributes() {
        return this.attributes;
    }

    public Exceptions getExceptions() {
        return this.exceptions;
    }

    public void setMaxLocals(int locals) {
        this.maxLocals = locals;
    }

    public int getMaxLocals() {
        return this.maxLocals;
    }

    @Override
	public String toString() {
        return "Code(max locals=" + this.maxLocals + ", max stack=" + this.maxStack + ")";
    }

	public void setCode(Code code) {
		this.code = code;
	}

}

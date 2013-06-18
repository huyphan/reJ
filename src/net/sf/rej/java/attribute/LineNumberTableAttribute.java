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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.rej.java.Code;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.instruction.DecompilationContext;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

public class LineNumberTableAttribute extends Attribute {

    /**
     * Keys are pc's and values are source code linenumbers, both instances
     * of integer class. Used for storing the mappings read from the attribute.
     */
    private Map<Integer, Integer> mappings = new HashMap<Integer, Integer>();
    
    private Map<Instruction, Integer> instructionMappings = new HashMap<Instruction, Integer>();
    private Code code = null;
    private boolean validated = false;

    public LineNumberTableAttribute(int nameIndex,
            ConstantPool pool) {
        super(nameIndex, pool);
    }

    public int getLineNumber(int pc) {
    	validateIfNecessary();
    	
        Integer ln = this.mappings.get(pc);
        if (ln != null) {
            return ln.intValue();
        } else {
            return -1;
        }
    }

    @Override
	public byte[] getPayload() {
    	return getLineNumberData();
    }
    
    @Override
    public void setPayload(byte[] data) {
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        int length = parser.getShortAsInt();
        for (int i = 0; i < length; i++) {
            int pc = parser.getShortAsInt();
            int ln = parser.getShortAsInt();
            this.mappings.put(pc, ln);
        }
        this.validated = true;
    }

    public byte[] getLineNumberData() {
    	validateIfNecessary();

    	ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(this.mappings.size());
        for (Entry<Integer, Integer> entry : this.mappings.entrySet()) {
            ser.addShort(entry.getKey());
            ser.addShort(entry.getValue());
        }

        return ser.getBytes();
    }

    @Override
	public String toString() {
        return "Linenumber table(" + this.mappings.size() + " entries)";
    }
    
    /**
     * Sets the relevant code block for determining changes in the linenumber
     * table.
     * @param code
     */
    public void setCode(Code code) {
    	this.code = code;
    	this.instructionMappings.clear();
    	DecompilationContext dc = code.createDecompilationContext();
    	dc.setPosition(0);
    	for (Instruction inst : code.getInstructions()) {
    		Integer srcLine = this.mappings.get(dc.getPosition());
    		if (srcLine != null) {
    			this.instructionMappings.put(inst, srcLine);
    		}
    		dc.incrementPosition(inst);
    	}
    }
    
    /**
     * Informs this line number table that the instructions which are being
     * associated with line numbers have changed and before allowing queries
     * or serialization to be made, the table has to be recalculated. No
     * immediate recalculation is done, however. A lazy approach is used
     * instead. The line number query and attribute serialization methods
     * check for the validity of the table and recalculate it if necessary
     * before returning the requested data.
     */
    public void invalidate() {
    	this.validated = false;
    }
    
    private void validateIfNecessary() {
    	if (!this.validated) {
    		validate();
    	}
    }
    
    private void validate() {
    	// called by code when it is modified
    	this.mappings.clear();
       	DecompilationContext dc = code.createDecompilationContext();
       	dc.setPosition(0);
       	for (Instruction inst : code.getInstructions()) {
       		Integer srcLine = this.instructionMappings.get(inst);
       		if (srcLine != null) {
       			this.mappings.put(dc.getPosition(), srcLine);
       		}
       		dc.incrementPosition(inst);
       	}
       	
       	this.validated = true;
    }

}

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
import java.util.List;

import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.instruction.Label;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

public class LocalVariableTableAttribute extends Attribute {

    private List<LocalVariable> localVariables = new ArrayList<LocalVariable>();

    public LocalVariableTableAttribute(int nameIndex, ConstantPool pool) {
        super(nameIndex, pool);
    }

    public LocalVariable getLocalVariable(int index, int pc) {
        for (LocalVariable lv : this.localVariables) {
            if (lv.getIndex() == index && lv.isInRange(pc)) {
                return lv;
            }
        }

        return null; // no local variable found for the given index
    }

    public List getLocalVariable(int pc) {
        List<LocalVariable> al = new ArrayList<LocalVariable>();
        for (LocalVariable lv : this.localVariables) {
            if (lv.getStartPc() == pc) {
                al.add(lv);
            }
        }

        return al;
    }

    public List<LocalVariable> getLocalVariables() {
        List<LocalVariable> al = new ArrayList<LocalVariable>();
        al.addAll(this.localVariables);

        return al;
    }

    public List<Label> getVariableLabels() {
        List<Label> labels = new ArrayList<Label>();
        for (LocalVariable lv : this.localVariables) {
            labels.addAll(lv.getLabels());
        }

        return labels;
    }

    @Override
    public byte[] getPayload() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(this.localVariables.size());
        for (LocalVariable lv : this.localVariables) {
            ser.addBytes(lv.getData());
        }
        return ser.getBytes();
    }
    
    @Override
    public void setPayload(byte[] data) {
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        int length = parser.getShortAsInt();
        for (int i = 0; i < length; i++) {
            LocalVariable lv = new LocalVariable(parser, pool);
            this.localVariables.add(lv);
        }
    }

    @Override
	public String toString() {
        return "LocalVariableTableAttribute(" + this.localVariables.size() + " definitions)";
    }

}

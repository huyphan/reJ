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
package net.sf.rej.java.instruction;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.Descriptor;
import net.sf.rej.java.JavaType;
import net.sf.rej.java.constantpool.RefInfo;
import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.ByteToolkit;

/**
 * Fetch field from object.
 * 
 * @author Sami Koivu
 */
public class _getfield extends Instruction {

	public static final int OPCODE = 0xb4;

	public static final String MNEMONIC = "getfield";

	private static final int SIZE = 3;

    private static final int STACK_CHANGES = 0;

	private int index = 0;

	public _getfield() {
	}

	@Override
	public int getOpcode() {
		return OPCODE;
	}

	@Override
	public String getMnemonic() {
		return MNEMONIC;
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void execute(ExecutionContext ec) {
	}

	@Override
	public void setData(byte[] data, DecompilationContext dc) {
		this.index = (int) ByteToolkit.getLong(data, 1, 2, true);
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(OPCODE);
		ser.addShort(this.index);
		return ser.getBytes();
	}

	@Override
	public Parameters getParameters() {
		Parameters params = getParameterTypes();
		params.addValue(this.index);
		return params;
	}

	@Override
	public Parameters getParameterTypes() {
		return new Parameters(
				new ParameterType[] { ParameterType.TYPE_CONSTANT_POOL_FIELD_REF });
	}

	@Override
	public void setParameters(Parameters params) {
		this.index = params.getInt(0);
	}

	@Override
	public List<StackElement> getPoppedElements(DecompilationContext dc) {
		List<StackElement> elements = new ArrayList<StackElement>();
		elements.add(new StackElement("objectref", StackElementType.REF));
		return elements;
	}

	@Override
	public List<StackElement> getPushedElements(DecompilationContext dc) {
		List<StackElement> elements = new ArrayList<StackElement>();
		RefInfo ri = (RefInfo) dc.getConstantPool().get(this.index);
		Descriptor desc = ri.getDescriptor();
		JavaType jt = desc.getReturn();
		if (jt.getDimensionCount() > 0 || (!jt.isPrimitive())) {
			// array or primitive are both refs
			elements.add(new StackElement("result", StackElementType.REF));
		} else {
			// primitive non-array
			if (jt.getType().equals("long")) {
				elements.add(new StackElement("result", StackElementType.LONG));				
			} else if (jt.getType().equals("float")) {
				elements.add(new StackElement("result", StackElementType.FLOAT));
			} else if (jt.getType().equals("double")) {
				elements.add(new StackElement("result", StackElementType.DOUBLE));
			} else {
				// boolean, byte, short, char and int are all of type int
				elements.add(new StackElement("result", StackElementType.INT));
			}
		}
		return elements;
	}

    @Override
    public int getStackChanges() {
        return STACK_CHANGES;
    }

}

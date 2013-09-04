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

import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.FloatInfo;
import net.sf.rej.java.constantpool.IntegerInfo;
import net.sf.rej.java.constantpool.StringInfo;
import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.ByteToolkit;

/**
 * Push item from constant pool.
 * 
 * @author Sami Koivu
 */
public class _ldc extends Instruction {

	public static final int OPCODE = 0x12;

	public static final String MNEMONIC = "ldc";

	private static final int SIZE = 2;

    private static final int STACK_CHANGES = 1;

	private int index = 0;

	public _ldc() {
	}

	public _ldc(int index) {
		if (index > 255)
			throw new RuntimeException("_ldc(int) index out of range.");
		this.index = index;
	}

	@Override
	public void setData(byte[] data, DecompilationContext dc) {
		this.index = (int) ByteToolkit.getLong(data, 1, 1, true);
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(OPCODE);
		ser.addByte(this.index);
		return ser.getBytes();
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
	public Parameters getParameters() {
		Parameters params = getParameterTypes();
		params.addValue(this.index);
		return params;
	}

	@Override
	public Parameters getParameterTypes() {
		return new Parameters(
				new ParameterType[] { ParameterType.TYPE_CONSTANT_POOL_CONSTANT });
	}

	@Override
	public void setParameters(Parameters params) {
		this.index = params.getInt(0);

	}

	@Override
	public List<StackElement> getPushedElements(DecompilationContext dc) {
		List<StackElement> elements = new ArrayList<StackElement>();
		ConstantPoolInfo cpi = dc.getConstantPool().get(this.index);
		if (cpi instanceof IntegerInfo) {
			elements.add(new StackElement("value", StackElementType.INT));
		} else if (cpi instanceof FloatInfo) {
			elements.add(new StackElement("value", StackElementType.FLOAT));
		} else if (cpi instanceof StringInfo) {
			elements.add(new StackElement("value", StackElementType.REF));
		} else {
			throw new AssertionError("ldc points to an invalid item on the constant pool: " + cpi.getClass());
		}
		return elements;
	}

    @Override
    public int getStackChanges() {
        return STACK_CHANGES;
    }
}

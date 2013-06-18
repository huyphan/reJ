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
import net.sf.rej.java.constantpool.DoubleInfo;
import net.sf.rej.java.constantpool.LongInfo;
import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.ByteToolkit;

/**
 * Push long or double from constant pool.
 * 
 * @author Sami Koivu
 */
public class _ldc2_w extends Instruction {

	public static final int OPCODE = 0x14;

	public static final String MNEMONIC = "ldc2_w";

	public static final int SIZE = 3;

	private int index = 0;

	public _ldc2_w() {
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
		if (cpi instanceof DoubleInfo) {
			elements.add(new StackElement("value", StackElementType.DOUBLE));
		} else if (cpi instanceof LongInfo) {
			elements.add(new StackElement("value", StackElementType.LONG));
		} else {
			throw new AssertionError("ldc2_w points to an invalid item on the constant pool: " + cpi.getClass());
		}
		return elements;
	}

}

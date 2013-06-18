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

import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.ByteToolkit;

/**
 * Increment local variable by constant.
 * 
 * @author Sami Koivu
 */
public class _iinc extends Instruction implements Widenable {

	public static final int OPCODE = 0x84;

	public static final String MNEMONIC = "iinc";

	private static final int SIZE = 3;

	private static final int WIDE_SIZE = 6;

	private int index = 0;

	private int increment = 0;

	public _iinc() {
	}

	public _iinc(int localVariable, int increment) {
		this.index = localVariable;
		this.increment = increment;
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
		this.index = (int) ByteToolkit.getLong(data, 1, 1, true);
		this.increment = (int) ByteToolkit.getLong(data, 2, 1, true);
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(OPCODE);
		ser.addByte(this.index);
		ser.addByte(this.increment);

		return ser.getBytes();
	}

	public int getWideSize(DecompilationContext dc) {
		return WIDE_SIZE;
	}

	public byte[] getWideData(DecompilationContext dc) {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(_wide.OPCODE);
		ser.addByte(OPCODE);
		ser.addShort(this.index);
		ser.addShort(this.increment);

		return ser.getBytes();
	}

	public void setWideData(byte[] data, DecompilationContext dc) {
		this.index = (int) ByteToolkit.getLong(data, 2, 2, true);
		this.increment = (int) ByteToolkit.getLong(data, 4, 2, true);
	}

	@Override
	public Parameters getParameters() {
		Parameters params = getParameterTypes();
		params.addValue(this.index);
		params.addValue(this.increment);
		return params;
	}

	public Parameters getParametersWide() {
		Parameters params = getParameterTypesWide();
		params.addValue(this.index);
		params.addValue(this.increment);
		return params;
	}

	@Override
	public Parameters getParameterTypes() {
		return new Parameters(new ParameterType[] { ParameterType.TYPE_LOCAL_VARIABLE,
				ParameterType.TYPE_CONSTANT });
	}

	public Parameters getParameterTypesWide() {
		return new Parameters(new ParameterType[] { ParameterType.TYPE_LOCAL_VARIABLE_WIDE,
				ParameterType.TYPE_CONSTANT_WIDE });
	}

	@Override
	public void setParameters(Parameters params) {
		this.index = params.getInt(0);
		this.increment = params.getInt(1);
	}

	public void setParametersWide(Parameters params) {
		this.index = params.getInt(0);
		this.increment = params.getInt(1);
	}

}

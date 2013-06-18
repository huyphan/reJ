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

import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.ByteToolkit;

/**
 * Create new multidimensional array.
 * 
 * @author Sami Koivu
 */
public class _multianewarray extends Instruction {

	public static final int OPCODE = 0xc5;

	public static final String MNEMONIC = "multianewarray";

	private static final int SIZE = 4;

	private int index = 0;

	private int dimensions = 0;

	public _multianewarray() {
	}

	public _multianewarray(int index, int dimensions) {
		this.index = index;
		this.dimensions = dimensions;
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
		this.dimensions = (int) ByteToolkit.getLong(data, 3, 1, true);
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(OPCODE);
		ser.addShort(this.index);
		ser.addByte(this.dimensions);
		return ser.getBytes();
	}

	@Override
	public Parameters getParameters() {
		Parameters params = getParameterTypes();
		params.addValue(this.index);
		params.addValue(this.dimensions);
		return params;
	}

	@Override
	public Parameters getParameterTypes() {
		return new Parameters(new ParameterType[] { ParameterType.TYPE_CONSTANT_POOL_CLASS,
				ParameterType.TYPE_CONSTANT });
	}

	@Override
	public void setParameters(Parameters params) {
		this.index = params.getInt(0);
		this.dimensions = params.getInt(1);
	}

	@Override
	public List<StackElement> getPoppedElements(DecompilationContext dc) {
		List<StackElement> elements = new ArrayList<StackElement>();
		for (int i=0; i < this.dimensions; i++) {
			elements.add(new StackElement("count" + (i+1), StackElementType.INT));			
		}

		return elements;
	}

	@Override
	public List<StackElement> getPushedElements(DecompilationContext dc) {
		List<StackElement> elements = new ArrayList<StackElement>();
		elements.add(new StackElement("arrayref", StackElementType.REF));
		return elements;
	}

}

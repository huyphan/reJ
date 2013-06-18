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
 * Create new array.
 * 
 * @author Sami Koivu
 */
public class _newarray extends Instruction {

	public static final int TYPE_BOOLEAN = 4;

	public static final int TYPE_CHAR = 5;

	public static final int TYPE_FLOAT = 6;

	public static final int TYPE_DOUBLE = 7;

	public static final int TYPE_BYTE = 8;

	public static final int TYPE_SHORT = 9;

	public static final int TYPE_INT = 10;

	public static final int TYPE_LONG = 11;

	public static final int OPCODE = 0xbc;

	public static final String MNEMONIC = "newarray";

	private static final int SIZE = 2;

	private int arrayType = 0;

	public _newarray() {
	}

	public _newarray(int type) {
		this.arrayType = type;
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
		this.arrayType = (int) ByteToolkit.getLong(data, 1, 1, true);
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(OPCODE);
		ser.addByte(this.arrayType);

		return ser.getBytes();
	}

	@Override
	public Parameters getParameters() {
		Parameters params = getParameterTypes();
		params.addValue(this.arrayType);
		return params;
	}

	@Override
	public Parameters getParameterTypes() {
		return new Parameters(new ParameterType[] { ParameterType.TYPE_ARRAYTYPE });
	}

	@Override
	public void setParameters(Parameters params) {
		this.arrayType = params.getInt(0);
	}

	public static String getTypeName(int arrayType) {
		switch (arrayType) {
		case _newarray.TYPE_BOOLEAN:
			return "boolean";
		case _newarray.TYPE_BYTE:
			return "byte";
		case _newarray.TYPE_CHAR:
			return "char";
		case _newarray.TYPE_DOUBLE:
			return "double";
		case _newarray.TYPE_FLOAT:
			return "float";
		case _newarray.TYPE_INT:
			return "int";
		case _newarray.TYPE_LONG:
			return "long";
		case _newarray.TYPE_SHORT:
			return "short";
		default:
			throw new RuntimeException("Invalid arraytype value: " + arrayType);
		}

	}

	@Override
	public List<StackElement> getPoppedElements(DecompilationContext dc) {
		List<StackElement> elements = new ArrayList<StackElement>();
		elements.add(new StackElement("count", StackElementType.INT));			
		return elements;
	}

	@Override
	public List<StackElement> getPushedElements(DecompilationContext dc) {
		List<StackElement> elements = new ArrayList<StackElement>();
		elements.add(new StackElement("arrayref", StackElementType.REF));
		return elements;
	}

}

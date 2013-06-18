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

/**
 * Duplicate the top one or two operand stack values.
 * 
 * @author Sami Koivu
 */
public class _dup2 extends Instruction {

	public static final int OPCODE = 0x5c;

	public static final String MNEMONIC = "dup2";

	private static final int SIZE = 1;

	public _dup2() {
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
	public byte[] getData(DecompilationContext dc) {
		return new byte[] { OPCODE };
	}

	@Override
	public Parameters getParameterTypes() {
		return Parameters.EMPTY_PARAMS;
	}

	@Override
	public Parameters getParameters() {
		return Parameters.EMPTY_PARAMS;
	}

	@Override
	public void setData(byte[] data, DecompilationContext dc) {
	}

	@Override
	public void setParameters(Parameters params) {
	}

	@Override
	public List<StackElement> getPoppedElements(DecompilationContext dc) {
		// TODO: this is not the perfect solution, pairs can also be one CAT2
		List<StackElement> elements = new ArrayList<StackElement>();
		elements.add(new StackElement("value2", StackElementType.ANY));
		elements.add(new StackElement("value1", StackElementType.ANY));
		return elements;
	}

	@Override
	public List<StackElement> getPushedElements(DecompilationContext dc) {
		// TODO: this is not the perfect solution, pairs can also be one CAT2
		List<StackElement> elements = new ArrayList<StackElement>();
		elements.add(new StackElement("value2", StackElementType.ANY));
		elements.add(new StackElement("value1", StackElementType.ANY));
		elements.add(new StackElement("value2", StackElementType.ANY));
		elements.add(new StackElement("value1", StackElementType.ANY));
		return elements;
	}

}

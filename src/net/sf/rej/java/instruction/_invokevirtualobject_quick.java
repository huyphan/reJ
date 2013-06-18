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

public class _invokevirtualobject_quick extends Instruction {

	public static final int OPCODE = 0xdb;

	public static final String MNEMONIC = "invokevirtualobject_quick";

	private static final int SIZE = 3;

	public _invokevirtualobject_quick() {
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
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		return null;
	}

	@Override
	public Parameters getParameters() {
		return null;
	}

	@Override
	public Parameters getParameterTypes() {
		return null;
	}

	@Override
	public void setParameters(Parameters params) {
	}

}

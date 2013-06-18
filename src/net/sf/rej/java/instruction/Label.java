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

/**
 * The concept "label" does not exist in the java bytecode. Instead it's created
 * in the ReJ API to help the user of the API to avoid having to manipulate
 * relative pc position values, whenever an instruction is added or removed
 * between for example between a goto instruction and it's target. The
 * positition would have to be recalculated every time and with non-fixed size
 * instructions the task would not be trivial. Logically a Label is not an
 * instruction, but since it shares a lot of functionality with instructions,
 * making the class extend Instruction was a practical (lazy) choice.
 * 
 * @author Sami Koivu
 */

public class Label extends Instruction {

	private int pos;

	private String id;

	public Label(int pos) {
		this.pos = pos;
		this.id = "label_" + pos;
	}

	public Label(int pos, String id) {
		this.pos = pos;
		this.id = id;
	}

	public void setPosition(int pos) {
		this.pos = pos;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public int getOpcode() {
		throw new RuntimeException("No opcode for label");
	}

	@Override
	public String getMnemonic() {
		return "label" + this.pos;
	}

	@Override
	public void execute(ExecutionContext ec) {
	}

	public int getPosition() {
		return this.pos;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return "label at position " + this.pos;
	}

	@Override
	public void setData(byte[] data, DecompilationContext dc) {
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		return new byte[0];
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
	public void setParameters(Parameters params) {
	}

}

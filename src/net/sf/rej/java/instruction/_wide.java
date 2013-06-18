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

import net.sf.rej.java.InstructionSet;
import net.sf.rej.util.ByteParser;

/**
 * The first form of the wide instruction modifies one of the instructions
 * iload, fload, aload, lload, dload, istore, fstore, astore, lstore, dstore, or
 * ret. The second form applies only to the iinc instruction.
 */

public class _wide extends Instruction {

	public static final int OPCODE = 0xc4;

	public static final String MNEMONIC = "wide";

	private int opcode;

	private Instruction instruction;

	private int size = -1;

	public _wide() {
	}
	
	public _wide(Instruction instruction) {
		this.instruction = instruction;
	}

	@Override
	public int getOpcode() {
		return OPCODE;
	}

	@Override
	public String getMnemonic() {
		return MNEMONIC + " " + this.instruction.getMnemonic();
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public int getSize(DecompilationContext dc) {
		if (this.instruction != null)
			return ((Widenable) this.instruction).getWideSize(dc);

		ByteParser parser = dc.getParser();
		parser.getByte(); // throw away 0xc4(wide)
		this.opcode = parser.peekByte();
		this.instruction = InstructionSet.getInstance().getInstruction(
				this.opcode);
		if (!(this.instruction instanceof Widenable))
			throw new RuntimeException("Instruction "
					+ this.instruction.getMnemonic() + " is not Widenable.");
		int length = ((Widenable) this.instruction).getWideSize(dc);

		this.size = length;
		return this.size;
	}

	@Override
	public void execute(ExecutionContext ec) {

	}

	@Override
	public void setData(byte[] data, DecompilationContext dc) {
		((Widenable) this.instruction).setWideData(data, dc);
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		return ((Widenable) this.instruction).getWideData(dc);
	}

	@Override
	public Parameters getParameters() {
		return ((Widenable) this.instruction).getParametersWide();
	}

	@Override
	public Parameters getParameterTypes() {
		return ((Widenable) this.instruction).getParameterTypesWide();
	}

	@Override
	public void setParameters(Parameters params) {
		((Widenable) this.instruction).setParametersWide(params);
	}
	
	@Override
	public Instruction createNewInstance() throws InstantiationException, IllegalAccessException {
		return new _wide(this.instruction.createNewInstance());
	}
	
	public Instruction getInstruction() {
		return this.instruction;
	}

}

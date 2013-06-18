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
package net.sf.rej.gui.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.gui.Undoable;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Code;
import net.sf.rej.java.InstructionCopier;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Label;

public class InsertCodeAction implements Undoable {
	
	private ClassFile cf;
	private Code targetCode;
	private int pos;
	private Code newCode;
	
	private int oldPoolSize = -1;
	private List<Instruction> addedInstructions = new ArrayList<Instruction>();

	public InsertCodeAction(ClassFile cf, Code targetCode, int pos, Code newCode) {
		this.cf = cf;
		this.targetCode = targetCode;
		this.pos = pos;
		this.newCode = newCode;
	}

	public void execute() {
		this.oldPoolSize = this.cf.getPool().size();
		List<Instruction> list = new ArrayList<Instruction>();
		for (Instruction inst : this.newCode.getInstructions()) {
			list.add(inst);
		}
		InstructionCopier instructionCopier = new InstructionCopier();
		for (Instruction inst : this.newCode.getInstructions()) {
			if (inst instanceof Label) continue;
			
			Instruction copy = instructionCopier.copyInstruction(inst, this.newCode.createDecompilationContext().getConstantPool(), this.cf.getPool());
			this.addedInstructions.add(copy);
			int index = list.indexOf(inst);
			list.set(index, copy);
			List<Label> labels = inst.getLabels();
			List<Label> copyLabels = copy.getLabels();
			for (int i=0; i < labels.size(); i++) {
				Label label = labels.get(i);
				int labelIndex = list.indexOf(label);
				if (labelIndex != -1) {
					list.set(labelIndex, copyLabels.get(i));
				} else {
					list.add(copyLabels.get(i));
				}
			}

		}
		
		this.targetCode.add(this.pos, list);
	}

	public void undo() {
		for (Instruction inst : this.addedInstructions) {
			this.targetCode.remove(inst);
		}
		
		ConstantPool cp = this.cf.getPool();
		while (cp.size() > this.oldPoolSize) {
			cp.removeLast();
		}
	}

}

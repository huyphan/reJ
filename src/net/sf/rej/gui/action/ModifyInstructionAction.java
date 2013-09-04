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

import net.sf.rej.gui.Undoable;
import net.sf.rej.java.Code;
import net.sf.rej.java.Method;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Parameters;

public class ModifyInstructionAction implements Undoable {

    private Instruction newInstruction;
    private Instruction oldInstruction;
    private int pc;
    private Code code = null;
    private Method method = null;

    public ModifyInstructionAction(Instruction instruction, int pc, Code code) {
        this.newInstruction = instruction;
        this.pc = pc;
        this.code = code;
    }

    public ModifyInstructionAction(Instruction instruction, int pc, Method method) {
        this.newInstruction = instruction;
        this.pc = pc;
        this.method = method;
    }

    public void execute() {
        if (this.method != null) {
            this.code = method.getAttributes().getCode().getCode();
        }
        this.oldInstruction = this.code.getInstructionAtPC(this.pc);
        this.code.modifyInstructionAtPC(this.pc, this.newInstruction);
    }

    public void undo() {
        if (this.method != null) {
            this.code = method.getAttributes().getCode().getCode();
        }
        this.code.modifyInstructionAtPC(this.pc, this.oldInstruction);
    }

}
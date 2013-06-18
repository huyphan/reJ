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

public class InsertInstructionAction implements Undoable {

    private Instruction instruction;
    private int pc;
    private Code code = null;
    private Method method = null;

    public InsertInstructionAction(Instruction instruction, int pc, Code code) {
        this.instruction = instruction;
        this.pc = pc;
        this.code = code;
    }
    
    public InsertInstructionAction(Instruction instruction, int pc, Method method) {
        this.instruction = instruction;
        this.pc = pc;
        this.method = method;
	}

    public void execute() {
    	if (this.method != null) {
    		this.code = method.getAttributes().getCode().getCode();
    	}
        this.code.addInstructionAtPC(this.pc, this.instruction);
        this.code.updateLabelPositions();
    }

    public void undo() {
    	if (this.method != null) {
    		this.code = method.getAttributes().getCode().getCode();
    	}
        this.code.remove(this.instruction);
        this.code.updateLabelPositions();
    }

}
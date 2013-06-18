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
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Parameters;

public class AddClassRefAction implements Undoable {
    private String className;
    private Instruction instruction;
    private int paramIndex;
    private ConstantPool cp;

    private int createdClassRef;
    private int originalParamValue;

    public AddClassRefAction(String className, Instruction instr, int index, ConstantPool cp) {
        this.className = className;
        this.instruction = instr;
        this.paramIndex = index;
        this.cp = cp;
    }

    public void execute() {
        this.createdClassRef = this.cp.forceAddClassRef(this.className);
        Parameters params = this.instruction.getParameters();
        this.originalParamValue = params.getInt(this.paramIndex);
        params.setValue(this.paramIndex, Integer.valueOf(this.createdClassRef));
        this.instruction.setParameters(params);
    }

    public void undo() {
        Parameters params = this.instruction.getParameters();
        params.setValue(this.paramIndex, Integer.valueOf(this.originalParamValue));
        this.instruction.setParameters(params);
        this.cp.removeLast();
    }

}

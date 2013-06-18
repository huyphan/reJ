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

// TODO: this class is too complicated -- do something about it
public class AddMethodRefAction implements Undoable {
    private String className;
    private String methodName;
    private String typeName;
    private Instruction instruction;
    private int paramIndex;
    private ConstantPool pool;

    private int createdNameAndTypeIndex = -1;
    private int createdClassIndex = -1;
    private int createdRefIndex = -1;

    private int originalParamValue = -1;

    public AddMethodRefAction(String className, String methodName, String typeName, Instruction instruction, int i, ConstantPool pool) {
        this.className = className;
        this.methodName = methodName;
        this.typeName = typeName;
        this.instruction = instruction;
        this.paramIndex = i;
        this.pool = pool;
    }

    public void execute() {
        int index = this.pool.indexOfNameAndTypeRef(this.methodName, this.typeName);
        if (index == -1) {
            this.createdNameAndTypeIndex = this.pool.optionalAddNameAndTypeRef(this.methodName, this.typeName);
        }

        index = this.pool.indexOfClassRef(this.className);
        if (index == -1) {
            this.createdClassIndex = this.pool.optionalAddClassRef(this.className);
        }

        index = this.pool.indexOfMethodRef(this.className, this.methodName, this.typeName);
        if (index == -1) {
            this.createdRefIndex = this.pool.optionalAddMethodRef(this.className, this.methodName, this.typeName);
            index = this.createdRefIndex;
        }

        Parameters params = this.instruction.getParameters();
        this.originalParamValue = params.getInt(this.paramIndex);
        params.setValue(this.paramIndex, Integer.valueOf(index));
        this.instruction.setParameters(params);
    }

    public void undo() {
        if (this.createdRefIndex != -1) {
            this.pool.removeLast();
        }

        if (this.createdClassIndex != -1) {
            this.pool.removeLast();
        }

        if (this.createdNameAndTypeIndex != -1) {
            this.pool.removeLast();
        }

        Parameters params = this.instruction.getParameters();
        params.setValue(this.paramIndex, Integer.valueOf(this.originalParamValue));
        this.instruction.setParameters(params);
    }

}

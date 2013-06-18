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
package net.sf.rej.gui.editor.row;

import net.sf.rej.gui.editor.Breakpoint;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Code;
import net.sf.rej.java.instruction.DecompilationContext;
import net.sf.rej.java.instruction.Instruction;

public class CodeRow implements EditorRow {

    private MethodDefRow method;
    private Instruction instruction;
    private int pos = 0;
    private Integer lineNumber = null;
    private DecompilationContext dc;
    private Code code = null;
    private Breakpoint breakpoint = null;
    private boolean executionRow = false;

    public CodeRow(ClassFile cf, MethodDefRow method, Instruction instruction) {
        this.method = method;
        this.instruction = instruction;
    }

    public void setPosition(int pos) {
        this.pos = pos;
    }

    public void setLineNumber(int ln) {
        this.lineNumber = Integer.valueOf(ln);
    }

    public Instruction getInstruction() {
        return this.instruction;
    }

    public int getPosition() {
        return this.pos;
    }

    /**
     * Returns the source line number, or -1 if no information about source
     * line numbers is available.
     * @return the source code line number.
     */
    public int getLineNumber() {
    	if (hasLineNumber()) {
    		return this.lineNumber.intValue();
    	}

    	return -1;
    }

    public boolean hasLineNumber() {
        return this.lineNumber != null;
    }

    public void setDecompilationContext(DecompilationContext dc) {
        this.dc = dc;
    }

    public DecompilationContext getDecompilationContext() {
        return this.dc;
    }

    public void setParentCode(Code code) {
        this.code = code;
    }

    public Code getParentCode() {
        return this.code;
    }

    public MethodDefRow getEnclosingMethodDef() {
        return this.method;
    }

	public Breakpoint getBreakpoint() {
		return this.breakpoint;
	}
	
	public void setBreakpoint(Breakpoint bp) {
		this.breakpoint = bp;
	}

	public void setExecutionRow(boolean executionRow) {
		this.executionRow = executionRow;
	}
	
	public boolean isExecutionRow() {
		return this.executionRow;
	}

}

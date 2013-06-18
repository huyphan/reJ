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

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Method;

public class MethodDefRow implements EditorRow {

	private ClassFile cf;
	private Method method;
	private boolean openTag;
	private boolean hasBody = false;
	private boolean executionRow = false;

	// hierarchial access to rows in editor
	private List<LocalVariableDefRow> localVariables = new ArrayList<LocalVariableDefRow>();

	private List<EditorRow> codeRows = new ArrayList<EditorRow>();

	public MethodDefRow(ClassFile cf, Method method, boolean openTag, boolean hasBody) {
		this.cf = cf;
		this.method = method;
		this.openTag = openTag;
		this.hasBody = hasBody;
	}

	public Method getMethod() {
		return this.method;
	}

	public boolean isClosing() {
		return !this.openTag;
	}

	public void addLocalVariable(LocalVariableDefRow lvdr) {
		this.localVariables.add(lvdr);
	}

	public void addCodeRow(EditorRow er) {
		this.codeRows.add(er);
	}

	public List<EditorRow> getCodeRows() {
		return this.codeRows;
	}

	public List<LocalVariableDefRow> getLocalVariables() {
		return this.localVariables;
	}

	public ClassFile getClassFile() {
		return this.cf;
	}
	
	public boolean hasBody() {
		return this.hasBody;
	}

	public void setExecutionRow(boolean executionRow) {
		this.executionRow = executionRow;
	}
	
	public boolean isExecutionRow() {
		return this.executionRow;
	}

}

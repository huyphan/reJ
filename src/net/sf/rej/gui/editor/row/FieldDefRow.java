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

import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Field;

public class FieldDefRow implements EditorRow {

	private ClassFile cf;
	private Field field;

	public FieldDefRow(ClassFile cf, Field field) {
		this.cf = cf;
		this.field = field;
	}

	public Field getField() {
		return this.field;
	}

	public ClassFile getClassFile() {
		return this.cf;
	}

}

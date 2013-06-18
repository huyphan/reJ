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

/**
 * Import definition rows. Instances are import statement rows in the bytecode
 * editor.
 * 
 * @author Sami Koivu
 */
public class ImportDefRow implements EditorRow {

	private String imp;

	public ImportDefRow(String imp) {
		this.imp = imp;
	}

	public String getImport() {
		return this.imp;
	}

	@Override
	public String toString() {
		return "import " + this.imp + ";";
	}

}

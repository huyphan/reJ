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
package net.sf.rej.gui.compare;

public class FileItem {
	private String file;
	private String fullNameA;
	private String fullNameB;
	private Style style;
	
	public FileItem(String file, String fullName) {
		this.file = file;
		this.fullNameA = fullName;
		this.fullNameB = fullName;
	}
	
	public FileItem(String fileA, String fileB, String fullNameA, String fullNameB) {
		this.file = fileA + " / " + fileB;
		this.fullNameA = fullNameA;
		this.fullNameB = fullNameB;
	}

	public String getFullNameA() {
		return this.fullNameA;
	}

	public String getFullNameB() {
		return this.fullNameB;
	}

	@Override
	public String toString() {
		return file;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

}

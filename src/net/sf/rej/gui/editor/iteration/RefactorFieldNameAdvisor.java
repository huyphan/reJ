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
package net.sf.rej.gui.editor.iteration;

import net.sf.rej.java.Descriptor;

public class RefactorFieldNameAdvisor extends RefactoringAdvisorAdapter {
	private String className;
	private Descriptor desc;
	private String oldFieldName;
	private String newFieldName;

	public RefactorFieldNameAdvisor(String className, Descriptor desc, String oldFieldName, String newFieldName) {
		this.className = className;
		this.desc = desc;
		this.oldFieldName = oldFieldName;
		this.newFieldName = newFieldName;
	}
	
	@Override
	public String newFieldNameFor(String className, String fieldName, Descriptor descriptor) {
		boolean classNameMatch = this.className.equals(className);
		boolean methodNameMatch = this.oldFieldName.equals(fieldName);
		boolean descMatch = this.desc.equals(descriptor);
		if (classNameMatch && methodNameMatch && descMatch) {
			return this.newFieldName;
		} else {
			return null;
		}
	}

}

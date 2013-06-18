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

import java.util.HashMap;
import java.util.List;

import net.sf.rej.obfuscation.NameSequence;

public class ClassNameObfuscatingAdvisor extends RefactoringAdvisorAdapter {

	private HashMap<String, String> classRenames = new HashMap<String, String>();
	private NameSequence classNameSequence = new NameSequence();
	
	public ClassNameObfuscatingAdvisor(List<String> classNames) {
		for (String className : classNames) {
			String newClassName = this.classNameSequence.getNextName();
			this.classRenames.put(className, newClassName);
		}
	}

	@Override
	public String newClassNameFor(String fullClassName) {
		String newClassName = this.classRenames.get(fullClassName);
		if (newClassName == null) {
			newClassName = fullClassName;
		}
		
		return newClassName;
	}

}

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ClassNameLegalizingAdvisor extends RefactoringAdvisorAdapter {
	
	private static final Logger logger = Logger.getLogger(ClassNameLegalizingAdvisor.class.getName());

	private static final String[] KEYWORDS = {"abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"};
	private static List<String> KEYWORDLIST;
	static {
		KEYWORDLIST = Arrays.asList(KEYWORDS);
	}
	
	private HashMap<String, String> classRenames = new HashMap<String, String>();
	
	public ClassNameLegalizingAdvisor(List<String> classNames) {
		for (String className : classNames) {
			String[] components = className.split("\\.");
			StringBuffer newName = new StringBuffer();
			for (String component : components) {
				if (newName.length() > 0) {
					newName.append(".");
				}
				
				if (KEYWORDLIST.contains(component)) {
					newName.append("_");
				}
				newName.append(component);
			}
			
			// TODO: if class name was package.if and there already existed a package._if this code will cause an ambiguity error
			if (!className.equals(newName.toString())) {
				this.classRenames.put(className, newName.toString());
				logger.fine("Class rename: " + className + " => " + newName);
			}
		}
	}

	@Override
	public String newClassNameFor(String fullClassName) {
		String newClassName = this.classRenames.get(fullClassName);
		
		return newClassName;
	}
	
}

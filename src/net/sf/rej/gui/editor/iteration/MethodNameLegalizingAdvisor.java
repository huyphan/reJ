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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.sf.rej.java.Descriptor;
import net.sf.rej.obfuscation.NameSequence;

public class MethodNameLegalizingAdvisor extends RefactoringAdvisorAdapter {
	
	private static final Logger logger = Logger.getLogger(MethodNameLegalizingAdvisor.class.getName());

	private static final String[] KEYWORDS = {"abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"};
	private static List<String> KEYWORDLIST;
	static {
		KEYWORDLIST = Arrays.asList(KEYWORDS);
	}
	
	
	private List<String> classNames;
	private List<String> methodNames = new ArrayList<String>();
	private HashMap<Key, String> methodRenames = new HashMap<Key, String>();
	private NameSequence methodNameSequence = new NameSequence();
	
	public MethodNameLegalizingAdvisor(List<String> classNames) {
		this.classNames = classNames;
	}

	@Override
	public String newMethodNameFor(String className, String targetName, Descriptor desc) {
		if (this.classNames.contains(className)) {
			// has a new name been set for this method already?
			Key key = new Key(className, targetName, desc);
			String newMethodName = this.methodRenames.get(key);
			if (newMethodName == null) {
				if (KEYWORDLIST.contains(targetName)) {
					// illegal name, let's rename
					newMethodName = getUniqueMethodName(className, desc);
					this.methodRenames.put(key, newMethodName);
					this.methodNames.add(className + "." + newMethodName + " " + desc.getRawParams());
					logger.fine("Method rename (illegal name): " + targetName + " => " +newMethodName);
				} else if (this.methodNames.contains(className + "." + targetName + " " + desc.getRawParams())) {
					// name in use already, let's rename
					newMethodName = getUniqueMethodName(className, desc);
					this.methodRenames.put(key, newMethodName);
					this.methodNames.add(className + "." + newMethodName + " " + desc.getRawParams());
					logger.fine("Method rename (duplicate name): " + targetName + " => " +newMethodName);
				} else {
					// don't need to do anything to the field name, but let's set the new name
					// for this field as the old name
					this.methodRenames.put(key, targetName);
					this.methodNames.add(className + "." + targetName + " " + desc.getRawParams());
				}
				return newMethodName;
			} else {
				// a new name has already been set for the method
				return newMethodName;
			}
		} else {
			return null;
		}
	}
	
	public String getUniqueMethodName(String className, Descriptor desc) {
		while (true) {
			String candidate = this.methodNameSequence.getNextName();
			if (!this.methodNames.contains(className + "." + candidate + " " + desc.getRawParams())) {
				return candidate;
			}
		}
	}

	public class Key {
		private String className;
		private String targetName;
		private Descriptor desc;
		
		public Key(String className, String targetName, Descriptor desc) {
			this.className = className;
			this.targetName = targetName;
			this.desc = desc;
		}
		
		@Override
		public int hashCode() {
			int hashCode = 0;
			hashCode += this.className.hashCode();
			hashCode *= 63;
			hashCode += this.targetName.hashCode();
			hashCode *= 63;
			hashCode += this.desc.hashCode();
			
			return hashCode;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key)) return false;
			
			Key key = (Key)obj;
			
			return this.className.equals(key.className)
			    && this.targetName.equals(key.targetName)
			    && this.desc.equals(key.desc);
		}
	}
}

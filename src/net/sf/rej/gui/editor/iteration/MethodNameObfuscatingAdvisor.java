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

import net.sf.rej.java.Descriptor;
import net.sf.rej.obfuscation.NameSequence;

public class MethodNameObfuscatingAdvisor extends RefactoringAdvisorAdapter {

	@SuppressWarnings("unused")
	private List<String> classNames;
	@SuppressWarnings("unused")
	private HashMap<Key, String> methodRenames = new HashMap<Key, String>();
	@SuppressWarnings("unused")
	private NameSequence methodNameSequence = new NameSequence();
	
	public MethodNameObfuscatingAdvisor(List<String> classNames) {
		this.classNames = classNames;
	}

	@Override
	public String newMethodNameFor(String className, String targetName, Descriptor desc) {
		/*
		 * TODO: Needs logic to check which methods are overriding superclass
		 * methods, or implementing an interface method in order to avoid
		 * renaming a method that is part of an interface which exists
		 * outside the project.
		 */
		return null;
		
		/*if (classNames.contains(className)) {
			Key key = new Key(className, targetName, desc);
			String newMethodName = this.methodRenames.get(key);
			if (newMethodName == null) {
				newMethodName = this.methodNameSequence.getNextName();
				this.methodRenames.put(key, newMethodName);
			}
			
			return newMethodName;
		} else {
			return null;
		}*/
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

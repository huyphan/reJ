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

public class FieldNameObfuscatingAdvisor extends RefactoringAdvisorAdapter {

	@SuppressWarnings("unused")
	private List<String> classNames;
	@SuppressWarnings("unused")
	private HashMap<Key, String> fieldRenames = new HashMap<Key, String>();
	@SuppressWarnings("unused")
	private NameSequence fieldNameSequence = new NameSequence();
	
	public FieldNameObfuscatingAdvisor(List<String> classNames) {
		this.classNames = classNames;
	}
	
	@Override
	public String newFieldNameFor(String className, String targetName, Descriptor desc) {
		/*
		 * TODO: Needs logic to check which fields are referring to superclasses, or interfaces.
		 */

		return null;
		// only change the types that are a part of the project
		/*if (classNames.contains(className)) {
			Key key = new Key(className, targetName, desc);
			String newFieldName = this.fieldRenames.get(key);
			if (newFieldName == null) {
				newFieldName = this.fieldNameSequence.getNextName();
				this.fieldRenames.put(key, newFieldName);
				//System.out.println("Field rename: " + targetName + " => " + newFieldName);
				if (targetName.equals("selectedArchive")) {
					System.out.println(newFieldName + " = " + desc.getRawDesc());
				}
			}
			
			return newFieldName;
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

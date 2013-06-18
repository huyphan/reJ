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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Keywords;
import net.sf.rej.obfuscation.NameSequence;

public class FieldNameLegalizingAdvisor extends RefactoringAdvisorAdapter {

	private static final Logger logger = Logger.getLogger(FieldNameLegalizingAdvisor.class.getName());
	
	private List<String> classNames;
	private List<String> fieldNames = new ArrayList<String>();
	private HashMap<Key, String> fieldRenames = new HashMap<Key, String>();
	private NameSequence fieldNameSequence = new NameSequence();
	
	public FieldNameLegalizingAdvisor(List<String> classNames) {
		this.classNames = classNames;
	}
	
	@Override
	public String newFieldNameFor(String className, String targetName, Descriptor desc) {
		if (this.classNames.contains(className)) {
			// has a new name been set for this field already?
			Key key = new Key(className, targetName, desc);
			String newFieldName = this.fieldRenames.get(key);
			if (newFieldName == null) {
				if (Keywords.isKeyword(targetName)) {
					// illegal name, let's rename
					newFieldName = getUniqueFieldName(className);
					this.fieldRenames.put(key, newFieldName);
					this.fieldNames.add(className + "." + newFieldName);
					logger.fine("Field rename (illegal name): " + targetName + " => " +newFieldName);
				} else if (this.fieldNames.contains(className + "." + targetName)) {
					// name in use already, let's rename
					newFieldName = getUniqueFieldName(className);
					this.fieldRenames.put(key, newFieldName);
					this.fieldNames.add(className + "." + newFieldName);
					logger.fine("Field rename (duplicate name): " + targetName + " => " +newFieldName);
				} else {
					// don't need to do anything to the field name, but let's set the new name
					// for this field as the old name
					this.fieldRenames.put(key, targetName);
					this.fieldNames.add(className + "." + targetName);
				}
				return newFieldName;
			} else {
				// a new name has already been set for the field
				return newFieldName;
			}
		} else {
			return null;
		}
	}
	
	public String getUniqueFieldName(String className) {
		while (true) {
			String candidate = this.fieldNameSequence.getNextName();
			if (!this.fieldNames.contains(className + "." + candidate)) {
				return candidate;
			}
		}
	}

	public static class Key {
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

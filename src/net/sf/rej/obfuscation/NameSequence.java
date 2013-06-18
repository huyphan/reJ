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
package net.sf.rej.obfuscation;

import net.sf.rej.java.Keywords;


/**
 * A class that generates a sequence of names (that are all legal java
 * identifiers, that is, they are not keywords)
 * 
 * @author Sami Koivu
 */
public class NameSequence {
	
	private int index = 0;
	private String charSet = "abcdefghijklmnopqrstuvwxyz";
	
	public NameSequence() {
		// do-nothing constructor
	}
	
	public String getNextName() {
		while (true) {
			String candidate = internalGetNextName();
			if (!Keywords.isKeyword(candidate)) {
				return candidate;
			}
		}
	}
	
	public String internalGetNextName() {
		StringBuffer sb = new StringBuffer();
		
		int i = this.index;
		int size = this.charSet.length();
		while (true) {
			int mod = i % size;
			sb.insert(0, this.charSet.charAt(mod));
			i -= mod;
			i /= size;
			
			if (i == 0) break;
		}
		
		index++;
		return sb.toString();
	}
}

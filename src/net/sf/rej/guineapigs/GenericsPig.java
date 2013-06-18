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
package net.sf.rej.guineapigs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenericsPig<E> {
	public GenericsPig(E o) {
		List<String> list = new ArrayList<String>();
		list.clear();
	}
	
	public E method(E o) {
		E s = null;
		return s;
	}
	
	public <G> G[] method2(G g) {
		return null;
	}
	
	public void method3(List<E> list, HashMap<String, Integer> list2) {
		
	}
	
	 public static <E extends Number> List<? super E> process(List<E> nums) {
		 return null;
	 }
	 
}

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


//@AnnotationTypePig
//@Documented
//@Deprecated
//@Inherited
//@Override
//@Retention(RetentionPolicy.CLASS)
//@Target(ElementType.TYPE)

@SuppressWarnings("unchecked")
public class AnnotationUserPig {
	
	@AnnotationTypePig2(value="asdf", test=0)
	public void method() {
		System.out.println();
		@AnnotationTypePig2(value="bbbb", test=91)
		int i = 121;
		i++;
		System.out.println(i);
	}
	
}

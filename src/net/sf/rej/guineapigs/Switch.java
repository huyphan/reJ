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

public class Switch {

	public Switch() {
	}

	public void tableswitch() {

		int i = 3;

		switch (i) {
		case 0:
			System.out.println("asdf");
			break;
		case 1:
			System.out.println("ddd");
			break;
		case 2:
			System.out.println("ddd");
			break;
		default:
			System.out.println("jeejee");
			break;
		}
	}

	public void lookupswitch() {
		int i = 13;
		switch (i) {
		case 0:
			i = 0;
			break;
		case 500:
			i = 500;
			break;
		case 1500:
			i = 1500;
			break;
		}
	}
}

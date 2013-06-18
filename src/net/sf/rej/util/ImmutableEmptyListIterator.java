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
package net.sf.rej.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ImmutableEmptyListIterator<E> implements ListIterator<E> {

	public void add(E o) {
	}

	public boolean hasNext() {
		return false;
	}

	public boolean hasPrevious() {
		return false;
	}

	public E next() {
		throw new NoSuchElementException("Empty list.");
	}

	public int nextIndex() {
		return 0;
	}

	public E previous() {
		throw new NoSuchElementException("Empty list.");
	}

	public int previousIndex() {
		return -1;
	}

	public void remove() {
	}

	public void set(E o) {
	}

}

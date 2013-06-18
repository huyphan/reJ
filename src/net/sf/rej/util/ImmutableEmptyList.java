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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ImmutableEmptyList<E> implements List<E> {
	
	public boolean add(Object o) {
		return false;
	}

	public void add(int index, Object element) {
	}

	public boolean addAll(Collection c) {
		return false;
	}

	public boolean addAll(int index, Collection c) {
		return false;
	}

	public void clear() {
	}

	public boolean contains(Object o) {
		return false;
	}

	public boolean containsAll(Collection c) {
		return c.size() == 0;
	}

	public E get(int index) {
		throw new IndexOutOfBoundsException("Index: " + index + " Size: 0");
	}

	public int indexOf(Object o) {
		return -1;
	}

	public boolean isEmpty() {
		return true;
	}

	public Iterator<E> iterator() {
		return new ImmutableEmptyIterator<E>();
	}

	public int lastIndexOf(Object o) {
		return -1;
	}

	public ListIterator<E> listIterator() {
		return new ImmutableEmptyListIterator<E>();
	}

	public ListIterator<E> listIterator(int index) {
		return new ImmutableEmptyListIterator<E>();
	}

	public boolean remove(Object o) {
		return false;
	}

	public E remove(int index) {
		throw new IndexOutOfBoundsException("Index: " + index + " Size: 0");
	}

	public boolean removeAll(Collection c) {
		return false;
	}

	public boolean retainAll(Collection c) {
		return false;
	}

	public E set(int index, Object element) {
		throw new IndexOutOfBoundsException("Index: " + index + " Size: 0");
	}

	public int size() {
		return 0;
	}

	public List<E> subList(int fromIndex, int toIndex) {
		if (fromIndex == 0 && toIndex == 0) {
			return this;
		} else {
			throw new IndexOutOfBoundsException("Size: 0");
		}
	}

	public Object[] toArray() {
		return new Object[0];
	}

	public <T> T[] toArray(T[] a) {
		return a;
	}

}

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

/**
 * Wrapper class
 *
 * A wrapper class used in Swing classes(JList, JTable,
 *         JComboBox,...) that has a separate textual display String and content
 *         object.
 * @param <E> the type of the value-object being wrapped.
 */

public class Wrapper<E> {
    private E wrapped;
    private String display;

    public Wrapper() {
    }

    public void setContent(E o) {
        this.wrapped = o;
    }

    public E getContent() {
        return this.wrapped;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
	public String toString() {
        return this.display;
    }

    @Override
	public boolean equals(Object other) {
        if (!(other instanceof Wrapper))
            return false;

        Wrapper otherWrapper = (Wrapper) other;
        return (otherWrapper.wrapped.equals(this.wrapped));
    }

    @Override
	public int hashCode() {
        return this.wrapped.hashCode();
    }

}

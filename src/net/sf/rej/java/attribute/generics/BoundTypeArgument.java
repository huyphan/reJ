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
package net.sf.rej.java.attribute.generics;

/**
 * A <code>TypeArgument</code> that is bound to a reference type. It is
 * either upper bound or lower bound.
 * 
 * @author Sami Koivu
 */
public class BoundTypeArgument implements TypeArgument {
	public static enum Bound {UPPER_BOUND, LOWER_BOUND}
	
	private GenericJavaType type;
	private Bound bound;
	
	public BoundTypeArgument(GenericJavaType type, Bound bound) {
		this.type = type;
		this.bound = bound;
	}

	public BoundTypeArgument(GenericJavaType type, char bound) {
		this.type = type;
		this.bound = bound == '+' ? Bound.UPPER_BOUND : Bound.LOWER_BOUND;
	}
	
	@Override
	public String toString() {
		if (this.bound == Bound.UPPER_BOUND) {
			return "? extends " + this.type;
		} else {
			return "? super " + this.type;
		}
	}
	
	public String getBoundString() {
		if (this.bound == Bound.UPPER_BOUND) {
			return "extends";
		} else {
			return "super";
		}		
	}

	public GenericJavaType getBound() {
		return this.type;
	}
}

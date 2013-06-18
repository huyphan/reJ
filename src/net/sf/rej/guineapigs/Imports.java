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

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.security.AllPermission;
import java.util.ArrayList;

public abstract class Imports {

	public Imports() {
		java.sql.Date date1 = new java.sql.Date(0);
		java.util.Date date2 = new java.util.Date();
		System.out.println(date1 + " - " + date2);
		new IOException();
	}
	
	public abstract ArrayList typeInReturnType();
	
	public abstract void typesInParameters(Color color, Dimension dimension, AllPermission ap);

}

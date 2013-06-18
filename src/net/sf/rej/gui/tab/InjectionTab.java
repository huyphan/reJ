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
package net.sf.rej.gui.tab;

import javax.swing.JPanel;

import net.sf.rej.gui.Link;

/**
 * <code>InjectionTab</code> is a graphical element for doing mass injection of code into a
 * project. Unused at the moment.
 *
 * @author Sami Koivu
 */

public class InjectionTab extends JPanel implements Tabbable {

	public InjectionTab() {
	}

	public String getTabTitle() {
		return "Injection";
	}

	public void redo() {
	}

	public void undo() {
	}

	public void insert() {
	}

	public void remove() {
	}

	public void goTo(Link link) {
	}

	public void find() {
	}

	public void findNext() {
	}

	public void outline() {
	}

	public void leavingTab() {
	}

	public void enteringTab() {
	}

}

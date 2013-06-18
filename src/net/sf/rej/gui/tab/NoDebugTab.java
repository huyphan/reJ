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

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.rej.gui.Link;

public class NoDebugTab extends JPanel implements Tabbable {
	
	public NoDebugTab() {
		add(new JLabel("Class com.sun.jdi.Bootstrap not found."));
		add(new JLabel("Probably due to the fact that JDI library (tools.jar) is not on classpath."));
		add(new JLabel("In order to have debugging support, please correct this and restart reJ."));
	}

	public void enteringTab() {
	}

	public void find() {
	}

	public void findNext() {
	}

	public String getTabTitle() {
		return "Debug (disabled)";
	}

	public void goTo(Link link) {
	}

	public void insert() {
	}

	public void leavingTab() {
	}

	public void outline() {
	}

	public void redo() {
	}

	public void remove() {
	}

	public void undo() {
	}

}

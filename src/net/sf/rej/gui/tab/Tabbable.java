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

import net.sf.rej.gui.Link;

/**
 * Interface implemented by the main window tabs. Gathering functionality common
 * to most tabs.
 * 
 * @author Sami Koivu
 */

public interface Tabbable {

	/**
	 * TODO: Why is undo/redo on this level? Should "remove file from project"
	 * be in the same UNDO context as remove instruction from class+
	 */
	public void undo();

	public void redo();

	/**
	 * Insert a new entity on this tab. Be that a file into the project(Fileset
	 * tab), a constant pool entry(ConstantPool etc) etc.
	 */
	public void insert();

	/**
	 * Remove selected entry(entries) from this tab.
	 */
	public void remove();

	/**
	 * Navigate to the location pointed by the given Link object.
	 * 
	 * @param link
	 *            A pointer object referencing for example a particular method,
	 *            or instruction.
	 */
	public void goTo(Link link);

	/**
	 * Search within the tab. For example, within a file set tab, search for a
	 * given file.
	 */
	public void find();

	/**
	 * Continue search within tab.
	 */
	public void findNext();

	/**
	 * Do a quick outline of the tab elements.
	 */
	public void outline();

	/**
	 * Called whenever the user navigates away from this tab.
	 */
	public void leavingTab();

	/**
	 * Returns the title of the tab.
	 * @return a title to be displayed in the tabbed pane.
	 */
	public String getTabTitle();

	/**
	 * Called whenever the user navigates to this tab.
	 */
	public void enteringTab();

}
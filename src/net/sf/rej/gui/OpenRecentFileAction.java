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
package net.sf.rej.gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

public class OpenRecentFileAction extends AbstractAction {

	private File file;

	public OpenRecentFileAction(File file) {
		super(OpenRecentFileAction.getName(file));
		this.file = file;
	}

	public void actionPerformed(ActionEvent e) {
		SystemFacade.getInstance().openFile(this.file);
	}
	
	public static String getName(File f) {
		String path = f.getParentFile().getAbsolutePath();
		if (path.length() > 30) {
			path = path.substring(0, 27) + "...";
		}
		return f.getName() + " [" + path + "]";
	}

}

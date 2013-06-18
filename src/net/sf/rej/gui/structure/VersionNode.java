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
package net.sf.rej.gui.structure;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.action.ModifyClassVersionAction;
import net.sf.rej.gui.dialog.ClassVersionDialog;
import net.sf.rej.java.ClassFile;

public class VersionNode extends StructureNode {

	private ClassFile cf;
	
	private Action modifyAction = new AbstractAction("Change Version..") {
		public void actionPerformed(ActionEvent e) {
			ClassVersionDialog dlg = new ClassVersionDialog(MainWindow.getInstance());
			dlg.invoke(cf.getMajorVersion(), cf.getMinorVersion());
			if (!dlg.wasCancelled()) {
				SystemFacade.getInstance().performAction(new ModifyClassVersionAction(cf, dlg.getVersion()));
			}
		}
	};


	public VersionNode(ClassFile cf) {
		this.cf = cf;
	}

	@Override
	public String toString() {
		return "Class file version: " + this.cf.getMajorVersion() + "."
				+ this.cf.getMinorVersion() + " (Java " + this.cf.getJavaVersionCompabilityString() + ")";
	}
	
	@Override
	public JPopupMenu getContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu(); 
		contextMenu.add(new JMenuItem(this.modifyAction));
		return contextMenu;
	}

}

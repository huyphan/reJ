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
package net.sf.rej.gui.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.files.Project;
import net.sf.rej.gui.Undoable;

/**
 * An <code>Undoable</code> action which removes a set of files (defined by a
 * <code>List</code>) from the given <code>Project</code>.
 * 
 * @author Sami Koivu
 */

public class RemoveFileAction implements Undoable {

	private Project project;

	private List<String> files = new ArrayList<String>();

	public RemoveFileAction(Project project, List<String> files) {
		this.project = project;
		this.files.addAll(files);
	}

	public void execute() {
		for (int i = 0; i < this.files.size(); i++) {
			String file = this.files.get(i);
			this.project.removeFile(file);
		}
	}

	public void undo() {
		for (int i = 0; i < this.files.size(); i++) {
			String file = this.files.get(i);
			// TODO: Calling addfile to restore the file in the project, doesn't
			// make the projects
			// state return to unmodified. Maybe the modified status setting
			// should be externalized
			// to an action of it's own, after all.
			this.project.addFile(file);
		}
	}

}
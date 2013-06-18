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

import net.sf.rej.files.Project;
import net.sf.rej.gui.Undoable;
import net.sf.rej.java.ClassFile;

public class MarkClassFileModifiedAction implements Undoable {

    private Project project;
    private String filename;
    private ClassFile cf;

    public MarkClassFileModifiedAction(Project project, String filename, ClassFile cf) {
        this.project = project;
        this.filename = filename;
        this.cf = cf;
    }

    public void execute() {
        this.project.setClassFileMofidied(true, this.filename, this.cf);
    }

    public void undo() {
        this.project.setClassFileMofidied(false, this.filename, this.cf);
    }

}

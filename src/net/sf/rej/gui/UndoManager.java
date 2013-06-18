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

import java.util.Stack;

public class UndoManager {

    private Stack<Undoable> undo = new Stack<Undoable>();
    private Stack<Undoable> redo = new Stack<Undoable>();

    public UndoManager() {
        // do-nothing constructor
    }

    public void add(Undoable undoable) {
        this.undo.push(undoable);
        this.redo.removeAllElements();
    }

    public void undo() {
        if (this.undo.size() > 0) {
            Undoable u = this.undo.pop();
            this.redo.push(u);
            u.undo();
        }
    }

    public void redo() {
        if (this.redo.size() > 0) {
            Undoable u = this.redo.pop();
            this.undo.push(u);
            u.execute();
        }
    }

}

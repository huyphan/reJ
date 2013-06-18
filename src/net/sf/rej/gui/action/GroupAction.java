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

import net.sf.rej.gui.Undoable;

/**
 * Utility class to group to Undoable objects together. This <code>GroupAction</code> can
 * then be added into the Undo stack, causing the two or more grouped actions to
 * always execute/get undone at the same time.
 *
 * A group of <code>Undoable</code> objects A, B and C will always be executed in the order:<br/>
 * A<br/>
 * B<br/>
 * C<br/>
 * <br/>
 * And always undone in the order:<br/>
 * C<br/>
 * B<br/>
 * A<br/>

 * @author Sami Koivu
 */
public class GroupAction implements Undoable {

    private List<Undoable> actions = new ArrayList<Undoable>();

    public GroupAction() {
        // default constructor, does nothing.
    }

    public void add(Undoable action) {
        this.actions.add(action);
    }

    public void execute() {
        for (int i = 0; i < this.actions.size(); i++) {
            Undoable action = this.actions.get(i);
            action.execute();
        }
    }

    public void undo() {
        for (int i = this.actions.size() - 1; i >= 0; i--) {
            Undoable action = this.actions.get(i);
            action.undo();
        }
    }

    public boolean isEmpty() {
        return this.actions.isEmpty();
    }

}
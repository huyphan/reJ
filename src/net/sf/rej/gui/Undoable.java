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

/**
 * Interface for all actions that can be undone(UNDOed). The basic idea is that an
 * action knows how to execute itself, and how to undo that execution. The
 * contract of Undoable demands also that { Undoable.execute(); Undoable.undo(); }
 * can be called any number of times. Meaning from the user interface point of
 * view that the user can trigger the action, undo it, redo it, undo it, redo
 * it, undo it as many times as the user wishes.
 *
 * However execute won't be called twice without calling undo in between and
 * undo won't be called twice without calling execute in between.
 *
 * @author KOIVUSAM
 */

public interface Undoable {

    /**
     * Execute the command/action that fulfills the Undoable contract.
     */
    public void execute();

    /**
     * Undo all the changes caused by a call to the execute() method of this instance.
     */
    public void undo();

}
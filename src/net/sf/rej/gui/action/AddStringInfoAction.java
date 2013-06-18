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

import net.sf.rej.gui.Undoable;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.StringInfo;
import net.sf.rej.java.constantpool.UTF8Info;

public class AddStringInfoAction implements Undoable {

	private ConstantPool cp;
	private String str;
	private UTF8Info utf8 = null;
	private StringInfo stringInfo = null;

	public AddStringInfoAction(ConstantPool cp, String str) {
		this.cp = cp;
		this.str = str;
	}

	public void execute() {
		if (this.utf8 == null) {
			// the first time this is executed, create the items
			this.utf8 = new UTF8Info(this.str, this.cp);
			int utf8Index = this.cp.forceAdd(this.utf8);
			this.stringInfo = new StringInfo(utf8Index, this.cp);
			this.cp.forceAdd(this.stringInfo);
		} else {
			/* if an actions(undo, redo) is performed, do not recreate
			   the items, because in that case if a next redo in the
			   redo stack refers to one of these objects, it won't
			   work.
			 */
			this.cp.forceAdd(this.utf8);
			this.cp.forceAdd(this.stringInfo);
		}

	}

	public void undo() {
		this.cp.removeLast();
		this.cp.removeLast();
	}

}

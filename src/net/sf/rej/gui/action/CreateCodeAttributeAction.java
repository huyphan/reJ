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
import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.java.constantpool.ConstantPool;

public class CreateCodeAttributeAction implements Undoable {
	private Attributes attrs;
	private CodeAttribute codeAttr = null;
	private ConstantPool cp;
	private int oldPoolSize = -1;
	
	public CreateCodeAttributeAction(Attributes attrs, ConstantPool cp) {
		this.attrs = attrs;
		this.cp = cp;
		this.oldPoolSize = this.cp.size();
	}

	public void execute() {
		if (this.codeAttr == null) {
			int index = this.cp.optionalAddUtf8("Code");
			this.codeAttr = new CodeAttribute(index, this.cp, 0, 0);
		}
		
		this.attrs.addAttribute(this.codeAttr);
	}

	public void undo() {
		this.attrs.removeAttribute(this.codeAttr);
		this.cp.shrinkToSize(this.oldPoolSize);
	}

}

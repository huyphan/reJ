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
package net.sf.rej.obfuscation;

import net.sf.rej.gui.IterationContext;
import net.sf.rej.gui.IteratorAgentAdapter;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.Undoable;
import net.sf.rej.gui.action.RemoveAttributeAction;
import net.sf.rej.java.attribute.Attribute;
import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.LocalVariableTableAttribute;

/**
 * This class removes local variable information from classfiles.
 * 
 * @author Sami Koivu
 */
public abstract class LocalVarStripper extends IteratorAgentAdapter {
	
	private boolean batchMode = false;
	
	public LocalVarStripper(boolean batchMode) {
		this.batchMode = batchMode;
	}

	@Override
	public void processAttribute(IterationContext ic, Attribute attr) {
		if (attr instanceof LocalVariableTableAttribute) {
			LocalVariableTableAttribute lvAttr = (LocalVariableTableAttribute) attr;
			Attributes attrs = ic.getCodeAttribute().getAttributes();
			Undoable u = new RemoveAttributeAction(attrs, lvAttr);
			if (this.batchMode) {
				u.execute();
			} else {
				SystemFacade.getInstance().performAction(u, ic.getFilename());
			}
		}
	}

	public String getName() {
		return "Local variable stripper";
	}

}

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

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Method;

public class MethodsNode extends StructureNode {
	private ClassFile cf;
	private List<MethodNode> methods;

	public MethodsNode(ClassFile cf) {
		this.cf = cf;
		refresh();
	}

	@Override
	public String toString() {
		return "Class Methods (" + this.cf.getMethods().size() + ")";
	}

	public void refresh() {
		this.methods = new ArrayList<MethodNode>();
		List list = this.cf.getMethods();
		for (int i = 0; i < list.size(); i++) {
			Method method = (Method) list.get(i);
			MethodNode methodNode = new MethodNode(this.cf, method);
			methodNode.setParent(this);
			this.methods.add(methodNode);
		}
	}

	@Override
	public List getChildren() {
		return this.methods;
	}

}

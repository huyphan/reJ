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

import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.Attribute;
import net.sf.rej.java.attribute.Attributes;

public class MethodAttributesNode extends StructureNode {
	private Method method;

	private List<AttributeNode> attributes;

	public MethodAttributesNode(Method method) {
		this.method = method;
		refresh();
	}

	@Override
	public String toString() {
		return "Attributes (" + this.attributes.size() + ")";
	}

	public void refresh() {
		this.attributes = new ArrayList<AttributeNode>();
		Attributes attrs = this.method.getAttributes();
		List list = attrs.getAttributes();
		for (int i = 0; i < list.size(); i++) {
			Attribute attr = (Attribute) list.get(i);
			this.attributes.add(new AttributeNode(attrs, attr));
		}
	}

	@Override
	public List getChildren() {
		return this.attributes;
	}

}

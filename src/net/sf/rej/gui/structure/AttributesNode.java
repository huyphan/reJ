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

import net.sf.rej.java.attribute.Attribute;
import net.sf.rej.java.attribute.Attributes;

public class AttributesNode extends StructureNode {
	private Attributes attrs;
	private List<AttributeNode> attributes;

	public AttributesNode(Attributes attrs) {
		this.attrs = attrs;
		refresh();
	}

	@Override
	public String toString() {
		return "Attributes (" + this.attrs.getAttributes().size() + ")";
	}

	@Override
	public List getChildren() {
		return this.attributes;
	}

	public void refresh() {
		this.attributes = new ArrayList<AttributeNode>();
		List list = this.attrs.getAttributes();
		for (int i = 0; i < list.size(); i++) {
			Attribute attr = (Attribute) list.get(i);
			AttributeNode attrNode = new AttributeNode(this.attrs, attr);
			attrNode.setParent(this);
			this.attributes.add(attrNode);
		}
	}
	
	public Attributes getAttributesObject() {
		return this.attrs;
	}

}

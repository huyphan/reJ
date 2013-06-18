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
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreeNode;

import net.sf.rej.util.Enumerator;

public class StructureNode implements TreeNode {
	private StructureNode parent = null;

	public StructureNode() {
	}

	public int getChildCount() {
		return getChildren().size();
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	public void setParent(StructureNode node) {
		this.parent = node;
	}
	
	public TreeNode getParent() {
		return this.parent;
	}

	public TreeNode getChildAt(int childIndex) {
		return (TreeNode) getChildren().get(childIndex);
	}

	public int getIndex(TreeNode node) {
		return getChildren().indexOf(node);
	}

	public Enumeration children() {
		return new Enumerator(getChildren());
	}

	public List getChildren() {
		return new ArrayList();
	}

	public JPopupMenu getContextMenu() {
		return null;
	}
}

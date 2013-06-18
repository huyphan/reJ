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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.action.ModifyAttributeDataAction;
import net.sf.rej.gui.hexeditor.ByteArrayDataProvider;
import net.sf.rej.gui.hexeditor.HexEditorDialog;
import net.sf.rej.java.attribute.Attribute;
import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.CodeAttribute;

public class AttributeNode extends StructureNode {
	private Attribute attribute;
	private List<AttributesNode> subs;
	
	private Action hexEditAction = new AbstractAction("Edit attribute data..") {
		byte[] data;
		public void actionPerformed(ActionEvent e) {
			data = attribute.getPayload();
			ByteArrayDataProvider badp = new ByteArrayDataProvider(data);
			HexEditorDialog dlg = new HexEditorDialog(MainWindow.getInstance(), badp, 8, false);
			boolean cancelled = dlg.invoke();
			if (!cancelled) {
				SystemFacade.getInstance().performAction(new ModifyAttributeDataAction(attribute, data));
			}
		}
	};

	public AttributeNode(Attributes attributes, Attribute attr) {
		this.attribute = attr;
		refresh();
	}

	@Override
	public String toString() {
		return this.attribute.toString();
	}

	@Override
	public List getChildren() {
		return this.subs;
	}

	public void refresh() {
		this.subs = new ArrayList<AttributesNode>();
		if (this.attribute instanceof CodeAttribute) {
			CodeAttribute ca = (CodeAttribute) this.attribute;
			Attributes attrs = ca.getAttributes();
			AttributesNode attrsNode = new AttributesNode(attrs);
			attrsNode.setParent(this);
			this.subs.add(attrsNode);
		}
	}
	
	@Override
	public JPopupMenu getContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu(); 
		contextMenu.add(new JMenuItem(this.hexEditAction));
		return contextMenu;
	}

	public Attribute getAttributeObject() {
		return this.attribute;
	}


}

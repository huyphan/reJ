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

public class ClassFileNode extends StructureNode {

    private ClassFile cf;
    private List<StructureNode> subNodes;

    public ClassFileNode(ClassFile cf) {
        this.cf = cf;
        refresh();
    }

    @Override
	public String toString() {
        return "Class " + this.cf.getFullClassName();
    }

    @Override
	public List getChildren() {
        return this.subNodes;
    }

    public void refresh() {
        this.subNodes = new ArrayList<StructureNode>();
        this.subNodes.add(new MagicNode(this.cf));
        this.subNodes.add(new VersionNode(this.cf));
        this.subNodes.add(new ConstantPoolNode(this.cf));
        this.subNodes.add(new AccessFlagsNode(this.cf));
        this.subNodes.add(new ThisClassNode(this.cf));
        this.subNodes.add(new SuperClassNode(this.cf));
        this.subNodes.add(new InterfacesNode(this.cf));
        this.subNodes.add(new FieldsNode(this.cf));
        this.subNodes.add(new MethodsNode(this.cf));
        this.subNodes.add(new AttributesNode(this.cf.getAttributes()));
        for (StructureNode child : this.subNodes) {
        	child.setParent(this);
        }
    }
}

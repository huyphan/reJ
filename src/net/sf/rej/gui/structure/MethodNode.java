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

public class MethodNode extends StructureNode {
    private Method method;
    private List<StructureNode> contents;

    public MethodNode(ClassFile cf, Method method) {
        this.method = method;
        refresh();
    }

    @Override
	public String toString() {
        return this.method.getSignatureLine();
    }

    @Override
	public List getChildren() {
        return this.contents;
    }

    public void refresh() {
        this.contents = new ArrayList<StructureNode>();
        this.contents.add(new MethodAccessFlagsNode(this.method));
        this.contents.add(new MethodNameNode(this.method));
        this.contents.add(new MethodDescriptorNode(this.method));
        this.contents.add(new AttributesNode(this.method.getAttributes()));
        for (StructureNode child : this.contents) {
        	child.setParent(this);
        }
    }
    
    public Method getMethod() {
    	return this.method;
    }

}

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
package net.sf.rej.gui.split;

import java.util.Map;

import net.sf.rej.gui.structure.AccessFlagsNode;
import net.sf.rej.gui.structure.AttributeNode;
import net.sf.rej.gui.structure.AttributesNode;
import net.sf.rej.gui.structure.ClassFileNode;
import net.sf.rej.gui.structure.ConstantPoolNode;
import net.sf.rej.gui.structure.FieldNode;
import net.sf.rej.gui.structure.FieldsNode;
import net.sf.rej.gui.structure.InterfaceNode;
import net.sf.rej.gui.structure.InterfacesNode;
import net.sf.rej.gui.structure.MagicNode;
import net.sf.rej.gui.structure.MethodAccessFlagsNode;
import net.sf.rej.gui.structure.MethodDescriptorNode;
import net.sf.rej.gui.structure.MethodNameNode;
import net.sf.rej.gui.structure.MethodNode;
import net.sf.rej.gui.structure.MethodsNode;
import net.sf.rej.gui.structure.StructureNode;
import net.sf.rej.gui.structure.SuperClassNode;
import net.sf.rej.gui.structure.ThisClassNode;
import net.sf.rej.gui.structure.VersionNode;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Field;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.util.Range;

public class StructureToHexSync implements StructureSplitSynchronizer {

	private HexSplit hexEditor;
	private Map<Object, Range> offsets;

	public StructureToHexSync(HexSplit hexEditor) {
		this.hexEditor = hexEditor;
	}

	public void setOffsets(Map<Object, Range> offsets) {
		this.offsets = offsets;
	}
	
	public Range getRange(StructureNode node) {
		if (node instanceof ClassFileNode) {
			return null;
		} else if (node instanceof MagicNode) {
			return this.offsets.get(ClassFile.OffsetTag.MAGIC);
		} else if (node instanceof VersionNode) {
			return this.offsets.get(ClassFile.OffsetTag.VERSION);			
		} else if (node instanceof ConstantPoolNode) {
			return this.offsets.get(ClassFile.OffsetTag.CONSTANT_POOL);
		} else if (node instanceof AccessFlagsNode) {
			return this.offsets.get(ClassFile.OffsetTag.ACCESS_FLAGS);
		} else if (node instanceof ThisClassNode) {
			return this.offsets.get(ClassFile.OffsetTag.THIS_CLASS);
		} else if (node instanceof SuperClassNode) {
			return this.offsets.get(ClassFile.OffsetTag.SUPER_CLASS);
		} else if (node instanceof InterfacesNode) {
			return this.offsets.get(ClassFile.OffsetTag.INTERFACE_DATA);
		} else if (node instanceof InterfaceNode) {
			InterfaceNode iNode = (InterfaceNode) node;
			return this.offsets.get(iNode.getInterface());
		} else if (node instanceof FieldsNode) {
			return this.offsets.get(ClassFile.OffsetTag.FIELD_DATA);
		} else if (node instanceof FieldNode) {
			FieldNode fNode = (FieldNode) node;
			return this.offsets.get(fNode.getField());
		} else if (node instanceof MethodsNode) {
			return this.offsets.get(ClassFile.OffsetTag.METHOD_DATA);
		} else if (node instanceof MethodNode) {
			MethodNode mNode = (MethodNode) node;
			return this.offsets.get(mNode.getMethod());
		} else if (node instanceof MethodAccessFlagsNode) {
			Range parentRange = getRange((StructureNode)node.getParent());
			MethodAccessFlagsNode mafNode = (MethodAccessFlagsNode) node;
			Method method = mafNode.getMethod();
			Range afRange = method.getOffsetMap().get(Method.OffsetTag.ACCESS_FLAGS);
			return afRange.offsetBy(parentRange.getOffset());
		} else if (node instanceof MethodNameNode) {
			Range parentRange = getRange((StructureNode)node.getParent());
			MethodNameNode mafNode = (MethodNameNode) node;
			Method method = mafNode.getMethod();
			Range afRange = method.getOffsetMap().get(Method.OffsetTag.METHOD_NAME);
			return afRange.offsetBy(parentRange.getOffset());
		} else if (node instanceof MethodDescriptorNode) {			
			Range parentRange = getRange((StructureNode)node.getParent());
			MethodDescriptorNode mafNode = (MethodDescriptorNode) node;
			Method method = mafNode.getMethod();
			Range afRange = method.getOffsetMap().get(Method.OffsetTag.METHOD_DESCRIPTOR);
			return afRange.offsetBy(parentRange.getOffset());
		} else if (node instanceof AttributeNode) {
			AttributeNode aNode = (AttributeNode) node;
			AttributesNode an = (AttributesNode) aNode.getParent();
			Range range = getRange(an);
			Map<Object, Range> map = an.getAttributesObject().getOffsetMap(range.getOffset());
			return map.get(aNode.getAttributeObject());
		} else if (node instanceof AttributesNode) {
			StructureNode parent = (StructureNode) node.getParent();
			if (parent instanceof ClassFileNode) {
				return this.offsets.get(ClassFile.OffsetTag.ATTRIBUTES);
			} else if (parent instanceof MethodNode) {
				MethodNode mNode = (MethodNode) parent;
				Range parentRange = getRange(parent);
				Map<Object, Range> map = mNode.getMethod().getOffsetMap();
				return map.get(Method.OffsetTag.ATTRIBUTES).offsetBy(parentRange.getOffset());
			} else if (parent instanceof FieldNode) {
				FieldNode fNode = (FieldNode) parent;
				Range parentRange = getRange(parent);
				Map<Object, Range> map = fNode.getField().getOffsetMap();
				return map.get(Field.OffsetTag.ATTRIBUTES).offsetBy(parentRange.getOffset());
			} else if (parent instanceof AttributeNode) {
				// code attribute attributes
				AttributeNode aNode = (AttributeNode) parent;
				CodeAttribute cAttr = (CodeAttribute) aNode.getAttributeObject();
				Range range = cAttr.getOffsetMap().get(CodeAttribute.OffsetTag.ATTRIBUTES);
				Range parentRange = getRange(parent);
				return range.offsetBy(parentRange.getOffset());
			}
			return null;
		} else {
			return null;
		}		
	}

	public void sync(StructureNode node) {
		
		Range range = getRange(node);
		if (range == null) {
			this.hexEditor.getHexEditor().getSelectionModel().clearSelection();
		} else {
			this.hexEditor.getHexEditor().getSelectionModel().setSelectedInverval(range.getOffset(), range.getOffset() + range.getSize());
		}
		this.hexEditor.repaint();
		this.hexEditor.getHexEditor().ensureSelectionIsVisible();
	}

}

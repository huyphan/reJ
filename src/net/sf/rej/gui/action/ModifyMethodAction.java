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

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.gui.Undoable;
import net.sf.rej.java.AccessFlags;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.ExceptionDescriptor;
import net.sf.rej.java.attribute.ExceptionsAttribute;
import net.sf.rej.java.constantpool.ConstantPool;

public class ModifyMethodAction implements Undoable {
	private ConstantPool cp;
    private Method method;
    private String methodName;
    private Descriptor desc;
    private AccessFlags flags;
    private int maxStackSize;
    private int maxLocals;

    private AccessFlags oldFlags;
    private int oldStack;
    private int oldLocals;
    private int oldNameIndex;
    private int oldDescIndex;
    private ExceptionsAttribute oldExAttr;

    private List<Integer> createdPoolItems = new ArrayList<Integer>();
    private List exceptionNames;

    private boolean hasCode = true;

    public ModifyMethodAction(ConstantPool pool, Method method, String name, Descriptor desc, AccessFlags accessFlags,
            int maxStack, int maxLocals, List exceptionNames) {
    	this.cp = pool;
        this.method = method;
        this.methodName = name;
        this.desc = desc;
        this.flags = accessFlags;
        this.maxStackSize = maxStack;
        this.maxLocals = maxLocals;

        this.oldFlags = new AccessFlags(method.getAccessFlags());

        this.hasCode = method.getAttributes().getCode() != null;
        if (hasCode) {
        	this.oldStack = method.getAttributes().getCode().getMaxStackSize();
        	this.oldLocals = method.getAttributes().getCode().getMaxLocals();
        }
        this.oldNameIndex = method.getNameIndex();
        this.oldDescIndex = method.getDescriptorIndex();
        this.oldExAttr = method.getAttributes().getExceptionsAttribute();

        this.exceptionNames = exceptionNames;
    }

    public void execute() {
        int nameIndex = cp.indexOfUtf8(this.methodName);
        if (nameIndex == -1) {
            nameIndex = cp.optionalAddUtf8(this.methodName);
            this.createdPoolItems.add(Integer.valueOf(nameIndex));
        }

        this.method.setNameIndex(nameIndex);

        int descIndex = cp.indexOfUtf8(this.desc.getRawDesc());
        if (descIndex == -1) {
            descIndex = cp.optionalAddUtf8(this.desc.getRawDesc());
            this.createdPoolItems.add(descIndex);
        }

        this.method.setDescriptorIndex(descIndex);

        this.method.setAccessFlags(this.flags);
        if (hasCode) {
        	this.method.getAttributes().getCode().setStackSize(this.maxStackSize);
        	this.method.getAttributes().getCode().setMaxLocals(this.maxLocals);
        }

        int exAttrNameIndex = cp.indexOfUtf8("Exceptions");
        if (exAttrNameIndex == -1) {
            exAttrNameIndex = cp.optionalAddUtf8("Exceptions");
            this.createdPoolItems.add(exAttrNameIndex);
        }

        List<ExceptionDescriptor> exceptionList = new ArrayList<ExceptionDescriptor>();
        for (int i=0; i < this.exceptionNames.size(); i++) {
        	String exceptionName = (String)this.exceptionNames.get(i);
        	int exIndex = cp.indexOfClassRef(exceptionName);
        	if (exIndex == -1) {
        		exIndex = cp.forceAddClassRef(exceptionName);
                this.createdPoolItems.add(exIndex);
        	}
        	exceptionList.add(new ExceptionDescriptor(cp, exIndex));
        }

        Attributes attrs = method.getAttributes();
        ExceptionsAttribute ea = new ExceptionsAttribute(exAttrNameIndex, this.cp, exceptionList);
        if (this.oldExAttr != null) {
        	attrs.removeAttribute(this.oldExAttr);
        }
        attrs.addAttribute(ea);
    }

    public void undo() {
        this.method.setNameIndex(this.oldNameIndex);
        this.method.setDescriptorIndex(this.oldDescIndex);
        this.method.setAccessFlags(this.oldFlags);
        if (hasCode) {
        	this.method.getAttributes().getCode().setStackSize(this.oldStack);
        	this.method.getAttributes().getCode().setMaxLocals(this.oldLocals);
        }

        Attributes attrs = method.getAttributes();
        attrs.removeAttribute(attrs.getExceptionsAttribute());
        if (this.oldExAttr != null) {
        	attrs.addAttribute(this.oldExAttr);
        }

        for (int index : this.createdPoolItems) {
        	this.cp.removeLast(index);
        }
    }

}
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
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Code;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Method;
import net.sf.rej.java.MethodFactory;
import net.sf.rej.java.attribute.ExceptionDescriptor;
import net.sf.rej.java.constantpool.ConstantPool;

/**
 * An <code>Undoable</code> action which adds a method definition to a class.
 *
 * @author Sami Koivu
 */
public class InsertMethodAction implements Undoable {

    private ClassFile cf;
    private String methodName;
    private Descriptor desc;
    private int maxStackSize;
    private int maxLocals;
    private AccessFlags accessFlags;

    private List<Integer> createdPoolItems = new ArrayList<Integer>();

    private List<String> exceptionNames;

    private Method method = null;

    private static MethodFactory methodFactory = new MethodFactory();

    public InsertMethodAction(ClassFile cf, String methodName, Descriptor desc, AccessFlags accessFlags, int maxStackSize, int maxLocals, List<String> exceptionNames) {
        this.cf = cf;
        this.methodName = methodName;
        this.desc = desc;
        this.maxStackSize = maxStackSize;
        this.maxLocals = maxLocals;
        this.accessFlags = accessFlags;
        this.exceptionNames = exceptionNames;
    }

    public void execute() {
        ConstantPool cp = this.cf.getPool();

        int nameIndex = cp.indexOfUtf8(this.methodName);
        if (nameIndex == -1) {
            nameIndex = cp.optionalAddUtf8(this.methodName);
            this.createdPoolItems.add(nameIndex);
        }

        int descIndex = cp.indexOfUtf8(this.desc.getRawDesc());
        if (descIndex == -1) {
            descIndex = cp.optionalAddUtf8(this.desc.getRawDesc());
            this.createdPoolItems.add(descIndex);
        }

        int codeAttrNameIndex = cp.indexOfUtf8("Code");
        if (codeAttrNameIndex == -1) {
            codeAttrNameIndex = cp.optionalAddUtf8("Code");
            this.createdPoolItems.add(codeAttrNameIndex);
        }

        int exAttrNameIndex = cp.indexOfUtf8("Exceptions");
        if (exAttrNameIndex == -1) {
            exAttrNameIndex = cp.optionalAddUtf8("Exceptions");
            this.createdPoolItems.add(exAttrNameIndex);
        }

        List<ExceptionDescriptor> exceptionList = new ArrayList<ExceptionDescriptor>();
        for (int i=0; i < this.exceptionNames.size(); i++) {
        	String exceptionName = this.exceptionNames.get(i);
        	int exIndex = cp.indexOfClassRef(exceptionName);
        	if (exIndex == -1) {
        		exIndex = cp.forceAddClassRef(exceptionName);
                this.createdPoolItems.add(exIndex);
        	}
        	exceptionList.add(new ExceptionDescriptor(cp, exIndex));
        }

        this.method = methodFactory.createMethod(this.cf, this.accessFlags, nameIndex, descIndex, codeAttrNameIndex, this.maxStackSize, this.maxLocals, exAttrNameIndex, exceptionList);

        this.cf.add(this.method);
    }

    public void undo() {
        this.cf.remove(this.method);

        for (int i=0; i < this.createdPoolItems.size(); i++) {
        	this.cf.getPool().removeLast();
        }
    }

	public Code getCode() {
		return this.method.getAttributes().getCode().getCode();
	}

}

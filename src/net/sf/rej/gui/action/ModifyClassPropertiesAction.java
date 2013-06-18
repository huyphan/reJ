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
import net.sf.rej.java.Interface;
import net.sf.rej.java.constantpool.ConstantPool;

public class ModifyClassPropertiesAction implements Undoable {
    private ClassFile cf;
    private AccessFlags accessFlags;
    private String className;
    private String superName;
    private List<Interface> remainingInterfaces;
    private List<String> newInterfaceNames;

    private AccessFlags oldAccessFlags;
    private List<Interface> oldInterfaces;
    private int oldClassIndex;
    private int oldSuperIndex;

    private List<Integer> createdPoolIndices = new ArrayList<Integer>();

    public ModifyClassPropertiesAction(ClassFile cf, AccessFlags flags, String className, String superName, List<Interface> remainingInterfaces, List<String> newInterfaces) {
        this.cf = cf;
        this.accessFlags = flags;
        this.className = className;
        this.superName = superName;
        this.remainingInterfaces = remainingInterfaces;
        this.newInterfaceNames = newInterfaces;

        this.oldAccessFlags = new AccessFlags(this.cf.getAccessFlags());
        this.oldInterfaces = this.cf.getInterfaces();
        this.oldClassIndex = this.cf.getThisClass();
        this.oldSuperIndex = this.cf.getSuperClass();
    }

    public void execute() {
        this.cf.setAccessFlags(this.accessFlags.getValue());
        ConstantPool cp = this.cf.getPool();

        int classIndex = cp.indexOfClassRef(this.className);
        if (classIndex == -1) {
            classIndex = cp.optionalAddClassRef(this.className);
            this.createdPoolIndices.add(classIndex);
        }
        this.cf.setThisClass(classIndex);

        int superIndex = 0;
        if (this.superName != null) {
        	superIndex = cp.indexOfClassRef(this.superName);
        	if (superIndex == -1) {
        		superIndex = cp.optionalAddClassRef(this.superName);
        		this.createdPoolIndices.add(superIndex);
        	}
        }
        this.cf.setSuperClass(superIndex);

        List<Interface> newInterfaceList = new ArrayList<Interface>();
        newInterfaceList.addAll(this.remainingInterfaces);
        for (int i=0; i < this.newInterfaceNames.size(); i++) {
        	String interfaceName = this.newInterfaceNames.get(i);
        	int index = cp.indexOfClassRef(interfaceName);
        	if (index == -1) {
        		index = cp.optionalAddClassRef(interfaceName);
        		this.createdPoolIndices.add(index);
        	}
        	newInterfaceList.add(new Interface(index, cp));
        }
        this.cf.setInterfaces(newInterfaceList);

    }

    public void undo() {
        this.cf.setAccessFlags(this.oldAccessFlags.getValue());

        for (int i=0; i < this.createdPoolIndices.size(); i++) {
        	this.cf.getPool().removeLast();
        }
        
        this.cf.setThisClass(this.oldClassIndex);
        this.cf.setSuperClass(this.oldSuperIndex);
        this.cf.setInterfaces(this.oldInterfaces);
    }

}

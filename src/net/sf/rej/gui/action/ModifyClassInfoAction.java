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
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPool;

public class ModifyClassInfoAction implements Undoable {
	private ConstantPool cp;
    private ClassInfo info;
    private String value;

    private int oldNameIndex;

    private List<Integer> createdPoolItems = new ArrayList<Integer>();

    public ModifyClassInfoAction(ConstantPool cp, ClassInfo info, String newName) {
    	this.cp = cp;
        this.info = info;
        this.value = Descriptor.arrayTypeToRaw(newName);

        this.oldNameIndex = info.getNameIndex();
    }

    public void execute() {
        int nameIndex = cp.indexOfUtf8(this.value);
        if (nameIndex == -1) {
            nameIndex = cp.optionalAddUtf8(this.value);
            this.createdPoolItems.add(nameIndex);
        }

        this.info.setNameIndex(nameIndex);
    }

    public void undo() {
        for (int i=0; i < this.createdPoolItems.size(); i++) {
        	this.cp.removeLast();
        }

        this.info.setNameIndex(this.oldNameIndex);
    }

}

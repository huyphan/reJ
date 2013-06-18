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

import net.sf.rej.gui.Undoable;
import net.sf.rej.java.AccessFlags;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Field;
import net.sf.rej.java.constantpool.ConstantPool;

public class ModifyFieldAction implements Undoable {
	private ConstantPool cp;
    private Field field;
    private String fieldName;
    private Descriptor desc;
    private AccessFlags flags;

    private AccessFlags oldFlags;
    private int oldNameIndex;
    private int oldDescIndex;

    private int createdNameIndex = -1;
    private int createdDescIndex = -1;

    public ModifyFieldAction(ConstantPool pool, Field field, String name, Descriptor desc, AccessFlags accessFlags) {
    	this.cp = pool;
        this.field = field;
        this.fieldName = name;
        this.desc = desc;
        this.flags = accessFlags;

        this.oldFlags = new AccessFlags(field.getAccessFlags());
        this.oldNameIndex = field.getNameIndex();
        this.oldDescIndex = field.getDescriptorIndex();
    }

    public void execute() {
        int nameIndex = cp.indexOfUtf8(this.fieldName);
        if (nameIndex == -1) {
            nameIndex = cp.optionalAddUtf8(this.fieldName);
            this.createdNameIndex = nameIndex;
        }

        this.field.setNameIndex(nameIndex);

        int descIndex = cp.indexOfUtf8(this.desc.getRawDesc());
        if (descIndex == -1) {
            descIndex = cp.optionalAddUtf8(this.desc.getRawDesc());
            this.createdDescIndex = descIndex;
        }

        this.field.setDescriptorIndex(descIndex);

        this.field.setAccessFlags(this.flags);
    }

    public void undo() {
        if (this.createdDescIndex != -1) {
            this.cp.removeLast();
        }

        if (this.createdNameIndex != -1) {
            this.cp.removeLast();
        }

        this.field.setNameIndex(this.oldNameIndex);
        this.field.setDescriptorIndex(this.oldDescIndex);
        this.field.setAccessFlags(this.oldFlags);
    }

}
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
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Field;
import net.sf.rej.java.FieldFactory;
import net.sf.rej.java.constantpool.ConstantPool;

/**
 * An <code>Undoable</code> action which adds a member field into a class.
 *
 * @author Sami Koivu
 */
public class InsertFieldAction implements Undoable {

    private ClassFile cf;
    private String fieldName;
    private Descriptor desc;
    private AccessFlags accessFlags;

    private int createdNameIndex = -1;
    private int createdDescIndex = -1;

    private Field field = null;

    private static FieldFactory fieldFactory = new FieldFactory();

    public InsertFieldAction(ClassFile cf, String fieldName, Descriptor desc, AccessFlags accessFlags) {
        this.cf = cf;
        this.fieldName = fieldName;
        this.desc = desc;
        this.accessFlags = accessFlags;
    }

    public void execute() {
        ConstantPool cp = this.cf.getPool();

        int nameIndex = cp.indexOfUtf8(this.fieldName);
        if (nameIndex == -1) {
            nameIndex = cp.optionalAddUtf8(this.fieldName);
            this.createdNameIndex = nameIndex;
        }

        int descIndex = cp.indexOfUtf8(this.desc.getRawDesc());
        if (descIndex == -1) {
            descIndex = cp.optionalAddUtf8(this.desc.getRawDesc());
            this.createdDescIndex = descIndex;
        }

        this.field = fieldFactory.createField(this.cf, this.accessFlags, nameIndex, descIndex);

        this.cf.add(this.field);
    }

    public void undo() {
        this.cf.remove(this.field);

        if (this.createdDescIndex != -1) {
            this.cf.getPool().removeLast();
        }

        if (this.createdNameIndex != -1) {
            this.cf.getPool().removeLast();
        }
    }

}

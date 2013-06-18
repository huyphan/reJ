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
import net.sf.rej.java.Field;
import net.sf.rej.java.constantpool.ConstantPool;

public class RenameFieldAction implements Undoable {
	private ConstantPool cp;
    private Field field;
    private String fieldName;

    private int oldNameIndex;

    private List<Integer> createdPoolItems = new ArrayList<Integer>();

    public RenameFieldAction(ConstantPool pool, Field field, String name) {
    	this.cp = pool;
        this.field = field;
        this.fieldName = name;

        this.oldNameIndex = field.getNameIndex();
    }

    public void execute() {
        int nameIndex = cp.indexOfUtf8(this.fieldName);
        if (nameIndex == -1) {
            nameIndex = cp.optionalAddUtf8(this.fieldName);
            this.createdPoolItems.add(nameIndex);
        }

        this.field.setNameIndex(nameIndex);
    }

    public void undo() {
        for (int i=0; i < this.createdPoolItems.size(); i++) {
        	this.cp.removeLast();
        }

        this.field.setNameIndex(this.oldNameIndex);
    }

}
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
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.NameAndTypeInfo;

public class ModifyNameAndTypeInfoAction implements Undoable {
	private ConstantPool cp;
    private NameAndTypeInfo info;
    private Descriptor desc;
    private String name;
    
    private int oldDescIndex = -1;
    private int oldNameIndex = -1;
    
    private List<Integer> createdConstantPoolEntries = new ArrayList<Integer>();

    public ModifyNameAndTypeInfoAction(ConstantPool cp, NameAndTypeInfo info, Descriptor desc) {
    	this.cp = cp;
        this.info = info;
        this.desc = desc;
        this.name = null;

        this.oldDescIndex = info.getDescriptorIndex();
    }
    
    public ModifyNameAndTypeInfoAction(ConstantPool cp, NameAndTypeInfo info, String name) {
    	this.cp = cp;
    	this.info = info;
    	this.desc = null;
    	this.name = name;
    	
    	this.oldNameIndex = info.getNameIndex();
    }

    public void execute() {
    	if (this.desc != null) {
    		String raw = this.desc.getRawDesc();
    		int descIndex = this.cp.indexOfUtf8(raw);
    		if (descIndex == -1) {
    			descIndex = this.cp.optionalAddUtf8(raw);
    			this.createdConstantPoolEntries.add(descIndex);
    		}
    		this.info.setDescriptorIndex(descIndex);
    	}
    	
    	if (this.name != null) {
    		int nameIndex = this.cp.indexOfUtf8(this.name);
    		if (nameIndex == -1) {
    			nameIndex = this.cp.optionalAddUtf8(this.name);
    			this.createdConstantPoolEntries.add(nameIndex);
    		}
    		this.info.setNameIndex(nameIndex);
    	}
    }

    public void undo() {
        if (this.oldNameIndex != -1) {
        	this.info.setNameIndex(this.oldNameIndex);
        }
        
        if (this.oldDescIndex != -1) {
        	this.info.setDescriptorIndex(this.oldDescIndex);
        }
        
        for (int i=0; i < this.createdConstantPoolEntries.size(); i++) {
        	this.cp.removeLast();
        }
    }

}

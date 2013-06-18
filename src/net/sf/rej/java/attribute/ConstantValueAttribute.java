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
package net.sf.rej.java.attribute;

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteToolkit;

public class ConstantValueAttribute extends Attribute {

	private int constant;

	public ConstantValueAttribute(int nameIndex, ConstantPool pool) {
		super(nameIndex, pool);
	}

	@Override
	public String toString() {
		ConstantPoolInfo cpi = this.pool.get(this.constant);
		return "Constant: " + cpi.getValue();
	}
	
	public String getValue() {
		ConstantPoolInfo cpi = this.pool.get(this.constant);
		return cpi.getValue();
	}
	
	public int getConstantIndex() {
		return this.constant;
	}
	
	public void setConstantIndex(int index) {
		this.constant = index;
	}

    @Override
	public byte[] getPayload() {
    	return ByteToolkit.longToTwoBytes(this.constant, true);
    }
    
    @Override
    public void setPayload(byte[] data) {
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        this.constant = parser.getShortAsInt();
    }
    
    public ConstantPoolInfo getCPI() {
    	return this.pool.get(this.constant);
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof ConstantValueAttribute)) {
    		return false;
    	}
    	
    	ConstantValueAttribute other = (ConstantValueAttribute)obj;
    	return other.getCPI().equals(getCPI());
    }
    
    @Override
    public int hashCode() {
    	return getCPI().hashCode();
    }

}

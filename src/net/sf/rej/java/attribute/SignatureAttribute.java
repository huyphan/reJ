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

import net.sf.rej.java.attribute.generics.Signatures;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

public class SignatureAttribute extends Attribute {
	
	private int signatureIndex;

	public SignatureAttribute(int nameIndex, ConstantPool pool) {
		super(nameIndex, pool);
	}

	@Override
	public void setPayload(byte[] data) {
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);
        this.signatureIndex = parser.getShortAsInt();
	}
	
	@Override
	public byte[] getPayload() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(this.signatureIndex);
        
        return ser.getBytes();
	}
	
	public String getSignatureString() {
		return this.pool.get(this.signatureIndex).getValue();
	}
	
	@Override
	public String toString() {
		return "Signature = " + Signatures.getSignature(getSignatureString());
	}

}

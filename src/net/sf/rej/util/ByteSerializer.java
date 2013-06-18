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
package net.sf.rej.util;

import java.io.ByteArrayOutputStream;

/**
 * A class that wraps a byte array for stream-type writing.
 * 
 * @author Sami Koivu
 */

public class ByteSerializer {

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private boolean bigEndian = false;

    public ByteSerializer(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }

    public byte[] getBytes() {
        return this.baos.toByteArray();
    }

    public void addBytes(byte[] data) {
        addBytes(data, 0, data.length);
    }

    public void addBytes(byte[] data, int length) {
        addBytes(data, 0, length);
    }

    public void addBytes(byte[] data, int offset, int length) {
        this.baos.write(data, offset, length);
    }

    public void addByte(int b) {
        this.baos.write(b);
    }

    public void addShort(int i) {
        if (i > 0xffff)
            throw new RuntimeException(
                    "ByteSerializer.addInt(long) does not support integers this big: "
                            + i);

        byte[] data = ByteToolkit.longToTwoBytes(i, this.bigEndian);
        addBytes(data);
    }

    public void addInt(long l) {
        // if(l < 0) throw new RuntimeException("ByteSerializer.addLong(long)
        // does not support negative values.(" + l + ")");

        byte[] data = ByteToolkit.longToFourBytes(l, this.bigEndian);
        // if(l < 0) System.out.println(l + " = " +
        // ByteToolkit.getHexString(data));
        addBytes(data);
    }

    public int size() {
        return this.baos.size();
    }

}

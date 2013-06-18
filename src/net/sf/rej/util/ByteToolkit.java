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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class ByteToolkit {

    public static byte[] getBytes(byte[] data, int pos, int length) {
        byte[] newData = new byte[length];
        for (int i = 0; i < length; i++) {
            newData[i] = data[i + pos];
        }

        return newData;
    }

    /**
     * First byte is the least significant - tested with numbers < 256
     * @param data byte[]
     * @param pos int
     * @param length int
     * @return long
     */
    public static long getLong(byte[] data, int pos, int length) {
        long l = 0;
        int i = 0;
        while (i < length) {
            l <<= 8;
            l += getByte(data[pos + length - i - 1]); // ugly reverse, but works
            i++;
        }
        return l;
    }

    public static long getLong(byte[] data, int pos, int length,
            boolean bigEndian) {
        if (bigEndian) {
            long l = 0;
            int i = 0;
            while (i < length) {
                l <<= 8;
                l += getByte(data[pos + i]); // ugly reverse, but works
                i++;
            }
            return l;
        } else {
            long l = 0;
            int i = 0;
            while (i < length) {
                l <<= 8;
                l += getByte(data[pos + length - i - 1]); // ugly reverse, but
                                                         // works
                i++;
            }
            return l;
        }
    }

    public static long getSignedLong(byte[] data, int pos, int length,
            boolean bigEndian) {
        if (bigEndian) {
            long max = 1;
            long l = 0;
            int i = 0;
            while (i < length) {
                max <<= 8;
                l <<= 8;
                l += getByte(data[pos + i]); // ugly reverse, but works
                i++;
            }
            long maxPos = max >> 1;
            if (l >= maxPos)
                l -= max;

            return l;
        } else {
            long max = 1;
            long l = 0;
            int i = 0;
            while (i < length) {
                l <<= 8;
                max <<= 8;
                l += getByte(data[pos + length - i - 1]); // ugly reverse, but
                                                         // works
                i++;
            }
            long maxPos = max >> 1;
            if (l >= maxPos)
                l -= max;

            return l;
        }
    }

    public static int getByte(byte b) {
        int i = b;
        if (b < 0)
            i = 256 + b;
        return i;
    }

    public static byte[] longToByteArray(long l) {
        byte data[] = null;
        if (l < 256) {
            data = new byte[1];
            data[0] = (byte) l;
            return data;
        } else if (l < 65536) {
            data = new byte[2];
            data[1] = (byte) (l & 0xFF);
            data[0] = (byte) (l / 256);
            return data;
        } else {
            throw new RuntimeException(
                    "Integer size unsupported @ ByteToolkit.intToByteArray()");
        }
    }

    public static byte[] longToTwoBytes(long l, boolean bigEndian) {
        if (bigEndian) {
            byte[] data = new byte[2];
            data[1] = (byte) (l & 0xFF);
            data[0] = (byte) (l >> 8);
            return data;

        } else {
            byte[] data = new byte[2];
            data[0] = (byte) (l & 0xFF);
            data[1] = (byte) (l >> 8);
            return data;
        }
    }

    public static byte[] longToFourBytes(long l, boolean bigEndian) {
        if (bigEndian) {
            byte[] data = new byte[4];
            data[3] = (byte) (l & 0xFF);
            data[2] = (byte) ((l >> 8) & 0xFF);
            data[1] = (byte) ((l >> 16) & 0xFF);
            data[0] = (byte) ((l >> 24) & 0xFF);
            return data;

        } else {
            byte[] data = new byte[4];
            data[0] = (byte) (l & 0xFF);
            data[1] = (byte) ((l >> 8) & 0xFF);
            data[2] = (byte) ((l >> 16) & 0xFF);
            data[3] = (byte) ((l >> 24) & 0xFF);
            return data;
        }
    }

    public static String byteArrayToDebugString(byte[] b) {
        return byteArrayToDebugString(b, 0, b.length);
    }

    public static String byteArrayToDebugString(byte[] ba, int length) {
        return byteArrayToDebugString(ba, 0, length);
    }

    public static String byteArrayToDebugString(byte[] ba, int start, int length) {
        StringBuffer buf = new StringBuffer(10 * length);
        for (int i = start; i < start + length; i++) {
            int b = ba[i];
            char c = (char) ba[i];
            if (c < 0x20)
                c = '.';
            String str = Integer.toHexString(b);
            if (b < 0)
                b = 256 + b;
            if (str.length() > 2)
                str = str.substring(str.length() - 2, str.length());
            buf.append(b + "/0x" + str + "/" + c + " ");
        }
        return buf.toString();
    }

    public static String bytesToHex(String byteStr, int offset)
            throws Exception {
        final char lookup[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f' };

        int i = 0;
        int c = 0;
        int j = 0;
        String hexStr = null;
        int pos = offset;
        byte[] hex = new byte[32];

        try {
            byte[] bytes = byteStr.getBytes("UTF-8");

            for (i = 0; i < 16; i++) {
                c = bytes[i] & 0xFF;
                j = c >> 4;
                hex[pos++] = (byte) lookup[j];
                j = (c & 0xF);
                hex[pos++] = (byte) lookup[j];
            }
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }

        hexStr = new String(hex);

        return hexStr;
    }

    private static char[] hex = "0123456789abcdef".toCharArray();

    public static String getHexString(long longValue, int size) {
    	long l = longValue;
        StringBuffer sb = new StringBuffer();
        if (l >= 0) {
            //positive
            for (int i = 0; i < size; i++) {
                int d = (int) (l & 0xF);
                l >>= 4;
                sb.insert(0, hex[d]);
            }
        } else {
            // negative
            l = (0 - l) - 1;
            for (int i = 0; i < size; i++) {
                int d = (int) (l & 0xF);
                l >>= 4;
                sb.insert(0, hex[15 - d]);
            }
        }

        return sb.toString();
    }

    public static String getHexString(long l) {
        return getHexString(l, 8);
    }

    public static String getHexString(byte[] data, int size) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < size - data.length; i++) {
            sb.append("00 ");
        }

        for (int i = 0; i < data.length; i++) {
            String str = Integer.toHexString(data[i]);
            if (str.length() == 1)
                sb.append("0");

            if (str.length() > 2)
                str = str.substring(str.length() - 2, str.length());

            sb.append(str);
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    public static String getHexString(byte[] data) {
        return ByteToolkit.getHexString(data, data.length);
    }

    public static String byteArrayToPrintableString(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            char c = (char) bytes[i];
            if (c < 0x20)
                c = '.';
            buf.append(c);
        }
        return buf.toString();
    }

    public static boolean areEqual(byte[] bytes1, byte[] bytes2) {
        // size compare, early return
        if (bytes1.length != bytes2.length)
            return false;

        for (int i = 0; i < bytes1.length; i++) {
            // byte compare, early return
            if (bytes1[i] != bytes2[i])
                return false;
        }

        return true;
    }

    private static MessageDigest md = null;

    public static byte[] getMD5(byte[] data) {
        try {
            if (md == null)
                md = MessageDigest.getInstance("MD5");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        synchronized (md) {
            return md.digest(data);
        }
    }

    public static byte[] getMD5(InputStream is) throws IOException {
        try {
            if (md == null)
                md = MessageDigest.getInstance("MD5");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        synchronized (md) {
            md.reset();
            byte[] buf = new byte[4096];
            while (true) {
                int count = is.read(buf);
                if (count == -1)
                    break;
                md.update(buf, 0, count);
            }

            return md.digest();
        }
    }

}
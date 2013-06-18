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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;

public class IOToolkit {

	public static byte[] readStream(int length, InputStream is)
			throws IOException {
		int read = 0; // count of already read bytes
		byte[] buf = new byte[length]; // byte array to read into

		while (read < length) { // read until length bytes have been read
			int i = is.read(buf, read, length - read); // try to read as many
														// bytes as there are
														// left
			if (i == -1)
				throw new IOException("No more bytes left.");
			read += i; // increase count with the actual # of bytes read
		}

		return buf;
	}

	public static void writeStream(InputStream is, OutputStream os)
			throws IOException {
		byte[] buf = new byte[4096]; // byte array to read into

		while (true) { // read until length bytes have been read
			int i = is.read(buf); // try to read as many bytes as there are
									// left
			if (i == -1)
				break;
			os.write(buf, 0, i);
		}
	}

	/**
	 * Calculate a checksum for the stream.
	 * @param is InputStream the stream for which to calculate the checksum.
	 * @param crc the checksum object to update.
	 * @throws IOException problem in the I/O processing.
	 */
	public static void updateCRCWithStream(InputStream is, CRC32 crc) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] buf = new byte[256];
		while (true) {
			int i = bis.read(buf);

			if (i == -1) {
				break;
			}

			crc.update(buf, 0, i);
		}

	}

	/**
	 * Compares two <code>InputStream</code>s for equality. Both streams are
	 * read and the content is compared.
	 * @param isA first stream to compare
	 * @param isB second stream to compare
	 * @return true if the streams have equal data, false otherwise.
	 * @throws IOException a problem in the I/O processing.
	 */
	public static boolean areEqual(InputStream isA, InputStream isB) throws IOException {
		byte[] bufA = new byte[256]; // byte array to read into
		byte[] bufB = new byte[256]; // byte array to read into

		while (true) { 
			int countA = isA.read(bufA);
			int countB = isB.read(bufB);
			
			if (countA != countB) {
				return false; // early return
			}
			
			if (countA == -1)
				break;
			// countB is automatically -1 too, because of the previous if
			
			for (int i=0; i < countA; i++) {
				if (bufA[i] != bufB[i]) return false; // early return
			}
		}
		
		return true;
	}

}

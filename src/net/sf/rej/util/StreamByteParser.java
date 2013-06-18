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

/**
 * A <code>ByteParser</code> subclass providing parsing of a stream
 * instead of a byte array (as is the case with ByteParser class).
 * 
 * @author Sami Koivu
 */
public class StreamByteParser implements ByteParser {
	/**
	 * The <code>InputStream</code> that is being read is wrapped in
	 * a <code>CountInputStream</code> so that current read position
	 * can be determined.
	 */
    private CountInputStream in;
    
    /**
     * Holds the bigEndian mode of this parser.
     */
    private boolean bigEndian = false;

    /**
     * Initializes a new parser with the given <code>InputStream</code>.
     * @param in the steam to parse.
     */
    public StreamByteParser(InputStream in) {
        this.in = new CountInputStream(in);
    }

	public byte getByte() throws ParsingException {
        try {
            return (byte)this.in.read();
        } catch(IOException ioe) {
            throw new ParsingException(ioe);
        }
    }

	public byte[] getBytes(int count) throws ParsingException {
        try {
            return IOToolkit.readStream(count, this.in);
        } catch(IOException ioe) {
            throw new ParsingException(ioe);
        }
    }

	public int getByteAsInt() throws ParsingException {
        try {
            return this.in.read();
        } catch(IOException ioe) {
            throw new ParsingException(ioe);
        }
    }

	public int getShortAsInt() throws ParsingException {
		int i = (int) ByteToolkit.getLong(getBytes(2), 0, 2,
				this.bigEndian);

        return i;
    }

	public long getInt() throws ParsingException {
		long l = ByteToolkit.getLong(getBytes(4), 0, 4, this.bigEndian);

		return l;
    }

    @Deprecated
	public ByteParser getNewParser() throws ParsingException {
        throw new ParsingException("Cannot create a new parser out of a StreamByteParser instance.");
    }

	public void setBigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }

	public boolean hasMore() throws ParsingException {
        try {
            this.in.mark(1);
            int test = this.in.read();
            this.in.reset();
            return test != -1;
        } catch(IOException ioe) {
            throw new ParsingException(ioe);
        }
    }

	public int getPosition() {
        return this.in.getPosition();
    }

	public int peekByte() {
        try {
            this.in.mark(2);
            int i = getByteAsInt();
            this.in.reset();
            return i;
        } catch(IOException ioe) {
            throw new ParsingException(ioe);
        }
    }

}

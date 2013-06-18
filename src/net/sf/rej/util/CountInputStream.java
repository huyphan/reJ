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

public class CountInputStream extends BufferedInputStream {
    private int pos = 0;
    private int markedPos = -1;

    public CountInputStream(InputStream is) {
        super(is);
    }

    @Override
	public int read() throws IOException {
        this.pos++;
        return super.read();
    }
    
    @Override
    public synchronized void mark(int readlimit) {
    	super.mark(readlimit);
    	this.markedPos = pos;
    }
    
    @Override
    public synchronized void reset() throws IOException {
    	this.pos = markedPos;
    	super.reset();
    }
    
    public int getPosition() {
        return this.pos;
    }

}

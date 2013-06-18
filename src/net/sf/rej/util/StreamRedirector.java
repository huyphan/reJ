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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Reads an <code>InputStream</code> in a loop for as long as there is
 * data available, writing the data, line-by-line to a PrintStream.
 * 
 * @author Sami Koivu
 */
public class StreamRedirector implements Runnable {
	BufferedReader br = null;
	PrintStream os = null;
	
	/**
	 * Initializes this object with the given <code>InputStream</code> to read
	 * from and the given <code>PrintStream</code> to write into.
	 * 
	 * @param is the stream to write.
	 * @param os the stream in which to write.
	 */
	public StreamRedirector(InputStream is, PrintStream os) {
		this.br = new BufferedReader(new InputStreamReader(is));
		this.os = os;
	}
	
	public void run() {
		try {
			while (true) {
				String line = br.readLine();
				if (line == null) break;
				this.os.println(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}

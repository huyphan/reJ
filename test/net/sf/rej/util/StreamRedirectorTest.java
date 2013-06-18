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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link net.sf.rej.util.StreamRedirector}
 * 
 * @author Sami Koivu
 */
public class StreamRedirectorTest extends TestCase {

	/**
	 * Test method for {@link net.sf.rej.util.StreamRedirector#run()}.
	 */
	@Test
	public final void testRedirection() {
		ByteArrayOutputStream srcData = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(srcData);
		out.println("Testing");
		out.println("1, 2, 3");
		out.println("...");
		byte[] data = srcData.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		StreamRedirector streamRedirector = new StreamRedirector(bais, ps);
		streamRedirector.run();
		byte[] resultData = baos.toByteArray();
		try {
			Assert.assertTrue("Byte data redirected is equal", Arrays.equals(data, resultData));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(data));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(resultData));
			throw ae;
		}
	}

}

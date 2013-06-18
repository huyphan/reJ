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

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link net.sf.rej.util.ByteArrayByteParser}
 * 
 * @author Sami Koivu
 */
public class ByteArrayByteParserTest extends TestCase {

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#getByte()}.
	 */
	@Test
	public final void testGetByte() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		Assert.assertEquals("ca", (byte)0xca, parser.getByte());
		Assert.assertEquals("fe", (byte)0xfe, parser.getByte());
		Assert.assertEquals("00", (byte)0x00, parser.getByte());
		Assert.assertEquals("ba", (byte)0xba, parser.getByte());
		Assert.assertEquals("be", (byte)0xbe, parser.getByte());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#getBytes(int)}.
	 */
	@Test
	public final void testGetBytes() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		byte[] firstTwo = parser.getBytes(2);
		byte[] expectedFirstTwo = {(byte)0xca, (byte)0xfe};
		try {
			Assert.assertTrue("Equality of first two bytes", Arrays.equals(expectedFirstTwo, firstTwo));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(expectedFirstTwo));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(firstTwo));
			throw ae;
		}

		byte[] rest = parser.getBytes(3);
		byte[] expectedRest = {0x00, (byte)0xba, (byte)0xbe};
		try {
			Assert.assertTrue("Equality of last threebytes", Arrays.equals(expectedRest, rest));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(expectedRest));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(rest));
			throw ae;
		}
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#getByteAsInt()}.
	 */
	@Test
	public final void testGetByteAsInt() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		Assert.assertEquals("ca", 0xca, parser.getByteAsInt());
		Assert.assertEquals("fe", 0xfe, parser.getByteAsInt());
		Assert.assertEquals("00", 0x00, parser.getByteAsInt());
		Assert.assertEquals("ba", 0xba, parser.getByteAsInt());
		Assert.assertEquals("be", 0xbe, parser.getByteAsInt());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#getShortAsInt()}.
	 */
	@Test
	public final void testGetShortAsInt() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		parser.setBigEndian(true);
		Assert.assertEquals("first short", 0xca*256 + 0xfe, parser.getShortAsInt());
		Assert.assertEquals("second short", 0xba, parser.getShortAsInt());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#getInt()}.
	 */
	@Test
	public final void testGetInt() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		parser.setBigEndian(true);
		long expected = 0xca;
		expected *= 256;
		expected += 0xfe;
		expected *= 256;
		expected += 0;
		expected *= 256;
		expected += 0xba;
		
		Assert.assertEquals("int", expected, parser.getInt());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#setBigEndian(boolean)}.
	 */
	@Test
	public final void testSetBigEndian() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		parser.setBigEndian(true);
		Assert.assertEquals("big endian short", 0xca*256 + 0xfe, parser.getShortAsInt());
		parser.setBigEndian(false);
		Assert.assertEquals("little endian short", 0xba*256, parser.getShortAsInt());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#hasMore()}.
	 */
	@Test
	public final void testHasMore() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		Assert.assertTrue("At the start", parser.hasMore());
		parser.getByte();
		Assert.assertTrue("After first", parser.hasMore());
		parser.getByte();
		Assert.assertTrue("After second", parser.hasMore());
		parser.getByte();
		Assert.assertTrue("After third", parser.hasMore());
		parser.getByte();
		Assert.assertTrue("After fourth", parser.hasMore());
		parser.getByte();
		Assert.assertFalse("At the end", parser.hasMore());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#getPosition()}.
	 */
	@Test
	public final void testGetPosition() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		Assert.assertEquals("start", 0, parser.getPosition());
		parser.getByte();
		Assert.assertEquals("after read (position 1)", 1, parser.getPosition());
		parser.peekByte();
		Assert.assertEquals("after peek (position 1)", 1, parser.getPosition());		
		parser.getByte();
		Assert.assertEquals("after read (position 2)", 2, parser.getPosition());
		parser.getByte();
		Assert.assertTrue("has more", parser.hasMore());
		Assert.assertEquals("after hasMore()", 3, parser.getPosition());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#peekByte()}.
	 */
	@Test
	public final void testPeekByte() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		Assert.assertEquals("ca", 0xca, parser.peekByte());
		Assert.assertEquals("ca", 0xca, parser.getByteAsInt());
		Assert.assertEquals("fe", 0xfe, parser.getByteAsInt());
		Assert.assertEquals("00", 0x00, parser.peekByte());		
		Assert.assertEquals("00", 0x00, parser.peekByte());		
		Assert.assertEquals("00", 0x00, parser.peekByte());		
		Assert.assertEquals("00", 0x00, parser.getByteAsInt());
		Assert.assertEquals("ba", 0xba, parser.getByteAsInt());
		Assert.assertEquals("be", 0xbe, parser.getByteAsInt());
	}

	
	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#ByteArrayByteParser(byte[])}.
	 */
	@Test
	public final void testByteArrayByteParserByteArray() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		Assert.assertEquals("ca", (byte)0xca, parser.getByte());
		Assert.assertEquals("fe", (byte)0xfe, parser.getByte());
		Assert.assertEquals("00", (byte)0x00, parser.getByte());
		Assert.assertEquals("ba", (byte)0xba, parser.getByte());
		Assert.assertEquals("be", (byte)0xbe, parser.getByte());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#ByteArrayByteParser(byte[], int)}.
	 */
	@Test
	public final void testByteArrayByteParserByteArrayInt() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data, 3);
		Assert.assertEquals("ba", (byte)0xba, parser.getByte());
		Assert.assertEquals("be", (byte)0xbe, parser.getByte());
	}

	/**
	 * Test method for {@link net.sf.rej.util.ByteArrayByteParser#getNewParser()}.
	 */
	@Test
	public final void testGetNewParser() {
		byte[] data = {(byte)0xca, (byte)0xfe, 0x00, (byte)0xba, (byte)0xbe};
		ByteArrayByteParser parser = new ByteArrayByteParser(data);
		Assert.assertEquals("ca", (byte)0xca, parser.getByte());
		Assert.assertEquals("fe", (byte)0xfe, parser.getByte());
		Assert.assertEquals("00", (byte)0x00, parser.getByte());
		@SuppressWarnings("deprecation")
		ByteParser newParser = parser.getNewParser();
		Assert.assertEquals("ba", (byte)0xba, parser.getByte());
		Assert.assertEquals("be", (byte)0xbe, parser.getByte());

		Assert.assertEquals("new parser 0xba", (byte)0xba, newParser.getByte());
		Assert.assertEquals("new parser 0xbe", (byte)0xbe, newParser.getByte());
		
	}

}

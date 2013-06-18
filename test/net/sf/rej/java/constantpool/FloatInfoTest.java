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
package net.sf.rej.java.constantpool;

import java.util.Arrays;

import junit.framework.TestCase;

import net.sf.rej.util.ByteToolkit;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link net.sf.rej.java.constantpool.FloatInfo}
 * 
 * @author Sami Koivu
 */
public class FloatInfoTest extends TestCase {

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.FloatInfo#getData()}.
	 */
	@Test
	public final void testGetData() {
		FloatInfo fi = new FloatInfo(0, new ConstantPool());
		byte[] data = fi.getData();
		byte[] expected = {ConstantPoolInfo.FLOAT, 0, 0, 0, 0};
		try {
			Assert.assertTrue("0", Arrays.equals(expected, data));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(expected));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(data));
			throw ae;
		}

		fi = new FloatInfo(19, new ConstantPool());
		data = fi.getData();
		expected = new byte[] {ConstantPoolInfo.FLOAT, 0, 0, 0, 19};
		try {
			Assert.assertTrue("19", Arrays.equals(expected, data));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(expected));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(data));
			throw ae;
		}

		fi = new FloatInfo(258, new ConstantPool());
		data = fi.getData();
		expected = new byte[] {ConstantPoolInfo.FLOAT, 0, 0, 1, 2};
		try {
			Assert.assertTrue("258", Arrays.equals(expected, data));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(expected));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(data));
			throw ae;
		}

	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.FloatInfo#getTypeString()}.
	 */
	@Test
	public final void testGetTypeString() {
		ConstantPool cp = new ConstantPool();
		FloatInfo fi = new FloatInfo(0, cp);
		Assert.assertEquals("type string", "Float constant", fi.getTypeString());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.FloatInfo#FloatInfo(int, net.sf.rej.java.constantpool.ConstantPool)}.
	 */
	@Test
	public final void testFloatInfoIntConstantPool() {
		FloatInfo fi = new FloatInfo(0, new ConstantPool());
		Assert.assertEquals("0", 0, fi.getBytes());
		fi = new FloatInfo(1, new ConstantPool());
		Assert.assertEquals("1", 1, fi.getBytes());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.FloatInfo#FloatInfo(float, net.sf.rej.java.constantpool.ConstantPool)}.
	 */
	@Test
	public final void testFloatInfoFloatConstantPool() {
		FloatInfo fi = new FloatInfo(0.0f, new ConstantPool());
		Assert.assertEquals("0.0f", 0.0f, fi.getFloatValue());
		fi = new FloatInfo(1.1f, new ConstantPool());
		Assert.assertEquals("1.1f", 1.1f, fi.getFloatValue());
		fi = new FloatInfo(-1.1f, new ConstantPool());
		Assert.assertEquals("-1.1f", -1.1f, fi.getFloatValue());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.FloatInfo#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		FloatInfo a = new FloatInfo(0f, new ConstantPool());
		
		Assert.assertTrue("same instance", a.equals(a));
		
		FloatInfo b = new FloatInfo(0f, new ConstantPool());
		
		Assert.assertTrue("both zero a.equals(b)", a.equals(b));
		Assert.assertTrue("both zero b.equals(a)", b.equals(a));
		
		b.setFloatValue(1f);
		Assert.assertFalse("a=0, b=1 a.equals(b)", a.equals(b));
		Assert.assertFalse("a=0, b=1 b.equals(a)", b.equals(a));
		
		a.setFloatValue(Float.NaN);
		Assert.assertFalse("a=NaN, b=1 a.equals(b)", a.equals(b));
		Assert.assertFalse("a=NaN, b=1 b.equals(a)", b.equals(a));

		b.setFloatValue(Float.NaN);
		Assert.assertTrue("a=NaN, b=NaN a.equals(b)", a.equals(b));
		Assert.assertTrue("a=NaN, b=NaN b.equals(a)", b.equals(a));

		a.setFloatValue(Float.POSITIVE_INFINITY);
		Assert.assertFalse("a=positive inf, b=NaN a.equals(b)", a.equals(b));
		Assert.assertFalse("a=positive inf, b=NaN b.equals(a)", b.equals(a));

		b.setFloatValue(3.5f);
		a.setFloatValue(Float.NEGATIVE_INFINITY);
		Assert.assertFalse("a=negative inf, b=3.5f a.equals(b)", a.equals(b));
		Assert.assertFalse("a=negative inf, b=3.5f b.equals(a)", b.equals(a));

		b.setFloatValue(3.5f);
		a.setFloatValue(3.5f);
		Assert.assertTrue("a=3.5f, b=3.5f a.equals(b)", a.equals(b));
		Assert.assertTrue("a=3.5f, b=3.5f b.equals(a)", b.equals(a));

		a.setFloatValue(-3.5f);
		Assert.assertFalse("a=-3.5f, b=3.5f a.equals(b)", a.equals(b));
		Assert.assertFalse("a=-3.5f, b=3.5f b.equals(a)", b.equals(a));
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.FloatInfo#getFloatValue()}.
	 */
	@Test
	public final void testGetFloatValue() {
		ConstantPool cp = new ConstantPool();
		FloatInfo fi = new FloatInfo(1.0f, cp);
		Assert.assertEquals("1.0f", 1.0f, fi.getFloatValue());
		fi = new FloatInfo(0.0f, cp);
		Assert.assertEquals("0.0f", 0.0f, fi.getFloatValue());
		fi = new FloatInfo(-1.0f, cp);
		Assert.assertEquals("-1.0f", -1.0f, fi.getFloatValue());
		fi = new FloatInfo(618.8f, cp);
		Assert.assertEquals("618.8f", 618.8f, fi.getFloatValue());
		fi = new FloatInfo(Float.NEGATIVE_INFINITY, cp);
		Assert.assertEquals("negative infinity", Float.NEGATIVE_INFINITY, fi.getFloatValue());
		fi = new FloatInfo(Float.POSITIVE_INFINITY, cp);
		Assert.assertEquals("positive infinity", Float.POSITIVE_INFINITY, fi.getFloatValue());
		fi = new FloatInfo(Float.NaN, cp);
		Assert.assertEquals("not a number", Float.NaN, fi.getFloatValue());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.FloatInfo#setFloatValue(float)}.
	 */
	@Test
	public final void testSetFloatValue() {
		ConstantPool cp = new ConstantPool();
		FloatInfo fi = new FloatInfo(0.0f, cp);
		fi.setFloatValue(1.0f);
		Assert.assertEquals("1.0f", 1.0f, fi.getFloatValue());
		fi.setFloatValue(0.0f);
		Assert.assertEquals("0.0f", 0.0f, fi.getFloatValue());
		fi.setFloatValue(-1.0f);
		Assert.assertEquals("-1.0f", -1.0f, fi.getFloatValue());
		fi.setFloatValue(618.8f);
		Assert.assertEquals("618.8f", 618.8f, fi.getFloatValue());
		fi.setFloatValue(Float.NEGATIVE_INFINITY);
		Assert.assertEquals("negative infinity", Float.NEGATIVE_INFINITY, fi.getFloatValue());
		fi.setFloatValue(Float.POSITIVE_INFINITY);
		Assert.assertEquals("positive infinity", Float.POSITIVE_INFINITY, fi.getFloatValue());
		fi.setFloatValue(Float.NaN);
		Assert.assertEquals("not a number", Float.NaN, fi.getFloatValue());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.FloatInfo#getBytes()}.
	 */
	@Test
	public final void testGetBytes() {
		FloatInfo fi = new FloatInfo(0, new ConstantPool());
		Assert.assertEquals("0", 0, fi.getBytes());
		fi = new FloatInfo(1, new ConstantPool());
		Assert.assertEquals("1", 1, fi.getBytes());
	}

}

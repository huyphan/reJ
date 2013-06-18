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
 * Unit tests for {@link net.sf.rej.java.constantpool.DoubleInfo}
 * 
 * @author Sami Koivu
 */
public class DoubleInfoTest extends TestCase {

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.DoubleInfo#getData()}.
	 */
	@Test
	public final void testGetData() {
		DoubleInfo di = new DoubleInfo(0, 0, new ConstantPool());
		byte[] data = di.getData();
		byte[] expected = {ConstantPoolInfo.DOUBLE, 0, 0, 0, 0, 0, 0, 0, 0};
		try {
			Assert.assertTrue("0", Arrays.equals(expected, data));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(expected));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(data));
			throw ae;
		}

		di = new DoubleInfo(0, 19, new ConstantPool());
		data = di.getData();
		expected = new byte[] {ConstantPoolInfo.DOUBLE, 0, 0, 0, 0, 0, 0, 0, 19};
		try {
			Assert.assertTrue("19", Arrays.equals(expected, data));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(expected));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(data));
			throw ae;
		}

		di = new DoubleInfo(0, 258, new ConstantPool());
		data = di.getData();
		expected = new byte[] {ConstantPoolInfo.DOUBLE, 0, 0, 0, 0, 0, 0, 1, 2};
		try {
			Assert.assertTrue("258", Arrays.equals(expected, data));
		} catch(AssertionError ae) {
			System.out.println("Expected: " + ByteToolkit.byteArrayToDebugString(expected));
			System.out.println("Result: " + ByteToolkit.byteArrayToDebugString(data));
			throw ae;
		}

	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.DoubleInfo#getTypeString()}.
	 */
	@Test
	public final void testGetTypeString() {
		ConstantPool cp = new ConstantPool();
		DoubleInfo di = new DoubleInfo(0, cp);
		Assert.assertEquals("type string", "Double constant", di.getTypeString());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.DoubleInfo#DoubleInfo(int, net.sf.rej.java.constantpool.ConstantPool)}.
	 */
	@Test
	public final void testDoubleInfoIntConstantPool() {
		DoubleInfo di = new DoubleInfo(0, 0, new ConstantPool());
		Assert.assertEquals("0 high bytes", 0l, di.getHighBytes());
		Assert.assertEquals("0 low bytes", 0l, di.getLowBytes());
		di = new DoubleInfo(0, 1, new ConstantPool());
		Assert.assertEquals("0 high bytes", 0l, di.getHighBytes());
		Assert.assertEquals("1 low bytes", 1l, di.getLowBytes());
		di = new DoubleInfo(2, 1, new ConstantPool());
		Assert.assertEquals("2 high bytes", 2l, di.getHighBytes());
		Assert.assertEquals("1 low bytes", 1l, di.getLowBytes());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.DoubleInfo#DoubleInfo(float, net.sf.rej.java.constantpool.ConstantPool)}.
	 */
	@Test
	public final void testDoubleInfoDoubleConstantPool() {
		DoubleInfo di = new DoubleInfo(0.0d, new ConstantPool());
		Assert.assertEquals("0.0d", 0.0d, di.getDoubleValue());
		di = new DoubleInfo(1.1d, new ConstantPool());
		Assert.assertEquals("1.1d", 1.1d, di.getDoubleValue());
		di = new DoubleInfo(-1.1d, new ConstantPool());
		Assert.assertEquals("-1.1d", -1.1d, di.getDoubleValue());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.DoubleInfo#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		DoubleInfo a = new DoubleInfo(0f, new ConstantPool());
		
		Assert.assertTrue("same instance", a.equals(a));
		
		DoubleInfo b = new DoubleInfo(0f, new ConstantPool());
		
		Assert.assertTrue("both zero a.equals(b)", a.equals(b));
		Assert.assertTrue("both zero b.equals(a)", b.equals(a));
		
		b.setDoubleValue(1f);
		Assert.assertFalse("a=0, b=1 a.equals(b)", a.equals(b));
		Assert.assertFalse("a=0, b=1 b.equals(a)", b.equals(a));
		
		a.setDoubleValue(Double.NaN);
		Assert.assertFalse("a=NaN, b=1 a.equals(b)", a.equals(b));
		Assert.assertFalse("a=NaN, b=1 b.equals(a)", b.equals(a));

		b.setDoubleValue(Double.NaN);
		Assert.assertTrue("a=NaN, b=NaN a.equals(b)", a.equals(b));
		Assert.assertTrue("a=NaN, b=NaN b.equals(a)", b.equals(a));

		a.setDoubleValue(Double.POSITIVE_INFINITY);
		Assert.assertFalse("a=positive inf, b=NaN a.equals(b)", a.equals(b));
		Assert.assertFalse("a=positive inf, b=NaN b.equals(a)", b.equals(a));

		b.setDoubleValue(3.5d);
		a.setDoubleValue(Double.NEGATIVE_INFINITY);
		Assert.assertFalse("a=negative inf, b=3.5d a.equals(b)", a.equals(b));
		Assert.assertFalse("a=negative inf, b=3.5d b.equals(a)", b.equals(a));

		b.setDoubleValue(3.5d);
		a.setDoubleValue(3.5d);
		Assert.assertTrue("a=3.5d, b=3.5d a.equals(b)", a.equals(b));
		Assert.assertTrue("a=3.5d, b=3.5d b.equals(a)", b.equals(a));

		a.setDoubleValue(-3.5d);
		Assert.assertFalse("a=-3.5d, b=3.5d a.equals(b)", a.equals(b));
		Assert.assertFalse("a=-3.5d, b=3.5d b.equals(a)", b.equals(a));
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.DoubleInfo#getDoubleValue()}.
	 */
	@Test
	public final void testGetDoubleValue() {
		ConstantPool cp = new ConstantPool();
		DoubleInfo di = new DoubleInfo(1.0d, cp);
		Assert.assertEquals("1.0d", 1.0d, di.getDoubleValue());
		di = new DoubleInfo(0.0d, cp);
		Assert.assertEquals("0.0d", 0.0d, di.getDoubleValue());
		di = new DoubleInfo(-1.0d, cp);
		Assert.assertEquals("-1.0d", -1.0d, di.getDoubleValue());
		di = new DoubleInfo(618.8d, cp);
		Assert.assertEquals("618.8d", 618.8d, di.getDoubleValue());
		di = new DoubleInfo(Double.NEGATIVE_INFINITY, cp);
		Assert.assertEquals("negative indinity", Double.NEGATIVE_INFINITY, di.getDoubleValue());
		di = new DoubleInfo(Double.POSITIVE_INFINITY, cp);
		Assert.assertEquals("positive indinity", Double.POSITIVE_INFINITY, di.getDoubleValue());
		di = new DoubleInfo(Double.NaN, cp);
		Assert.assertEquals("not a number", Double.NaN, di.getDoubleValue());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.DoubleInfo#setDoubleValue(float)}.
	 */
	@Test
	public final void testSetDoubleValue() {
		ConstantPool cp = new ConstantPool();
		DoubleInfo di = new DoubleInfo(0.0d, cp);
		di.setDoubleValue(1.0d);
		Assert.assertEquals("1.0d", 1.0d, di.getDoubleValue());
		di.setDoubleValue(0.0d);
		Assert.assertEquals("0.0d", 0.0d, di.getDoubleValue());
		di.setDoubleValue(-1.0d);
		Assert.assertEquals("-1.0d", -1.0d, di.getDoubleValue());
		di.setDoubleValue(618.8d);
		Assert.assertEquals("618.8d", 618.8d, di.getDoubleValue());
		di.setDoubleValue(Double.NEGATIVE_INFINITY);
		Assert.assertEquals("negative indinity", Double.NEGATIVE_INFINITY, di.getDoubleValue());
		di.setDoubleValue(Double.POSITIVE_INFINITY);
		Assert.assertEquals("positive indinity", Double.POSITIVE_INFINITY, di.getDoubleValue());
		di.setDoubleValue(Double.NaN);
		Assert.assertEquals("not a number", Double.NaN, di.getDoubleValue());
	}

	/**
	 * Test method for {@link net.sf.rej.java.constantpool.DoubleInfo#getHighBytes()}
	 * and {@link net.sf.rej.java.constantpool.DoubleInfo#getLowBytes()}.
	 */
	@Test
	public final void testGetBytes() {
		DoubleInfo di = new DoubleInfo(0, 0, new ConstantPool());
		Assert.assertEquals("0 high bytes", 0l, di.getHighBytes());
		Assert.assertEquals("0 low bytes", 0l, di.getLowBytes());
		di = new DoubleInfo(0, 1, new ConstantPool());
		Assert.assertEquals("0 high bytes", 0l, di.getHighBytes());
		Assert.assertEquals("1 low bytes", 1l, di.getLowBytes());
		di = new DoubleInfo(2, 1, new ConstantPool());
		Assert.assertEquals("2 high bytes", 2l, di.getHighBytes());
		Assert.assertEquals("1 low bytes", 1l, di.getLowBytes());
	}

}

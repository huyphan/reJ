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
package net.sf.rej.java;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class JavaTypeTest extends TestCase {

	@Test
	public final void testJavaTypeString() {
		{
			JavaType jt = new JavaType("int");
			Assert.assertEquals("int", jt.getType());
			Assert.assertEquals(0, jt.getDimensionCount());
		}
		{
			JavaType jt = new JavaType("short[]");
			Assert.assertEquals("short", jt.getType());
			Assert.assertEquals(1, jt.getDimensionCount());
		}
		{
			JavaType jt = new JavaType("java.util.List[][]");
			Assert.assertEquals("java.util.List", jt.getType());
			Assert.assertEquals(2, jt.getDimensionCount());
		}
	}

	@Test
	public final void testJavaTypeStringInt() {
		{
			JavaType jt = new JavaType("int", 0);
			Assert.assertEquals("int", jt.getType());
			Assert.assertEquals(0, jt.getDimensionCount());
		}
		{
			JavaType jt = new JavaType("short", 1);
			Assert.assertEquals("short", jt.getType());
			Assert.assertEquals(1, jt.getDimensionCount());
		}
		{
			JavaType jt = new JavaType("java.util.List", 2);
			Assert.assertEquals("java.util.List", jt.getType());
			Assert.assertEquals(2, jt.getDimensionCount());
		}
	}

	@Test
	public final void testGetType() {
		{
			JavaType jt = new JavaType("java.util.List", 0);
			Assert.assertEquals("java.util.List", jt.getType());
		}
		{
			JavaType jt = new JavaType("foo.bar");
			Assert.assertEquals("foo.bar", jt.getType());
		}
	}

	@Test
	public final void testGetDimensions() {
		{
			JavaType jt = new JavaType("java.util.List", 0);
			Assert.assertEquals("", jt.getDimensions());
		}
		{
			JavaType jt = new JavaType("java.util.List", 1);
			Assert.assertEquals("[]", jt.getDimensions());
		}
		{
			JavaType jt = new JavaType("foo.bar[][]");
			Assert.assertEquals("[][]", jt.getDimensions());
		}
	}

	@Test
	public final void testIsPrimitive() {
		{
			JavaType jt = new JavaType("java.util.List", 1);
			Assert.assertFalse(jt.isPrimitive());
		}
		{
			JavaType jt = new JavaType("int", 0);
			Assert.assertTrue(jt.isPrimitive());
		}
		{
			JavaType jt = new JavaType("byte");
			Assert.assertTrue(jt.isPrimitive());
		}
		{
			JavaType jt = new JavaType("short");
			Assert.assertTrue(jt.isPrimitive());
		}
		{
			JavaType jt = new JavaType("char");
			Assert.assertTrue(jt.isPrimitive());
		}
		{
			JavaType jt = new JavaType("long");
			Assert.assertTrue(jt.isPrimitive());
		}
		{
			JavaType jt = new JavaType("float");
			Assert.assertTrue(jt.isPrimitive());
		}
		{
			JavaType jt = new JavaType("double");
			Assert.assertTrue(jt.isPrimitive());
		}
		{
			JavaType jt = new JavaType("boolean");
			Assert.assertTrue(jt.isPrimitive());
		}
	}

	@Test
	public final void testToString() {
		{
			JavaType jt = new JavaType("boolean");
			Assert.assertEquals("boolean", jt.toString());
		}
		{
			JavaType jt = new JavaType("java.lang.String");
			Assert.assertEquals("java.lang.String", jt.toString());
		}
		{
			JavaType jt = new JavaType("java.lang.String", 2);
			Assert.assertEquals("java.lang.String[][]", jt.toString());
		}		
	}

	@Test
	public final void testGetDimensionCount() {
		{
			JavaType jt = new JavaType("boolean");
			Assert.assertEquals(0, jt.getDimensionCount());
		}
		{
			JavaType jt = new JavaType("java.lang.String");
			Assert.assertEquals("java.lang.String", jt.toString());
		}
		{
			JavaType jt = new JavaType("java.lang.String", 2);
			Assert.assertEquals("java.lang.String[][]", jt.toString());
		}		
	}

	@Test
	public final void testGetRaw() {
		{
			JavaType jt = new JavaType("boolean");
			Assert.assertEquals("Z", jt.getRaw());
		}
		{
			JavaType jt = new JavaType("int");
			Assert.assertEquals("I", jt.getRaw());
		}
		{
			JavaType jt = new JavaType("long");
			Assert.assertEquals("J", jt.getRaw());
		}
		{
			JavaType jt = new JavaType("java.lang.String");
			Assert.assertEquals("Ljava/lang/String;", jt.getRaw());
		}
		{
			JavaType jt = new JavaType("char[][]");
			Assert.assertEquals("[[C", jt.getRaw());
		}
		{
			JavaType jt = new JavaType("java.util.List", 1);
			Assert.assertEquals("[Ljava/util/List;", jt.getRaw());
		}
	}

	@Test
	public final void testEqualsObject() {
		{
			JavaType jt1 = new JavaType("java.util.List", 1);
			JavaType jt2 = new JavaType("java.util.List[]");
			Assert.assertTrue(jt1.equals(jt2));
			Assert.assertTrue(jt2.equals(jt1));
			Assert.assertTrue(jt1.hashCode() == jt2.hashCode());
		}
		{
			JavaType jt1 = new JavaType("long", 2);
			JavaType jt2 = new JavaType("long[][]");
			Assert.assertTrue(jt1.equals(jt2));
			Assert.assertTrue(jt2.equals(jt1));
			Assert.assertTrue(jt1.hashCode() == jt2.hashCode());
		}
		{
			JavaType jt1 = new JavaType("double", 2);
			JavaType jt2 = new JavaType("double");
			Assert.assertFalse(jt1.equals(jt2));
			Assert.assertFalse(jt2.equals(jt1));
		}
		{
			JavaType jt1 = new JavaType("java.lang.String");
			JavaType jt2 = new JavaType("java.lang.Integer");
			Assert.assertFalse(jt1.equals(jt2));
			Assert.assertFalse(jt2.equals(jt1));
		}
	}
	
	@Test
	public final void testDropDimension() {
		{
			JavaType jt = new JavaType("boolean", 2);
			Assert.assertEquals(2, jt.getDimensionCount());
			jt.dropDimension();
			Assert.assertEquals(1, jt.getDimensionCount());
			jt.dropDimension();
			Assert.assertEquals(0, jt.getDimensionCount());
		}
	}

}

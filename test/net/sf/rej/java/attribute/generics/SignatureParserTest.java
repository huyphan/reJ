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
package net.sf.rej.java.attribute.generics;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class SignatureParserTest extends TestCase {

	@Test
	public final void testHasMore() {
		SignatureParser parser = new SignatureParser("");
		Assert.assertFalse("Empty signature returns false right away", parser.hasMore());

		parser = new SignatureParser("Z");
		Assert.assertTrue("One boolean left", parser.hasMore());
		parser.getGenericType();
		Assert.assertFalse("At the end of a signature", parser.hasMore());
	}

	@Test
	public final void testGetDimensionCount() {
		{
			SignatureParser parser = new SignatureParser("[[[B");
			int dimCount = parser.getDimensionCount();
			Assert.assertEquals("Three dimensions", 3, dimCount);
		}
		
		{
			SignatureParser parser = new SignatureParser("Ljava/lang/Object;");
			int dimCount = parser.getDimensionCount();
			Assert.assertEquals("No dimensions", 0, dimCount);
		}
	}

	@Test
	public final void testGetGenericType() {
		SignatureParser parser = new SignatureParser("ILjava/lang/String;TT;[[[JLjava/util/List<Ljava/lang/String;>;");
		{
			GenericJavaType gjt = parser.getGenericType();
			Assert.assertEquals("Simple primitive", "int", gjt.getBaseType().getType());
		}
		{
			GenericJavaType gjt = parser.getGenericType();
			Assert.assertEquals("Reference type", "java.lang.String", gjt.getBaseType().getType());
		}
		{
			GenericJavaType gjt = parser.getGenericType();
			Assert.assertEquals("Generic param identifier", "T", gjt.getBaseType().getType());
		}
		{
			GenericJavaType gjt = parser.getGenericType();
			Assert.assertEquals("Array of primitive", "long", gjt.getBaseType().getType());
			Assert.assertEquals("Array of primitive", 3, gjt.getBaseType().getDimensionCount());
		}
		{
			GenericJavaType gjt = parser.getGenericType();
			Assert.assertEquals("Reference type with type argument", "java.util.List", gjt.getBaseType().getType());
			List<TypeArgument> typeArgs = gjt.getTypeArguments();
			Assert.assertEquals("Type argument count", 1,  typeArgs.size());
			TypeArgument typeArg = typeArgs.get(0);
			GenericJavaType genTypeArg = (GenericJavaType) typeArg;
			Assert.assertEquals("Type argument 1", "java.lang.String",  genTypeArg.getBaseType().getType());			
		}
	}

	@Test
	public final void testGetPrimitiveName() {
		Assert.assertEquals("byte", SignatureParser.getPrimitiveName('B'));
		Assert.assertEquals("char", SignatureParser.getPrimitiveName('C'));
		Assert.assertEquals("double", SignatureParser.getPrimitiveName('D'));
		Assert.assertEquals("float", SignatureParser.getPrimitiveName('F'));
		Assert.assertEquals("int", SignatureParser.getPrimitiveName('I'));
		Assert.assertEquals("long", SignatureParser.getPrimitiveName('J'));
		Assert.assertEquals("short", SignatureParser.getPrimitiveName('S'));
		Assert.assertEquals("boolean", SignatureParser.getPrimitiveName('Z'));
		Assert.assertEquals("void", SignatureParser.getPrimitiveName('V'));
	}

	@Test
	public final void testGetFirstIndex() {
		SignatureParser parser = new SignatureParser("OneTwoOneThreeCatDogBlues");
		Assert.assertEquals("One target", 0, parser.getFirstIndex("One"));
		Assert.assertEquals("One target", 17, parser.getFirstIndex("Dog"));

		Assert.assertEquals("Two targets", 0, parser.getFirstIndex("One", "Two"));
		Assert.assertEquals("Two targets", 3, parser.getFirstIndex("Dog", "Two"));

		Assert.assertEquals("Three targets", 3, parser.getFirstIndex("Dog", "Two", "Three"));
	}

	@Test
	public final void testGetGenericDefName() {
		SignatureParser parser = new SignatureParser("T:Ljava/lang/Object;");
		String name = parser.getTypeParameterIdentifier();
		Assert.assertEquals("Type argument identifier", name, "T");
	}

	@Test
	public final void testGetFormalTypeParameter() {
		{
			SignatureParser parser = new SignatureParser("<NAME:Ljava/lang/Object;>");
			List<FormalTypeParameter> typeParams = parser.getFormalTypeParameters();
			Assert.assertEquals("Type param count", 1, typeParams.size());
			FormalTypeParameter typeParam = typeParams.get(0);
			Assert.assertEquals("Type param identifier", "NAME", typeParam.getIdentifier());
			String superClass = typeParam.getClassBound().getBaseType().getType();
			Assert.assertEquals("Class bound", "java.lang.Object", superClass);
		}
		{
			SignatureParser parser = new SignatureParser("<T:Ljava/lang/Object;:Ljava/util/List;:Ljava/lang/Runnable;>");
			List<FormalTypeParameter> typeParams = parser.getFormalTypeParameters();
			Assert.assertEquals("Type param count", 1, typeParams.size());
			FormalTypeParameter typeParam = typeParams.get(0);
			String superClass = typeParam.getClassBound().getBaseType().getType();
			Assert.assertEquals("Class bound", "java.lang.Object", superClass);
			List<GenericJavaType> intfs = typeParam.getInterfaceBounds();
			Assert.assertEquals("Interface bound count", 2, intfs.size());
			Assert.assertEquals("Interfaces 1", "java.util.List", intfs.get(0).getBaseType().getType());
			Assert.assertEquals("Interfaces 2", "java.lang.Runnable", intfs.get(1).getBaseType().getType());			
		}
		{
			SignatureParser parser = new SignatureParser("<NAME::Ljava/lang/Runnable;>");
			List<FormalTypeParameter> typeParams = parser.getFormalTypeParameters();
			Assert.assertEquals("Type param count", 1, typeParams.size());
			FormalTypeParameter typeParam = typeParams.get(0);
			Assert.assertNull("No class bound", typeParam.getClassBound());
			List<GenericJavaType> intfs = typeParam.getInterfaceBounds();
			Assert.assertEquals("Interface bound count", 1, intfs.size());
			Assert.assertEquals("Interface 1", "java.lang.Runnable", intfs.get(0).getBaseType().getType());
		}
		{
			SignatureParser parser = new SignatureParser("<CCC1:Ljava/lang/Number;CCC2::Ljava/util/List<Ljava/lang/String;>;>");
			List<FormalTypeParameter> typeParams = parser.getFormalTypeParameters();
			Assert.assertEquals("Type param count", 2, typeParams.size());
			{
				FormalTypeParameter typeParam = typeParams.get(0);
				Assert.assertEquals("Type param identifier", "CCC1", typeParam.getIdentifier());
				String superClass = typeParam.getClassBound().getBaseType().getType();
				Assert.assertEquals("Class bound", "java.lang.Number", superClass);
			}
			{
				FormalTypeParameter typeParam = typeParams.get(1);
				Assert.assertEquals("Type param identifier", "CCC2", typeParam.getIdentifier());
				String superClass = typeParam.getInterfaceBounds().get(0).toString();
				Assert.assertEquals("Interface bound", "java.util.List<java.lang.String>", superClass);
			}
		}
	}

	@Test
	public final void testGetMethodParameters() {
		SignatureParser parser = new SignatureParser("(Lnet/sf/rej/guineapigs/SignaturePig2<-Ljava/lang/Integer;>;I)V");
		List<GenericJavaType> params = parser.getMethodParameters();
		Assert.assertEquals("Param count", 2, params.size());
		GenericJavaType param1 = params.get(0);
		Assert.assertEquals("Param 1 base type", "net.sf.rej.guineapigs.SignaturePig2", param1.getBaseType().getType());
		List<TypeArgument> typeParams = param1.getTypeArguments();
		Assert.assertEquals("Param 1 type parameters count", 1, typeParams.size());
		BoundTypeArgument typeParam = (BoundTypeArgument) typeParams.get(0);
		Assert.assertEquals("Param 1 type param type toString()", "? super java.lang.Integer", typeParam.toString());
		Assert.assertEquals("Param 2 base type", "int", params.get(1).getBaseType().getType());
	}

	@Test
	public final void testGetMethodParametersWithEmptyParams() {
		SignatureParser parser = new SignatureParser("()V");
		List<GenericJavaType> params = parser.getMethodParameters();
		Assert.assertEquals("No params", 0, params.size());
	}

}

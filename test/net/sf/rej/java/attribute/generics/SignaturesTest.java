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

public class SignaturesTest extends TestCase {

	@Test
	public final void testGetClassSignature() {
		{
			ClassSignature cs = Signatures.getClassSignature("Ljava/util/ArrayList<Ljava/lang/String;>;Ljava/io/Serializable;");
			Assert.assertEquals("Superclass", "java.util.ArrayList<java.lang.String>", cs.getSuperClassSignature().toString());
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 1, intfs.size());
			Assert.assertEquals("Interface 1", "java.io.Serializable", intfs.get(0).toString());
		}
		{
			ClassSignature cs = Signatures.getClassSignature("<TypeTest:Ljava/lang/Object;>Ljava/lang/Object;");
			FormalTypeParameter gd = cs.getFormalTypeParameters().get(0);
			Assert.assertEquals("Generic def name", "TypeTest", gd.getIdentifier());
			Assert.assertEquals("Generic def supertype", "java.lang.Object", gd.getClassBound().toString());
			Assert.assertEquals("Superclass", "java.lang.Object", cs.getSuperClassSignature().toString());
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 0, intfs.size());
		}
		{
			ClassSignature cs = Signatures.getClassSignature("<M:Ljava/util/ArrayList;>Ljavax/swing/JPanel;");
			FormalTypeParameter gd = cs.getFormalTypeParameters().get(0);
			Assert.assertEquals("Generic def name", "M", gd.getIdentifier());
			Assert.assertEquals("Generic def supertype", "java.util.ArrayList", gd.getClassBound().toString());
			Assert.assertEquals("Superclass", "javax.swing.JPanel", cs.getSuperClassSignature().toString());
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 0, intfs.size());
		}
		{
			ClassSignature cs = Signatures.getClassSignature("<MMM:Ljava/lang/Object;>Ljava/lang/Object;Ljava/io/Serializable;Lnet/sf/rej/guineapigs/SignaturePig5<Ljava/lang/String;>;");
			FormalTypeParameter gd = cs.getFormalTypeParameters().get(0);
			Assert.assertEquals("Generic def name", "MMM", gd.getIdentifier());
			Assert.assertEquals("Generic def supertype", "java.lang.Object", gd.getClassBound().toString());
			Assert.assertEquals("Superclass", "java.lang.Object", cs.getSuperClassSignature().toString());
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 2, intfs.size());
			Assert.assertEquals("Interface 1", "java.io.Serializable", intfs.get(0).toString());
			Assert.assertEquals("Interface 2", "net.sf.rej.guineapigs.SignaturePig5<java.lang.String>", intfs.get(1).toString());
		}
		{
			ClassSignature cs = Signatures.getClassSignature("<TTT::Ljava/lang/Runnable;:Ljava/lang/Comparable;>Ljava/lang/Object;");
			FormalTypeParameter gd = cs.getFormalTypeParameters().get(0);
			Assert.assertEquals("Generic def name", "TTT", gd.getIdentifier());
			Assert.assertNull("Generic supertype", gd.getClassBound());
			List<GenericJavaType> genIntfs = gd.getInterfaceBounds();
			Assert.assertEquals("Gen Interface count", 2, genIntfs.size());
			Assert.assertEquals("Gen Interface 1", "java.lang.Runnable", genIntfs.get(0).toString());
			Assert.assertEquals("Gen Interface 2", "java.lang.Comparable", genIntfs.get(1).toString());

			Assert.assertEquals("Superclass", "java.lang.Object", cs.getSuperClassSignature().toString());
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 0, intfs.size());
		}
		{
			ClassSignature cs = Signatures.getClassSignature("<ABC:Ljavax/swing/JPanel;:Ljava/io/Serializable;:Ljava/lang/Iterable<Ljava/lang/String;>;>Ljavax/swing/JLabel;Ljava/lang/Runnable;");
			FormalTypeParameter gd = cs.getFormalTypeParameters().get(0);
			Assert.assertEquals("Generic def name", "ABC", gd.getIdentifier());
			Assert.assertEquals("Generic supertype", "javax.swing.JPanel", gd.getClassBound().toString());
			List<GenericJavaType> genIntfs = gd.getInterfaceBounds();
			Assert.assertEquals("Gen Interface count", 2, genIntfs.size());
			Assert.assertEquals("Gen Interface 1", "java.io.Serializable", genIntfs.get(0).toString());
			Assert.assertEquals("Gen Interface 2", "java.lang.Iterable<java.lang.String>", genIntfs.get(1).toString());

			Assert.assertEquals("Superclass", "javax.swing.JLabel", cs.getSuperClassSignature().toString());
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 1, intfs.size());
			Assert.assertEquals("Interface 1", "java.lang.Runnable", intfs.get(0).toString());
		}
		{
			ClassSignature cs = Signatures.getClassSignature("<AAA:Ljava/lang/Object;>Ljava/util/ArrayList<TAAA;>;");
			FormalTypeParameter gd = cs.getFormalTypeParameters().get(0);
			Assert.assertEquals("Generic def name", "AAA", gd.getIdentifier());
			Assert.assertEquals("Generic supertype", "java.lang.Object", gd.getClassBound().toString());
			List<GenericJavaType> genIntfs = gd.getInterfaceBounds();
			Assert.assertEquals("Gen Interface count", 0, genIntfs.size());

			Assert.assertEquals("Superclass", "java.util.ArrayList", cs.getSuperClassSignature().getBaseType().getType());
			List<TypeArgument> typeParams = cs.getSuperClassSignature().getTypeArguments();
			Assert.assertEquals("Type parameter count", 1, typeParams.size());
			GenericJavaType typeParam1 = (GenericJavaType) typeParams.get(0);
			Assert.assertEquals("Type parameter 1 Type", Types.TYPE_PARAMETER_IDENTIFIER , typeParam1.getType());
			Assert.assertEquals("Type parameter 1", "AAA", typeParam1.toString());
						
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 0, intfs.size());
		}
		{
			ClassSignature cs = Signatures.getClassSignature("<BBB:Ljava/lang/Object;>Ljava/util/ArrayList<[Ljava/lang/String;>;");
			FormalTypeParameter gd = cs.getFormalTypeParameters().get(0);
			Assert.assertEquals("Generic def name", "BBB", gd.getIdentifier());
			Assert.assertEquals("Generic supertype", "java.lang.Object", gd.getClassBound().toString());
			List<GenericJavaType> genIntfs = gd.getInterfaceBounds();
			Assert.assertEquals("Gen Interface count", 0, genIntfs.size());

			Assert.assertEquals("Superclass", "java.util.ArrayList", cs.getSuperClassSignature().getBaseType().getType());
			List<TypeArgument> typeParams = cs.getSuperClassSignature().getTypeArguments();
			Assert.assertEquals("Type parameter count", 1, typeParams.size());
			GenericJavaType typeParam1 = (GenericJavaType) typeParams.get(0);
			Assert.assertEquals("Type parameter 1 Type", Types.REFERENCE_TYPE , typeParam1.getType());
			Assert.assertEquals("Type parameter 1 Ref Type", "java.lang.String", typeParam1.getBaseType().getType());
			Assert.assertEquals("Type parameter 1 Dimensions", 1, typeParam1.getBaseType().getDimensionCount());
						
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 0, intfs.size());
		}

		{
			ClassSignature cs = Signatures.getClassSignature("<CCC1:Ljava/lang/Number;CCC2::Ljava/util/List<Ljava/lang/String;>;>Ljava/lang/Object;");
			List<FormalTypeParameter> typeParams = cs.getFormalTypeParameters();
			Assert.assertEquals("Type param count", 2, typeParams.size());
			{
				FormalTypeParameter typeParam = typeParams.get(0);
				Assert.assertEquals("Type param identifier", "CCC1", typeParam.getIdentifier());
				Assert.assertEquals("Class bound", "java.lang.Number", typeParam.getClassBound().toString());
				List<GenericJavaType> genIntfs = typeParam.getInterfaceBounds();
				Assert.assertEquals("Interface bound count", 0, genIntfs.size());
			}
			{
				FormalTypeParameter typeParam = typeParams.get(1);
				Assert.assertEquals("Type param identifier", "CCC2", typeParam.getIdentifier());
				Assert.assertNull("No class bound", typeParam.getClassBound());
				List<GenericJavaType> genIntfs = typeParam.getInterfaceBounds();
				Assert.assertEquals("Interface bound count", 1, genIntfs.size());
				Assert.assertEquals("Interface bound", "java.util.List<java.lang.String>", genIntfs.get(0).toString());
			}

			Assert.assertEquals("Superclass", "java.lang.Object", cs.getSuperClassSignature().getBaseType().getType());
			List<TypeArgument> typeArgs = cs.getSuperClassSignature().getTypeArguments();
			Assert.assertEquals("Type argument count", 0, typeArgs.size());
						
			List<GenericJavaType> intfs = cs.getSuperInterfaceSignatures();
			Assert.assertEquals("Interface count", 0, intfs.size());
		}

	}

	@Test
	public final void testGetFieldSignature() {
		{
			FieldSignature signature = Signatures.getFieldSignature("TM;");
			Assert.assertEquals("Type type", Types.TYPE_PARAMETER_IDENTIFIER, signature.getType().getType());
			Assert.assertEquals("Type name", "M", signature.getType().toString());
		}

		{
			FieldSignature signature = Signatures.getFieldSignature("Ljava/util/List<Ljava/lang/String;>;");
			Assert.assertEquals("Type", "java.util.List<java.lang.String>", signature.getType().toString());
		}

		{
			FieldSignature signature = Signatures.getFieldSignature("Ljava/util/List<*>;");
			Assert.assertEquals("Type toString", "java.util.List<*>", signature.getType().toString());
			List<TypeArgument> typeParams = signature.getType().getTypeArguments();
			Assert.assertEquals("Type param count", 1, typeParams.size());
			Assert.assertTrue("Type param type is wildcard", typeParams.get(0) instanceof Any);			
		}

		{
			FieldSignature signature = Signatures.getFieldSignature("Ljava/util/List<+Ljava/lang/Number;>;");
			Assert.assertEquals("Type toString", "java.util.List<? extends java.lang.Number>", signature.getType().toString());
			List<TypeArgument> typeParams = signature.getType().getTypeArguments();
			Assert.assertEquals("Type param count", 1, typeParams.size());
			Assert.assertTrue("Type param type is bound", typeParams.get(0) instanceof BoundTypeArgument);			
		}

		{
			FieldSignature signature = Signatures.getFieldSignature("Ljava/util/Map<+Ljava/lang/Number;-Ljava/util/ArrayList;>;");
			Assert.assertEquals("Type toString", "java.util.Map<? extends java.lang.Number,? super java.util.ArrayList>", signature.getType().toString());
			List<TypeArgument> typeParams = signature.getType().getTypeArguments();
			Assert.assertEquals("Type param count", 2, typeParams.size());
			Assert.assertTrue("Type param 1 type is bound", typeParams.get(0) instanceof BoundTypeArgument);			
			Assert.assertTrue("Type param 2 type is bound", typeParams.get(1) instanceof BoundTypeArgument);			
		}

	}

	@Test
	public final void testGetMethodSignature() {
		{
			MethodSignature signature = Signatures.getMethodSignature("<H:Ljava/lang/Object;>(TH;)V");
			FormalTypeParameter gd = signature.getFormalTypeParameters().get(0);
			Assert.assertEquals("Generic def name", "H", gd.getIdentifier());
			Assert.assertEquals("Generic supertype", "java.lang.Object", gd.getClassBound().toString());
			List<GenericJavaType> genIntfs = gd.getInterfaceBounds();
			Assert.assertEquals("Gen Interface count", 0, genIntfs.size());
			List<GenericJavaType> params = signature.getParameters();
			Assert.assertEquals("Parameter count", 1, params.size());
			Assert.assertEquals("Parameter 1", "H", params.get(0).toString());
			
			GenericJavaType ret = signature.getReturnType();
			Assert.assertEquals("Return type", "void", ret.toString());
		}

		{
			MethodSignature signature = Signatures.getMethodSignature("(Lnet/sf/rej/guineapigs/SignaturePig2<+Ljava/lang/Integer;>;)V");
			Assert.assertNull("Generic def", signature.getFormalTypeParameters());
			List<GenericJavaType> params = signature.getParameters();
			Assert.assertEquals("Parameter count", 1, params.size());
			Assert.assertEquals("Parameter 1", "net.sf.rej.guineapigs.SignaturePig2<? extends java.lang.Integer>", params.get(0).toString());
			
			GenericJavaType ret = signature.getReturnType();
			Assert.assertEquals("Return type", "void", ret.toString());
		}

		{
			MethodSignature signature = Signatures.getMethodSignature("(Lnet/sf/rej/guineapigs/SignaturePig2<-Ljava/lang/Integer;>;)V");
			Assert.assertNull("Generic def", signature.getFormalTypeParameters());
			List<GenericJavaType> params = signature.getParameters();
			Assert.assertEquals("Parameter count", 1, params.size());
			Assert.assertEquals("Parameter 1", "net.sf.rej.guineapigs.SignaturePig2<? super java.lang.Integer>", params.get(0).toString());
			
			GenericJavaType ret = signature.getReturnType();
			Assert.assertEquals("Return type", "void", ret.toString());
		}

	}

}

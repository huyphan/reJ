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
package net.sf.rej.guineapigs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SignaturePig extends ArrayList<String> implements Serializable {
// Ljava/util/ArrayList<Ljava/lang/String;>;Ljava/io/Serializable;
}

class SignaturePig2<TypeTest> {
// <TypeTest:Ljava/lang/Object;>Ljava/lang/Object;
	public TypeTest getType() {
		return null;
	}
	
	public void setType(TypeTest test) {
		// this:
		// Lnet/sf/rej/guineapigs/SignaturePig2<TTypeTest;>;
	}
	
}

class SignaturePig3 <M extends ArrayList> extends JPanel {
// <M:Ljava/util/ArrayList;>Ljavax/swing/JPanel;
	
	public M a;
	// TM;
	
	public List<String> listString;
	// Ljava/util/List<Ljava/lang/String;>;
	
	public List<?> wildCard;
	// Ljava/util/List<*>;
	
	public List<? extends Number> t;
	// Ljava/util/List<+Ljava/lang/Number;>;
	
	public Map<? extends Number, ? super ArrayList> map;
	// Ljava/util/Map<+Ljava/lang/Number;-Ljava/util/ArrayList;>;

}

class SignaturePig4 {
	public <H> void method(H h) {
	// <H:Ljava/lang/Object;>(TH;)V
	}
	
	public void method(SignaturePig2<? extends Integer> param) {
	// (Lnet/sf/rej/guineapigs/SignaturePig2<+Ljava/lang/Integer;>;)V
	}

	public void method2(SignaturePig2<? super Integer> param) {
	// (Lnet/sf/rej/guineapigs/SignaturePig2<-Ljava/lang/Integer;>;)V
	}
	
}

interface SignaturePig5<GGG> extends Serializable, Cloneable, Runnable {
	
}

interface SignaturePig6<MMM> extends Serializable, SignaturePig5<String> {
// <MMM:Ljava/lang/Object;>Ljava/lang/Object;Ljava/io/Serializable;Lnet/sf/rej/guineapigs/SignaturePig5<Ljava/lang/String;>;	
}

class SignaturePig8 {
	public SignaturePig2<String> ex(SignaturePig2 pig) throws Exception {
		return null;
	}
}

class SignaturePig9 <TTT extends Runnable & Comparable> {
// <TTT::Ljava/lang/Runnable;:Ljava/lang/Comparable;>Ljava/lang/Object;
}

abstract class SignaturePig10 <ABC extends JPanel & Serializable & Iterable<String>> extends JLabel implements Runnable {
// <ABC:Ljavax/swing/JPanel;:Ljava/io/Serializable;:Ljava/lang/Iterable<Ljava/lang/String;>;>Ljavax/swing/JLabel;Ljava/lang/Runnable;
}

class SignaturePig11 <AAA> extends ArrayList<AAA> {
// <AAA:Ljava/lang/Object;>Ljava/util/ArrayList<TAAA;>;
}

class SignaturePig12 <BBB> extends ArrayList<String[]> {
// <BBB:Ljava/lang/Object;>Ljava/util/ArrayList<[Ljava/lang/String;>;
}

class SignaturePig13 <CCC1 extends Number, CCC2 extends List<String>> {
// <CCC1:Ljava/lang/Number;CCC2::Ljava/util/List<Ljava/lang/String;>;>Ljava/lang/Object;
}

class SignaturePig14 {
	public void ex(List<? super int[]> l) throws Exception {
	}
}

class SignaturePig15 {
	public void varargs(List<String> strs, List<Number> ... lists) {
		
	}
	
	public void varargs2(List<Number>[] ... lists) {
		
	}
	
	@SuppressWarnings("unchecked")
	public void varargs3(List<Number>[][] test) {
		varargs2(new List[0], new List[0]);
	}
	
	public void varargs4(int[] ... vararg) {
		
	}
}

package net.sf.rej;

import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link net.sf.rej.Imports}
 * 
 * @author Sami Koivu
 */
public class ImportsTest extends TestCase {

	private Imports imports;

	@Before
	@Override
	public void setUp() throws Exception {
		this.imports = new Imports("test.home");
	}

	/**
	 * Test method for {@link net.sf.rej.Imports#Imports(java.lang.String)}.
	 */
	@Test
	public void testHomePackageFuncionality() {
		String classNotInHomePackage = "test.nothome.ClassNotInHome";
		String classInHomePackage = "test.home.ClassInHomePackage";
		imports.addType(classInHomePackage);
		imports.addType(classNotInHomePackage);
		Set<String> importSet = imports.getImports();

		Assert.assertTrue("Create an import for non-home package classes", importSet
				.contains(classNotInHomePackage));
		Assert.assertFalse("Do not create an import for home package classes",
				importSet.contains(classInHomePackage));
	}

	/**
	 * Test method for {@link net.sf.rej.Imports#addType(java.lang.String)}.
	 */
	@Test
	public void testAddType() {
		String shortNameForAddedClass = "ClassAdded";
		String shortNameForNotAddedClass = "NotAddedClass";
		String addedClass = "package." + shortNameForAddedClass;
		String notAddedClass = "package." + shortNameForNotAddedClass;
		imports.addType(addedClass);
		Set<String> importSet = imports.getImports();
		Assert.assertTrue("Create import for class that was added", importSet
				.contains(addedClass));
		Assert.assertFalse("Do not create import for class that was not added",
				importSet.contains(notAddedClass));
		Assert.assertEquals("Short name for added class", shortNameForAddedClass,
				imports.getShortName(addedClass));
		Assert.assertEquals("Long name for not added class", notAddedClass, imports
				.getShortName(notAddedClass));
	}

	/**
	 * Test method for {@link net.sf.rej.Imports#getImports()}.
	 */
	@Test
	public void testGetImports() {
		String classA = "ClassA";
		String classB = "packagex.ClassB";
		String classC = "pac.age.ClassC";
		imports.addType(classA);
		imports.addType(classB);
		imports.addType(classC);
		Set<String> set = imports.getImports();
		Assert.assertEquals("Import size (one class is in default package)", 2, set
				.size());
		Assert.assertFalse("No import for class A", set.contains(classA));
		Assert.assertTrue("Import for class B", set.contains(classB));
		Assert.assertTrue("Import for class C", set.contains(classC));
	}

	/**
	 * Test method for {@link net.sf.rej.Imports#getPackage(java.lang.String)}.
	 */
	@Test
	public void testGetPackage() {
		Assert.assertEquals("Default package", "", Imports.getPackage("MyClass"));
		Assert.assertEquals("Zero dot package", "package", Imports
				.getPackage("package.MyClass"));
		Assert.assertEquals("One dot package", "pack.age", Imports
				.getPackage("pack.age.MyClass"));
	}

	/**
	 * Test method for {@link net.sf.rej.Imports#getType(java.lang.String)}.
	 */
	@Test
	public void testGetClassString() {
		Assert.assertEquals("Default package", "MyClass", Imports.getType("MyClass"));
		Assert.assertEquals("Zero dot package", "MyClass", Imports
				.getType("package.MyClass"));
		Assert.assertEquals("One dot package", "MyClass", Imports
				.getType("pack.age.MyClass"));
	}

	/**
	 * Test method for
	 * {@link net.sf.rej.Imports#isInDefaultPackage(java.lang.String)}.
	 */
	@Test
	public void testIsInDefaultPackage() {
		Assert.assertTrue("Default package", Imports.isInDefaultPackage("MyClass"));
		Assert.assertFalse("Zero dot package", Imports
				.isInDefaultPackage("package.MyClass"));
		Assert.assertFalse("One dot package", Imports
				.isInDefaultPackage("pack.age.MyClass"));
	}

	/**
	 * Test method for {@link net.sf.rej.Imports#isInJavaLang(java.lang.String)}.
	 */
	@Test
	public void testIsInJavaLang() {
		Assert.assertTrue("Package java.lang", Imports
				.isInJavaLang("java.lang.MyClass"));
		Assert.assertFalse("Default package", Imports.isInJavaLang("MyClass"));
		Assert.assertFalse("Zero dot package", Imports.isInJavaLang("package.MyClass"));
		Assert.assertFalse("One dot package", Imports.isInJavaLang("pack.age.MyClass"));
	}

	/**
	 * Test method for
	 * {@link net.sf.rej.Imports#areInSamePackage(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAreInSamePackage() {
		Assert.assertTrue("Same package", Imports.areInSamePackage("package.ClassA",
				"package.ClassB"));
		Assert.assertFalse("Different package", Imports.areInSamePackage(
				"packagea.ClassA", "packageb.ClassB"));
		Assert.assertTrue("Same package (no package)", Imports.areInSamePackage(
				"ClassA", "ClassB"));
		Assert.assertFalse("Different package", Imports.areInSamePackage("ClassA",
				"package.ClassB"));
		Assert.assertFalse("Different package", Imports.areInSamePackage(
				"package.a.ClassA", "package.b.ClassB"));
	}

	/**
	 * Test method for {@link net.sf.rej.Imports#getShortName(java.lang.String)}.
	 */
	@Test
	public void testGetShortName() {
		this.imports.addType("java.lang.String");
		this.imports.addType("package.TypeA");
		this.imports.addType("package.TypeB");
		this.imports.addType("MyClass");
		this.imports.addType("java.util.Date");
		this.imports.addType("java.sql.Date");
		Assert.assertEquals("Class not added", "java.util.List", this.imports
				.getShortName("java.util.List"));
		Assert.assertEquals("Ambiguous short name", "java.util.Date", this.imports
				.getShortName("java.util.Date"));
		Assert.assertEquals("Ambiguous short name", "java.sql.Date", this.imports
				.getShortName("java.sql.Date"));
		Assert.assertEquals("Added class in default package", "MyClass", this.imports
				.getShortName("MyClass"));
		Assert.assertEquals("Not added class in default package", "MyOtherClass",
				this.imports.getShortName("MyOtherClass"));
		Assert.assertEquals("Added java.lang", "String", this.imports
				.getShortName("java.lang.String"));
		Assert.assertEquals("Not added java.lang", "Object", this.imports
				.getShortName("java.lang.Object"));
		Assert.assertEquals("Two types in same package", "TypeA", this.imports
				.getShortName("package.TypeA"));
		Assert.assertEquals("Two types in same package", "TypeB", this.imports
				.getShortName("package.TypeB"));
	}

}

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
package net.sf.rej;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Handles creation of import statements (for improved readability) and
 * the conversion between FQNs and short names of types, taking in
 * consideration naming conflicts. This is a purely logical class used
 * by the user interface classes to guide in the presentation of import
 * statements and full/short names of types.
 * 
 * @author Sami Koivu
 */
public class Imports {

	/**
	 * The home package for this import object
	 */
    private String homePackage;
    
    /**
     * FQNs of types added to this imports object that are
     * -Not in the default package (ie. no package)
     * -Not in the home package of this imports object
     * -Not in java.lang
     */
    private Set<String> types = new TreeSet<String>();
    
    /**
     * FQNs of types that have been processed already.
     */
    private List<String> allNames = new ArrayList<String>();
    
    /**
     * Short names of types which would create unambiguity and
     * therefore cannot be shortened.
     */
    private List<String> ambigous = new ArrayList<String>();

    /**
     * Initializes this <code>Imports</code> object setting homePackage
     * as the home of this object. No import statements are created
     * for types in the home package.
     * @param homePackage the home package, for example: "com.mycompany.myproject"
     */
    public Imports(String homePackage) {
        this.homePackage = homePackage;
    }

    /**
     * Adds the FQN given as parameter to this import set.
     * @param fullTypeName the fully qualified name of the type to add
     * to this import object.
     */
    public void addType(String fullTypeName) {
        if (fullTypeName.indexOf(";") > -1) {
            throw new RuntimeException("invalid class");
        }
        // classes in the default package require no imports
        if (!isInDefaultPackage(fullTypeName)
                && !getPackage(fullTypeName).equals(this.homePackage)
                && !isInJavaLang(fullTypeName)) {
            this.types.add(fullTypeName);
        }

        if (!this.allNames.contains(fullTypeName)) {
            // go through allnames to see if the class short name is alredy used
            String shortName = getType(fullTypeName);
            for (int i = 0; i < this.allNames.size(); i++) {
                String name = this.allNames.get(i);
                if (getType(name).equals(shortName)) {
                    // short name used twice by two distinct classes
                    this.ambigous.add(shortName);
                    break;
                }
            }

            // add to full names to state this fully qualified name is already
            // handled, and is in use
            this.allNames.add(fullTypeName);
        }
    }

    /**
     * Returns a list of import statements for this imports object.
     * The required import statements are given based on the types
     * added to this object and it's home package and keeping in mind
     * that types in the java.lang package or the default package
     * do not require import statements.
     * @return a set of FQNs for types that require import statements.
     */
    public Set<String> getImports() {
        Set<String> fullList = new TreeSet<String>();
        List<String> tempList = new ArrayList<String>();
        Iterator<String> it = this.types.iterator();
        String last = "";

        while (it.hasNext()) {
            String name = it.next();
            if (tempList.size() != 0) {
                last = tempList.get(tempList.size() - 1);
            }

            if (this.ambigous.contains(getType(name)))
                continue;

            if (!areInSamePackage(name, last)) {
                fullList.addAll(tempList);
                tempList.clear();
            }
            tempList.add(name);
        }

        if (tempList.size() > 0) {
            fullList.addAll(tempList);
            tempList.clear();
        }

        return fullList;
    }

    /**
     * Returns the package portion of the given FQN or an empty <code>
     * String</code> if the FQN contains no package definition.
     * @param type a FQN of a class.
     * @return the package of the given FQN.
     */
    public static String getPackage(String type) {
        int i = type.lastIndexOf(".");
        if (i == -1) {
            return "";
        } else {
            return type.substring(0, i);
        }
    }

    /**
     * Returns the short name of the given FQN. If the FQN constains
     * no package definition, the FQN is returned as-is.
     * @param type a FQN of a type.
     * @return the short name for the type given as parameter.
     */
    public static String getType(String type) {
        int i = type.lastIndexOf(".");
        if (i == -1) {
            return type;
        } else {
            return type.substring(i + 1);
        }
    }

    /**
     * Returns a boolean value indicating whether or not the given FQN
     * of a type is in the default package (in other words, has no
     * package definition).
     * @param type a fully qualified name of a type.
     * @return true if the type is in the default package.
     */
    public static boolean isInDefaultPackage(String type) {
        return type.indexOf(".") == -1;
    }
    
    /**
     * Returns a boolean value indicating whether or not the given FQN
     * of a type is in the <code>java.lang</code> package or not.
     * @param type a fully qualified name of a type.
     * @return true if the type is in the <code>java.lang</code> package.
     */
    public static boolean isInJavaLang(String type) {
    	return "java.lang".equals(getPackage(type));
    }

    /**
     * Return a boolean value indicating whether or not the two given
     * fully qualified names are in th same package or not.
     * @param type1 the first type to compare.
     * @param type2 the second type to compare.
     * @return true if the two FQNs are in the same package (for example,
     * <code>java.lang.Object</code> and <code>java.lang.String</code>).
     */
    public static boolean areInSamePackage(String type1, String type2) {
        int i1 = type1.lastIndexOf(".");
        int i2 = type2.lastIndexOf(".");
        if ((i1 == i2)) {
        	if (i1 == -1 || (type1.substring(0, i1).equals(type2.substring(0, i2)))) {
        		return true;
        	} 
        }

        return false;
    }

    /**
     * Returns the "short name" for the type. The concept of short name
     * here means the FQN without the package part. In other words, if
     * the FQN is <code>java.lang.String</code> the short name would be
     * <code>String</code>. If the import object judges that shortening
     * the name would be risking ambiguity, the full name is returned
     * instead.
     * @param fqn the FQN of the class to be shortened.
     * @return the short name.
     */
    public String getShortName(String fqn) {
        if (!this.ambigous.contains(getType(fqn))
                && this.types.contains(fqn)) {
            int i = fqn.lastIndexOf(".");
            if (i != -1) {
                String name = fqn.substring(i + 1);
                return name;
            }
        } else if (isInJavaLang(fqn)
                || getPackage(fqn).equals(this.homePackage)) {
            int i = fqn.lastIndexOf(".");
            return fqn.substring(i + 1);
        }

        // class is in the default package(no package), or it isn't in the
        // import
        // list, probably because of a risk of ambiguity
        return fqn;
    }

}
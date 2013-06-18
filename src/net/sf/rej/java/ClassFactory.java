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

import java.util.ArrayList;

import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.constantpool.ConstantPool;

public class ClassFactory {
    // TODO: Factory should be configurable

    private byte[] magic = {(byte)0xca, (byte)0xfe, (byte)0xba, (byte)0xbe};
    private int minorVersion = 0;
    private int majorVersion = 46;
    private AccessFlags flags = new AccessFlags();
    private String superClass = "java.lang.Object";

    public ClassFactory() {
        this.flags.setPublic(true);
    }
    
    public ClassFile createClass(String fullClassName) {
    	return createClass(fullClassName, this.superClass);
    }

    public ClassFile createClass(String fullClassName, String superClass) {
        ClassFile cf = new ClassFile();
        cf.validateMagic(this.magic);

        cf.setMinorVersion(this.minorVersion);
        cf.setMajorVersion(this.majorVersion);

        ConstantPool pool = new ConstantPool();
        pool.init(1);
        cf.setPool(pool);

        cf.setAccessFlags(this.flags.getValue());
        cf.setThisClass(pool.optionalAddClassRef(fullClassName));
        if (superClass != null) {
        	cf.setSuperClass(pool.optionalAddClassRef(superClass));
        } else {
        	cf.setSuperClass(0); // 0 signals no super class
        }

        cf.setInterfaces(new ArrayList<Interface>());
        cf.setFields(new ArrayList<Field>());
        cf.setMethods(new ArrayList<Method>());

        cf.setAttributes(new Attributes());

        return cf;
    }

}

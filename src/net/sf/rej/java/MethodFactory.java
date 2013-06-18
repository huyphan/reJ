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

import java.util.List;

import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.java.attribute.ExceptionDescriptor;
import net.sf.rej.java.attribute.ExceptionsAttribute;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteParser;

/**
 * A factory for creating <code>Method</code> objects.
 *
 * @author Sami Koivu
 */
public class MethodFactory {
    // TODO: Make configurable

    public MethodFactory() {
    }

    public Method createMethod(ByteParser parser, ConstantPool pool) {
        return new Method(parser, pool);
    }

    public Method createMethod(ClassFile cf, AccessFlags accessFlags, int nameIndex, int descIndex, int codeAttrNameIndex, int maxStackSize, int maxLocals, int exAttrNameIndex, List<ExceptionDescriptor> exceptions) {
        ConstantPool cp = cf.getPool();
        Method method = new Method(cp);
        method.setAccessFlags(accessFlags);
        method.setNameIndex(nameIndex);
        method.setDescriptorIndex(descIndex);
        Attributes attributes = new Attributes();
        CodeAttribute ca = new CodeAttribute(codeAttrNameIndex, cp, maxStackSize, maxLocals);
        attributes.addAttribute(ca);
        ExceptionsAttribute ea = new ExceptionsAttribute(exAttrNameIndex, cp, exceptions);
        attributes.addAttribute(ea);
        method.setAttributes(attributes);
        return method;
    }

}

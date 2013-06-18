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

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteParser;

/**
 * FieldFactory
 *
 * @author Sami Koivu
 */
public class FieldFactory {
    // TODO: Make configurable

    public FieldFactory() {
    }

    public Field createField(ByteParser parser, ConstantPool pool) {
        return new Field(parser, pool);
    }

    public Field createField(ClassFile cf, AccessFlags accessFlags, int nameIndex, int descIndex) {
        ConstantPool cp = cf.getPool();
        Field field = new Field(cp);
        field.setAccessFlags(accessFlags);
        field.setNameIndex(nameIndex);
        field.setDescriptorIndex(descIndex);
        return field;
    }

}

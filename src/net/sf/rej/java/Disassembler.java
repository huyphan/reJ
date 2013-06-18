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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.attribute.Attributes;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.StreamByteParser;

public class Disassembler {

    // TODO: Quick parse of methods/fields (without parsing the instructions)

    public static String parseName(InputStream in) {
        ByteParser parser = new StreamByteParser(in);
        parser.setBigEndian(true);
        ClassFile cf = parseBasics(parser);
        return cf.getFullClassName();
    }

    public static ClassFile parseBasics(ByteParser parser) {
        ClassFile cf = new ClassFile();
        // cafebabe :)
        cf.validateMagic(parser.getBytes(4));

        cf.setMinorVersion(parser.getShortAsInt());
        cf.setMajorVersion(parser.getShortAsInt());

        int contantPoolCount = parser.getShortAsInt();

        ConstantPool pool = new ConstantPool();
        pool.init(contantPoolCount);
        cf.setPool(pool);

        for (int i = 1; i < contantPoolCount; i++) {
            ConstantPoolInfo cpi = ConstantPoolInfo.getCPI(parser, pool);
            pool.set(i, cpi);

            // 8byte types take up two indices
            if (cpi.getType() == ConstantPoolInfo.LONG
                    || cpi.getType() == ConstantPoolInfo.DOUBLE)
                i++;
        }

        cf.setAccessFlags(parser.getShortAsInt());
        cf.setThisClass(parser.getShortAsInt());
        cf.setSuperClass(parser.getShortAsInt());
        return cf;
    }

    public static ClassFile readClass(byte[] data) {
        // TODO: parse from stream
        ByteParser parser = new ByteArrayByteParser(data);
        parser.setBigEndian(true);

        ClassFile cf = parseBasics(parser);

        int interfaceCount = parser.getShortAsInt();

        List<Interface> interfaces = new ArrayList<Interface>(interfaceCount);
        for (int i = 0; i < interfaceCount; i++) {
            interfaces.add(new Interface(parser.getShortAsInt(), cf.getPool()));
        }
        cf.setInterfaces(interfaces);

        int fieldsCount = parser.getShortAsInt();
        List<Field> fields = new ArrayList<Field>(fieldsCount);
        for (int i = 0; i < fieldsCount; i++) {
            Field f = new Field(parser, cf.getPool());
            fields.add(f);
        }
        cf.setFields(fields);

        int methodCount = parser.getShortAsInt();

        List<Method> methods = new ArrayList<Method>(methodCount);
        for (int i = 0; i < methodCount; i++) {
            Method method = new Method(parser, cf.getPool());
            methods.add(method);
        }
        cf.setMethods(methods);

        cf.setAttributes(new Attributes(parser, cf.getPool()));
        return cf;
    }

}

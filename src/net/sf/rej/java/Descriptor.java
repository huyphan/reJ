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
import java.util.List;

public final class Descriptor {

    private List<JavaType> paramParsed;
    private JavaType retParsed;
    private boolean hasParamPart = false;

    public static final Descriptor NO_PARAM_VOID = new Descriptor("V");

    public Descriptor(String desc) {
        this.paramParsed = new ArrayList<JavaType>();
        if (desc.indexOf("(") != -1) {
        	this.hasParamPart = true;
            String param = desc.substring(1, desc.indexOf(")"));
            DescriptorParser parser = new DescriptorParser(param);
            while (parser.hasMore()) {
            	this.paramParsed.add(new JavaType(parser.getType()));
            }
        }

        String ret = desc.substring(desc.indexOf(")") + 1);
        this.retParsed = new JavaType(new DescriptorParser(ret).getType());
    }

    public String getParams() {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i < this.paramParsed.size(); i++) {
        	JavaType jt = this.paramParsed.get(i);
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(jt);
        }
        return sb.toString();
    }

    public void setReturn(JavaType type) {
    	this.retParsed = type;
    }

    public JavaType getReturn() {
        return this.retParsed;
    }

    public List<JavaType> getParamList() {
        List<JavaType> al = new ArrayList<JavaType>();
        al.addAll(this.paramParsed);

        return al;
    }

    public void setParamList(List<JavaType> list) {
    	this.paramParsed = list;
    	this.hasParamPart = true;
    }

    public String getRawDesc() {
        return this.getRawParams() + this.retParsed.getRaw();
    }

    public String getRawParams() {
    	if (this.hasParamPart) {
    		StringBuffer sb = new StringBuffer();
    		sb.append("(");
    		for (int i=0; i < this.paramParsed.size(); i++) {
    			JavaType jt = this.paramParsed.get(i);
    			sb.append(jt.getRaw());
    		}
    		sb.append(")");

    		return sb.toString();
    	} else {
    		return "";
    	}
    }

    @Override
	public boolean equals(Object o) {
        if (!(o instanceof Descriptor))
            return false;

        // TODO: could be more elegant to compare the JavaType objects
        return ((Descriptor) o).getRawDesc().equals(this.getRawDesc());
    }

    @Override
	public int hashCode() {
        return this.getRawDesc().hashCode();
    }

    public static class DescriptorParser {
        private String data;
        private int pos = 0;

        public DescriptorParser(String descriptor) {
            this.data = descriptor;
        }

        public String getType() {
            switch (this.data.charAt(this.pos++)) {
            case 'B':
                return "byte";
            case 'C':
                return "char";
            case 'D':
                return "double";
            case 'F':
                return "float";
            case 'I':
                return "int";
            case 'J':
                return "long";
            case 'S':
                return "short";
            case 'Z':
                return "boolean";
            case 'L':
                int end = this.data.indexOf(";", this.pos);
                String c = this.data.substring(this.pos, end);
                this.pos = end + 1;
                return c.replace('/', '.');
            case 'V':
                return "void";
            case '[':
                return getType() + "[]";
            default:
                throw new RuntimeException("Error parsing descriptor: "
                        + this.data + " illegal definition: "
                        + this.data.charAt(this.pos - 1));
            }
        }

        public boolean hasMore() {
            return this.pos < this.data.length();
        }

    }
    
    public static String arrayTypeToRaw(String type) {
    	StringBuffer raw = new StringBuffer(type);
    	StringBuffer array = new StringBuffer();
    	while (true) {
    		int index = raw.lastIndexOf("[]");
    		if (index == -1) break;
    		
    		raw.delete(index, raw.length());
    		array.append('[');
    	}
    	
    	if (array.length() > 0) {
    		return array + "L" + raw.toString().replace('.', '/') + ";";
    	} else {
    		return type.replace('.', '/');
    	}
    }
    
}

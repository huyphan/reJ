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
package net.sf.rej.gui;

import net.sf.rej.files.Project;
import net.sf.rej.gui.tab.Tab;
import net.sf.rej.java.Field;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.Method;

/**
 * Objects that point to a certain tab of the application, certain class, method and or line of code.
 *
 * @author Sami Koivu
 */
public class Link {

    public static final int ANCHOR_UNDEFINED = 0;
    public static final int ANCHOR_CLASS_DEF = 1;
    public static final int ANCHOR_FIELD_DEF = 2;
    public static final int ANCHOR_METHOD_DEF = 3;
    public static final int ANCHOR_METHOD_LV = 5;
    public static final int ANCHOR_METHOD_CODE = 4;
    public static final int ANCHOR_PC_OFFSET = 6;
    public static final int ANCHOR_SOURCE_LINE_NUMBER = 7;

    private String text = null;
    private Project project = null;
    
    private String file = null;
    
    private Tab tab = null;
    private int anchor = ANCHOR_UNDEFINED;
    private Field field = null;
    private Method method = null;
    private LocalVariable lv = null;
    private int position = -1;

    public Link() {
    }
    
    @Override
	public String toString() {
        return this.text;
    }

    public int getAnchor() {
        return this.anchor;
    }

    public Field getField() {
        return this.field;
    }

    public String getFile() {
        return this.file;
    }

    public LocalVariable getLv() {
        return this.lv;
    }

    public Method getMethod() {
        return this.method;
    }

    public int getPosition() {
        return this.position;
    }

    public Tab getTab() {
        return this.tab;
    }

    public Project getProject() {
        return this.project;
    }

    public String getText() {
        return this.text;
    }
        
    public String dump() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("text=" + this.text);
    	sb.append(", project=" + this.project);
    	sb.append(", file=" + this.file);
    	sb.append(", tab=" + this.tab);
    	sb.append(", anchor=" + this.anchor);
    	sb.append(", field=" + ((field == null) ? "null" : this.field.getSignatureLine()));
    	sb.append(", method=" + ((method == null) ? "null" : this.method.getSignatureLine()));
    	sb.append(", lv=" + this.lv);
    	sb.append(", pc=" + this.position);
    	return sb.toString();
    }

	public void setAnchor(int anchor) {
		this.anchor = anchor;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setLv(LocalVariable lv) {
		this.lv = lv;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public void setText(String text) {
		this.text = text;
	}
}

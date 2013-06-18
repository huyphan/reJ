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
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Field;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.CodeAttribute;
import net.sf.rej.java.instruction.DecompilationContext;

public class IterationContext {

    private Project project = null;
    private String filename = null;
    private ClassFile cf = null;
    private Method method = null;
    private Field field = null;
    private DecompilationContext dc = null;
    private CodeAttribute ca = null;

    public IterationContext() {
        // do-nothing constructor
    }

    public ClassFile getCf() {
        return this.cf;
    }

    public Field getField() {
        return this.field;
    }

    public String getFilename() {
        return this.filename;
    }

    public Method getMethod() {
        return this.method;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setCf(ClassFile cf) {
        this.cf = cf;
    }

    public DecompilationContext getDc() {
        return this.dc;
    }

    public void setDc(DecompilationContext dc) {
        this.dc = dc;
    }

    public void setCodeAttribute(CodeAttribute ca) {
        this.ca = ca;
    }

    public CodeAttribute getCodeAttribute() {
        return this.ca;
    }

}

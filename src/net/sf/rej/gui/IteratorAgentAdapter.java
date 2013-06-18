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

import net.sf.rej.AbstractIteratorAgent;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Field;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.Attribute;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.instruction.Instruction;

public class IteratorAgentAdapter extends AbstractIteratorAgent {

    public void processFile(String filename) {
    }

    public void processClass(IterationContext ic, ClassFile cf) {
    }

    public void processInterface(IterationContext ic, String interfaceName) {
    }

    public void processField(IterationContext ic, Field field) {
    }

    public void processMethod(IterationContext ic, Method method) {
    }

    public void processLocalVariable(IterationContext ic, LocalVariable lv) {
    }

    public void processInstruction(IterationContext ic, Instruction instruction) {
    }

    public void processAttribute(IterationContext ic, Attribute attr) {
    }

    public void processConstantPoolInfo(IterationContext ic, ConstantPoolInfo cpi) {
    }

    public void finished(IterationContext ic, int totalCount) {
    }

    public void processException(Exception ex) {
    }

    public void postProcessFile(IterationContext ic) {
    }

}

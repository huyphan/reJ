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

import javax.swing.SwingUtilities;

import net.sf.rej.AbstractIteratorAgent;
import net.sf.rej.gui.tab.Tab;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Field;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.Method;
import net.sf.rej.java.attribute.Attribute;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.instruction.Instruction;

/**
 * Superclass for doing searches on a project using iteration.
 *
 * @author Sami Koivu
 * @see IteratorAgent
 */
public abstract class DefaultMatcher extends AbstractIteratorAgent {

    protected int resultCount = 0;

    public abstract boolean matches(String match);

    public abstract void addLink(Link link);

    private void addResult(Link link) {
        this.resultCount++;
        this.addLink(link);
    }

    public void processClass(IterationContext ic, ClassFile cf) {
        if (matches(cf.getFullClassName())) {
        	Link link = new Link();
        	link.setText("Type name : " + ic.getCf().getFullClassName());
        	link.setAnchor(Link.ANCHOR_CLASS_DEF);
        	link.setProject(ic.getProject());
        	link.setFile(ic.getFilename());
        	link.setTab(Tab.EDITOR);
            addResult(link);
        }
        
        String superClassName = cf.getSuperClassName();
        if (superClassName != null && matches(superClassName)) {
        	Link link = new Link();
        	link.setText("Superclass name : " + ic.getCf().getFullClassName());
        	link.setAnchor(Link.ANCHOR_CLASS_DEF);
        	link.setProject(ic.getProject());
        	link.setFile(ic.getFilename());
        	link.setTab(Tab.EDITOR);
            addResult(link);
        }
    }

    public void processFile(String filename) {
        SystemFacade.getInstance().setStatus("Searching " + filename + " ...");
    }

    public void processInterface(IterationContext ic, String interfaceName) {
        if (matches(interfaceName)) {
        	Link link = new Link();
        	link.setText("Interface name : " + ic.getCf().getFullClassName());
        	link.setAnchor(Link.ANCHOR_CLASS_DEF);
        	link.setProject(ic.getProject());
        	link.setFile(ic.getFilename());
        	link.setTab(Tab.EDITOR);
            addResult(link);
        }
    }

    public void processField(IterationContext ic, Field field) {
        if (matches(field.getSignatureLine())) {
        	Link link = new Link();
        	link.setText("Field signature : " + ic.getCf().getFullClassName() + "." + field.getName());
        	link.setAnchor(Link.ANCHOR_FIELD_DEF);
        	link.setProject(ic.getProject());
        	link.setFile(ic.getFilename());
        	link.setTab(Tab.EDITOR);
        	link.setField(field);
            addResult(link);
        }
    }

    public void processMethod(IterationContext ic, Method method) {
        if (matches(method.getSignatureLine())) {
        	Link link = new Link();
        	link.setText("Method signature : " + ic.getCf().getFullClassName() + "." + method.getName());
        	link.setAnchor(Link.ANCHOR_METHOD_DEF);
        	link.setProject(ic.getProject());
        	link.setFile(ic.getFilename());
        	link.setTab(Tab.EDITOR);
        	link.setMethod(method);
            addResult(link);
        }
    }

    public void processLocalVariable(IterationContext ic, LocalVariable lv) {
        if (matches(lv.getSignatureLine())) {
        	Link link = new Link();
        	link.setText("Local variable : " + ic.getCf().getFullClassName() + "." + ic.getMethod().getName() + " / " + lv.getName());
        	link.setAnchor(Link.ANCHOR_METHOD_LV);
        	link.setProject(ic.getProject());
        	link.setFile(ic.getFilename());
        	link.setTab(Tab.EDITOR);
        	link.setMethod(ic.getMethod());
        	link.setLv(lv);
            addResult(link);
        }
    }

    public void processInstruction(IterationContext ic, Instruction instruction) {
        String instructionLine = instruction.getMnemonic() + " " + instruction.getParameters().getString(ic.getDc());
        if (matches(instructionLine)) {
        	Link link = new Link();
        	link.setText("Instruction : " + ic.getCf().getFullClassName() + "." + ic.getMethod().getName() + " / " + instructionLine);
        	link.setAnchor(Link.ANCHOR_METHOD_CODE);
        	link.setProject(ic.getProject());
        	link.setFile(ic.getFilename());
        	link.setTab(Tab.EDITOR);
        	link.setMethod(ic.getMethod());
        	link.setPosition(ic.getDc().getPosition());
            addResult(link);

        }
    }

    public void finished(final IterationContext ic, final int totalCount) {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    	        SystemFacade.getInstance().setStatus("Search done. Searched in " + totalCount + " files. Found " + DefaultMatcher.this.resultCount + " results.");
    		}
    	});
    }

    public void processAttribute(IterationContext ic, Attribute attr) {
        // TODO: search from attributes?
    }

    public void processConstantPoolInfo(IterationContext ic, ConstantPoolInfo cpi) {
        // TODO: search constantpoolinfos
    }

    public void postProcessFile(IterationContext ic) {
    }

    public void processException(final Exception ex) {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			SystemFacade.getInstance().handleException(ex);    			
    		}
    	});
    }

}

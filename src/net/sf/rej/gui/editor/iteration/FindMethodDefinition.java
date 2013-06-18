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
package net.sf.rej.gui.editor.iteration;

import net.sf.rej.gui.IterationContext;
import net.sf.rej.gui.IteratorAgentAdapter;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.tab.Tab;
import net.sf.rej.java.Descriptor;
import net.sf.rej.java.Method;

/**
 * <code>IteratorAgent</code> which gets a class name, method name and field
 * descriptor as parameters and finds all method defnitions which match them.
 * 
 * @author Sami Koivu
 */
public class FindMethodDefinition extends IteratorAgentAdapter {
	private int resultCount = 0;
	private String className;
	private String methodName;
	private Descriptor desc;
	
	public FindMethodDefinition(String className, String methodName, Descriptor desc) {
		this.className = className;
		this.methodName = methodName;
		this.desc = desc;
	}

	@Override
	public void processMethod(IterationContext sc, Method method) {
		boolean classNamesMatch = sc.getCf().getFullClassName().equals(
				className);
		boolean methodNamesMatch = method.getName().equals(methodName);
		boolean descriptorsMatch = method.getDescriptor().equals(desc);
		if (classNamesMatch && methodNamesMatch && descriptorsMatch) {
			Link link = new Link();
			link.setText("Method definition : " + sc.getCf().getFullClassName() + "." + method.getName());
			link.setAnchor(Link.ANCHOR_METHOD_DEF);
			link.setProject(sc.getProject());
			link.setFile(sc.getFilename());
			link.setTab(Tab.EDITOR);
			link.setMethod(sc.getMethod());

			MainWindow.getInstance().getSearchTab().addResult(link);
			this.resultCount++;
		}
	}

}

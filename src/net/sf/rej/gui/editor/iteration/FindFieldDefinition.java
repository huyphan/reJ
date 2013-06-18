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
import net.sf.rej.java.Field;

/**
 * <code>IteratorAgent</code> which gets a class name, field name and field
 * descriptor as parameters and finds all field defnitions which match them.
 * 
 * @author Sami Koivu
 */
public class FindFieldDefinition extends IteratorAgentAdapter {
	private int resultCount = 0;

	private String className;

	private String fieldName;

	private Descriptor desc;

	public FindFieldDefinition(String className, String fieldName,
			Descriptor desc) {
		this.className = className;
		this.fieldName = fieldName;
		this.desc = desc;
	}

	@Override
	public void processField(IterationContext sc, Field field) {
		boolean classNamesMatch = sc.getCf().getFullClassName().equals(
				className);
		boolean methodNamesMatch = field.getName().equals(fieldName);
		boolean descriptorsMatch = field.getDescriptor().equals(desc);
		if (classNamesMatch && methodNamesMatch && descriptorsMatch) {
			Link link = new Link();
			link.setText("Field definition : " + sc.getCf().getFullClassName() + "." + field.getName());
			link.setAnchor(Link.ANCHOR_FIELD_DEF);
			link.setProject(sc.getProject());
			link.setFile(sc.getFilename());
			link.setTab(Tab.EDITOR);
			link.setField(field);
			MainWindow.getInstance().getSearchTab().addResult(link);
			this.resultCount++;
		}
	}

}

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
package net.sf.rej.gui.event;

import net.sf.rej.files.Project;
import net.sf.rej.gui.debug.wrappers.IStackFrame;
import net.sf.rej.gui.debug.wrappers.IThreadReference;
import net.sf.rej.gui.debug.wrappers.IVirtualMachine;
import net.sf.rej.java.ClassFile;

public class Event {
	private EventType type;
	private ClassFile cf;
	private Project project;
	private String file;
	private EventDispatcher dispatcher;
	// debugging related
	private IThreadReference threadReference;
	private IStackFrame stackFrame;
	private IVirtualMachine vm;
	
	public Event(EventType type) {
		this.type = type;
	}
	
	public EventType getType() {
		return this.type;
	}

	public void setClassFile(ClassFile cf) {
		this.cf = cf;
	}
	
	public ClassFile getClassFile() {
		return this.cf;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public Project getProject() {
		return this.project;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public String getFile() {
		return this.file;
	}
	
	public void setDispatcher(EventDispatcher notificator) {
		this.dispatcher = notificator;
	}
	
	public EventDispatcher getDispatcher() {
		return this.dispatcher;
	}

	public void setThread(IThreadReference obj) {
		this.threadReference = obj;
	}
	
	public IThreadReference getThread() {
		return this.threadReference;
	}

	public void setVM(IVirtualMachine vm) {
		this.vm = vm;
	}
	
	public IVirtualMachine getVM() {
		return this.vm;
	}
	
	public void setStackFrame(IStackFrame stackFrame) {
		this.stackFrame = stackFrame;
	}

	public IStackFrame getStackFrame() {
		return stackFrame;
	}
}

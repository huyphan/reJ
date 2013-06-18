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
package net.sf.rej.gui.debug;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.debug.wrappers.StackFrameWrapper;
import net.sf.rej.gui.debug.wrappers.ThreadReferenceWrapper;
import net.sf.rej.gui.debug.wrappers.VirtualMachineWrapper;
import net.sf.rej.gui.editor.Breakpoint;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;

public class VMEventHandler implements Runnable, EventObserver {
	
	private static final Logger logger = Logger.getLogger(VMEventHandler.class.getName());
	
	private VirtualMachine vm;
	private boolean interrupted = false;
	private EventDispatcher dispatcher = null;
	private boolean suspended = false;
	private ThreadReference currentThread = null;
	private boolean suspendOnStartup = false;
	
	public VMEventHandler() {
	}
	
	public void setVM(VirtualMachine vm) {
		this.vm = vm;
	}
	
	public void setSuspendOnStartup(boolean suspendOnStartup) {
		this.suspendOnStartup = suspendOnStartup;
	}

	public void run() {
		eventHandlingLoop:
		while (!this.interrupted) {
			try {
				EventSet es = this.vm.eventQueue().remove(2000);
				if (es != null) {
					for (Event event : es) {
						if (event instanceof VMStartEvent) {
							this.dispatcher.notifyObservers(new net.sf.rej.gui.event.Event(EventType.DEBUG_ATTACH));
							if (this.suspendOnStartup) {
								this.suspended = true;
								net.sf.rej.gui.event.Event evt = new net.sf.rej.gui.event.Event(EventType.DEBUG_SUSPENDED);
								evt.setVM(new VirtualMachineWrapper(vm));
								this.dispatcher.notifyObservers(evt);
							}
						} else if (event instanceof VMDeathEvent) {
						} else if (event instanceof VMDisconnectEvent) {
							break eventHandlingLoop;
						} else if (event instanceof BreakpointEvent || event instanceof StepEvent) {
							final ThreadReference thread;
							if (event instanceof BreakpointEvent) {
								BreakpointEvent be = (BreakpointEvent) event;
								thread = be.thread();
							} else {
								StepEvent se = (StepEvent) event;
								thread = se.thread();
								this.vm.eventRequestManager().deleteEventRequest(se.request());
							}
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									net.sf.rej.gui.event.Event event = new net.sf.rej.gui.event.Event(EventType.DEBUG_SUSPENDED);
									event.setVM(new VirtualMachineWrapper(vm));
									dispatcher.notifyObservers(event);
									event = new net.sf.rej.gui.event.Event(EventType.DEBUG_THREAD_CHANGE_REQUESTED);
									event.setThread(new ThreadReferenceWrapper(thread));
									dispatcher.notifyObservers(event);
									event = new net.sf.rej.gui.event.Event(EventType.DEBUG_STACK_FRAME_CHANGE_REQUESTED);
									try {
										event.setStackFrame(new StackFrameWrapper(thread.frame(0)));
									} catch (IncompatibleThreadStateException e) {
										e.printStackTrace();
									}
									dispatcher.notifyObservers(event);
								}
									
							});
						} else if (event instanceof ClassPrepareEvent) {
							ClassPrepareEvent cpe = (ClassPrepareEvent) event;
							// set breakpoints
							for (Breakpoint bp : EditorFacade.getInstance().getBreakpoints()) {
								if (bp.getClassName().equals(cpe.referenceType().name())) {
									List<Method> mlist = cpe.referenceType().methodsByName(bp.getMethodName(), bp.getMethodDesc().getRawDesc());
									if (mlist.size() > 0) {
										Location loc = mlist.get(0).locationOfCodeIndex(bp.getPc());
										BreakpointRequest bpr = vm.eventRequestManager().createBreakpointRequest(loc);
										bpr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
										bpr.setEnabled(true);
									} else {
										logger.warning("Error, breakpoint " + bp + " method not found in EventHandler.");
									}
								}
							}
							this.vm.resume();
						}
					}
				}
			} catch(InterruptedException ie) {
				// do nothing, the while-condition checks for interruptions
			}
		}
		if (this.interrupted) {
			this.vm.dispose();
		}
		
		this.dispatcher.notifyObservers(new net.sf.rej.gui.event.Event(EventType.DEBUG_DETACH));
	}
	
	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}
	
	public void processEvent(net.sf.rej.gui.event.Event event) {
		switch (event.getType()) {
		case INIT:
			this.dispatcher = event.getDispatcher();
			break;
		case DEBUG_RESUME_REQUESTED:
			if (this.suspended) {
				this.vm.resume();
				this.dispatcher.notifyObservers(new net.sf.rej.gui.event.Event(EventType.DEBUG_RESUMED));
			}
			break;
		case DEBUG_SUSPEND_REQUESTED:
			if (!this.suspended) {
				this.vm.suspend();
				// Suspend removes all Step-request
				this.vm.eventRequestManager().deleteEventRequests(this.vm.eventRequestManager().stepRequests());
				net.sf.rej.gui.event.Event evt = new net.sf.rej.gui.event.Event(EventType.DEBUG_SUSPENDED);
				evt.setVM(new VirtualMachineWrapper(vm));
				this.dispatcher.notifyObservers(evt);
			}
			break;
		case DEBUG_RESUMED:
			this.suspended = false;
			break;
		case DEBUG_SUSPENDED:
			this.suspended = true;
			break;
		case DEBUG_THREAD_CHANGED:
			this.currentThread = (ThreadReference) event.getThread().getThreadReferenceObject();
			break;
		case DEBUG_STEP_INTO_REQUESTED: {
	    	StepRequest sr = this.vm.eventRequestManager().createStepRequest(this.currentThread, StepRequest.STEP_MIN, StepRequest.STEP_INTO);
	    	sr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
	    	sr.setEnabled(true);
	    	vm.resume();
	    	this.dispatcher.notifyObservers(new net.sf.rej.gui.event.Event(EventType.DEBUG_RESUMED));
	    	break;
		}
		case DEBUG_STEP_OUT_REQUESTED: {
	    	StepRequest sr = vm.eventRequestManager().createStepRequest(this.currentThread, StepRequest.STEP_MIN, StepRequest.STEP_OUT);
	    	sr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
	    	sr.setEnabled(true);
	    	vm.resume();
	    	this.dispatcher.notifyObservers(new net.sf.rej.gui.event.Event(EventType.DEBUG_RESUMED));
	    	break;
		}
		case DEBUG_STEP_OVER_REQUESTED: {
	    	StepRequest sr = this.vm.eventRequestManager().createStepRequest(this.currentThread, StepRequest.STEP_MIN, StepRequest.STEP_OVER);
	    	sr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
	    	sr.setEnabled(true);
	    	vm.resume();
	    	this.dispatcher.notifyObservers(new net.sf.rej.gui.event.Event(EventType.DEBUG_RESUMED));
			break;
		}
		case PROJECT_UPDATE:
		case CLASS_OPEN:
		case CLASS_UPDATE:
		case CLASS_REPARSE:
		case CLASS_PARSE_ERROR:
		case DISPLAY_PARAMETER_UPDATE:
		case DEBUG_ATTACH:
		case DEBUG_DETACH:
		case DEBUG_THREAD_CHANGE_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGE_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGED:
			// do nothing
			break;
		}
	}

}

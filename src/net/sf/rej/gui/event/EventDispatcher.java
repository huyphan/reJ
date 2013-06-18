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

import java.util.ArrayList;
import java.util.Collection;


/**
 * Class that holds a collection of observers and a notify method which
 * notifies all registered observers. 
 * 
 * @author Sami Koivu
 */
public class EventDispatcher {
	private Collection<EventObserver> observers = new ArrayList<EventObserver>();
	
	public void registerObserver(EventObserver observer) {
		this.observers.add(observer);
		Event event = new Event(EventType.INIT);
		event.setDispatcher(this);
		observer.processEvent(event);
	}
	
	public void notifyObservers(Event event) {
		// DEBUG, print invoking line and event type
		//StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		//System.out.println(ste[3] + " " + event.getType());
		
		Collection<EventObserver> copy = new ArrayList<EventObserver>();
		copy.addAll(this.observers);
		for (EventObserver observer : copy) {
			observer.processEvent(event);
		}
	}

}

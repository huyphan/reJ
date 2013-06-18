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
package net.sf.rej.util;

/**
 * Utility class for measuring the duration of tasks for informal uses.
 * Has an elapsedSeconds method for retrieving the time elapsed
 * between calls to the methods start and stop in seconds.
 * 
 * @author Sami Koivu
 */
public class StopWatch {

	/**
	 * Start time in milliseconds, as returned by
	 * <code>System.currentTimeMillis</code>
	 */
	private long startTime = -1;

	/**
	 * Initializes a new object and starts the timer.
	 */
	public StopWatch() {
		this(true);
	}

	/**
	 * Initializes a new object and depending on the given parameter
	 * either stars the timer, or not. If the timer is not started
	 * it may be started with a call to the start method.
	 * @param start
	 */
	public StopWatch(boolean start) {
		if (start)
			start();
	}

	/**
	 * Starts the timer. This method may only be called once per
	 * object.
	 */
	public void start() {
		assert this.startTime == -1 : "Timer must not be started.";

		this.startTime = System.currentTimeMillis();
	}

	
	/**
	 * Returns the number of elapsed seconds since the timer was
	 * started (either via the constructor or a call to the start
	 * method).
	 * @return seconds elapsed.
	 */
	public int elapsedSeconds() {
		return (int) ((System.currentTimeMillis() - this.startTime) / 1000);
	}

}

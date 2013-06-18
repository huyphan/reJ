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
package net.sf.rej;

import net.sf.rej.gui.IteratorAgent;
import net.sf.rej.gui.ProgressMonitor;

/**
 * <code>AbstractIteratorAgent</code> is an abstract implementation of the
 * <code>IteratorAgent</code> interface providing a method for setting a
 * <code>ProgressMonitor</code> and calling the <code>ProgressMonitor</code>'s
 * method to inform it about progress.
 * 
 * @author Sami Koivu
 */

public abstract class AbstractIteratorAgent implements IteratorAgent {
	protected ProgressMonitor pm = null;

	/**
	 * Sets the the <code>ProgressMonitor</code> instance to receive notifications on
	 * progress. 
	 * 
	 * @param pm monitor to which notification on progress are delegated to.
	 */
	public void setProgressMonitor(ProgressMonitor pm) {
		this.pm = pm;
	}

	public void progressed(int progressPct) {
		if (this.pm != null) {
			this.pm.setProgress(progressPct);
		}
	}

	public void scopeChanged(int min, int max) {
		if (this.pm != null) {
			this.pm.setProgressScope(min, max);
		}
	}

}
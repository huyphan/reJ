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
package net.sf.rej.cmdline;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.gui.IteratorAgent;
import net.sf.rej.obfuscation.LineNumberStripper;

public class Obfuscate {

	public static void main(String[] args) {
		CommandLineParams params = CommandLineParams.parse(args);
		List<IteratorAgent> obfuscators = new ArrayList<IteratorAgent>();

		if (params.isSwitchOn("ln") || params.isSwitchOn("all")) {
			obfuscators.add(new LineNumberStripper(true) {
				@Override
				public void processException(Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}
}

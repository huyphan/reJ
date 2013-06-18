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

public class CommandLineParams {
	private String[] args;

	private CommandLineParams(String[] args) {
		this.args = args;
	}

	public static CommandLineParams parse(String[] args) {
		return new CommandLineParams(args);
	}

	public boolean isSwitchOn(String sw) {
		for (int i = 0; i < this.args.length; i++) {
			if (this.args[i].equals("-" + sw)) {
				return true;
			}
		}

		return false;
	}

	public String getNonSwitchArgument(int pindex)
			throws NoSuchArgumentException {
		int i = 0;
		int index = pindex;
		while (i < this.args.length) {
			if (this.args[i].startsWith("-")) {
				i++;
			} else {
				if (index == 0) {
					return this.args[i];
				}
				index--;
			}
		}

		throw new NoSuchArgumentException();
	}

}

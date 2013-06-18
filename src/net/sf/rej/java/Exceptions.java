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
package net.sf.rej.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.instruction.Label;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

public class Exceptions {

	private List<ExceptionInfo> exceptions = new ArrayList<ExceptionInfo>();

	public Exceptions(ByteParser parser, ConstantPool pool) {
		int length = parser.getShortAsInt();
		for (int i = 0; i < length; i++) {
			ExceptionInfo e = new ExceptionInfo(parser, pool);
			this.exceptions.add(e);
		}
	}

	public Exceptions() {
	}

	public byte[] getData() {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addShort(this.exceptions.size());
		for (int i = 0; i < this.exceptions.size(); i++) {
			ExceptionInfo e = this.exceptions.get(i);
			ser.addBytes(e.getData());
		}

		return ser.getBytes();
	}

	public List<ExceptionInfo> getExceptionInfos() {
		return this.exceptions;
	}

	public List<Label> getLabels() {
		List<Label> labels = new ArrayList<Label>();
		for (int i = 0; i < this.exceptions.size(); i++) {
			ExceptionInfo e = this.exceptions.get(i);
			labels.addAll(e.getLabels());
		}
		return labels;
	}

}

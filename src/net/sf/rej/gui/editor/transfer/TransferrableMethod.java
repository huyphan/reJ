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
package net.sf.rej.gui.editor.transfer;

import java.util.List;

import net.sf.rej.java.Code;
import net.sf.rej.java.Descriptor;

public class TransferrableMethod implements Transferrable {
	private String methodName;
	private Descriptor descriptor;
	private int accessFlags;
	private Integer maxStack = null;
	private Integer maxLocals = null;
	private List<String> exceptions;
	private Code code = null;
	

	public void setMethodName(String name) {
		this.methodName = name;
	}

	public void setDescriptor(Descriptor descriptor) {
		this.descriptor = descriptor;
	}

	public void setAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	public void setMaxStack(Integer maxStackSize) {
		this.maxStack = maxStackSize;
	}

	public void setMaxLocals(Integer maxLocals) {
		this.maxLocals = maxLocals;
	}

	public void setExceptions(List<String> exceptionNames) {
		this.exceptions = exceptionNames;
	}

	public Descriptor getDescriptor() {
		return descriptor;
	}

	public Integer getMaxLocals() {
		return maxLocals;
	}

	public Integer getMaxStack() {
		return maxStack;
	}

	public int getAccessFlags() {
		return accessFlags;
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public String getMethodName() {
		return methodName;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

}

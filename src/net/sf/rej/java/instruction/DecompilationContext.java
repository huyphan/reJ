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
package net.sf.rej.java.instruction;

import net.sf.rej.java.Exceptions;
import net.sf.rej.java.attribute.LocalVariableTableAttribute;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.util.ByteParser;

/**
 * <code>DecompilationContext</code> is a context class used by instruction
 * instances to obtain information about the context they appear in.
 * 
 * @author Sami Koivu
 */
public class DecompilationContext {

	private int position = 0;

	private ConstantPool cp;

	private ByteParser parser;

	private Exceptions exceptions = null;

	LocalVariableTableAttribute lvtAttr;
	
	public DecompilationContext() {
	}

	public void setPosition(int pos) {
		this.position = pos;
	}

	public int getPosition() {
		return this.position;
	}
	
	/**
	 * Increments the pc position of this context by the size of the given
	 * instruction.
	 * @param instruction
	 */
	public void incrementPosition(Instruction instruction) {
		this.position += instruction.getSize(this);
	}

	public void incrementPosition(int count) {
		this.position += count;
	}

	public void setParser(ByteParser parser) {
		this.parser = parser;
	}

	@Deprecated
	//	 substitute method with other means to allow stream parsing
	public ByteParser getParser() {
		return this.parser.getNewParser();
	}

	public ConstantPool getConstantPool() {
		return this.cp;
	}

	public void setConstantPool(ConstantPool cp) {
		this.cp = cp;
	}

	public void setExceptions(Exceptions exceptions) {
		this.exceptions = exceptions;
	}

	public Exceptions getExceptions() {
		return this.exceptions;
	}

	public void setLocalVariableTable(LocalVariableTableAttribute lvtAttr) {
		this.lvtAttr = lvtAttr;
	}

	public LocalVariableTableAttribute getLocalVariableTable() {
		return this.lvtAttr;
	}

	public DecompilationContext createNew() {
		DecompilationContext dc = new DecompilationContext();
		dc.cp = this.cp;
		dc.exceptions = this.exceptions;
		dc.lvtAttr = this.lvtAttr;
		dc.parser = this.parser;
		dc.position = 0;
		return dc;
	}

}

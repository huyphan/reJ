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

import java.util.List;

import net.sf.rej.util.ImmutableEmptyList;

/**
 * This class represents a java bytecode instruction. It is the abstract
 * superclass of instruction type classes(<code>_aload</code>,
 * <code>_nop</code>, <code>_invokeinterface</code>, etc..)
 * 
 * @author Sami Koivu
 */

public abstract class Instruction {
	
	private static final List<Label> EMPTY_LABEL_LIST = new ImmutableEmptyList<Label>();
	private static final List<StackElement> EMPTY_STACK_ELEMENT_LIST = new ImmutableEmptyList<StackElement>();

	protected Instruction() {
	}

	/**
	 * Return the Opcode of this instruction.
	 * 
	 * @return The Opcode value of this instruction.
	 */
	public abstract int getOpcode();

	/**
	 * Return the mnemonic of this instruction. Such as aload, invokespecial,
	 * nop, etc..
	 * 
	 * @return mnemonic.
	 */
	public abstract String getMnemonic();

	/**
	 * Return the size of this instruction.
	 * 
	 * @return Size of the instruction in bytes.
	 */
	public abstract int getSize();

	/**
	 * Reserved for possible future use.
	 * 
	 * @param ec
	 *            Context for execution.
	 */
	public abstract void execute(ExecutionContext ec);

	/**
	 * Override for variable sized instructions. This method should be called to
	 * query the size of an instruction. The default implementation simply calls
	 * the getSize() method, but for instructions where the size depetends on
	 * the location of the instruction in a code block, this method needs to be
	 * overridden, since it offers access to the decompilation context where the
	 * current position and other attributes may be queried.
	 * 
	 * @param dc
	 *            Context information(most importantly the pc position).
	 * @return Size of instruction in this given context in bytes.
	 */
	public int getSize(DecompilationContext dc) {
		return getSize();
	}

	/**
	 * Set the data for this instruction. Called by the decompiler to initialize
	 * the parameters of the instruction from bytecode data.
	 * 
	 * @param data
	 *            A byte arrray with the instruction data, starting with the
	 *            opcode byte(s) and followed by any parameters if applicable.
	 * @param dc
	 *            A callback mechanism to offer the instruction information
	 *            about the context where it's located.
	 */
	public abstract void setData(byte[] data, DecompilationContext dc);

	/**
	 * Get the bytecode data for this instruction. In other words, tell the
	 * instruction to serialize itself. Some instructions use padding to put the
	 * subsequent parameters in a pc position that is divisible by 4 and thus
	 * require the pc position which they can obtain from the
	 * DecompilationContext
	 * 
	 * @param dc
	 *            A callback mechanism to offer the instruction information
	 *            about the context where it's located.
	 * @return A byte array with the instruction data.
	 */
	public abstract byte[] getData(DecompilationContext dc);

	/**
	 * Return a Parameters object describing the types of parameters(if any) and
	 * the values set to this particular instance for those parameters. For
	 * different parameter types see the Parameters class. Modifications in the
	 * values of the parameters class are not reflected by this instruction
	 * unless setParameters(Parameters) is called subsequently.
	 * 
	 * @return A <code>Parameters</code> object with the types and current
	 *         values of the parameters.
	 */
	public abstract Parameters getParameters();

	/**
	 * Return a Parameters object describing the types of parameters(if any)
	 * that are applicable to this type of an instruction. This method is
	 * identical to the getParameters() method, only the Parameters object
	 * return by a call to this method, does not contain the values of the
	 * parameters, just the type information.
	 * 
	 * @return A <code>Parameters</code> object with the types of parameters
	 *         applicable for this type of an instruction.
	 */
	public abstract Parameters getParameterTypes();

	/**
	 * Update the parameter values of this instruction with the values in the
	 * Parameters object defined by params. Observe that the types and count of
	 * the parameters in the Parameters object MUST match those that this
	 * instruction expects. This can be ensured by obtaining the Parameters
	 * object with a call to getParameters() of this instruction.
	 * 
	 * @param params
	 *            A <code>Parameters</code> object with new values for this
	 *            instruction.
	 */
	public abstract void setParameters(Parameters params);

	/**
	 * Return a list with labels relevant to this instructions. Labels are
	 * symbolic and do not exist in the actual bytecode. This method returns a
	 * List containing Label objects, and by setting the position of a label
	 * with a call to Label.setPosition(int) this instruction is updated with
	 * the information. The labels are used for example for the goto
	 * instruction.
	 * 
	 * @return A <code>List</code> of <code>Label</code> objects.
	 */
	public List<Label> getLabels() {
		return EMPTY_LABEL_LIST;
	}
	
	/**
	 * Returns a textual description of this instruction instance. The default
	 * implementation just returns the Mnemonic returned by getMnemonic().
	 * 
	 * @return Description
	 */
	@Override
	public String toString() {
		return getMnemonic();
	}

	/**
	 * Returns a new instance of this instruction. No parameters are copied.
	 * If the instruction is a wide instruction, also the contained, widened
	 * instruction will be cloned.
	 * @return a new blank instance of this instruction.
	 * @throws InstantiationException a problem with instantiation.
	 * @throws IllegalAccessException access exception.
	 */
	public Instruction createNewInstance() throws InstantiationException, IllegalAccessException {
		return this.getClass().newInstance();
	}
	
	/**
	 * Returns the elements pushed to the stack by this instance.
	 * This information is static for most instructions, but for example, for
	 * the invokexxx instructions it depends on the instruction that is being
	 * invoked. 
	 * @param dc the decompilation context so that the processing can access
	 * the constant pool, when necessary.
	 * @return a <code>StackElements</code> instance describing the elements
	 * pushed onto the stack.
	 */
	public List<StackElement> getPushedElements(DecompilationContext dc) {
		return EMPTY_STACK_ELEMENT_LIST;
	}
	
	/**
	 * Returns the elements popped from the stack by this instance.
	 * This information is static for most instructions, but for example, for
	 * the invokexxx instructions it depends on the instruction that is being
	 * invoked. 
	 * @param dc the decompilation context so that the processing can access
	 * the constant pool, when necessary.
	 * @return a <code>StackElements</code> instance describing the elements
	 * popped from the stack.
	 */
	public List<StackElement> getPoppedElements(DecompilationContext dc) {
		return EMPTY_STACK_ELEMENT_LIST;
	}

}
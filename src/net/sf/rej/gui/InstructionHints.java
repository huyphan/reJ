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
package net.sf.rej.gui;

import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction._wide;

/**
 * This class is used for obtaining hints / descriptions of the different
 * instructions.
 * 
 * @author Sami Koivu
 */
public class InstructionHints {
	private final String[] hints = new String[256];
	
	public InstructionHints() {
        this.hints[0] = "Do nothing";
        this.hints[1] = "Push null";
        this.hints[2] = "Push int constant -1";
        this.hints[3] = "Push int constant 0";
        this.hints[4] = "Push int constant 1";
        this.hints[5] = "Push int constant 2";
        this.hints[6] = "Push int constant 3";
        this.hints[7] = "Push int constant 4";
        this.hints[8] = "Push int constant 5";
        this.hints[9] = "Push the long constant 0 onto the operand stack.";
        this.hints[10] = "Push the long constant 1 onto the operand stack.";
        this.hints[11] = "Push the float constant 0.0 onto the operand stack.";
        this.hints[12] = "Push the float constant 1.0 onto the operand stack.";
        this.hints[13] = "Push the float constant 2.0 onto the operand stack.";
        this.hints[14] = "Push the double constant 0.0 onto the operand stack";
        this.hints[15] = "Push the double constant 1.0 onto the operand stack";
        this.hints[16] = "Push byte";
        this.hints[17] = "Push short";
        this.hints[18] = "Push item from constant pool";
        this.hints[19] = "Push item from constant pool";
        this.hints[20] = "Push long or double from constant pool";
        this.hints[21] = "Load int from local variable";
        this.hints[22] = "Load long from local variable";
        this.hints[23] = "Load float from local variable";
        this.hints[24] = "Load double from local variable";
        this.hints[25] = "Load reference from local variable";
        this.hints[26] = "Load int from local variable 0";
        this.hints[27] = "Load int from local variable 1";
        this.hints[28] = "Load int from local variable 2";
        this.hints[29] = "Load int from local variable 3";
        this.hints[30] = "Load long from local variable 0";
        this.hints[31] = "Load long from local variable 1";
        this.hints[32] = "Load long from local variable 2";
        this.hints[33] = "Load long from local variable 3";
        this.hints[34] = "Load float from local variable 0";
        this.hints[35] = "Load float from local variable 1";
        this.hints[36] = "Load float from local variable 2";
        this.hints[37] = "Load float from local variable 3";
        this.hints[38] = "Load double from local variable 0";
        this.hints[39] = "Load double from local variable 1";
        this.hints[40] = "Load double from local variable 2";
        this.hints[41] = "Load double from local variable 3";
        this.hints[42] = "Load reference from local variable 0";
        this.hints[43] = "Load reference from local variable 1";
        this.hints[44] = "Load reference from local variable 2";
        this.hints[45] = "Load reference from local variable 3";
        this.hints[46] = "Load int from array";
        this.hints[47] = "Load long from array";
        this.hints[48] = "Load float from array";
        this.hints[49] = "Load double from array";
        this.hints[50] = "Load reference from array";
        this.hints[51] = "Load byte or boolean from array";
        this.hints[52] = "Load char from array";
        this.hints[53] = "Load short from array";
        this.hints[54] = "Store int into local variable";
        this.hints[55] = "Store long into local variable";
        this.hints[56] = "Store float into local variable";
        this.hints[57] = "Store double into local variable";
        this.hints[58] = "Store reference into local variable";
        this.hints[59] = "Store int into local variable 0";
        this.hints[60] = "Store int into local variable 1";
        this.hints[61] = "Store int into local variable 2";
        this.hints[62] = "Store int into local variable 3";
        this.hints[63] = "Store long into local variable 0";
        this.hints[64] = "Store long into local variable 1";
        this.hints[65] = "Store long into local variable 2";
        this.hints[66] = "Store long into local variable 3";
        this.hints[67] = "Store float into local variable 0";
        this.hints[68] = "Store float into local variable 1";
        this.hints[69] = "Store float into local variable 2";
        this.hints[70] = "Store float into local variable 3";
        this.hints[71] = "Store double into local variable 0";
        this.hints[72] = "Store double into local variable 1";
        this.hints[73] = "Store double into local variable 2";
        this.hints[74] = "Store double into local variable 3";
        this.hints[75] = "Store reference into local variable 0";
        this.hints[76] = "Store reference into local variable 1";
        this.hints[77] = "Store reference into local variable 2";
        this.hints[78] = "Store reference into local variable 3";
        this.hints[79] = "Store into int array";
        this.hints[80] = "Store into long array";
        this.hints[81] = "Store into float array";
        this.hints[82] = "Store into double array";
        this.hints[83] = "Store into reference array (..., arrayref, index -> ..., value)";
        this.hints[84] = "Store into byte or boolean array";
        this.hints[85] = "Store into char array";
        this.hints[86] = "Store into short array";
        this.hints[87] = "Pop top operand stack word";
        this.hints[88] = "Pop top two operand stack words";
        this.hints[89] = "Duplicate top operand stack word";
        this.hints[90] = "Duplicate the top operand stack value and insert two values down";
        this.hints[91] = "Duplicate the top operand stack value and insert two or three values down";
        this.hints[92] = "Duplicate the top one or two operand stack values";
        this.hints[93] = "Duplicate the top one or two operand stack values and insert two or three values down";
        this.hints[94] = "Duplicate the top one or two operand stack values and insert two, three, or four values down";
        this.hints[95] = "Swap top two operand stack words";
        this.hints[96] = "Add int";
        this.hints[97] = "Add long";
        this.hints[98] = "Add float";
        this.hints[99] = "Add long";
        this.hints[100] = "Subtract int";
        this.hints[101] = "Subtract long";
        this.hints[102] = "Subtract float";
        this.hints[103] = "Subtract double";
        this.hints[104] = "Multiply int";
        this.hints[105] = "Multiply long";
        this.hints[106] = "Multiply float";
        this.hints[107] = "Multiply double";
        this.hints[108] = "Divide int";
        this.hints[109] = "Divide long";
        this.hints[110] = "Divide float";
        this.hints[111] = "Divide double";
        this.hints[112] = "Remainder int";
        this.hints[113] = "Remainder long";
        this.hints[114] = "Remainder float";
        this.hints[115] = "Remainder double";
        this.hints[116] = "Negate int";
        this.hints[117] = "Negate long";
        this.hints[118] = "Negate float";
        this.hints[119] = "Negate double";
        this.hints[120] = "Shift left int";
        this.hints[121] = "Shift left";
        this.hints[122] = "Arithmetic shift right int";
        this.hints[123] = "Arithmetic shift right long";
        this.hints[124] = "Logical shift right int";
        this.hints[125] = "Logical shift right long";
        this.hints[126] = "Boolean AND int";
        this.hints[127] = "Boolean AND long";
        this.hints[128] = "Boolean OR int";
        this.hints[129] = "Boolean OR long";
        this.hints[130] = "Boolean XOR int";
        this.hints[131] = "Boolean XOR long";
        this.hints[132] = "Increment local variable by constant";
        this.hints[133] = "Convert int to long";
        this.hints[134] = "Convert int to float";
        this.hints[135] = "Convert int to double";
        this.hints[136] = "Convert long to int";
        this.hints[137] = "Convert long to float";
        this.hints[138] = "Convert long to double";
        this.hints[139] = "Convert float to int";
        this.hints[140] = "Convert float to long";
        this.hints[141] = "Convert float to double";
        this.hints[142] = "Convert double to int";
        this.hints[143] = "Convert double to long";
        this.hints[144] = "Convert double to float";
        this.hints[145] = "Convert int to byte";
        this.hints[146] = "Convert int to char";
        this.hints[147] = "Convert int to short";
        this.hints[148] = "Compare long";
        this.hints[149] = "Compare float";
        this.hints[150] = "Compare float";
        this.hints[151] = "Compare double";
        this.hints[152] = "Compare double";
        this.hints[153] = "Branch if int comparison with zero succeeds(if int is zero)";
        this.hints[154] = "Branch if int comparison with zero succeeds(if int is not zero)";
        this.hints[155] = "Branch if int comparison with zero succeeds(if int is lower than zero)";
        this.hints[156] = "Branch if int comparison with zero succeeds(if int is greater than or equal to zero)";
        this.hints[157] = "Branch if int comparison with zero succeeds(if int is greater than zero)";
        this.hints[158] = "Branch if int comparison with zero succeeds(if int is lower than or equal to zero)";
        this.hints[159] = "Branch if int comparison(equals) succeeds.";
        this.hints[160] = "Branch if int comparison(not equals) succeeds.";
        this.hints[161] = "Branch if int comparison(lower than) succeeds.";
        this.hints[162] = "Branch if int comparison(greater than or equal) succeeds.";
        this.hints[163] = "Branch if int comparison(greater than) succeeds.";
        this.hints[164] = "Branch if int comparison(lower than or equal) succeeds.";
        this.hints[165] = "Branch if reference comparison(equals) succeeds.";
        this.hints[166] = "Branch if reference comparison(not equals) succeeds.";
        this.hints[167] = "Branch always";
        this.hints[168] = "Jump subroutine";
        this.hints[169] = "Return from subroutine";
        this.hints[170] = "Access jump table by index and jump";
        this.hints[171] = "Access jump table by key match and jump";
        this.hints[172] = "Return int from method";
        this.hints[173] = "Return long from method";
        this.hints[174] = "Return float from method";
        this.hints[175] = "Return float from method";
        this.hints[176] = "Return reference from method";
        this.hints[177] = "Return void from method";
        this.hints[178] = "Get static field from class";
        this.hints[179] = "Set static field in class";
        this.hints[180] = "Fetch field from object";
        this.hints[181] = "Set field in object";
        this.hints[182] = "Invoke instance method";
        this.hints[183] = "Invoke instance method; special handling for superclass, private, and instance initialization method invocations";
        this.hints[184] = "Invoke a class (static) method";
        this.hints[185] = "Invoke interface method";
        this.hints[186] = "Undefined instruction 0xBA";
        this.hints[187] = "Create new object";
        this.hints[188] = "Create new array";
        this.hints[189] = "Create new array of reference";
        this.hints[190] = "Get length of array";
        this.hints[191] = "Throw exception";
        this.hints[192] = "Check whether object is of given type";
        this.hints[193] = "Determine if object is of given type";
        this.hints[194] = "Enter monitor for object";
        this.hints[195] = "Exit monitor for object";
        this.hints[196] = "(wide) Increment local variable by constant";
        this.hints[197] = "Create new multidimensional array";
        this.hints[198] = "Branch if reference is null";
        this.hints[199] = "Branch if reference not null";
        this.hints[200] = "Branch always(wide index)";
        this.hints[201] = "Jump subroutine(wide index)";
        this.hints[202] = "(Reserved)breakpoint";
        this.hints[203] = "???";
        this.hints[204] = "???";
        this.hints[205] = "Push long or double from constant pool";
        this.hints[206] = "???";
        this.hints[207] = "???";
        this.hints[208] = "???";
        this.hints[209] = "???";
        this.hints[210] = "Get static field from class";
        this.hints[211] = "???";
        this.hints[212] = "???";
        this.hints[213] = "???";
        this.hints[214] = "???";
        this.hints[215] = "???";
        this.hints[216] = "???";
        this.hints[217] = "???";
        this.hints[218] = "???";
        this.hints[219] = "???";
        this.hints[220] = "";
        this.hints[221] = "???";
        this.hints[222] = "???";
        this.hints[223] = "???";
        this.hints[224] = "Check whether object is of given type";
        this.hints[225] = "???";
        this.hints[226] = "???";
        this.hints[227] = "???";
        this.hints[228] = "???";
        this.hints[229] = "";
        this.hints[230] = "";
        this.hints[231] = "";
        this.hints[232] = "";
        this.hints[233] = "";
        this.hints[234] = "";
        this.hints[235] = "";
        this.hints[236] = "";
        this.hints[237] = "";
        this.hints[238] = "";
        this.hints[239] = "";
        this.hints[240] = "";
        this.hints[241] = "";
        this.hints[242] = "";
        this.hints[243] = "";
        this.hints[244] = "";
        this.hints[245] = "";
        this.hints[246] = "";
        this.hints[247] = "";
        this.hints[248] = "";
        this.hints[249] = "";
        this.hints[250] = "";
        this.hints[251] = "";
        this.hints[252] = "";
        this.hints[253] = "";
        this.hints[254] = "(Reserved) impdep1";
        this.hints[255] = "(Reserved) impdp2";
	}

	/**
	 * Returns the hint for the given instruction opcode.
	 * @param opcode the opcode of the instruction requested.
	 * @return a description of what the instruction does.
	 */
	public String getHint(int opcode) {
		return this.hints[opcode];
	}
	
	/**
	 * Returns the hint for the given instruction. If the instruction is a
	 * widened instruction, the hint for the contained, widened instruction
	 * is returned.
	 * @param instr identifies the instruction whose hint is being requested.
	 * @return a description of what the instruction does.
	 */
	public String getHint(Instruction instr) {
		if (instr.getOpcode() == _wide.OPCODE) {
			_wide wide = (_wide) instr;
			return "wide " + this.hints[wide.getInstruction().getOpcode()];
		} else {
			return this.hints[instr.getOpcode()];
		}
	}
}

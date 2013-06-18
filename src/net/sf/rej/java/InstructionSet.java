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

import java.util.Vector;

import net.sf.rej.java.instruction.*;

/**
 * Class that handles the relationship between Instruction subclasses
 * and instruction opcodes.
 * 
 * @author Sami Koivu
 */
public class InstructionSet {

	private static InstructionSet instance = null;

	private static Class[] instructions = null;

	static {
		instructions = new Class[256];
		instructions[0] = _nop.class;
		instructions[1] = _aconst_null.class;
		instructions[2] = _iconst_m1.class;
		instructions[3] = _iconst_0.class;
		instructions[4] = _iconst_1.class;
		instructions[5] = _iconst_2.class;
		instructions[6] = _iconst_3.class;
		instructions[7] = _iconst_4.class;
		instructions[8] = _iconst_5.class;
		instructions[9] = _lconst_0.class;
		instructions[10] = _lconst_1.class;
		instructions[11] = _fconst_0.class;
		instructions[12] = _fconst_1.class;
		instructions[13] = _fconst_2.class;
		instructions[14] = _dconst_0.class;
		instructions[15] = _dconst_1.class;
		instructions[16] = _bipush.class;
		instructions[17] = _sipush.class;
		instructions[18] = _ldc.class;
		instructions[19] = _ldc_w.class;
		instructions[20] = _ldc2_w.class;
		instructions[21] = _iload.class;
		instructions[22] = _lload.class;
		instructions[23] = _fload.class;
		instructions[24] = _dload.class;
		instructions[25] = _aload.class;
		instructions[26] = _iload_0.class;
		instructions[27] = _iload_1.class;
		instructions[28] = _iload_2.class;
		instructions[29] = _iload_3.class;
		instructions[30] = _lload_0.class;
		instructions[31] = _lload_1.class;
		instructions[32] = _lload_2.class;
		instructions[33] = _lload_3.class;
		instructions[34] = _fload_0.class;
		instructions[35] = _fload_1.class;
		instructions[36] = _fload_2.class;
		instructions[37] = _fload_3.class;
		instructions[38] = _dload_0.class;
		instructions[39] = _dload_1.class;
		instructions[40] = _dload_2.class;
		instructions[41] = _dload_3.class;
		instructions[42] = _aload_0.class;
		instructions[43] = _aload_1.class;
		instructions[44] = _aload_2.class;
		instructions[45] = _aload_3.class;
		instructions[46] = _iaload.class;
		instructions[47] = _laload.class;
		instructions[48] = _faload.class;
		instructions[49] = _daload.class;
		instructions[50] = _aaload.class;
		instructions[51] = _baload.class;
		instructions[52] = _caload.class;
		instructions[53] = _saload.class;
		instructions[54] = _istore.class;
		instructions[55] = _lstore.class;
		instructions[56] = _fstore.class;
		instructions[57] = _dstore.class;
		instructions[58] = _astore.class;
		instructions[59] = _istore_0.class;
		instructions[60] = _istore_1.class;
		instructions[61] = _istore_2.class;
		instructions[62] = _istore_3.class;
		instructions[63] = _lstore_0.class;
		instructions[64] = _lstore_1.class;
		instructions[65] = _lstore_2.class;
		instructions[66] = _lstore_3.class;
		instructions[67] = _fstore_0.class;
		instructions[68] = _fstore_1.class;
		instructions[69] = _fstore_2.class;
		instructions[70] = _fstore_3.class;
		instructions[71] = _dstore_0.class;
		instructions[72] = _dstore_1.class;
		instructions[73] = _dstore_2.class;
		instructions[74] = _dstore_3.class;
		instructions[75] = _astore_0.class;
		instructions[76] = _astore_1.class;
		instructions[77] = _astore_2.class;
		instructions[78] = _astore_3.class;
		instructions[79] = _iastore.class;
		instructions[80] = _lastore.class;
		instructions[81] = _fastore.class;
		instructions[82] = _dastore.class;
		instructions[83] = _aastore.class;
		instructions[84] = _bastore.class;
		instructions[85] = _castore.class;
		instructions[86] = _sastore.class;
		instructions[87] = _pop.class;
		instructions[88] = _pop2.class;
		instructions[89] = _dup.class;
		instructions[90] = _dup_x1.class;
		instructions[91] = _dup_x2.class;
		instructions[92] = _dup2.class;
		instructions[93] = _dup2_x1.class;
		instructions[94] = _dup2_x2.class;
		instructions[95] = _swap.class;
		instructions[96] = _iadd.class;
		instructions[97] = _ladd.class;
		instructions[98] = _fadd.class;
		instructions[99] = _dadd.class;
		instructions[100] = _isub.class;
		instructions[101] = _lsub.class;
		instructions[102] = _fsub.class;
		instructions[103] = _dsub.class;
		instructions[104] = _imul.class;
		instructions[105] = _lmul.class;
		instructions[106] = _fmul.class;
		instructions[107] = _dmul.class;
		instructions[108] = _idiv.class;
		instructions[109] = _ldiv.class;
		instructions[110] = _fdiv.class;
		instructions[111] = _ddiv.class;
		instructions[112] = _irem.class;
		instructions[113] = _lrem.class;
		instructions[114] = _frem.class;
		instructions[115] = _drem.class;
		instructions[116] = _ineg.class;
		instructions[117] = _lneg.class;
		instructions[118] = _fneg.class;
		instructions[119] = _dneg.class;
		instructions[120] = _ishl.class;
		instructions[121] = _lshl.class;
		instructions[122] = _ishr.class;
		instructions[123] = _lshr.class;
		instructions[124] = _iushr.class;
		instructions[125] = _lushr.class;
		instructions[126] = _iand.class;
		instructions[127] = _land.class;
		instructions[128] = _ior.class;
		instructions[129] = _lor.class;
		instructions[130] = _ixor.class;
		instructions[131] = _lxor.class;
		instructions[132] = _iinc.class;
		instructions[133] = _i2l.class;
		instructions[134] = _i2f.class;
		instructions[135] = _i2d.class;
		instructions[136] = _l2i.class;
		instructions[137] = _l2f.class;
		instructions[138] = _l2d.class;
		instructions[139] = _f2i.class;
		instructions[140] = _f2l.class;
		instructions[141] = _f2d.class;
		instructions[142] = _d2i.class;
		instructions[143] = _d2l.class;
		instructions[144] = _d2f.class;
		instructions[145] = _i2b.class;
		instructions[146] = _i2c.class;
		instructions[147] = _i2s.class;
		instructions[148] = _lcmp.class;
		instructions[149] = _fcmpl.class;
		instructions[150] = _fcmpg.class;
		instructions[151] = _dcmpl.class;
		instructions[152] = _dcmpg.class;
		instructions[153] = _ifeq.class;
		instructions[154] = _ifne.class;
		instructions[155] = _iflt.class;
		instructions[156] = _ifge.class;
		instructions[157] = _ifgt.class;
		instructions[158] = _ifle.class;
		instructions[159] = _if_icmpeq.class;
		instructions[160] = _if_icmpne.class;
		instructions[161] = _if_icmplt.class;
		instructions[162] = _if_icmpge.class;
		instructions[163] = _if_icmpgt.class;
		instructions[164] = _if_icmple.class;
		instructions[165] = _if_acmpeq.class;
		instructions[166] = _if_acmpne.class;
		instructions[167] = _goto.class;
		instructions[168] = _jsr.class;
		instructions[169] = _ret.class;
		instructions[170] = _tableswitch.class;
		instructions[171] = _lookupswitch.class;
		instructions[172] = _ireturn.class;
		instructions[173] = _lreturn.class;
		instructions[174] = _freturn.class;
		instructions[175] = _dreturn.class;
		instructions[176] = _areturn.class;
		instructions[177] = _return.class;
		instructions[178] = _getstatic.class;
		instructions[179] = _putstatic.class;
		instructions[180] = _getfield.class;
		instructions[181] = _putfield.class;
		instructions[182] = _invokevirtual.class;
		instructions[183] = _invokespecial.class;
		instructions[184] = _invokestatic.class;
		instructions[185] = _invokeinterface.class;
		instructions[186] = _xxxunusedxxx.class;
		instructions[187] = _new.class;
		instructions[188] = _newarray.class;
		instructions[189] = _anewarray.class;
		instructions[190] = _arraylength.class;
		instructions[191] = _athrow.class;
		instructions[192] = _checkcast.class;
		instructions[193] = _instanceof.class;
		instructions[194] = _monitorenter.class;
		instructions[195] = _monitorexit.class;
		instructions[196] = _wide.class;
		instructions[197] = _multianewarray.class;
		instructions[198] = _ifnull.class;
		instructions[199] = _ifnonnull.class;
		instructions[200] = _goto_w.class;
		instructions[201] = _jsr_w.class;
		instructions[203] = _ldc_quick.class;
		instructions[204] = _ldc_w_quick.class;
		instructions[205] = _ldc2_w_quick.class;
		instructions[206] = _getfield_quick.class;
		instructions[207] = _putfield_quick.class;
		instructions[208] = _getfield2_quick.class;
		instructions[209] = _putfield2_quick.class;
		instructions[210] = _getstatic_quick.class;
		instructions[211] = _putstatic_quick.class;
		instructions[212] = _getstatic2_quick.class;
		instructions[213] = _putstatic2_quick.class;
		instructions[214] = _invokevirtual_quick.class;
		instructions[215] = _invokenonvirtual_quick.class;
		instructions[216] = _invokesuper_quick.class;
		instructions[217] = _invokestatic_quick.class;
		instructions[218] = _invokeinterface_quick.class;
		instructions[219] = _invokevirtualobject_quick.class;
		instructions[221] = _new_quick.class;
		instructions[222] = _anewarray_quick.class;
		instructions[223] = _multianewarray_quick.class;
		instructions[224] = _checkcast_quick.class;
		instructions[225] = _instanceof_quick.class;
		instructions[226] = _invokevirtual_quick_w.class;
		instructions[227] = _getfield_quick_w.class;
		instructions[228] = _putfield_quick_w.class;
		instructions[202] = _breakpoint.class;
		instructions[254] = _impdep1.class;
		instructions[255] = _impdep2.class;
	}

	private InstructionSet() {
	}

	public static synchronized InstructionSet getInstance() {
		if (instance == null)
			instance = new InstructionSet();

		return instance;
	}

	public Instruction getInstruction(int opcode) {
		Class c = InstructionSet.instructions[opcode];
		try {
			return (Instruction) c.newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Vector<Instruction> getInstructions() {
		Vector<Instruction> v = new Vector<Instruction>();
		for (int i = 0; i < instructions.length; i++) {
			if (getInstruction(i) != null) {
				v.add(getInstruction(i));
			}
		}

		return v;
	}

}

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
package net.sf.rej.gui.editor;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.instruction.*;

public class InstructionList {

	public InstructionList() {
	}
	
	public List<Instruction> getList() {
		List<Instruction> list = new ArrayList<Instruction>();
		list.add(new _nop());
		list.add(new _aconst_null());
		
		list.add(new _iconst_m1());
		list.add(new _iconst_0());
		list.add(new _iconst_1());
		list.add(new _iconst_2());
		list.add(new _iconst_3());
		list.add(new _iconst_4());
		list.add(new _iconst_5());
		list.add(new _lconst_0());
		list.add(new _lconst_1());
		list.add(new _fconst_0());
		list.add(new _fconst_1());
		list.add(new _fconst_2());
		list.add(new _dconst_0());
		list.add(new _dconst_1());
		
		list.add(new _bipush());
		list.add(new _sipush());
		
		list.add(new _ldc());
		list.add(new _ldc_w());
		list.add(new _ldc2_w());
		
		list.add(new _iload());
		list.add(new _lload());
		list.add(new _fload());
		list.add(new _dload());
		list.add(new _aload());
		list.add(new _iload_0());
		list.add(new _iload_1());
		list.add(new _iload_2());
		list.add(new _iload_3());
		list.add(new _lload_0());
		list.add(new _lload_1());
		list.add(new _lload_2());
		list.add(new _lload_3());
		list.add(new _fload_0());
		list.add(new _fload_1());
		list.add(new _fload_2());
		list.add(new _fload_3());
		list.add(new _dload_0());
		list.add(new _dload_1());
		list.add(new _dload_2());
		list.add(new _dload_3());
		list.add(new _aload_0());
		list.add(new _aload_1());
		list.add(new _aload_2());
		list.add(new _aload_3());
		list.add(new _iaload());
		list.add(new _laload());
		list.add(new _faload());
		list.add(new _daload());
		list.add(new _aaload());
		list.add(new _baload());
		list.add(new _caload());
		list.add(new _saload());
		
		list.add(new _istore());
		list.add(new _lstore());
		list.add(new _fstore());
		list.add(new _dstore());
		list.add(new _astore());
		list.add(new _istore_0());
		list.add(new _istore_1());
		list.add(new _istore_2());
		list.add(new _istore_3());
		list.add(new _lstore_0());
		list.add(new _lstore_1());
		list.add(new _lstore_2());
		list.add(new _lstore_3());
		list.add(new _fstore_0());
		list.add(new _fstore_1());
		list.add(new _fstore_2());
		list.add(new _fstore_3());
		list.add(new _dstore_0());
		list.add(new _dstore_1());
		list.add(new _dstore_2());
		list.add(new _dstore_3());
		list.add(new _astore_0());
		list.add(new _astore_1());
		list.add(new _astore_2());
		list.add(new _astore_3());
		list.add(new _iastore());
		list.add(new _lastore());
		list.add(new _fastore());
		list.add(new _dastore());
		list.add(new _aastore());
		list.add(new _bastore());
		list.add(new _castore());
		list.add(new _sastore());
		
		list.add(new _pop());
		list.add(new _pop2());
		list.add(new _dup());
		list.add(new _dup_x1());
		list.add(new _dup_x2());
		list.add(new _dup2());
		list.add(new _dup2_x1());
		list.add(new _dup2_x2());
		list.add(new _swap());
		
		list.add(new _iadd());
		list.add(new _ladd());
		list.add(new _fadd());
		list.add(new _dadd());
		
		list.add(new _isub());
		list.add(new _lsub());
		list.add(new _fsub());
		list.add(new _dsub());
		
		list.add(new _imul());
		list.add(new _lmul());
		list.add(new _fmul());
		list.add(new _dmul());
		
		list.add(new _idiv());
		list.add(new _ldiv());
		list.add(new _fdiv());
		list.add(new _ddiv());
		
		list.add(new _irem());
		list.add(new _lrem());
		list.add(new _frem());
		list.add(new _drem());
		
		list.add(new _ineg());
		list.add(new _lneg());
		list.add(new _fneg());
		list.add(new _dneg());
		
		list.add(new _ishl());
		list.add(new _lshl());
		list.add(new _ishr());
		list.add(new _lshr());
		list.add(new _iushr());
		list.add(new _lushr());
		
		list.add(new _iand());
		list.add(new _land());
		list.add(new _ior());
		list.add(new _lor());
		list.add(new _ixor());
		list.add(new _lxor());
		
		list.add(new _iinc());
		
		list.add(new _i2l());
		list.add(new _i2f());
		list.add(new _i2d());
		list.add(new _l2i());
		list.add(new _l2f());
		list.add(new _l2d());
		list.add(new _f2i());
		list.add(new _f2l());
		list.add(new _f2d());
		list.add(new _d2i());
		list.add(new _d2l());
		list.add(new _d2f());
		list.add(new _i2b());
		list.add(new _i2c());
		list.add(new _i2s());
		
		list.add(new _lcmp());
		list.add(new _fcmpl());
		list.add(new _fcmpg());
		list.add(new _dcmpl());
		list.add(new _dcmpg());
		
		list.add(new _ifeq());
		list.add(new _ifne());
		list.add(new _iflt());
		list.add(new _ifge());
		list.add(new _ifgt());
		list.add(new _ifle());
		list.add(new _if_icmpeq());
		list.add(new _if_icmpne());
		list.add(new _if_icmplt());
		list.add(new _if_icmpge());
		list.add(new _if_icmpgt());
		list.add(new _if_icmple());
		list.add(new _if_acmpeq());
		list.add(new _if_acmpne());
		
		list.add(new _goto());
		list.add(new _jsr());
		
		list.add(new _ret());
		
		list.add(new _tableswitch());
		list.add(new _lookupswitch());
		
		list.add(new _ireturn());
		list.add(new _lreturn());
		list.add(new _freturn());
		list.add(new _dreturn());
		list.add(new _areturn());
		list.add(new _return());
		
		list.add(new _getstatic());
		list.add(new _putstatic());
		list.add(new _getfield());
		list.add(new _putfield());
		
		list.add(new _invokevirtual());
		list.add(new _invokespecial());
		list.add(new _invokestatic());
		list.add(new _invokeinterface());
		
		list.add(new _xxxunusedxxx());
		
		list.add(new _new());
		list.add(new _newarray());
		list.add(new _anewarray());
		
		list.add(new _arraylength());
		
		list.add(new _athrow());
		
		list.add(new _checkcast());
		list.add(new _instanceof());
		
		list.add(new _monitorenter());
		list.add(new _monitorexit());
		
		list.add(new _multianewarray());
		
		list.add(new _ifnull());
		list.add(new _ifnonnull());
		
		list.add(new _goto_w());
		list.add(new _jsr_w());
		
		list.add(new _breakpoint());
		
		list.add(new _ldc_quick());
		list.add(new _ldc_w_quick());
		list.add(new _ldc2_w_quick());
		
		list.add(new _getfield_quick());
		list.add(new _putfield_quick());
		list.add(new _getfield2_quick());
		list.add(new _putfield2_quick());
		list.add(new _getstatic_quick());
		list.add(new _putstatic_quick());
		list.add(new _getstatic2_quick());
		list.add(new _putstatic2_quick());
		
		list.add(new _invokevirtual_quick());
		list.add(new _invokenonvirtual_quick());
		list.add(new _invokesuper_quick());
		list.add(new _invokestatic_quick());
		list.add(new _invokeinterface_quick());
		list.add(new _invokevirtualobject_quick());
		
		list.add(new _new_quick());
		list.add(new _anewarray_quick());
		list.add(new _multianewarray_quick());
		
		list.add(new _checkcast_quick());
		list.add(new _instanceof_quick());
		
		list.add(new _invokevirtual_quick_w());
		
		list.add(new _getfield_quick_w());
		list.add(new _putfield_quick_w());
		
		list.add(new _impdep1());
		list.add(new _impdep2());
		// wide instructions
		list.add(new _wide(new _iload()));
		list.add(new _wide(new _fload()));
		list.add(new _wide(new _aload()));
		list.add(new _wide(new _lload()));
		list.add(new _wide(new _dload()));
		
		list.add(new _wide(new _istore()));
		list.add(new _wide(new _fstore()));
		list.add(new _wide(new _astore()));
		list.add(new _wide(new _lstore()));
		list.add(new _wide(new _dstore()));
		
		list.add(new _wide(new _ret()));

		list.add(new _wide(new _iinc()));

		return list;
	}

}

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

import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.DoubleInfo;
import net.sf.rej.java.constantpool.FloatInfo;
import net.sf.rej.java.constantpool.IntegerInfo;
import net.sf.rej.java.constantpool.LongInfo;
import net.sf.rej.java.constantpool.RefInfo;
import net.sf.rej.java.constantpool.StringInfo;
import net.sf.rej.java.instruction.Instruction;
import net.sf.rej.java.instruction.Label;
import net.sf.rej.java.instruction.Parameters;

public class InstructionCopier {
	public Instruction copyInstruction(Instruction inst, ConstantPool sourcePool, ConstantPool destinationPool) {
		Instruction copy = InstructionSet.getInstance().getInstruction(inst.getOpcode());
		Parameters params = inst.getParameters();
		Parameters copyParams = new Parameters();
		for(int i=0; i < params.getCount(); i++) {
        	copyParams.addParam(params.getType(i));
            switch (params.getType(i)) {
            case TYPE_ARRAYTYPE:
            	copyParams.addValue(params.getObject(i));
            	break;
            case TYPE_LOCAL_VARIABLE:
            case TYPE_LOCAL_VARIABLE_READONLY:
            case TYPE_LOCAL_VARIABLE_WIDE:
            	// TODO: should be done differently?
            	copyParams.addValue(params.getObject(i));
            	break;
            case TYPE_CONSTANT_WIDE:
            case TYPE_CONSTANT_READONLY:
            case TYPE_CONSTANT:
            	copyParams.addValue(params.getObject(i));
            	break;
            case TYPE_CONSTANT_POOL_CLASS: {
            	ClassInfo ci = (ClassInfo) sourcePool.get(params.getInt(i));
                int index = destinationPool.optionalAddClassRef(ci.getName());
                copyParams.addValue(index);
                break;
            }
            case TYPE_CONSTANT_POOL_CONSTANT: {
            	ConstantPoolInfo cpi = sourcePool.get(params.getInt(i));
            	switch (cpi.getType()) {
            	case ConstantPoolInfo.DOUBLE: {
            		DoubleInfo di = (DoubleInfo)cpi;
            		DoubleInfo diCopy = new DoubleInfo(di.getHighBytes(), di.getLowBytes(), destinationPool);
            		int index = destinationPool.optionalAdd(diCopy);
            		copyParams.addValue(index);
            		break;
            	}
            	case ConstantPoolInfo.FLOAT: {
            		FloatInfo fi = (FloatInfo)cpi;
            		FloatInfo fiCopy = new FloatInfo(fi.getBytes(), destinationPool);
            		int index = destinationPool.optionalAdd(fiCopy);
            		copyParams.addValue(index);
            		break;		            		
            	}
            	case ConstantPoolInfo.INTEGER: {
            		IntegerInfo ii = (IntegerInfo)cpi;
            		IntegerInfo iiCopy = new IntegerInfo(ii.getIntValue(), destinationPool);
            		int index = destinationPool.optionalAdd(iiCopy);
            		copyParams.addValue(index);
            		break;		            				            		
            	}
            	case ConstantPoolInfo.LONG: {
            		LongInfo li = (LongInfo)cpi;
            		LongInfo liCopy = new LongInfo(li.getLongValue(), destinationPool);
            		int index = destinationPool.optionalAdd(liCopy);
            		copyParams.addValue(index);
            		break;		            			
            	}
            	case ConstantPoolInfo.STRING: {
            		StringInfo si = (StringInfo)cpi;
            		int index = destinationPool.optionalAddString(si.getString());
            		copyParams.addValue(index);
            		break;		            				            		
            		
            	}
            	}
            	
            	break;
            }
            case TYPE_CONSTANT_POOL_FIELD_REF: {
            	RefInfo ri = (RefInfo) sourcePool.get(params.getInt(i));
            	int index = destinationPool.optionalAddFieldRef(ri.getClassName(), ri.getTargetName(), ri.getMethodType());
        		copyParams.addValue(index);
            	break;
            }
            case TYPE_CONSTANT_POOL_METHOD_REF: {
            	RefInfo ri = (RefInfo) sourcePool.get(params.getInt(i));
            	int index = destinationPool.optionalAddMethodRef(ri.getClassName(), ri.getTargetName(), ri.getMethodType());
        		copyParams.addValue(index);
            	break;
            }
            case TYPE_LABEL:
            	Label label = (Label) params.getObject(i);
            	copyParams.addValue(new Label(label.getPosition()));
            	break;
            case TYPE_SWITCH:
            	// TODO: Switch
            	break;
            }
		}
		
		copy.setParameters(copyParams);
		return copy;
	}
}

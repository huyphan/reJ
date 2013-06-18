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

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.Descriptor;
import net.sf.rej.java.LocalVariable;
import net.sf.rej.java.constantpool.ClassInfo;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.RefInfo;

public class Parameters {

    private List<ParameterType> types = new ArrayList<ParameterType>();
    private List<Object> values = new ArrayList<Object>();

    private boolean readOnly = false;

    public static final Parameters EMPTY_PARAMS = new Parameters(true);

    public Parameters() {
        // do-nothing constructor
    }

    private Parameters(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Parameters(ParameterType[] types) {
        for (int i = 0; i < types.length; i++) {
            addParam(types[i]);
        }
    }

    public int getCount() {
        return this.types.size();
    }

    public void addParam(ParameterType type) {
        if (this.readOnly)
            throw new RuntimeException("Modification not allowed.");
        this.types.add(type);
    }

    public void addValue(int i) {
        addValue(Integer.valueOf(i));
    }

    public void addValue(Object o) {
        if (this.readOnly)
            throw new RuntimeException("Modification not allowed.");

        if (this.values.size() + 1 > this.types.size())
            throw new RuntimeException(
                    "Parameter type/value mismatch; # of types="
                            + this.types.size() + " trying to insert value #"
                            + this.values.size() + 1);
        this.values.add(o);
    }

    public int getInt(int pos) {
        return ((Integer) this.values.get(pos)).intValue();
    }

    public ParameterType getType(int i) {
        return this.types.get(i);
    }

    public Object getObject(int i) {
        return this.values.get(i);
    }

    public void setValue(int i, Object object) {
        if (i >= this.types.size())
            throw new RuntimeException(
                    "Parameter type/value mismatch; # of types="
                            + this.types.size() + " inserting value #" + i
                            + " = " + object);
        this.values.set(i, object);
    }

    public String getString(DecompilationContext dc) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.values.size(); i++) {
            if (i > 0)
                sb.append(" ");
            switch (getType(i)) {
            case TYPE_ARRAYTYPE: {
                break;
            }
            case TYPE_LOCAL_VARIABLE:
            case TYPE_LOCAL_VARIABLE_READONLY:
            case TYPE_LOCAL_VARIABLE_WIDE: {
                LocalVariable lv = null;
                if (dc.getLocalVariableTable() != null)
                    lv = dc.getLocalVariableTable().getLocalVariable(getInt(i),
                            dc.getPosition());
                if (lv == null) {
                    sb.append(getInt(i));
                } else {
                    sb.append(lv.getName());
                }
                break;
            }
            case TYPE_CONSTANT_WIDE:
            case TYPE_CONSTANT_READONLY:
            case TYPE_CONSTANT: {
                sb.append(getInt(i));
                break;
            }
            case TYPE_CONSTANT_POOL_CLASS: {
                ConstantPoolInfo cpi = dc.getConstantPool().get(getInt(i));
                ClassInfo ci = (ClassInfo) cpi;
                sb.append(ci.getName());
                break;
            }
            case TYPE_CONSTANT_POOL_CONSTANT: {
                ConstantPoolInfo cpi = dc.getConstantPool().get(getInt(i));
                sb.append(cpi.getValue());
                break;
            }
            case TYPE_CONSTANT_POOL_FIELD_REF: {
                ConstantPoolInfo cpi = dc.getConstantPool().get(getInt(i));
                RefInfo ri = (RefInfo) cpi;
                Descriptor desc = ri.getDescriptor();
                sb.append(desc.getReturn() + " " + ri.getClassName() + "."
                        + ri.getTargetName());
                break;
            }
            case TYPE_CONSTANT_POOL_METHOD_REF: {
                ConstantPoolInfo cpi = dc.getConstantPool().get(getInt(i));
                RefInfo ri = (RefInfo) cpi;
                Descriptor desc = ri.getDescriptor();
                sb.append(desc.getReturn() + " " + ri.getClassName() + "."
                        + ri.getTargetName() + "(" + desc.getParams() + ")");
                break;
            }
            // nothing really searchable here.
            case TYPE_LABEL:
            case TYPE_SWITCH:
            }
        }

        return sb.toString();
    }

    @Override
	public int hashCode() {
        int hashCode = 0;
        for (int i=0; i < this.values.size(); i++) {
             hashCode *= 37;
             hashCode += getInt(i);
        }

        return hashCode;
    }

    @Override
	public boolean equals(Object o) {
        if (!(o instanceof Parameters)) {
            return false;
        }

        Parameters op = (Parameters) o;
        if (op.types.size() != this.types.size()) {
            return false;
        }

        for (int i=0; i < this.types.size(); i++) {
            if (this.getType(i) != op.getType(i)) {
                return false;
            }

            if (!this.getObject(i).equals(op.getObject(i))){
                return false;
            }
        }

        return true;
    }

    @Override
	public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.values.size(); i++) {
            if (i > 0)
                sb.append(",");
            switch (getType(i)) {
            case TYPE_ARRAYTYPE: {
                sb.append("ArrayType=" + getInt(i));
                break;
            }
            case TYPE_LOCAL_VARIABLE:
            case TYPE_LOCAL_VARIABLE_READONLY:
            case TYPE_LOCAL_VARIABLE_WIDE:
                sb.append("Local Variable=" + getInt(i));
                break;
            case TYPE_CONSTANT_WIDE:
            case TYPE_CONSTANT_READONLY:
            case TYPE_CONSTANT: {
                sb.append("Constant=" + getInt(i));
                break;
            }
            case TYPE_CONSTANT_POOL_CLASS: {
                sb.append("Class ref=" + getInt(i));
                break;
            }
            case TYPE_CONSTANT_POOL_CONSTANT: {
                sb.append("Pool constant=" + getInt(i));
                break;
            }
            case TYPE_CONSTANT_POOL_FIELD_REF: {
                sb.append("Field ref=" + getInt(i));
                break;
            }
            case TYPE_CONSTANT_POOL_METHOD_REF: {
                sb.append("Method ref=" + getInt(i));
                break;
            }
            // nothing really searchable here.
            case TYPE_LABEL:
            case TYPE_SWITCH:
            }
        }

        return sb.toString();

    }

}
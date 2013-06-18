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
package net.sf.rej.java.constantpool;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.rej.util.ByteSerializer;
import net.sf.rej.util.Range;

// TODO: write down the contract of this class, how it behaves in various situations; identical values, null values, etc

public class ConstantPool implements Iterable<ConstantPoolInfo> {

    private List<ConstantPoolInfo> cpi;

    public ConstantPool() {
        this.cpi = new ArrayList<ConstantPoolInfo>();
        this.cpi.add(null);
        
    }

    public void init(int size) {
        this.cpi = new ArrayList<ConstantPoolInfo>(size);
        for(int i=0; i < size; i++) {
            this.cpi.add(null);
        }
    }

    public ConstantPoolInfo get(int i) {
        return this.cpi.get(i);
    }

    public void set(int i, ConstantPoolInfo info) {
        this.cpi.set(i, info);
    }

    public int size() {
        return this.cpi.size();
    }
    
    public int indexOf(ConstantPoolInfo item) {
    	return this.cpi.indexOf(item);
    }

    public int optionalAdd(ConstantPoolInfo item) {
        int i = this.cpi.indexOf(item);
        if (i != -1) {
            return i;
        }

        return forceAdd(item);
    }

    public int forceAdd(ConstantPoolInfo item) {
        this.cpi.add(item);
        if (item.getType() == ConstantPoolInfo.DOUBLE || item.getType() == ConstantPoolInfo.LONG) {
        	this.cpi.add(null);
        	return size() - 2;
        }
        return size() - 1;
    }


    public int optionalAddFieldRef(String className, String fieldName, String type) {
        int index = indexOfFieldRef(className, fieldName, type);
        if (index != -1) {
            return index;
        }

        // create new
        int classIndex = optionalAddClassRef(className);
        int nameAndTypeIndex = optionalAddNameAndTypeRef(fieldName, type);
        RefInfo ref = new RefInfo(ConstantPoolInfo.FIELD_REF, classIndex,
                nameAndTypeIndex, this);
        return optionalAdd(ref);
    }

    public int optionalAddMethodRef(String className, String methodName, String type) {
        int index = indexOfMethodRef(className, methodName, type);
        if( index != -1) {
            return index;
        }

        // create new
        int classIndex = optionalAddClassRef(className);
        int nameAndTypeIndex = optionalAddNameAndTypeRef(methodName, type);
        RefInfo ref = new RefInfo(ConstantPoolInfo.METHOD_REF, classIndex,
                nameAndTypeIndex, this);
        return forceAdd(ref);
    }

    public int optionalAddNameAndTypeRef(String methodName, String type) {
        int index = indexOfNameAndTypeRef(methodName, type);
        if( index != -1) {
            return index;
        }

        // create new
        int nameIndex = optionalAddUtf8(methodName);
        int descriptorIndex = optionalAddUtf8(type);
        NameAndTypeInfo info = new NameAndTypeInfo(nameIndex, descriptorIndex,
                this);
        return optionalAdd(info);
    }

    public int optionalAddClassRef(String className) {
        int index = indexOfClassRef(className);
        if(index != -1) {
            return index;
        }

        // create new
        int nameIndex = optionalAddUtf8(className.replace('.', '/')); // TODO: verify that this going back and forth between slash(/) and dot(.) actually works and is sensible
        ClassInfo ci = new ClassInfo(nameIndex, this);
        return optionalAdd(ci);
    }

    public int forceAddClassRef(String className) {
        int nameIndex = optionalAddUtf8(className.replace('.', '/')); // TODO: verify that this going back and forth between slash(/) and dot(.) actually works and is sensible
        ClassInfo ci = new ClassInfo(nameIndex, this);
        return forceAdd(ci);

    }


    public int optionalAddUtf8(String text) {
        int index = indexOfUtf8(text);
        if(index != -1) {
            return index;
        }

        // create new
        return optionalAdd(new UTF8Info(text, this));
    }

    public int optionalAddString(String text) {
        for (int i = 0; i < size(); i++) {
            if (get(i) == null) {
                continue;
            }
            if (get(i).getType() == ConstantPoolInfo.STRING) {
                StringInfo si = (StringInfo) get(i);
                if (text.equals(si.getString())) {
                    return i;
                }
            }
        }

        // create new
        int utf8 = optionalAddUtf8(text);
        return optionalAdd(new StringInfo(utf8, this));
    }

	public void removeLast(int index) {
		assert this.cpi.size()-1 == index : "Index mismatch, only the last item of the constant pool may be removed.";
		removeLast();
	}

	public void removeLast() {
        this.cpi.remove(this.cpi.size()-1);
    }

    public int indexOfClassRef(String className) {
        for (int i = 0; i < this.size(); i++) {
            if (get(i) == null)
                continue;
            if (get(i).getType() == ConstantPoolInfo.CLASS) {
                ClassInfo ci = (ClassInfo) get(i);
                if (className.equals(ci.getName())) {
                    return i;
                }
            }
        }

        return -1;
    }

    public int indexOfNameAndTypeRef(String methodName, String type) {
        for (int i = 0; i < size(); i++) {
            if (get(i) == null)
                continue;
            if (get(i).getType() == ConstantPoolInfo.NAME_AND_TYPE) {
                NameAndTypeInfo info = (NameAndTypeInfo) get(i);
                if (methodName.equals(info.getName())
                        && type.equals(info.getDescriptorString())) {
                    return i;
                }
            }
        }

        return -1;
    }

    public int indexOfFieldRef(String className, String fieldName, String type) {
        for (int i = 0; i < size(); i++) {
            if (get(i) == null)
                continue;
            if (get(i).getType() == ConstantPoolInfo.FIELD_REF) {
                RefInfo ref = (RefInfo) get(i);
                if (className.equals(ref.getClassName())
                        && fieldName.equals(ref.getTargetName())
                        && type.equals(ref.getMethodType())) {
                    return i;
                }
            }
        }

        return -1;
    }


    public int indexOfMethodRef(String className, String methodName, String type) {
        for (int i = 0; i < size(); i++) {
            if (get(i) == null)
                continue;
            if (get(i).getType() == ConstantPoolInfo.METHOD_REF) {
                RefInfo ref = (RefInfo) get(i);
                if (className.equals(ref.getClassName())
                        && methodName.equals(ref.getTargetName())
                        && type.equals(ref.getMethodType())) {
                    return i;
                }
            }
        }

        return -1;
    }

    public int indexOfUtf8(String text) {
        for (int i = 0; i < size(); i++) {
            if (get(i) == null) {
                continue;
            }

            if (get(i).getType() == ConstantPoolInfo.UTF8) {
                UTF8Info utf8 = (UTF8Info) get(i);
                if (text.equals(utf8.getValue())) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Method for debugging, dumps the contents of the constant
     * pool to the given PrintStream.
     * @param out
     */
	public void dump(PrintStream out) {
		for (int i=0; i < this.cpi.size(); i++) {
			out.println(i + ": " + this.cpi.get(i));
		}
	}

	public Iterator<ConstantPoolInfo> iterator() {
		return new Iterator<ConstantPoolInfo>() {
			int index = 0;
			public boolean hasNext() {
				return index < size();
			}

			public ConstantPoolInfo next() {
				return get(index++);
			}

			public void remove() {
				throw new UnsupportedOperationException(ConstantPool.class.getName() + " does not support remove.");				
			}
			
		};
	}

	/**
	 * Removes items from the end of the constant pool until there are
	 * only size items left.
	 * @param size new size for the constantpool, must be smaller than
	 * the current size.
	 */
	public void shrinkToSize(int size) {
		assert(size < this.size()) : "Size can't be bigger than current size";
		
		while (size < this.size()) {
			removeLast();
		}
	}

	/**
	 * Serializes this constant pool into a byte array. The serialization format
	 * is according to the java class file format.
	 * @return this constant pool as a byte array.
	 */
	public byte[] getData() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(size());

        for (int i = 1; i < size(); i++) {
            ConstantPoolInfo cpi = get(i);
            ser.addBytes(cpi.getData());
            
            // 2 word(8 byte) types take up 2 indices
            if (cpi.getType() == ConstantPoolInfo.LONG
           		|| cpi.getType() == ConstantPoolInfo.DOUBLE) {
            	i++;
            }
        }
        
        return ser.getBytes();
	}

    /**
     * Returns a map of offsets of each significant element of this method.
     * The offsets returned by this method are only valid until this
     * object is modified. The keys in the map are
     * of type <code>OffsetTag</code>, <code>Attribute</code>. 
     * 
     * @param initialOffset an offset to be added to each of the offsets in the map.
     * @return a map of element offsets in class file data.
     */
    public Map<Object, Range> getOffsetMap(int initialOffset) {
    	Map<Object, Range> map = new HashMap<Object, Range>();
    	int offset = initialOffset;
    	
    	offset += 2; // size
    	
        for (int i = 1; i < size(); i++) {
            ConstantPoolInfo cpi = get(i);
            int length = cpi.getData().length;
            map.put(cpi, new Range(offset, length));
            offset += length;
            
            // 2 word(8 byte) types take up 2 indices
            if (cpi.getType() == ConstantPoolInfo.LONG
           		|| cpi.getType() == ConstantPoolInfo.DOUBLE) {
            	i++;
            }
        }

    	return map;
    }

}
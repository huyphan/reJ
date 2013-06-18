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
package net.sf.rej.files;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.sf.rej.gui.UndoManager;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.ClassParsingException;
import net.sf.rej.java.Disassembler;
/**
 * <code>Project</code> class is for modeling a project - that is, a file or a set of files open at a time.
 * @author Sami Koivu
 */
public class Project implements Modifications {

    // class file specfic undo managers
    private Map<String, UndoManager> undoManagers = new HashMap<String, UndoManager>();
    // project level undo manager
    private UndoManager undoManager = new UndoManager();

    private Set<String> modified = new LinkedHashSet<String>();

    private Map<String, Object> cache = new HashMap<String, Object>();
    private File file = null;
    private FileSet fileset = null;

    public Project() {
        // do-nothing constructor
    }

    public void setFile(File f) {
        this.file = f;
    }

    public File getFile() {
        return this.file;
    }

    @Override
	public String toString() {
        return this.file.getName();
    }

    public void setFileSet(FileSet fileset) {
        this.fileset = fileset;
    }

    public boolean isArchive() {
        return this.fileset != null;
    }

    public FileSet getFileSet() {
        return this.fileset;
    }

    public UndoManager getUndoManager(String file) {
        UndoManager um = null;
        if (this.undoManagers.containsKey(file)) {
            um = this.undoManagers.get(file);
        } else {
            um = new UndoManager();
            this.undoManagers.put(file, um);
        }

        return um;
    }

    public void setClassFileMofidied(boolean modified, String filename, ClassFile cf) {
        if(modified) {
            // mark file as modified and store the classfile
            this.modified.add(filename);
            this.cache.put(filename, cf);
        } else {
            // mark file as not modified and store a soft reference of the classfile
            // this is memory sensitive and will be released by the JVM is memory runs low
            this.modified.remove(filename);
            this.cache.put(filename, new SoftReference<ClassFile>(cf));
        }
    }

    public boolean isModified() {
        return this.modified.size() > 0;
    }

    public boolean isModified(String filename) {
        return this.modified.contains(filename);
    }

    public ClassFile getClassFile(String filename) throws IOException, ClassParsingException {
        if (this.cache.containsKey(filename)) {
            // file is already open and in the cache
            Object value = this.cache.get(filename);
            if(value instanceof ClassFile) {
                return (ClassFile)value;
            } else {
                ClassFile cf = (ClassFile)((SoftReference)value).get();
                if(cf != null) {
                    return cf;
                } // otherwise just load it from the i/o again
            }
        }

        byte[] data = this.fileset.getData(filename);
        try {
            ClassFile cf = Disassembler.readClass(data);
            this.cache.put(filename, new SoftReference<ClassFile>(cf));
            return cf;
        } catch (Exception e) {
            throw new ClassParsingException("Error parsing class file " + this.file + " : " + e.getMessage(), e);
        }
    }

    public void save() throws Exception {
        this.fileset.save(this);
        this.modified.clear();
    }

    public void saveAs(File file) throws Exception {
        this.fileset.saveAs(file, this);
        this.modified.clear();
        this.file = file;
    }

    public UndoManager getProjectUndoManager() {
        return this.undoManager;
    }

    public void removeFile(String file) {
        this.fileset.removeFile(file);
        this.modified.add(file);
    }

    public void addFile(String file) {
        this.fileset.addFile(file);
        this.modified.add(file);
    }

    public void clearCache() {
        this.cache.clear();
    }

    public byte[] getData(String filename) throws IOException {
        try {
            return getClassFile(filename).getData();
        } catch(ClassParsingException cpe) {
            cpe.printStackTrace();
            throw new IOException("Class file parsing failed where no parsing was supposed to happen; " + cpe.getMessage());
        }
    }

    public void addFile(String file, ClassFile cf) {
        addFile(file);
        this.cache.put(file, cf);
    }

}

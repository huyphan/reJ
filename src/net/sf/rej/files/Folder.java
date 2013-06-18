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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.rej.util.FileToolkit;
import net.sf.rej.util.IOToolkit;

public class Folder extends FileSet {

    private File f;

    private List<String> contents = null;

    public Folder(File f) throws IOException {
        this.f = f;
        if (!f.exists()) {
            throw new FileNotFoundException(f.getName());
        }

        refresh();
    }

    private Folder() {
        // empty constructor
    }

    @Override
    public long getLength(String file) {
        return getTarget(file).length();
    }

    public File getTarget(String file) {
        return new File(this.f, file);
    }

    @Override
    public InputStream getInputStream(String file) {
        try {
            return new FileInputStream(getTarget(file));
        } catch (Exception e) {
            // existence of file is verified in the constructor so this should
            // be unlikely
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return this.f.getName();
    }

    @Override
    public byte[] getData(String file) throws IOException {
        return FileToolkit.readBytes(getTarget(file));
    }

    @Override
    public List<String> getContentsList() {
    	List<String> contents = new ArrayList<String>();
    	contents.addAll(this.contents);
        return contents;
    }

    public static void addTraversal(String base, File file, List<String> al) {
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                al.add(base + "/" + files[i].getName());
            } else {
                if (base.length() == 0)
                    addTraversal(files[i].getName(), files[i], al);
                else
                    addTraversal(base + "/" + files[i].getName(), files[i], al);
            }
        }
    }

    public void startSerialization() {
        // empty method
    }

    public void write(String filename, byte[] data) throws IOException {
        write(getTarget(filename), data);
    }

    public void write(File file, byte[] data) throws IOException {
        FileToolkit.writeBytes(file, data);
    }


    public void finishSerialization() {
        // empty method
    }

    public void write(String filename, InputStream is) throws IOException {
        write(getTarget(filename), is);
    }

    public void write(File file, InputStream is) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        IOToolkit.writeStream(is, fos);
        fos.flush();
        fos.close();
    }

    @Override
    public void close() {
        // empty method
    }

    @Override
    public void removeFile(String filename) {
        this.contents.remove(filename);
    }

    @Override
    public void addFile(String filename) {
        getTarget(filename).getParentFile().mkdirs();
        this.contents.add(filename);
    }

    /*
     * @see net.sourceforge.rejava.gui.FileSet#refresh()
     */
    @Override
    public void refresh() {
        this.contents = new ArrayList<String>();
        addTraversal("", this.f, this.contents);
    }

    @Override
    public void save(Modifications mods) throws IOException {
        for (int i = 0; i < this.contents.size(); i++) {
            String filename = this.contents.get(i);
            if (mods.isModified(filename)) {
                byte[] data = mods.getData(filename);
                write(filename, data);
            }
        }
    }

    @Override
    public void saveAs(File file, Modifications mods) throws IOException {
        if (!file.exists()) {
            boolean ok = file.mkdirs();
            if (!ok) {
                throw new IOException("Could not create folder");
            }
        }

        for (int i = 0; i < this.contents.size(); i++) {
            String filename = this.contents.get(i);
            if (mods.isModified(filename)) {
                byte[] data = mods.getData(filename);
                write(new File(file, filename), data);
            } else {
                write(new File(file, filename), this.getInputStream(filename));
            }
        }

        this.f = file;
    }

    public void partialSave(String filename, byte[] data) throws IOException {
    	File target = getTarget(filename);
    	target.getParentFile().mkdirs();
        write(target, data);
    }

	@Override
	public void removeAllFiles() throws IOException {
		FileToolkit.deleteRecursively(this.f);
		this.f.mkdir();
		this.contents.clear();
	}

	public static Folder createNew(File file) throws IOException {
        Folder folder = new Folder();
        folder.f = file;
        folder.contents = new ArrayList<String>();
        file.mkdirs();
        return folder;
    }

	@Override
	public String getClasspath(String mainClass) {
		// This only works of the folder is the the root folder of the classes
		return this.f.getPath();
	}

}
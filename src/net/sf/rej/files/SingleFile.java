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

public class SingleFile extends FileSet {

    private File f;
    private List<InputStream> streams = new ArrayList<InputStream>();

    public SingleFile(File f) throws FileNotFoundException {
        if (!f.exists())throw new FileNotFoundException(f.getName());
        this.f = f;
    }

    private SingleFile() {
    	// for internal use of the class
        // do-nothing constructor
    }

    @Override
	public long getLength(String file) {
        return this.f.length();
    }

    @Override
	public InputStream getInputStream(String file) {
        try {
            FileInputStream fis = new FileInputStream(this.f);
            this.streams.add(fis); // keep a handle to the stream so we can
            // close it when FileSet.close() is called.
            return fis;
        } catch (IOException e) {
            // the constructor verifies the existence of file, so this should be unlikely
            throw new RuntimeException(e);
        }
    }

    @Override
	public String getName() {
        return this.f.getName();
    }

    @Override
	public byte[] getData(String file) throws IOException {
        return FileToolkit.readBytes(this.f);
    }

    @Override
	public List<String> getContentsList() {
        List<String> list = new ArrayList<String>();
        list.add(this.f.getName());
        return list;
    }

    public void write(byte[] data) throws IOException {
        FileToolkit.writeBytes(this.f, data);
    }

    public void write(InputStream is) throws IOException {
        FileOutputStream fos = new FileOutputStream(this.f);
        IOToolkit.writeStream(is, fos);
        fos.flush();
        fos.close();
    }

    /**
     * Call close for all FileInputStream objects returned by calls to
     * getInputStream(String filename)
     */
    @Override
	public void close() {
        for (InputStream is : this.streams) {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
	public void removeFile(String filename) {
        // can't be removed
    }

    @Override
	public void addFile(String filename) {
        // no files can be added
    }


    /*
     * @see net.sourceforge.rejava.gui.FileSet#refresh()
     */
    @Override
	public void refresh() {
        // no need to refresh
    }

    @Override
	public void save(Modifications ms) throws IOException {
        if (ms.isModified(this.getName())) {
            write(ms.getData(this.getName()));
        }
    }

    @Override
	public void saveAs(File file, Modifications ms) throws IOException {
        if (ms.isModified(this.getName())) {
            this.f = file;
            write(ms.getData(this.getName()));
        } else {
            byte[] data = getData(this.getName());
            this.f = file;
            write(data);
        }
    }

    public void partialSave(String filename, byte[] data) throws IOException {
        write(data);
    }

	@Override
	public void removeAllFiles() {
		// a type of SingleFile has no contents so nothing will be removed
	}

    public static SingleFile createNew(File file) throws IOException {
        SingleFile sf = new SingleFile();
        sf.f = file;
        File parent = file.getParentFile();
        if (!parent.exists()) {
        	parent.mkdirs();
        }
        file.createNewFile();
        return sf;
    }

	@Override
	public String getClasspath(String mainClass) {
		String[] packageElements = mainClass.split("\\.");
		File root = this.f;
		for (int i = 0; i < packageElements.length; i++) {
			root = root.getParentFile();
		}
		return root.getPath();
	}

}

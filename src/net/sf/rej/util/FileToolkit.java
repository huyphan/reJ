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
package net.sf.rej.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


/**
 * <code>FileToolkit</code> class contains file utility functions.
 *
 * @author Sami Koivu
 */
public class FileToolkit {

    private FileToolkit() {
        // class is not meant to be constructed
    }

    /**
     * Read the contents of File f into a String and return it.
     *
     * @param f Indicates the file to read.
     * @throws IOException - Exception while reading file
     * @return String Contents of File f
     */
    public static String readFile(File f) throws IOException {
        FileReader fr = new FileReader(f);
        int length = (int) f.length();
        int read = 0;
        char[] buf = new char[length];

        while (read < length) {
            int i = fr.read(buf, read, length - read);
            read += i;
        }
        fr.close();

        return new String(buf);
    }

    /**
     * Read the contents of the File into a byte array and return it.
     *
     * @param f
     *            File The File to read.
     * @throws IOException
     *             Exception while reading file
     * @return byte[] Contents of File f.
     */
    public static byte[] readBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        int length = (int) f.length();
        int read = 0;
        byte[] buf = new byte[length];

        while (read < length) {
            int i = fis.read(buf, read, length - read);
            read += i;
        }
        fis.close();

        return buf;
    }

    /**
     * Write bytes[] into File f. Overwriting the file if it exists.
     *
     * @param f
     *            File File to create/overwrite
     * @param bytes
     *            byte[] data to write into the file
     * @throws IOException
     *             Exception while writing file
     */
    public static void writeBytes(File f, byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(f, false);

        fos.write(bytes);
        fos.flush();
        fos.close();
    }

    /**
     * Deletes a directory and all of it's subdirectories.
     *
     * @param folder
     *            File file or directory to delete.
     * @throws IOException Exception while deleting
     */
    public static void deleteRecursively(File folder) throws IOException {
        if (!folder.exists())
            throw new IOException(folder.getPath() + " does not exist.");

        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                deleteRecursively(file);
            } else {
                boolean success = file.delete();
                if (!success)
                    throw new IOException("File " + file.getPath()
                            + " could not be deleted.");
            }
        }
        folder.delete();
    }

    public static File createNewFile(File path, String suffix) {
    	while (true) {
    		long time = System.currentTimeMillis();
    		File test = new File(path, time + suffix);
    		if (!test.exists()) {
    			return test;
    		}
    	}
    }

}

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
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.zip.CRC32;

import net.sf.rej.util.IOToolkit;

/**
 * <code>FileSet</code> objects model sets of files, such as a single file, a
 * folder (and subfolders) or an archive file. For implementing classes, note
 * that the path elements of the files contained within a fileset must be
 * delimited by a forward slahs '/' in order for some of the other
 * funcionalities, such as refactoring to work.
 * 
 * @author Sami Koivu
 */
public abstract class FileSet implements Serializable {

	public static final long serialVersionUID = 1;

	/**
	 * Get the contents of this file set, ie. the list of files that are
	 * included in this set.
	 * 
	 * @return List object contaning Strings representing filenames.
	 */
	public abstract List<String> getContentsList();

	/**
	 * Get the contents of file belonging in this set, identified by file.
	 * 
	 * @param file
	 *            String the file to get
	 * @return byte array with the contents of the file
	 * @throws IOException
	 *             Some of the internal I/O caused an exception
	 */
	public abstract byte[] getData(String file) throws IOException;

	/**
	 * Return the name of the set. Being the filename of the archive, folder or
	 * single file, etc.
	 * 
	 * @return Name of the set.
	 */
	public abstract String getName();

	/**
	 * Get an InputStream to contents of a file in this set, identified by file.
	 * Should be preferred ahead of getData(String) particularly with big files.
	 * 
	 * @param file
	 *            String the file to get
	 * @return InputStream to the contents of the file.
	 * @throws IOException
	 *             Some of the internal I/O caused an exception
	 */
	public abstract InputStream getInputStream(String file) throws IOException;

	/**
	 * Get the length of the file in this set identified by file.
	 * 
	 * @param file
	 *            String name of the file whose size is requested.
	 * @return long Size of the file
	 * @throws IOException
	 *             Underlying I/O caused an exception
	 */
	public abstract long getLength(String file) throws IOException;

	/**
	 * Close the FileSet. Free up resources. The contents of the FileSet cannot
	 * be accessed again after calling close().
	 * 
	 * @throws IOException
	 *             I/O problem closing the FileSet
	 */
	public abstract void close() throws IOException;

	/**
	 * Remove file denoted by filename from the set. The file is not actually
	 * removed, but could be just marked for removal in the next serialization.
	 * 
	 * @param filename
	 *            String file to remove
	 */
	public abstract void removeFile(String filename);

	/**
	 * Add a file to the set. File will be identified by filename.
	 * 
	 * @param filename
	 *            String file to add
	 */
	public abstract void addFile(String filename);

	// TODO: if a file exists already with the given name an exception should be
	// thrown.

	/**
	 * Refresh the FileSet, loading the contents from the filesystem
	 * 
	 * @throws IOException
	 *             I/O problem during refresh
	 */
	public abstract void refresh() throws IOException;

	/**
	 * A checksum of the fileset used to invalidate cached class indices for
	 * fast searching.
	 * 
	 * @return A checksum value.
	 * @throws IOException A problem in the I/O processing.
	 */
	public long getChecksum() throws IOException {
		List list = getContentsList();
		CRC32 crc = new CRC32();

		for (int i = 0; i < list.size(); i++) {
			String filename = (String) list.get(i);
			InputStream is = getInputStream(filename);
			IOToolkit.updateCRCWithStream(is, crc);
		}

		return crc.getValue();
	}

	public abstract void save(Modifications ms) throws IOException;

	public abstract void saveAs(File file, Modifications ms) throws IOException;
	
	public abstract void removeAllFiles() throws IOException;

	@Override
	public int hashCode() {
		return this.getClass().hashCode() * 37 + getName().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other != null && other.getClass() == this.getClass()) {
			FileSet otherFS = (FileSet) other;
			return otherFS.getName().equals(this.getName());
		}

		return false;
	}
	
	public void getContentsFrom(final FileSet fs) throws IOException {
		this.removeAllFiles();
		for (String contentFile : fs.getContentsList()) {
			this.addFile(contentFile);
		}
		
		this.save(new Modifications() {

			public byte[] getData(String filename) throws IOException {
				return fs.getData(filename);
			}

			public boolean isModified(String filename) {
				return true;
			}
			
		});
		
	}

	public abstract String getClasspath(String mainClass);
}
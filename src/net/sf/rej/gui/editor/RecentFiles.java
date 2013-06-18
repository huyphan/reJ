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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A set of functionalities for maintaing a recent files menu in the "File"
 * menu.
 * 
 * @author Sami Koivu
 */
public class RecentFiles {
	/*
	 * TODO: Rewrite this class to support more flexibility in terms of
     *       number of recent file items, and rethink the serialization
	 */
	private File file;

	private Properties properties = new Properties();

	private List<String> list = new ArrayList<String>();

	public RecentFiles(File file) {
		this.file = file;
	}

	public void load() throws IOException {
		FileInputStream fis = null;
		try {
			if (this.file.exists()) {
				fis = new FileInputStream(this.file);
				this.properties.load(fis);
			}
			this.list.clear();
			String file = this.properties.getProperty("recent.file.0");
			if (file != null) {
				this.list.add(file);
			}
			file = this.properties.getProperty("recent.file.1");
			if (file != null) {
				this.list.add(file);
			}
			file = this.properties.getProperty("recent.file.2");
			if (file != null) {
				this.list.add(file);
			}
			file = this.properties.getProperty("recent.file.3");
			if (file != null) {
				this.list.add(file);
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	public void serialize() throws IOException {
		FileOutputStream fos = new FileOutputStream(this.file);
		this.properties.store(fos, "Recent files list");
		fos.flush();
		fos.close();
	}

	public List<String> getList() {
		List<String> al = new ArrayList<String>();
		al.addAll(this.list);
		return al;
	}

	public void add(String file) throws IOException {
		if (this.list.contains(file)) {
			this.list.remove(file);
		}
		this.list.add(0, file);
		if (this.list.size() > 4) {
			this.list.remove(4);
		}
		this.properties.clear();
		for (int i = 0; i < 4; i++) {
			if (this.list.size() > i) {
				String f = this.list.get(i);
				this.properties.setProperty("recent.file." + i, f);
			}
		}

		serialize();
	}
}

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
package net.sf.rej.gui.preferences;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.rej.gui.tab.Tab;

public class Preferences {
	
    private File preferencesFile;
    
    private List<File> classPath = new ArrayList<File>();
    private Map<Tab, Boolean> tabStates = new HashMap<Tab, Boolean>();
    private Map<Settings, Object> settings = new HashMap<Settings, Object>();
    
    public Preferences() {
    	setDefaultSettings();
	}

    private void setDefaultSettings() {
    	this.settings.put(Settings.DISPLAY_EXTENDS_OBJECT, Boolean.FALSE);
    	this.settings.put(Settings.DISPLAY_GENERICS, Boolean.TRUE);
    	this.settings.put(Settings.DISPLAY_VARARGS, Boolean.TRUE);
	}

	@SuppressWarnings("unchecked")
	public void load() throws IOException, ClassNotFoundException {
        if (this.preferencesFile.exists()) {
            FileInputStream fis = new FileInputStream(this.preferencesFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
            	this.classPath = (List<File>)eofAwareReadObject(ois, "Classpath elements");
            	this.tabStates = (Map)eofAwareReadObject(ois, "Tab visibility states");
            	Map<Settings, Object> savedSettings = (Map)eofAwareReadObject(ois, "Settings");
            	this.settings.putAll(savedSettings);
            } finally {
            	ois.close();
            	fis.close();
            }
        }
    }
    
    private Object eofAwareReadObject(ObjectInputStream ois, String descriptiveName) throws IOException, ClassNotFoundException {
        try {
        	return ois.readObject();
        } catch(EOFException eof) {
        	throw new RuntimeException("EOF on preferences file, element: " + descriptiveName + ".", eof);
        }    	
    }

    public void setFile(File prefs) {
        this.preferencesFile = prefs;
    }

    public void save() throws IOException {
        FileOutputStream fos = new FileOutputStream(this.preferencesFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this.classPath);
        oos.writeObject(this.tabStates);
        oos.writeObject(this.settings);
        oos.close();
        fos.close();
    }
    
    public List<File> getClassPathList() {
    	return this.classPath;
    }
    
    public void setClassPathList(List<File> classPath) {
    	this.classPath = classPath;
    }
    
    public boolean isTabVisible(Tab tab) {
    	Boolean visible =  this.tabStates.get(tab);
    	return visible != null && visible.booleanValue();
    }
    
    public void setTabVisibility(Tab tab, boolean visible) {
    	this.tabStates.put(tab, visible);
    }
    
    public <T> T getSetting(Settings setting, Class<T> type) {
    	Object o = this.settings.get(setting);
    	return type.cast(o);
    }
    
    public boolean isSettingTrue(Settings setting) {
    	return getSetting(setting, Boolean.class).booleanValue();
    }

	public void setSetting(Settings setting, Boolean value) {
		this.settings.put(setting, value);
	}

	public void invertSetting(Settings setting) {
		boolean oldValue = getSetting(setting, Boolean.class);
		setSetting(setting, Boolean.valueOf(!oldValue));
	}

}

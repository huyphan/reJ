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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.sf.rej.java.ClassFile;
import net.sf.rej.java.Disassembler;
import net.sf.rej.util.FileToolkit;
import net.sf.rej.util.ParsingException;

/**
 * Manages relationships between fully qualified class names and the physical locations of
 * the classfiles they are defined in. Each set of files can be cached to
 * the disk, so that it is not necessary to recreate it at start-up.
 * 
 * @author Sami Koivu
 */
public class ClassIndex {
	
	private static final Logger logger = Logger.getLogger(ClassIndex.class.getName());
	
    private boolean upToDate = false;

    private List<FileSet> elements = new ArrayList<FileSet>();
    private HashMap<FileSet, List<ClassLocator>> indices = new HashMap<FileSet, List<ClassLocator>>();

    public ClassIndex() {
        // Do-nothing constructor
    }

    /**
     * Add a <code>FileSet</code> to the index and mark the index as being
     * not up to date. Nothing is loaded at this point. The <code>FileSet</code>
     * is simply queued for addition.
     * @param fs fileset to add to the index.
     */
    public void addElement(FileSet fs) {
        this.elements.add(fs);
        this.upToDate = false;
    }

    /**
     * Returns the <code>ClassFile<code> object for the class whose fully qualified name is
     * given as a parameter. Or <code>null</code> if no class is found.
     * @param name fully qualified name of the class to return.
     * @return <code>ClassFile</code> of the class name given as a parameter.
     * @throws IOException I/O Exception while reading/parsing the class.
     */
    public ClassFile getByFullName(String name) throws IOException {
        conditionalUpdate();
        ClassLocator cl = getLocator(name);
        if (cl != null) {
            byte[] data = cl.getFileSet().getData(cl.getFile());
            return Disassembler.readClass(data);
        }

        return null;
    }

    /**
     * Returns a <code>List</code> containing the <code>ClassLocator</code> of
     * all the classes in call the <code>FileSet</code>s of this index.
     * @return <code>java.util.List</code> containing <code>ClassLocator</code> objects.
     */
    public List<ClassLocator> getAll() {
        conditionalUpdate();
        List<ClassLocator> all = new ArrayList<ClassLocator>();
        for(int i=0; i < this.elements.size(); i++) {
            FileSet fs = this.elements.get(i);
            List<ClassLocator> locators = this.indices.get(fs);
            all.addAll(locators);
        }

        return all;
    }

    /**
     * Updates the index if it has been marked as not up to date.
     */
    public synchronized void conditionalUpdate() {
        if (!this.upToDate) {
            update();
        }
    }

    /**
     * Update the index, reading (from cache or disk) all the locators for
     * <code>FileSet</code>s for which there are no index entries yet.
     */
    public void update() {
        try {
            for(int i=0; i < this.elements.size(); i++) {
                FileSet fs = this.elements.get(i);
                if (!this.indices.containsKey(fs)) {
                	List<ClassLocator> locators = getCachedLocators(fs);
                	if (locators == null) {
                		locators = createIndexFor(fs);
                		saveLocatorsCache(fs, locators);
                	}
                    this.indices.put(fs, locators);
                }
            }

            this.upToDate = true;
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Try to find a .locatorcache file for the <code>FileSet</code> given
     * as a parameter. Then read and return the serialized <code>List</code>
     * or return <code>null</code> if it couldn't be located.
     * @param fs the <code>FileSet</code> for which the cached locators are requested.
     * @return A list of <code>ClassLocator</code> objects for the given <code>FilSet</code>.
     */
	private List<ClassLocator> getCachedLocators(FileSet fs) {
    	try {
    		long checksum = fs.getChecksum();
    		File[] files = new File(".").listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".locatorcache");
				}
    		});

    		for (int i=0; i < files.length; i++) {
    			try {
    				FileInputStream fis = new FileInputStream(files[i]);
    				ObjectInputStream ois = new ObjectInputStream(fis);
    				byte[] magic = new byte[4];
    				ois.read(magic);
    				if (!"RJLC".equals(new String(magic))) {
    					ois.close();
    					throw new RuntimeException("Bad magic.");
    				}
    				String cacheName = (String)ois.readObject();
    				long cacheChecksum = ois.readLong();
    				if (cacheChecksum == checksum && cacheName.equals(fs.getName())) {
						logger.fine("Read locators for " + cacheName + " from the cache.");
						@SuppressWarnings("unchecked")
    					List<ClassLocator> locators = (List<ClassLocator>)ois.readObject();
    					for (int j=0; j < locators.size(); j++) {
    						ClassLocator cl = locators.get(j);
    						cl.setFileSet(fs); // FileSet information does not get serialized
    					}
    					return locators; // early return
    				}
    				ois.close();
    			} catch (Exception e) {
    				System.out.println("Bad cache file: " + files[i].getAbsolutePath());
    				e.printStackTrace();
    			}
    		}
    	} catch(IOException e) {
    		System.out.println("Unable to read locator cache.");
    		e.printStackTrace();
    	}
    	return null;
    }

	/**
	 * Create a cache file for the given <code>FileSet</code> and the list of
	 * <code>ClassLocator</code> objects by writing the bytes
	 * 'R', 'J', 'L', 'C', followed by the serialized String containing the
	 * <code>FileSet</code> name, followed by a long value checksum of the <code>FileSet</code>
	 * and lastly followed by the serialized <code>List</code> of <code>ClassLocator</code>
	 * objects.
	 * @param fs the <code>FileSet</code> to save.
	 * @param locators the locators to save.
	 */
    private void saveLocatorsCache(FileSet fs, List<ClassLocator> locators) {
    	try {
    		long checksum = fs.getChecksum();
    		String name = fs.getName();
    		File file = FileToolkit.createNewFile(new File("."), ".locatorcache");
    		FileOutputStream fos = new FileOutputStream(file);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.write("RJLC".getBytes());
    		oos.writeObject(name);
    		oos.writeLong(checksum);
    		oos.writeObject(locators);
    		oos.flush();
    		oos.close();
    	} catch(IOException e) {
    		logger.warning("Unable to save locator cache.");
        	StringWriter sw = new StringWriter();
        	e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
    	}

    }

    /**
     * Create an index for the given <code>FileSet</code>.
     * @param fs set of files to index.
     * @return a <code>List</code> of <code>ClassLocator</code> objects.
     * @throws IOException I/O problem while processing the class files.
     */
    private List<ClassLocator> createIndexFor(FileSet fs) throws IOException {
        List<ClassLocator> locators = new ArrayList<ClassLocator>();
        List contents = fs.getContentsList();
        for(int i=0; i < contents.size(); i++) {
            String file = (String)contents.get(i);
            if (file.endsWith(".class")) {
                try {
                    InputStream is = fs.getInputStream(file);
                    String className = Disassembler.parseName(is);
                    is.close();
                    locators.add(new ClassLocator(className, fs, file));
                } catch(ParsingException pe) {
                    logger.warning("Error parsing fileset: " + fs.getName() + " file: " + file);
                	StringWriter sw = new StringWriter();
                	pe.printStackTrace(new PrintWriter(sw));
                    logger.warning(sw.toString());
                }
            }
        }

        return locators;
    }

    /**
     * Remove one fileset from the index
     * @param current
     */
    public void removeElement(FileSet current) {
        this.elements.remove(current);
        this.indices.remove(current);
    }

    /**
     * Return ClassLocator for the given full class name
     * @param className
     * @return ClassLocator An object for locating the class file and ClassFile object
     */
    public synchronized ClassLocator getLocator(String className) {
        for(int i=0; i < this.elements.size(); i++) {
            FileSet fs = this.elements.get(i);
            List locators = this.indices.get(fs);
            for (int j=0; j < locators.size(); j++) {
                ClassLocator cl = (ClassLocator)locators.get(j);
                if (cl.getFullName().equals(className)) {
                    return cl;
                }
            }
        }

        return null;
    }
    
    /**
     * Return a <code>List</code> of all the names classes in the given
     * <code>FileSet</code>.
     * @param fs a <code>FileSet</code> object for which the class names are requested.
     * @return a <code>List</code> of <code>String</object> objects.
     */
    public List<String> getClassNames(FileSet fs) {
    	List<ClassLocator> locators = this.indices.get(fs);
    	List<String> classNames = new ArrayList<String>(locators.size());
    	for (ClassLocator locator : locators) {
    		classNames.add(locator.getFullName());
    	}
    	
    	return classNames;
    }
    
    /**
     * Add a single locator to the index
     * @param fs <code>FileSet</code> where the file belongs to.
     * @param file name of file to add.
     * @throws IOException I/O problem while adding.
     */
    public void addLocator(FileSet fs, String file) throws IOException {
        if (file.endsWith(".class")) {
        	List<ClassLocator> locators = this.indices.get(fs);
            try {
                InputStream is = fs.getInputStream(file);
                String className = Disassembler.parseName(is);
                is.close();
                locators.add(new ClassLocator(className, fs, file));
            } catch(ParsingException pe) {
                logger.warning("Error parsing fileset: " + fs.getName() + " file: " + file);
            	StringWriter sw = new StringWriter();
            	pe.printStackTrace(new PrintWriter(sw));
                logger.warning(sw.toString());
            }
        }
    }
    
    /**
     * Remove a single locator from the index.
     * @param fs <code>FileSet</code> where the file belongs to.
     * @param file name of file to remove.
     */
    public void removeLocator(FileSet fs, String file) {
    	List<ClassLocator> locators = this.indices.get(fs);
    	for (ClassLocator locator : locators) {
    		if (locator.getFile().equals(file)) {
    			locators.remove(locator);
    			break;
    		}
    	}
    	
    }

}

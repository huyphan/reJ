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
package net.sf.rej.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sf.rej.ProjectIterator;
import net.sf.rej.files.Archive;
import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.files.FileSet;
import net.sf.rej.files.Folder;
import net.sf.rej.files.Project;
import net.sf.rej.files.SingleFile;
import net.sf.rej.gui.action.GroupAction;
import net.sf.rej.gui.action.MarkClassFileModifiedAction;
import net.sf.rej.gui.action.RemoveFileAction;
import net.sf.rej.gui.dialog.PreferencesDialog;
import net.sf.rej.gui.editor.RecentFiles;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.preferences.Preferences;
import net.sf.rej.gui.tab.Tab;
import net.sf.rej.java.ClassFactory;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.ClassParsingException;
import net.sf.rej.java.Disassembler;
import net.sf.rej.java.MethodFactory;

/**
 * Common entry point for ReJ GUI system level operations.
 * 
 * @author Sami Koivu
 */
public class SystemFacade implements EventObserver {

    private static SystemFacade instance = null;
    private RecentFiles recent = new RecentFiles(new File("rej.recentfiles"));
    private Preferences preferences = new Preferences();
    private PreferencesDialog preferencesDialog = new PreferencesDialog(MainWindow.getInstance());
    ClassIndex classIndex = new ClassIndex();
    private ClassFactory classFactory = new ClassFactory();
    private MethodFactory methodFactory = new MethodFactory();
    
    private Project project = null;
    private String openFile = null;
    private EventDispatcher dispatcher = null;

    private ProgressMonitor progressMonitor = new ProgressMonitor() {
        public void setProgress(int progressPct) {
            SystemFacade.this.setProgress(progressPct);
        }

        public void setProgressScope(int min, int max) {
            SystemFacade.this.setProgressScope(min, max);
        }

    };

    private SystemFacade() {
        init();
    }

    private void init() {
        try {
            this.recent.load();
            this.preferences.setFile(new File("rej.preferences"));
            this.preferences.load();
          
            // update class index
            List list = this.preferences.getClassPathList();
            for (int i = 0; i < list.size(); i++) {
                File file = (File) list.get(i);
                FileSet fs = getFileSet(file);
                this.classIndex.addElement(fs);
            }

            updateClassIndex();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static synchronized SystemFacade getInstance() {
        if (instance == null)
            instance = new SystemFacade();

        return instance;
    }

    public void showPreferencesDialog() {
    	try {
   			List<File> prevList = this.preferences.getClassPathList();
   			this.preferencesDialog.setClasspathList(prevList);
   			this.preferencesDialog.invoke();
   			List<File> curList = this.preferencesDialog.getClasspathList();
       		synchronized(this.preferences) {
    			this.preferences.setClassPathList(curList);
    			this.preferences.save();
    		}

    		List<File> newItems = new ArrayList<File>();
    		newItems.addAll(curList);
    		newItems.removeAll(prevList);

    		List<File> removedItems = new ArrayList<File>();
    		removedItems.addAll(prevList);
    		removedItems.removeAll(curList);

    		for (int i=0; i < removedItems.size(); i++) {
    			File file = removedItems.get(i);
    			FileSet fs = getFileSet(file);
    			this.classIndex.removeElement(fs);
    		}

    		for (int i=0; i < newItems.size(); i++){
    			File file = newItems.get(i);
    			FileSet fs = getFileSet(file);
    			this.classIndex.addElement(fs);
    		}

    		updateClassIndex();
    	} catch(Exception e) {
    		handleException(e);
    	}
    }

    /**
     * Exit application
     */
    public void exit() {
        if (this.project != null && this.project.isModified()) {
            int answer = JOptionPane.showOptionDialog(MainWindow.getInstance(),
                    "There are unsaved changes, quit without saving changes?",
                    "Unsaved changes.", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new String[] {
                            "Quit without saving", "Cancel" }, "Cancel");
            if (answer == 0) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    public void openFile(File f) {
        if (this.project != null) {
            this.classIndex.removeElement(this.project.getFileSet());
        }

        Project project = new Project();

        project.setFile(f);
        try {
            FileSet set = getFileSet(f);
            this.classIndex.addElement(set);
            updateClassIndex();
            project.setFileSet(set);

            openProject(project);
        } catch(Exception e) {
        	handleException(e);
        }
    }
    
    public void openProject(Project project) {
    	try {
    		if (this.project != null) {
    			Event closeEvent = new Event(EventType.PROJECT_UPDATE);
    			this.dispatcher.notifyObservers(closeEvent);
    		}
    		Event event = new Event(EventType.PROJECT_UPDATE);
    		event.setProject(project);
    		this.dispatcher.notifyObservers(event);

    		Event fileEvent = new Event(EventType.CLASS_OPEN);
            if (project.getFileSet() instanceof SingleFile) {
                fileEvent.setFile(project.getFile().getName());
                fileEvent.setClassFile(project.getClassFile(project.getFile().getName()));
            }
            this.dispatcher.notifyObservers(fileEvent);

            if (project.getFileSet() instanceof SingleFile) {
                MainWindow.getInstance().setTab(Tab.EDITOR);
            } else {
                MainWindow.getInstance().setTab(Tab.FILES);
            }

            setTitle();
            this.recent.add(project.getFile().getPath());
            MainWindow.getInstance().updateRecentFilesMenu();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void saveFile() {
        try {
            this.project.save();
            setTitle();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void saveFile(File file) {
        try {
            this.project.saveAs(file);
            setTitle();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setProgress(int i) {
        MainWindow.getInstance().setProgress(i);
    }

    public void setProgressScope(int min, int max) {
        MainWindow.getInstance().setProgressScope(min, max);
    }

    public void setStatus(final String status) {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    	        MainWindow.getInstance().setStatus(status);
    		}
    	});
    }

    public void handleException(Exception ex) {
        try {
        	MainWindow mw = MainWindow.getInstance();
        	if (mw != null) {
        		mw.setStatus("Last exception: " + ex.getMessage());
        		MainWindow.getInstance().showErrorMessage(
                    "Exception was thrown: " + ex.getMessage());
                ex.printStackTrace();
        	} else {
        		ex.printStackTrace();
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleExceptions(List errors) {
        try {
            Iterator iterator = errors.iterator();
            while (iterator.hasNext()) {
                Exception ex = (Exception) iterator.next();
                // TODO: cancel option to be able not view all errors
                MainWindow.getInstance().setStatus(
                        "Last exception: " + ex.getMessage());
                MainWindow.getInstance().showErrorMessage(
                        "Exception was thrown: " + ex.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileSet getFileSet(File f) throws IOException {
        if (f.getName().endsWith(".class")) {
            return new SingleFile(f);
        } else if (f.isDirectory()) {
            return new Folder(f);
        } else if (f.getName().endsWith(".zip") || f.getName().endsWith(".jar")) {
            return new Archive(f);
        } else {
            throw new RuntimeException("File of wrong type: " + f.getName());
        }
    }

    public void setTitle(String title) {
        MainWindow.getInstance().setTitle(title);
    }

    public void setTitle() {
        if (this.project.isModified()) {
            setTitle("reJ - *" + this.project.getFile().getPath());
        } else {
            setTitle("reJ - " + this.project.getFile().getPath());
        }
    }

    public void performAction(Undoable action) {
    	performAction(action, this.openFile);
    }

    /**
	 * Performs the action specified by <code>action</code> (by calling the
	 * execute() method) on the project file specified by
	 * <code>targetFile</code>. The action is added to the
	 * <code>UndoManager</code> of the target file and project modified status
	 * is set accordingly. An application-wide CLASS_UPDATE notification is
	 * dispatched.
	 * 
	 * @param action
	 *            <code>Undoable</code> that is to be performed.
	 * @param targetFile
	 *            the file on which the action is performed on.
	 */
    public void performAction(Undoable action, String targetFile) {
        try {
            UndoManager um = project.getUndoManager(targetFile);
            if (!this.project.isModified(targetFile)) {
            	// is the file in question is not modified yet, group the new action
            	// with an action that changes the status of the file.
                GroupAction ga = new GroupAction();
                ga.add(action);
            	ClassFile cf = getClassFile(targetFile);
                ga.add(new MarkClassFileModifiedAction(this.project, targetFile, cf));
                um.add(ga);
                ga.execute();
            } else {
            	um.add(action);
            	action.execute();
            }
            this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
            setTitle();
        } catch (Exception e) {
            handleException(e);
            throw new RuntimeException(e);
        }
    }

    public void performProjectAction(Undoable action) {
        try {
            UndoManager um = this.project.getProjectUndoManager();
            um.add(action);
            action.execute();
            Event event = new Event(EventType.PROJECT_UPDATE);
            event.setProject(this.project);
            this.dispatcher.notifyObservers(event);
            setTitle();
        } catch (Exception e) {
            handleException(e);
            throw new RuntimeException(e);
        }
    }

    public void performUndo(String targetFile) {
        try {
            UndoManager um = this.project.getUndoManager(targetFile);
            um.undo();
            this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
            setTitle();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void performRedo(String targetFile) {
        try {
            UndoManager um = this.project.getUndoManager(targetFile);
            um.redo();
            this.dispatcher.notifyObservers(new Event(EventType.CLASS_UPDATE));
            setTitle();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void performProjectUndo() {
        try {
            UndoManager um = this.project.getProjectUndoManager();
            um.undo();
            Event event = new Event(EventType.PROJECT_UPDATE);
            event.setProject(this.project);
            this.dispatcher.notifyObservers(event);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void performProjectRedo() {
        try {
            UndoManager um = this.project.getProjectUndoManager();
            um.redo();
            Event event = new Event(EventType.PROJECT_UPDATE);
            event.setProject(this.project);
            this.dispatcher.notifyObservers(event);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void performFind() {
        try {
            throw new RuntimeException("Find operation not implmented.");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void removeFile(List<String> files) {
        try {
            RemoveFileAction rfa = new RemoveFileAction(project, files);
            this.performProjectAction(rfa);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public List getRecentFiles() {
        return this.recent.getList();
    }

    public void goTo(Link link) {
        if (link.getFile() != null) {
        	if (!link.getFile().equals(this.openFile)) {
				Event event = new Event(EventType.CLASS_OPEN);
				try {
					ClassFile cf = this.project.getClassFile(link.getFile());
					event.setClassFile(cf);
					event.setFile(link.getFile());
					this.dispatcher.notifyObservers(event);
				} catch(Exception ex) {
					SystemFacade.getInstance().handleException(ex);
				}
        	}
        }
        MainWindow.getInstance().setTab(link.getTab());
        MainWindow.getInstance().getSelectedTab().goTo(link);
    }

    public void search(final IteratorAgent matcher) {
        ProjectIterator.iterate(project, matcher);
    }

    /**
     * Return the Default GUI progress monitor
     *
     * @return ProgressMonitor
     */
    public ProgressMonitor getProgressMonitor() {
        return this.progressMonitor;
    }

    /**
     * Return the ClassFile object of the selected file in the open project
     *
     * @param filename
     * @return ClassFile The parsed ClassFile object
     * @throws IOException
     *             I/O problem reading/parsing the ClassFile
     * @throws ClassParsingException
     *             Parsing Exception - the ClassFile was malformed
     */
    public ClassFile getClassFile(String filename) throws IOException,
            ClassParsingException {
        return this.project.getClassFile(filename);
    }

    public ClassFile getClassFile(ClassLocator classLocator)
            throws IOException, ClassParsingException {
        FileSet projectFileSet = this.project.getFileSet();
        if (projectFileSet.equals(classLocator.getFileSet())) {
            return getClassFile(classLocator.getFile());
        } else {
            // TODO: should these be cached as well? that is, ClassFile objects
            // of classes that are not in the open project(but are in the
            // classpath)
            try {
                byte[] data = classLocator.getFileSet().getData(
                        classLocator.getFile());
                ClassFile cf = Disassembler.readClass(data);
                return cf;
            } catch (Exception e) {
                throw new ClassParsingException("Error opening file "
                        + classLocator.dumpDetails(), e);
            }
        }
    }

    public ClassIndex getClassIndex() {
        return this.classIndex;
    }

    public void updateClassIndex() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                SystemFacade.this.classIndex.conditionalUpdate();
            }
        });
        thread.start();
    }

    public void createNewArchiveProject(File archiveFile) {
        if (archiveFile.exists()) {
            int answer = JOptionPane.showOptionDialog(MainWindow.getInstance(),
                    "File already exists. Overwrite?",
                    "File already exists.", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new String[] {
                            "Overwrite", "Cancel" }, "Cancel");
            if (answer != 0) {
                return; // EARLY RETURN
            }
        }
        
        try {
			Archive archive = Archive.createNew(archiveFile);
			Project project = new Project();
			project.setFile(archiveFile);
			project.setFileSet(archive);
			project.save();
			openProject(project);
		} catch (Exception e) {
			handleException(e);
		}
    }

    public void createNewClass(String fullClassName, String selectedFile) {
        // TODO: Creating a new class should be an undoable action?
        ClassFile cf = this.classFactory.createClass(fullClassName);
        this.project.addFile(selectedFile, cf);
        
        /* TODO: the file hasn't been saved into the set yet at this point
         * sor the following throws a NullPointerException
        try {
        	getClassIndex().addLocator(this.project.getFileSet(), selectedFile);
        } catch(IOException ioe) {
        	handleException(ioe);
        }*/
        
        Event event = new Event(EventType.PROJECT_UPDATE);
        event.setProject(this.project);
        this.dispatcher.notifyObservers(event);        
        
        event = new Event(EventType.CLASS_OPEN);
        event.setClassFile(cf);
        event.setFile(selectedFile);
        this.dispatcher.notifyObservers(event);
    }

    public MethodFactory getMethodFactory() {
        return this.methodFactory;
    }

	public void processEvent(Event event) {
		switch (event.getType()) {
		case INIT:
			this.dispatcher = event.getDispatcher();
			break;
		case PROJECT_UPDATE:
			this.project = event.getProject();
			break;
		case CLASS_OPEN:
			this.openFile = event.getFile();
			break;
		case CLASS_UPDATE:
		case CLASS_REPARSE:
		case CLASS_PARSE_ERROR:
		case DISPLAY_PARAMETER_UPDATE:
		case DEBUG_ATTACH:
		case DEBUG_DETACH:
		case DEBUG_RESUMED:
		case DEBUG_SUSPENDED:
		case DEBUG_THREAD_CHANGE_REQUESTED:
		case DEBUG_STEP_INTO_REQUESTED:
		case DEBUG_STEP_OUT_REQUESTED:
		case DEBUG_STEP_OVER_REQUESTED:
		case DEBUG_RESUME_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGE_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGED:
		case DEBUG_SUSPEND_REQUESTED:
		case DEBUG_THREAD_CHANGED:
			// do nothing
			break;
		}
	}
	
	public Preferences getPreferences() {
		return this.preferences;
	}

}

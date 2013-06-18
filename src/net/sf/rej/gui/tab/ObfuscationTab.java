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
package net.sf.rej.gui.tab;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.rej.AbstractIteratorAgent;
import net.sf.rej.ProjectIterator;
import net.sf.rej.files.FileSet;
import net.sf.rej.files.Folder;
import net.sf.rej.files.Project;
import net.sf.rej.gui.IterationContext;
import net.sf.rej.gui.IteratorAgent;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.editor.iteration.ClassNameLegalizingAdvisor;
import net.sf.rej.gui.editor.iteration.ClassNameObfuscatingAdvisor;
import net.sf.rej.gui.editor.iteration.FieldNameLegalizingAdvisor;
import net.sf.rej.gui.editor.iteration.IterationNotificationListener;
import net.sf.rej.gui.editor.iteration.MethodNameLegalizingAdvisor;
import net.sf.rej.gui.editor.iteration.RefactoringIterator;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.obfuscation.LineNumberStripper;
import net.sf.rej.obfuscation.LocalVarStripper;

public class ObfuscationTab extends JPanel implements Tabbable, EventObserver {
    private static final long serialVersionUID = 1L;

    private JCheckBox stripLineInfo = new JCheckBox("Strip linenumber information");
    private JCheckBox stripVarsCheck = new JCheckBox("Strip variable names");
    private JCheckBox renameClassesCheck = new JCheckBox("Generate new class names");
    //private JCheckBox renameFieldsCheck = new JCheckBox("Rename fields");
    private JCheckBox legalizeClassesCheck = new JCheckBox("Rename classes to legal names");
    private JCheckBox legalizeFieldsCheck = new JCheckBox("Rename fields to legal names");
    private JCheckBox legalizeMethodsCheck = new JCheckBox("Rename methods to legal names");
    private JCheckBox batchCheck = new JCheckBox("Batch mode(no undo)");
    private JButton obfuscateButton = new JButton("Obfuscate");
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JList output = new JList();
    private Project project = null;
    private EventDispatcher dispatcher;

    public ObfuscationTab() {
        try {
            this.setLayout(new GridBagLayout());
            this.obfuscateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						obfuscate();
					} catch(Exception ex) {
						SystemFacade.getInstance().handleException(ex);
					}
				}
            });
            this.add(this.stripLineInfo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(this.stripVarsCheck, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(this.renameClassesCheck, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            /*
            this.add(this.renameFieldsCheck, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
             */
            
            this.add(this.legalizeClassesCheck, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(this.legalizeMethodsCheck, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(this.legalizeFieldsCheck, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(this.batchCheck, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(this.obfuscateButton, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
                    , GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(this.jScrollPane1, new GridBagConstraints(0, 9, 1, 1, 1.0, 1.0
                    , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.jScrollPane1.getViewport().add(this.output, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redo() {
    }

    public void undo() {
    }

    public void insert() {
    }

    public void remove() {
    }

    public void goTo(Link link) {
    }

    public void find() {
    }

    public void findNext() {
    }


    void obfuscate() throws Exception {
        final boolean batch = this.batchCheck.isSelected(); 
    	final FileSet originalFileSet = project.getFileSet();
    	List<String> classNames = SystemFacade.getInstance().getClassIndex().getClassNames(originalFileSet);        
        if (batch && project.isModified()) {
        	JOptionPane.showMessageDialog(this, "The batch mode requires that you save all your unsaved changes first.");
        	return; // early return
        }
        
        
        File temp = File.createTempFile("rejava", "folder");
        boolean deleteSuccess = temp.delete();
        if (!deleteSuccess) {
        	JOptionPane.showMessageDialog(this, "Could not remove temporary file (" + temp.getAbsolutePath() + ") needed for batch operation.");
        	return; // early return
        }
        boolean mkdirSuccess = temp.mkdirs();
        if (!mkdirSuccess) {
        	JOptionPane.showMessageDialog(this, "Could not create temporary folder (" + temp.getAbsolutePath() + ") needed for batch operation.");
        	return; // early return
        }

        if (batch) {
        	final Folder tempFileSet = new Folder(temp);
        	tempFileSet.getContentsFrom(originalFileSet);
        	project.setFileSet(tempFileSet);
        }

        List<IteratorAgent> obfuscators = new ArrayList<IteratorAgent>();
        if (this.stripLineInfo.isSelected()) {
            obfuscators.add(new LineNumberStripper(batch) {
        		@Override
                public void processException(Exception ex) {
                    SystemFacade.getInstance().handleException(ex);
                }
                
                @Override
                public void postProcessFile(IterationContext ic) {
                	super.postProcessFile(ic);
                    if (batch) {
                    	save(ic);
                    }
                }
            });
        }

        if (this.stripVarsCheck.isSelected()) {
            obfuscators.add(new LocalVarStripper(batch) {
        		@Override
                public void processException(Exception ex) {
                    SystemFacade.getInstance().handleException(ex);
                }
                
                @Override
                public void postProcessFile(IterationContext ic) {
                	super.postProcessFile(ic);
                    if (batch) {
                    	save(ic);
                    }
                }
            });
        }
        
        if (this.renameClassesCheck.isSelected()) {
        	ClassNameObfuscatingAdvisor nameObfuscatorAdvisor = new ClassNameObfuscatingAdvisor(classNames);
        	AbstractIteratorAgent agent = new RefactoringIterator(nameObfuscatorAdvisor, batch) {
        		@Override
                public void processException(Exception ex) {
                    SystemFacade.getInstance().handleException(ex);
                }
                
                @Override
                public void postProcessFile(IterationContext ic) {
                	super.postProcessFile(ic);
                    if (batch) {
                    	save(ic);
                    }
                }
        	};
        	obfuscators.add(agent);
        }

        /*
        if (this.renameFieldsCheck.isSelected()) {
        	List<String> classNames = SystemFacade.getInstance().getClassIndex().getClassNames(project.getFileSet());
        	FieldNameObfuscatingAdvisor nameObfuscatorAdvisor = new FieldNameObfuscatingAdvisor(classNames);
        	AbstractIteratorAgent agent = new RefactoringIterator(nameObfuscatorAdvisor);
        	obfuscators.add(agent);
        }*/

        if (this.legalizeClassesCheck.isSelected()) {
        	ClassNameLegalizingAdvisor nameLegalizingAdvisor = new ClassNameLegalizingAdvisor(classNames);
        	AbstractIteratorAgent agent = new RefactoringIterator(nameLegalizingAdvisor, batch) {
        		@Override
                public void processException(Exception ex) {
                    SystemFacade.getInstance().handleException(ex);
                }
                
                @Override
                public void postProcessFile(IterationContext ic) {
                	super.postProcessFile(ic);
                    if (batch) {
                    	save(ic);
                    }
                }

        	};
        	obfuscators.add(agent);
        }

        if (this.legalizeMethodsCheck.isSelected()) {
        	MethodNameLegalizingAdvisor nameLegalizingAdvisor = new MethodNameLegalizingAdvisor(classNames);
        	AbstractIteratorAgent agent = new RefactoringIterator(nameLegalizingAdvisor, batch) {
        		@Override
                public void processException(Exception ex) {
                    SystemFacade.getInstance().handleException(ex);
                }
                
                @Override
                public void postProcessFile(IterationContext ic) {
                	super.postProcessFile(ic);
                    if (batch) {
                    	save(ic);
                    }
                }
        	};
        	obfuscators.add(agent);
        }

        if (this.legalizeFieldsCheck.isSelected()) {
        	FieldNameLegalizingAdvisor nameLegalizingAdvisor = new FieldNameLegalizingAdvisor(classNames);
        	AbstractIteratorAgent agent = new RefactoringIterator(nameLegalizingAdvisor, batch) {
        		@Override
                public void processException(Exception ex) {
                    SystemFacade.getInstance().handleException(ex);
                }
                
                @Override
                public void postProcessFile(IterationContext ic) {
                	super.postProcessFile(ic);
                    if (batch) {
                    	save(ic);
                    }
                }
        	};
        	obfuscators.add(agent);
        }

        for (int i = 0; i < obfuscators.size(); i++) {
            AbstractIteratorAgent obf = (AbstractIteratorAgent)obfuscators.get(i);
            obf.setProgressMonitor(SystemFacade.getInstance().
                                   getProgressMonitor());
        }
        ProjectIterator.iterate(project, obfuscators, new IterationNotificationListener() {

			public void finished() {
				if (batch) {
					try {
						FileSet temp = project.getFileSet();
						originalFileSet.getContentsFrom(temp);
						project.setFileSet(originalFileSet);
						temp.removeAllFiles();
						dispatcher.notifyObservers(new Event(EventType.PROJECT_UPDATE));
					} catch (IOException ioe) {
						SystemFacade.getInstance().handleException(ioe);
					}
				}
			}
        	
        });

    }
    
    public static void save(IterationContext ic) {
    	try {
    		Folder folder = (Folder)ic.getProject().getFileSet();
    		folder.partialSave(ic.getFilename(), ic.getCf().getData());
    	} catch(Exception e) {
    		SystemFacade.getInstance().handleException(e);
    	}
    }

	public void processEvent(Event event) {
		if (event.getType() == EventType.PROJECT_UPDATE) {
			this.project = event.getProject();
		} else if (event.getType() == EventType.INIT) {
			this.dispatcher = event.getDispatcher();
		}
	}

	public void outline() {
	}

	public void leavingTab() {
		// TODO Auto-generated method stub
	}
	
	public String getTabTitle() {
		return "Obfuscation";
	}

	public void enteringTab() {
	}

}

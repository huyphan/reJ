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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.files.Project;
import net.sf.rej.gui.debug.DebugControlPanel;
import net.sf.rej.gui.dialog.ClassChooseDialog;
import net.sf.rej.gui.dialog.NewClassDialog;
import net.sf.rej.gui.editor.LineIdentifierMode;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.preferences.Preferences;
import net.sf.rej.gui.preferences.Settings;
import net.sf.rej.gui.split.BytecodeToHexSync;
import net.sf.rej.gui.split.ConstantPoolToHexSync;
import net.sf.rej.gui.split.HexSplit;
import net.sf.rej.gui.split.SplitMode;
import net.sf.rej.gui.split.StructureToHexSync;
import net.sf.rej.gui.tab.CompareTab;
import net.sf.rej.gui.tab.ConstantPoolTab;
import net.sf.rej.gui.tab.DebugTab;
import net.sf.rej.gui.tab.EditorTab;
import net.sf.rej.gui.tab.FilesTab;
import net.sf.rej.gui.tab.HexEditorTab;
import net.sf.rej.gui.tab.InjectionTab;
import net.sf.rej.gui.tab.NoDebugTab;
import net.sf.rej.gui.tab.ObfuscationTab;
import net.sf.rej.gui.tab.SearchTab;
import net.sf.rej.gui.tab.StructureTab;
import net.sf.rej.gui.tab.Tab;
import net.sf.rej.gui.tab.Tabbable;
import net.sf.rej.java.ClassFile;

/**
 * Java Application entry point for running the reJ GUI.
 * In other words, the class with the main method. Also the
 * primary window of the GUI Editor.
 * 
 * @author Sami Koivu
 */
public class MainWindow extends JFrame implements EventObserver {
    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());

    static MainWindow instance;

    BorderLayout borderLayout1 = new BorderLayout();
    JToolBar toolbar = new JToolBar();
    JTabbedPane tabbedPane = new JTabbedPane();
    DebugControlPanel debugPanel = null;

    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = null;
    JMenu recentFilesMenu = null;
    JLabel status = new JLabel();

    JSplitPane splitPane = null;
    SplitMode split = SplitMode.NONE;
    HexSplit hexSplit = new HexSplit();
    
    EventDispatcher dispatcher = new EventDispatcher();

    JFileChooser fd = new JFileChooser();
    private Project project = null;

    // Tabs
    private FilesTab filesTab = new FilesTab();
    private StructureTab structureTab = new StructureTab();
    private ConstantPoolTab constantPoolTab = new ConstantPoolTab();
    private EditorTab editorTab = new EditorTab();
    private InjectionTab injectionTab = new InjectionTab();
    private ObfuscationTab obfuscationTab = new ObfuscationTab();
    private SearchTab searchTab = new SearchTab();
    private CompareTab compareTab = new CompareTab();
    private Tabbable debugTab = createDebugTab();
    private HexEditorTab hexTab = new HexEditorTab();

    /**
     * Maps menu checkboxes to tabs, for setting tab visibility.
     */
	Map<JCheckBoxMenuItem, Tab> viewCheckBoxes = new HashMap<JCheckBoxMenuItem, Tab>();
    
    private Action tabViewAction = new AbstractAction() {
    	public void actionPerformed(ActionEvent e) {
    		JCheckBoxMenuItem checkBox = (JCheckBoxMenuItem) e.getSource();
    		Tab tab = viewCheckBoxes.get(checkBox);
    		Tabbable tabComponent = getTab(tab);
    		boolean tabVisible = isTabVisible(tabComponent);
    		try {
    			if (checkBox.isSelected() && !tabVisible) {
    				// display tab
    				ensureTabIsVisible(tabComponent);
    				Preferences prefs = SystemFacade.getInstance().getPreferences();
    				prefs.setTabVisibility(tab, true);
    				prefs.save();
    			} else if (!checkBox.isSelected() && tabVisible) {
    				// hide tab
    				hideTab(tabComponent);
    				Preferences prefs = SystemFacade.getInstance().getPreferences();
    				prefs.setTabVisibility(tab, false);
    				prefs.save();
    			}
    		} catch (Exception ex) {
    			SystemFacade.getInstance().handleException(ex);
    		}
    	}
    };

    private Action newProjectAction = new AbstractAction("Project..") {
        public void actionPerformed(ActionEvent e) {
        	MainWindow.this.fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
        	MainWindow.this.fd.setDialogType(JFileChooser.SAVE_DIALOG);
        	MainWindow.this.fd.setDialogTitle("Select Archive file to create");
        	int i = MainWindow.this.fd.showDialog(instance, "Create");
        	if (i == JFileChooser.APPROVE_OPTION) {
        		SystemFacade.getInstance().createNewArchiveProject(MainWindow.this.fd.getSelectedFile());
        	}
        }
    };

    private Action newClassAction = new AbstractAction("Class..") {
        public void actionPerformed(ActionEvent e) {
            if (project != null) {
                NewClassDialog dialog = new NewClassDialog(MainWindow.this);
                dialog.invoke();
                if( !dialog.userCancelled()) {
                    SystemFacade.getInstance().createNewClass(dialog.getFullClassName(), dialog.getSelectedFile());
                }
            }
        }
    };

    private Action openAction = new AbstractAction("Open..") {
        public void actionPerformed(ActionEvent e) {
            MainWindow.this.fd
                    .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            MainWindow.this.fd.setDialogType(JFileChooser.OPEN_DIALOG);
            MainWindow.this.fd
                    .setDialogTitle("Select java .class file, an archive or a folder.");
            int i = MainWindow.this.fd.showDialog(instance, "Open");
            if (i == JFileChooser.APPROVE_OPTION) {
                SystemFacade.getInstance().openFile(
                        MainWindow.this.fd.getSelectedFile());
            }
        }
    };

    private Action saveAction = new AbstractAction("Save") {
        public void actionPerformed(ActionEvent e) {
            SystemFacade.getInstance().saveFile();
        }
    };

    private Action saveAsAction = new AbstractAction("Save as..") {
        public void actionPerformed(ActionEvent e) {
            MainWindow.this.fd
                    .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            MainWindow.this.fd.setDialogType(JFileChooser.SAVE_DIALOG);
            MainWindow.this.fd.setDialogTitle("Save class or file set.");
            int i = MainWindow.this.fd.showDialog(instance, "Save");
            if (i == JFileChooser.APPROVE_OPTION) {
                SystemFacade.getInstance().saveFile(
                        MainWindow.this.fd.getSelectedFile());
            }
        }
    };

    private Action exitAction = new AbstractAction("Exit") {
        public void actionPerformed(ActionEvent e) {
            SystemFacade.getInstance().exit();
        }
    };

    private Action undoAction = new AbstractAction("Undo") {
        public void actionPerformed(ActionEvent e) {
        	Tabbable tab = getSelectedTab();
        	if (tab != null) {
        		tab.undo();
        	}
        }
    };

    private Action redoAction = new AbstractAction("Redo") {
        public void actionPerformed(ActionEvent e) {
        	Tabbable tab = getSelectedTab();
        	if (tab != null) {
        		tab.redo();
        	}
        }
    };

    private Action outlineAction = new AbstractAction("Quick Outline..") {
        public void actionPerformed(ActionEvent e) {
        	Tabbable tab = getSelectedTab();
        	if (tab != null) {
        		tab.outline();
        	}
        }
    };
    
    private Action openTypeAction = new AbstractAction("Open Type..") {
        public void actionPerformed(ActionEvent e) {
        	if (MainWindow.this.project == null) {
        		return; // early return
        	}
        	
        	ClassIndex ci = SystemFacade.getInstance().getClassIndex();
        	ClassChooseDialog ccd = new ClassChooseDialog(MainWindow.this, ci);
        	ccd.setTitle("Open Type..");
        	ccd.invoke();
        	ClassLocator cl = ccd.getSelected();
        	if (cl != null) {
        		try {
					Event event = new Event(EventType.CLASS_OPEN);
        			ClassFile cf = SystemFacade.getInstance().getClassFile(cl);
					event.setClassFile(cf);
					event.setFile(cl.getFile());
					MainWindow.this.dispatcher.notifyObservers(event);
					MainWindow.this.setTab(Tab.EDITOR);
        		} catch(Exception ioe) {
        			SystemFacade.getInstance().handleException(ioe);
        		}
        	}
        }
    };
    
    private Action goToAction = new AbstractAction("Go to..") {
        public void actionPerformed(ActionEvent e) {
            int selection = JOptionPane.showOptionDialog(MainWindow.this, "Go to..", "Go to..", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {"Source line number", "pc offset"}, "Source line number");
            Link link = null;
            switch (selection) {
            	case 0: // source line #
            		String positionStr = JOptionPane.showInputDialog(MainWindow.this, "Source line number to go to:");
            		if (positionStr != null) {
            			int pos = Integer.parseInt(positionStr);
            			link = new Link();
            			link.setAnchor(Link.ANCHOR_SOURCE_LINE_NUMBER);
            			link.setTab(Tab.EDITOR);
            			link.setPosition(pos);
            		}
            		break;
            	case 1: // code block pc
            		positionStr = JOptionPane.showInputDialog(MainWindow.this, "Method pc offset to go to:");
            		if (positionStr != null) {
            			int pos = Integer.parseInt(positionStr);
            			link = new Link();
            			link.setAnchor(Link.ANCHOR_PC_OFFSET);
            			link.setTab(Tab.EDITOR);
            			link.setPosition(pos);
            		}
            		break;
            }
            
            if (link != null) {
            	SystemFacade.getInstance().goTo(link);
            }
        }
    };

    private Action insertAction = new AbstractAction("Insert..") {
        public void actionPerformed(ActionEvent e) {
        	Tabbable tab = getSelectedTab();
        	if (tab != null) {
        		tab.insert();
        	}
        }
    };

    private Action removeAction = new AbstractAction("Remove") {
        public void actionPerformed(ActionEvent e) {
        	Tabbable tab = getSelectedTab();
        	if (tab != null) {
        		tab.remove();
        	}
        }
    };

    private Action findAction = new AbstractAction("Find..") {
        public void actionPerformed(ActionEvent e) {
        	Tabbable tab = getSelectedTab();
        	if (tab != null) {
        		tab.find();
        	}
        }
    };

    private Action findNextAction = new AbstractAction("Find next") {
        public void actionPerformed(ActionEvent e) {
        	Tabbable tab = getSelectedTab();
        	if (tab != null) {
        		tab.findNext();
        	}
        }
    };

    private Action splitOffAction = new AbstractAction("None") {
        public void actionPerformed(ActionEvent e) {
        	clearContentPane();
        	hexSplit.setActive(false);
			getContentPane().add(tabbedPane, BorderLayout.CENTER);
			getContentPane().validate();
			split = SplitMode.NONE;
        }
    };

    private Action splitHexAction = new AbstractAction("Hex View") {
        public void actionPerformed(ActionEvent e) {
			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hexSplit, tabbedPane);
			hexSplit.setActive(true);
			clearContentPane();
			getContentPane().add(splitPane, BorderLayout.CENTER);
			getContentPane().validate();
			split = SplitMode.HEX;
			editorTab.setSplitSynchronizer(new BytecodeToHexSync(hexSplit));
			structureTab.setSplitSynchronizer(new StructureToHexSync(hexSplit));
			constantPoolTab.setSplitSynchronizer(new ConstantPoolToHexSync(hexSplit));
        }
    };

    private Action splitSourceAction = new AbstractAction("Java Source View") {
        public void actionPerformed(ActionEvent e) {
        	// TODO: implement source view
			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JPanel(), tabbedPane);
			clearContentPane();
			getContentPane().add(splitPane, BorderLayout.CENTER);
			getContentPane().validate();
			split = SplitMode.SOURCE;
        }
    };
    
    private Action cpTranslationOff = new AbstractAction("No translation") {
        public void actionPerformed(ActionEvent e) {
            EditorFacade.getInstance().setConstantPoolTranslationMode(ConstantPoolTranslationMode.OFF);
        }
    };

    private Action cpTranslationNormal = new AbstractAction("Translation") {
        public void actionPerformed(ActionEvent e) {
            EditorFacade.getInstance().setConstantPoolTranslationMode(ConstantPoolTranslationMode.TRANSLATION);
        }
    };

    private Action cpTranslationHybrid = new AbstractAction("Hybrid") {
        public void actionPerformed(ActionEvent e) {
            EditorFacade.getInstance().setConstantPoolTranslationMode(ConstantPoolTranslationMode.HYBRID);
        }
    };

    private Action lineIdOff = new AbstractAction("Off") {
        public void actionPerformed(ActionEvent e) {
            EditorFacade.getInstance().setLineMode(LineIdentifierMode.MODE_OFF);
        }
    };

    private Action lineIdPC = new AbstractAction("PC") {
        public void actionPerformed(ActionEvent e) {
            EditorFacade.getInstance().setLineMode(LineIdentifierMode.MODE_PC);
        }
    };

    private Action lineIdSrc = new AbstractAction("Sourcecode line") {
        public void actionPerformed(ActionEvent e) {
            EditorFacade.getInstance().setLineMode(
                    LineIdentifierMode.MODE_SOURCELINE);
        }
    };

    private Action preferencesAction = new AbstractAction("Preferences..") {
        public void actionPerformed(ActionEvent e) {
            SystemFacade.getInstance().showPreferencesDialog();
        }
    };

    private Action compareAction = new AbstractAction("Compare..") {
        public void actionPerformed(ActionEvent e) {
        	setTab(Tab.COMPARE);
        }
    };

    private Action extendsObjectAction = new AbstractAction("Display \"extends Object\"") {
        public void actionPerformed(ActionEvent e) {
        	try {
        		Preferences prefs = SystemFacade.getInstance().getPreferences();
        		prefs.invertSetting(Settings.DISPLAY_EXTENDS_OBJECT);
        		prefs.save();
        		dispatcher.notifyObservers(new Event(EventType.DISPLAY_PARAMETER_UPDATE));		
        	} catch(Exception ex) {
        		SystemFacade.getInstance().handleException(ex);
        	}
        }
    };
    
    private Action showGenericsAction = new AbstractAction("Display Generics") {
        public void actionPerformed(ActionEvent e) {
        	try {
        		Preferences prefs = SystemFacade.getInstance().getPreferences();
        		prefs.invertSetting(Settings.DISPLAY_GENERICS);
        		prefs.save();
        		dispatcher.notifyObservers(new Event(EventType.DISPLAY_PARAMETER_UPDATE));
        	} catch(Exception ex) {
        		SystemFacade.getInstance().handleException(ex);
        	}
        }
    };

    private Action showVarargsAction = new AbstractAction("Display Varargs") {
        public void actionPerformed(ActionEvent e) {
        	try {
        		Preferences prefs = SystemFacade.getInstance().getPreferences();
        		prefs.invertSetting(Settings.DISPLAY_VARARGS);
        		prefs.save();
        		dispatcher.notifyObservers(new Event(EventType.DISPLAY_PARAMETER_UPDATE));
        	} catch(Exception ex) {
        		SystemFacade.getInstance().handleException(ex);
        	}
        }
    };

    JPanel progressAndStatusPanel = new JPanel();
    JProgressBar progress = new JProgressBar();
    GridLayout gridLayout1 = new GridLayout();

    public MainWindow() {
        super("reJ");
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        this.tabbedPane.addChangeListener(new ChangeListener() {
        	Tabbable lastTab = null;
			public void stateChanged(ChangeEvent e) {
				if (lastTab != null) {
					lastTab.leavingTab();
				}
				lastTab = (Tabbable) tabbedPane.getSelectedComponent();
				if (lastTab != null) {
					lastTab.enteringTab();
				}
			}
        });
        
        this.fd.setFileHidingEnabled(false);
        try {
            this.addWindowListener(new WindowAdapter() {
                @Override
				public void windowClosing(WindowEvent we) {
                    SystemFacade.getInstance().exit();
                }
            });
            
            this.setJMenuBar(this.menuBar);

            this.setState(Frame.NORMAL);
            this.getContentPane().setLayout(this.borderLayout1);
            this.status.setText("Situation under control.");
            clearContentPane();
            this.getContentPane().add(this.tabbedPane, BorderLayout.CENTER);
            this.progressAndStatusPanel.setLayout(this.gridLayout1);
            this.gridLayout1.setRows(2);
            this.progressAndStatusPanel.add(this.progress, null);
            this.progressAndStatusPanel.add(this.status, null);

            // toolbar
            populateToolbar();

            createMenu();

            setVisible(true);
            pack();
            setExtendedState(Frame.MAXIMIZED_BOTH);
            
            // register components to receive messages
            this.dispatcher.registerObserver(this);
            this.dispatcher.registerObserver(this.compareTab);
            this.dispatcher.registerObserver(this.constantPoolTab);
            if (this.debugTab instanceof EventObserver) {
            	this.dispatcher.registerObserver((EventObserver) this.debugTab);
            }
            this.dispatcher.registerObserver(EditorFacade.getInstance());
            this.dispatcher.registerObserver(this.editorTab);
            this.dispatcher.registerObserver(this.hexTab);
            this.dispatcher.registerObserver(this.filesTab);
            this.dispatcher.registerObserver(this.obfuscationTab);
            this.dispatcher.registerObserver(this.structureTab);
            this.dispatcher.registerObserver(SystemFacade.getInstance());
            this.dispatcher.registerObserver(this.hexSplit);
            
    		conditionalSetVisible(Tab.COMPARE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public void clearContentPane() {
		this.getContentPane().removeAll();
		this.getContentPane().add(this.toolbar, BorderLayout.NORTH);
		this.getContentPane().add(this.progressAndStatusPanel, BorderLayout.SOUTH);
	}

	private void createMenu() {
		// File
		this.fileMenu = new JMenu("File");
		this.menuBar.add(this.fileMenu);
        JMenu newMenu = new JMenu("New");
        newMenu.add(this.newProjectAction);
        newMenu.add(this.newClassAction);
        this.fileMenu.add(newMenu);
        this.fileMenu.add(new JMenuItem(this.openAction));
        JMenuItem item = new JMenuItem(this.saveAction);
        this.fileMenu.add(item);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.CTRL_MASK));
        this.fileMenu.add(new JMenuItem(this.saveAsAction));
        this.fileMenu.add(new JSeparator());

        this.recentFilesMenu = new JMenu("Recent Files");
        updateRecentFilesMenu();
        this.fileMenu.add(this.recentFilesMenu);
        this.fileMenu.add(new JSeparator());
        this.fileMenu.add(new JMenuItem(this.exitAction));
		updateRecentFilesMenu();

		// Edit
		JMenu editMenu = new JMenu("Edit");
		this.menuBar.add(editMenu);
		item = new JMenuItem(this.undoAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
		        InputEvent.CTRL_MASK));
		editMenu.add(item);
		item = new JMenuItem(this.redoAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
		        InputEvent.CTRL_MASK));
		editMenu.add(item);
		editMenu.add(new JSeparator());
		editMenu.add(new JMenuItem(this.insertAction));
		item = new JMenuItem(this.removeAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		editMenu.add(item);
		editMenu.add(new JSeparator());
		item = new JMenuItem(this.findAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
		        InputEvent.CTRL_DOWN_MASK));
		editMenu.add(item);
		item = new JMenuItem(this.findNextAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		editMenu.add(item);

		// Navigate
		JMenu navigate = new JMenu("Navigate");
		this.menuBar.add(navigate);
		item = new JMenuItem(this.openTypeAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		navigate.add(item);            
		item = new JMenuItem(this.outlineAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		navigate.add(item);
		item = new JMenuItem(this.goToAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		navigate.add(item);
		
		// View
		JMenu view = new JMenu("View");
		this.menuBar.add(view);
		// View / Line identifier
		{
			JMenu sub = new JMenu("Line identifier");
			ButtonGroup bg = new ButtonGroup();
			JRadioButtonMenuItem btn = new JRadioButtonMenuItem(this.lineIdOff);
			bg.add(btn);
			sub.add(btn);
			btn = new JRadioButtonMenuItem(this.lineIdPC);
			btn.setSelected(true);
			bg.add(btn);
			sub.add(btn);
			btn = new JRadioButtonMenuItem(this.lineIdSrc);
			bg.add(btn);
			sub.add(btn);
			view.add(sub);
		}
		// View / Reference Translation
		{
			JMenu translation = new JMenu("Reference Translation");
			ButtonGroup bg = new ButtonGroup();
			JRadioButtonMenuItem btn = new JRadioButtonMenuItem(this.cpTranslationOff);
			bg.add(btn);
			translation.add(btn);
			btn = new JRadioButtonMenuItem(this.cpTranslationNormal);
			btn.setSelected(true);
			bg.add(btn);
			translation.add(btn);
			btn = new JRadioButtonMenuItem(this.cpTranslationHybrid);
			bg.add(btn);
			translation.add(btn);
			view.add(translation);
		}
		// View / Editor Split Mode
		{
			JMenu split = new JMenu("Split Mode");
			ButtonGroup bg = new ButtonGroup();
			JRadioButtonMenuItem btn = new JRadioButtonMenuItem(this.splitOffAction);
			btn.setSelected(true);
			bg.add(btn);
			split.add(btn);
			btn = new JRadioButtonMenuItem(this.splitHexAction);
			bg.add(btn);
			split.add(btn);
			btn = new JRadioButtonMenuItem(this.splitSourceAction);
			bg.add(btn);
			split.add(btn);
			view.add(split);
		}
		view.add(new JSeparator());
		// View / Display extends Object
		JCheckBoxMenuItem extendsObject = new JCheckBoxMenuItem(extendsObjectAction);
		extendsObject.setModel(new DefaultButtonModel() {
			@Override
			public boolean isSelected() {
				Preferences prefs = SystemFacade.getInstance().getPreferences();
				return prefs.getSetting(Settings.DISPLAY_EXTENDS_OBJECT, Boolean.class).booleanValue();
			}
		});
		view.add(extendsObject);
		// View / Show generics
		JCheckBoxMenuItem showGenerics = new JCheckBoxMenuItem(showGenericsAction);
		showGenerics.setModel(new DefaultButtonModel() {
			@Override
			public boolean isSelected() {
				Preferences prefs = SystemFacade.getInstance().getPreferences();
				return prefs.getSetting(Settings.DISPLAY_GENERICS, Boolean.class).booleanValue();
			}			
		});
		view.add(showGenerics);
		// View / Show varargs
		JCheckBoxMenuItem showVarargs = new JCheckBoxMenuItem(showVarargsAction);
		showVarargs.setModel(new DefaultButtonModel() {
			@Override
			public boolean isSelected() {
				Preferences prefs = SystemFacade.getInstance().getPreferences();
				return prefs.getSetting(Settings.DISPLAY_VARARGS, Boolean.class).booleanValue();
			}			
		});
		view.add(showVarargs);

		
		view.add(new JSeparator());

		// View / Tabs		
		JCheckBoxMenuItem box = new JCheckBoxMenuItem(tabViewAction);
		box.setText(this.structureTab.getTabTitle());
		view.add(box);
		this.viewCheckBoxes.put(box, Tab.STRUCTURE);
		
		box = new JCheckBoxMenuItem(tabViewAction);
		box.setText(this.hexTab.getTabTitle());
		view.add(box);
		this.viewCheckBoxes.put(box, Tab.HEX);

		box = new JCheckBoxMenuItem(tabViewAction);
		box.setText(this.constantPoolTab.getTabTitle());
		view.add(box);
		this.viewCheckBoxes.put(box, Tab.CONSTANTPOOL);

		box = new JCheckBoxMenuItem(tabViewAction);
		box.setText(this.editorTab.getTabTitle());
		view.add(box);
		this.viewCheckBoxes.put(box, Tab.EDITOR);

		box = new JCheckBoxMenuItem(tabViewAction);
		box.setText(this.obfuscationTab.getTabTitle());
		view.add(box);
		this.viewCheckBoxes.put(box, Tab.OBFUSCATION);

		box = new JCheckBoxMenuItem(tabViewAction);
		box.setText(this.searchTab.getTabTitle());
		view.add(box);
		this.viewCheckBoxes.put(box, Tab.SEARCH);

		box = new JCheckBoxMenuItem(tabViewAction);
		box.setText(this.debugTab.getTabTitle());
		view.add(box);
		this.viewCheckBoxes.put(box, Tab.DEBUG);

		box = new JCheckBoxMenuItem(tabViewAction);
		box.setText(this.compareTab.getTabTitle());
		view.add(box);
		this.viewCheckBoxes.put(box, Tab.COMPARE);
		
		for (JCheckBoxMenuItem chkBox : this.viewCheckBoxes.keySet()) {
			chkBox.setSelected(false);
		}

		
		// Tools
		JMenu tools = new JMenu("Tools");
		this.menuBar.add(tools);
		item = new JMenuItem(this.preferencesAction);
		tools.add(item);
		item = new JMenuItem(this.compareAction);
		tools.add(item);
	}

    public void updateRecentFilesMenu() {
    	this.recentFilesMenu.removeAll();
        List list = SystemFacade.getInstance().getRecentFiles();
        for (int i = 0; i < list.size(); i++) {
            File file = new File((String) list.get(i));
            this.recentFilesMenu.add(new JMenuItem(new OpenRecentFileAction(file)));
        }
    }

    public static void main(String args[]) throws Exception {
        if (args.length > 0 && args[0].equalsIgnoreCase("-debug")) {
            Handler fh = new FileHandler("reJ.log");
            Logger.getLogger("net.sf.rej").addHandler(fh);
        	Logger.getLogger("net.sf.rej").setLevel(Level.FINEST);        	
        } else if (args.length > 0 && args[0].equalsIgnoreCase("-nolog")) {
        	// no logging
        } else {
            Handler fh = new FileHandler("reJ.log");
            Logger.getLogger("net.sf.rej").addHandler(fh);
        	Logger.getLogger("net.sf.rej").setLevel(Level.WARNING);
        }

        SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    	        instance = new MainWindow();
    		}
    	});
    }

    public static MainWindow getInstance() {
        return instance;
    }

	public void processEvent(Event event) {
        // get currect project and update all tabs and all that
		if (event.getType() == EventType.PROJECT_UPDATE) {
			this.project = event.getProject();
			if (this.project != null) {
				insertControlTabs();
			} else {
				this.tabbedPane.removeAll();
				for (JCheckBoxMenuItem item : this.viewCheckBoxes.keySet()) {
					item.setSelected(true);
				}
				ensureTabIsVisible(this.filesTab);
			}
		} else if (event.getType() == EventType.CLASS_OPEN && event.getFile() != null) {
			insertConditionalTabs();
			selectFileRelatedTab();
		} else if (event.getType() == EventType.DEBUG_STACK_FRAME_CHANGED) {
			insertConditionalTabs();
			setTab(Tab.EDITOR);
    	} else if (event.getType() == EventType.DEBUG_ATTACH) {
			this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getDebugPanel(), this.tabbedPane);
			this.getContentPane().remove(this.tabbedPane);
			this.getContentPane().add(this.splitPane, BorderLayout.CENTER);
			this.getContentPane().validate();
    	} else if (event.getType() == EventType.DEBUG_DETACH) {
			this.getContentPane().remove(this.splitPane);
			this.getContentPane().add(this.tabbedPane, BorderLayout.CENTER);
			this.getContentPane().validate();
    	}
    }
	
	private void insertControlTabs() {
		ensureTabIsVisible(this.filesTab);
		conditionalSetVisible(Tab.SEARCH);
		conditionalSetVisible(Tab.DEBUG);
	}
	
	private void insertConditionalTabs() {
		conditionalSetVisible(Tab.STRUCTURE);
		conditionalSetVisible(Tab.CONSTANTPOOL);
		conditionalSetVisible(Tab.EDITOR);
		conditionalSetVisible(Tab.HEX);
		conditionalSetVisible(Tab.OBFUSCATION);
	}
	
	private void selectFileRelatedTab() {
		if (this.tabbedPane.indexOfComponent(this.editorTab) != -1) {
			setTab(Tab.EDITOR);
		} else if (this.tabbedPane.indexOfComponent(this.structureTab) != -1) {
			setTab(Tab.STRUCTURE);
		} else if (this.tabbedPane.indexOfComponent(this.hexTab) != -1) {
			setTab(Tab.HEX);
		} else if (this.tabbedPane.indexOfComponent(this.constantPoolTab) != -1) {
			setTab(Tab.CONSTANTPOOL);
		} else {
			// none of the file related tabs are open, open and select Bytecode editor
			setTab(Tab.EDITOR);
		}
	}

	private void conditionalSetVisible(Tab tab) {
		Preferences prefs = SystemFacade.getInstance().getPreferences();
		if (prefs.isTabVisible(tab)) {
			ensureTabIsVisible(getTab(tab));
		}
	}

	private void ensureTabIsVisible(Tabbable tab) {
		if (!isTabVisible(tab)) {
			this.tabbedPane.addTab(tab.getTabTitle(), (Component)tab);
			for (Entry<JCheckBoxMenuItem, Tab> set : this.viewCheckBoxes.entrySet()) {
				if (this.getTab(set.getValue()) == tab) {
					set.getKey().setSelected(true);
				}
			}
		}		
	}
	
    public Tabbable getSelectedTab() {
        return (Tabbable) this.tabbedPane.getSelectedComponent();
    }

    public void setProgress(final int i) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow.this.progress.setValue(i);
            }
        });
    }

    public void setProgressScope(final int min, final int max) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow.this.progress.setMinimum(min);
                MainWindow.this.progress.setMaximum(max);
                MainWindow.this.progress.setValue(min);
            }
        });
    }

    public void setStatus(String status) {
        this.status.setText(status);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void setTab(Tab tab) {
    	Tabbable tabbable = getTab(tab);
    	ensureTabIsVisible(tabbable);
        this.tabbedPane.setSelectedComponent((Component)tabbable);  	
    }
    
    public void hideTab(Tabbable tab) {
    	tabbedPane.remove((Component)tab);
    }
    
    public boolean isTabVisible(Tab tab) {
    	return isTabVisible(getTab(tab));
    }
    
    public boolean isTabVisible(Tabbable tab) {
    	return this.tabbedPane.indexOfComponent((Component)tab) != -1;
    }

    public Tabbable getTab(Tab tab) {
        switch (tab) {
        case FILES:
        	return this.filesTab;
        case STRUCTURE:
        	return this.structureTab;
        case CONSTANTPOOL:
        	return this.constantPoolTab;
        case EDITOR:
        	return this.editorTab;
        case HEX:
        	return this.hexTab;
        case INJECTION:
        	return this.injectionTab;
        case SEARCH:
        	return this.searchTab;
        case OBFUSCATION:
        	return this.obfuscationTab;
        case COMPARE:
        	return this.compareTab;
        case DEBUG:
       		return this.debugTab;
        }
        
        return null;
    }

    public SearchTab getSearchTab() {
        return this.searchTab;
    }
    
    public HexEditorTab getHexTab() {
        return this.hexTab;
    }
    
    public void populateToolbar() {
    	this.toolbar.removeAll();
        this.toolbar.add(this.openAction);
        this.toolbar.add(this.saveAction);
        this.toolbar.add(this.saveAsAction);
        this.toolbar.add(new JSeparator());
        this.toolbar.add(this.insertAction);
        this.toolbar.add(this.removeAction);
        this.toolbar.add(new JSeparator());
        this.toolbar.add(this.findAction);
        this.toolbar.add(new JSeparator());
        this.toolbar.add(this.exitAction);
    }

	private Tabbable createDebugTab() {
		try {
			@SuppressWarnings("unused")
			Class c = Class.forName("com.sun.jdi.Bootstrap");
			// JDI classes found on path, go ahead and create
			// the debug tab.
			return new DebugTab();
		} catch(ClassNotFoundException ncdfe) {
			logger.warning("Class com.sun.jdi.Bootstrap not found. Probably due to the fact that JDI library (tools.jar) is not on classpath. In order to have debugging support, please correct this and restart reJ.");
			return new NoDebugTab();
		}
	}

	public Tabbable getDebugTab() {
		return this.debugTab;
	}
	
	public EditorTab getEditorTab() {
		return this.editorTab;		
	}

	private JPanel getDebugPanel() {
		if (this.debugPanel == null) {
			this.debugPanel = new DebugControlPanel();
			this.dispatcher.registerObserver(this.debugPanel);
		}
		
		return this.debugPanel;
	}

}
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.sf.rej.files.ClassIndex;
import net.sf.rej.files.ClassLocator;
import net.sf.rej.files.Project;
import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.debug.DebugState;
import net.sf.rej.gui.debug.VMEventHandler;
import net.sf.rej.gui.dialog.ClassChooseDialog;
import net.sf.rej.gui.editor.Breakpoint;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.util.StreamRedirector;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;

public class DebugTab extends JPanel implements Tabbable, EventObserver {
	
	private static final Logger logger = Logger.getLogger(DebugTab.class.getName());

	private static final long serialVersionUID = 1L;
	private JRadioButton launchVM = null;
	private JRadioButton attachVM = null;
	private ButtonGroup bg = new ButtonGroup();
	private JPanel statusLabelsPanel = null;
	private JLabel debugStatusLabel = null;
	private JLabel debugStatus = null;
	private JPanel buttonPanel = null;
	private JButton attachButton = null;
	private JButton detachButton = null;
	private JPanel attachOptions = null;
	private JPanel launchOptions = null;
	private JLabel classpathLabel = null;
	private JLabel mainClassLabel = null;
	private JTextField classpathField = null;
	private JTextField mainClassField = null;
	private JLabel methodLabel = null;
	private JLabel hostLabel = null;
	private JLabel portLabel = null;
	private JTextField hostField = null;
	private JTextField portField = null;
	private JComboBox methodCombo = null;
	private JButton selectClassButton = null;
	private JPanel controls = null;
	private JCheckBox suspend = null;
	
	private VirtualMachine vm = null;
	private VMEventHandler eventHandler = null;
	private DebugState state = DebugState.NOT_ATTACHED;
	private ThreadReference openThread = null;
	private Project project = null;
	private EventDispatcher dispatcher = null;

	public String getTabTitle() {
		return "Debug";
	}

	public void find() {
	}

	public void findNext() {
	}

	public void goTo(Link link) {
	}

	public void insert() {
	}

	public void redo() {
	}

	public void remove() {
	}

	public void undo() {
	}

	/**
	 * This is the default constructor
	 */
	public DebugTab() {
		super();
		initialize();
	}

	private void initialize() {
		GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
		gridBagConstraints18.gridx = 0;
		gridBagConstraints18.gridy = 4;
		this.setSize(489, 358);
		this.setLayout(new FlowLayout());
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.add(getControls(), null);
		setState(DebugState.NOT_ATTACHED);
		this.eventHandler = new VMEventHandler();
	}

	/**
	 * This method initializes launchVM	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getLaunchVM() {
		if (launchVM == null) {
			launchVM = new JRadioButton();
			this.bg.add(launchVM);
			launchVM.setText("Launch new VM");
			launchVM.setSelected(true);
		}
		return launchVM;
	}

	/**
	 * This method initializes attachVM	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getAttachVM() {
		if (attachVM == null) {
			attachVM = new JRadioButton();
			this.bg.add(attachVM);
			attachVM.setText("Attach to a running VM");
		}
		return attachVM;
	}

	/**
	 * This method initializes statusLabelsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStatusLabelsPanel() {
		if (statusLabelsPanel == null) {
			debugStatus = new JLabel();
			debugStatusLabel = new JLabel();
			debugStatusLabel.setText("Status:");
			statusLabelsPanel = new JPanel();
			statusLabelsPanel.setLayout(new FlowLayout());
			statusLabelsPanel.setPreferredSize(new Dimension(100, 500));
			statusLabelsPanel.add(debugStatusLabel, null);
			statusLabelsPanel.add(debugStatus, null);
		}
		return statusLabelsPanel;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getAttachButton(), null);
			buttonPanel.add(getDetachButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes attachButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAttachButton() {
		if (attachButton == null) {
			attachButton = new JButton();
			attachButton.setText("Attach");
			attachButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean suspend = false;
					VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
					if (DebugTab.this.launchVM.isSelected()) {
						// Launch
						LaunchingConnector connector = vmm.defaultConnector();
						Map<String, Argument> args = connector.defaultArguments();
						//Project project = OpenProjects.getInstance().getCurrentProject();
						String mainClass = DebugTab.this.mainClassField.getText();
						args.get("options").setValue("-cp \"" + project.getFileSet().getClasspath(mainClass) + "\"" + DebugTab.this.classpathField.getText());
						args.get("suspend").setValue("true");
						suspend = DebugTab.this.suspend.isSelected();
						args.get("main").setValue(mainClass);
						try {
							DebugTab.this.vm = connector.launch(args);				
							StreamRedirector srErr = new StreamRedirector(vm.process().getErrorStream(), System.err);
							new Thread(srErr).start();
							StreamRedirector srOut = new StreamRedirector(vm.process().getInputStream(), System.out);
							new Thread(srOut).start();
						} catch (Exception ex) {
							SystemFacade.getInstance().handleException(ex);
							return;
						}
					} else {
						// Attach
						AttachingConnector connector = null;
						for (AttachingConnector ac : vmm.attachingConnectors()) {
							if (ac.transport().name().equals(DebugTab.this.methodCombo.getSelectedItem())) {
								connector = ac;
								break;
							}
						}
						if (connector == null) {
							SystemFacade.getInstance().setStatus("Attaching connector (" + DebugTab.this.methodCombo.getSelectedItem() + ") not available.");
							return;
						}
						Map<String, Argument> args = connector.defaultArguments();
						args.get("timeout").setValue("30000");
						String hostname = DebugTab.this.hostField.getText();
						if (connector.transport().name().equals("dt_socket")) {
							if (hostname.length() == 0) {
								args.get("hostname").setValue("127.0.0.1");
							} else {
								args.get("hostname").setValue(hostname);
							}
							args.get("port").setValue(DebugTab.this.portField.getText());
						} else {
							args.get("name").setValue(hostname);
						}

						try {
							DebugTab.this.vm = connector.attach(args);
							DebugTab.this.vm.suspend();
						} catch (Exception ex) {
							SystemFacade.getInstance().handleException(ex);
							return;
						}
					}
					try {
						// set breakpoints
						for (Breakpoint bp : EditorFacade.getInstance().getBreakpoints()) {
							List<ReferenceType> list = vm.classesByName(bp.getClassName());
							if (list.size() > 0) {
								List<Method> mlist = list.get(0).methodsByName(bp.getMethodName(), bp.getMethodDesc().getRawDesc());
								if (mlist.size() > 0) {
									Location loc = mlist.get(0).locationOfCodeIndex(bp.getPc());
									BreakpointRequest bpr = vm.eventRequestManager().createBreakpointRequest(loc);
									bpr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
									bpr.setEnabled(true);
								} else {
									logger.warning("Error, breakpoint " + bp + " method not found.");
								}
							} else {
								// add a class load breakpoint
								ClassPrepareRequest cpr = vm.eventRequestManager().createClassPrepareRequest();
								cpr.addClassFilter(bp.getClassName());
								cpr.setEnabled(true);
							}
						}
						
						DebugTab.this.eventHandler.setSuspendOnStartup(suspend);
						
						if (!suspend) {
							DebugTab.this.vm.resume();
						}
						
						DebugTab.this.eventHandler.setVM(vm);
						Thread thread = new Thread(DebugTab.this.eventHandler);
						thread.start();
					} catch (Exception ex) {
						SystemFacade.getInstance().handleException(ex);
					}

				}
			});
		}
		return attachButton;
	}

	/**
	 * This method initializes detachButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDetachButton() {
		if (detachButton == null) {
			detachButton = new JButton();
			detachButton.setText("Detach");
			detachButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					DebugTab.this.eventHandler.setInterrupted(true);
				}
			});
			detachButton.setEnabled(false);
		}
		return detachButton;
	}

	/**
	 * This method initializes attachOptions	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAttachOptions() {
		if (attachOptions == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridy = 0;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.gridx = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridy = 2;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridy = 1;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.EAST;
			gridBagConstraints12.gridy = 2;
			portLabel = new JLabel();
			portLabel.setText("Port:");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.gridy = 1;
			hostLabel = new JLabel();
			hostLabel.setText("Host:");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 0;
			methodLabel = new JLabel();
			methodLabel.setText("Attach method:");
			attachOptions = new JPanel();
			attachOptions.setLayout(new GridBagLayout());
			attachOptions.setPreferredSize(new Dimension(300, 100));
			attachOptions.add(methodLabel, gridBagConstraints10);
			attachOptions.add(hostLabel, gridBagConstraints11);
			attachOptions.add(portLabel, gridBagConstraints12);
			attachOptions.add(getHostField(), gridBagConstraints13);
			attachOptions.add(getPortField(), gridBagConstraints14);
			attachOptions.add(getMethodCombo(), gridBagConstraints15);
		}
		return attachOptions;
	}

	/**
	 * This method initializes launchOptions	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLaunchOptions() {
		if (launchOptions == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.gridwidth = 2;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 2;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 2;
			gridBagConstraints16.gridy = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.EAST;
			gridBagConstraints7.gridy = 1;
			mainClassLabel = new JLabel();
			mainClassLabel.setText("Main-Class:");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.EAST;
			gridBagConstraints6.gridy = 0;
			classpathLabel = new JLabel();
			classpathLabel.setText("Classpath:");
			launchOptions = new JPanel();
			launchOptions.setLayout(new GridBagLayout());
			launchOptions.setPreferredSize(new Dimension(400, 200));
			launchOptions.add(classpathLabel, gridBagConstraints6);
			launchOptions.add(mainClassLabel, gridBagConstraints7);
			launchOptions.add(getClasspathField(), gridBagConstraints8);
			launchOptions.add(getMainClassField(), gridBagConstraints9);
			launchOptions.add(getSelectClassButton(), gridBagConstraints16);
			launchOptions.add(getSuspend(), gridBagConstraints17);
		}
		return launchOptions;
	}

	/**
	 * This method initializes classpathField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getClasspathField() {
		if (classpathField == null) {
			classpathField = new JTextField();
			classpathField.setColumns(20);
		}
		return classpathField;
	}

	/**
	 * This method initializes mainClassField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMainClassField() {
		if (mainClassField == null) {
			mainClassField = new JTextField();
			mainClassField.setColumns(20);
		}
		return mainClassField;
	}

	/**
	 * This method initializes hostField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getHostField() {
		if (hostField == null) {
			hostField = new JTextField();
			hostField.setColumns(20);
		}
		return hostField;
	}

	/**
	 * This method initializes portField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPortField() {
		if (portField == null) {
			portField = new JTextField();
			portField.setColumns(20);
		}
		return portField;
	}

	/**
	 * This method initializes methodCombo	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getMethodCombo() {
		if (methodCombo == null) {
			methodCombo = new JComboBox(new String[] {"dt_socket", "dt_shmem"});
		}
		return methodCombo;
	}

	/**
	 * This method initializes selectClassButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSelectClassButton() {
		if (selectClassButton == null) {
			selectClassButton = new JButton();
			selectClassButton.setText("...");
			selectClassButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ClassIndex classIndex = SystemFacade.getInstance().getClassIndex();
					ClassChooseDialog ccd = new ClassChooseDialog(MainWindow.getInstance(), classIndex);
					ccd.invoke();
					ClassLocator locator = ccd.getSelected();
					if (locator != null) {
						DebugTab.this.mainClassField.setText(locator.getFullName());
					}
				}
			});
		}
		return selectClassButton;
	}

	/**
	 * This method initializes controls	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getControls() {
		if (controls == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.weightx = 1.0D;
			gridBagConstraints21.gridy = 5;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.anchor = GridBagConstraints.WEST;
			gridBagConstraints19.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.weighty = 0.0D;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints3.weighty = 0.0D;
			gridBagConstraints3.gridy = 4;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.weightx = 0.0D;
			gridBagConstraints4.weighty = 0.0D;
			gridBagConstraints4.insets = new Insets(0, 50, 0, 0);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 0.0D;
			gridBagConstraints5.weighty = 0.0D;
			gridBagConstraints5.insets = new Insets(0, 50, 0, 0);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = -1;
			gridBagConstraints.gridx = -1;
			controls = new JPanel();
			controls.setLayout(new GridBagLayout());
			controls.setPreferredSize(new Dimension(400, 300));
			controls.setMaximumSize(new Dimension(300, 300));
			controls.setBorder(BorderFactory.createTitledBorder(null, "Debug Controls", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			controls.add(getLaunchVM(), gridBagConstraints);
			controls.add(getAttachVM(), gridBagConstraints19);
			controls.add(getButtonPanel(), gridBagConstraints3);
			controls.add(getLaunchOptions(), gridBagConstraints5);
			controls.add(getAttachOptions(), gridBagConstraints4);
			controls.add(getStatusLabelsPanel(), gridBagConstraints21);
		}
		return controls;
	}

	/**
	 * This method initializes suspend	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getSuspend() {
		if (suspend == null) {
			suspend = new JCheckBox();
			suspend.setText("Suspend on startup");
		}
		return suspend;
	}
	
	public void setState(DebugState state) {
		this.state = state;
		switch (state) {
		case NOT_ATTACHED:
			debugStatus.setText("Not attached");
			debugStatus.setForeground(Color.RED.darker());
			this.attachButton.setEnabled(true);
			this.detachButton.setEnabled(false);
			break;
		case ATTACHED:
			debugStatus.setText("Attached");
			debugStatus.setForeground(Color.GREEN.darker());
			this.attachButton.setEnabled(false);
			this.detachButton.setEnabled(true);
			break;
		}
	}
	
	public VirtualMachine getVM() {
		return this.vm;
	}

	public void addBreakpoint(Breakpoint breakpoint) {
		if (this.state == DebugState.ATTACHED) {
			List<ReferenceType> list = vm.classesByName(breakpoint.getClassName());
			if (list.size() > 0) {
				List<Method> mlist = list.get(0).methodsByName(breakpoint.getMethodName(), breakpoint.getMethodDesc().getRawDesc());
				if (mlist.size() > 0) {
					Location loc = mlist.get(0).locationOfCodeIndex(breakpoint.getPc());
					BreakpointRequest bpr = vm.eventRequestManager().createBreakpointRequest(loc);
					bpr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
					bpr.setEnabled(true);
				} else {
					logger.warning("Error, live breakpoint " + breakpoint + " method not found.");
				}
			} else {
				// add a class load breakpoint
				ClassPrepareRequest cpr = vm.eventRequestManager().createClassPrepareRequest();
				cpr.addClassFilter(breakpoint.getClassName());
				cpr.setEnabled(true);
			}
		}
	}
	public void removeBreakpoint(Breakpoint breakpoint) {
		if (this.state == DebugState.ATTACHED) {
			List<BreakpointRequest> removes = new ArrayList<BreakpointRequest>();
			for (BreakpointRequest bpr : this.vm.eventRequestManager().breakpointRequests()) {
				if (bpr.location().declaringType().name().equals(breakpoint.getClassName())
				 && bpr.location().method().name().equals(breakpoint.getMethodName())
				 && bpr.location().method().signature().equals(breakpoint.getMethodDesc().getRawDesc())
				 && bpr.location().codeIndex() == breakpoint.getPc()) {
					removes.add(bpr);
				}
			}
			
			this.vm.eventRequestManager().deleteEventRequests(removes);
		}
	}
	
	public void setOpenThread(ThreadReference tr) {
		this.openThread = tr;
	}
	
	public ThreadReference getOpenThread() {
		return this.openThread;
	}

	public void processEvent(Event event) {
		if (event.getType() == EventType.PROJECT_UPDATE) {
			this.project = event.getProject();
		} else if (event.getType() == EventType.INIT) {
			this.dispatcher = event.getDispatcher();
			if (this.eventHandler != null) {
				this.dispatcher.registerObserver(this.eventHandler);
			}
		} else if (event.getType() == EventType.DEBUG_ATTACH) {
			this.setState(DebugState.ATTACHED);
		} else if (event.getType() == EventType.DEBUG_DETACH) {
			this.setState(DebugState.NOT_ATTACHED);
		}
	}

	public void outline() {
	}

	public void leavingTab() {
	}

	public void enteringTab() {
	}

}  //  @jve:decl-index=0:visual-constraint="121,55"

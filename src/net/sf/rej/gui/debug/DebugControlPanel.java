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
package net.sf.rej.gui.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.rej.gui.debug.wrappers.StackFrameWrapper;
import net.sf.rej.gui.debug.wrappers.ThreadReferenceWrapper;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.util.Wrapper;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class DebugControlPanel extends JPanel implements EventObserver {

	private static final long serialVersionUID = 1L;
	private JButton suspendButton = null;
	private JButton resumeButton = null;
	private JPanel statusPanel = null;
	private JLabel statusLabel = null;
	private JPanel stepPanel = null;
	private JButton stepOverButton = null;
	private JButton stepIntoButton = null;
	private JButton stepOutButton = null;
	private JPanel threadsPanel = null;
	private JPanel stackFramesPanel = null;
	private JScrollPane threadScrollPane = null;
	private JScrollPane stackFrameScrollPane = null;
	private JList threadList = null;
	private JList stackFrameList = null;
	
	private DefaultListModel threadModel;
	private DefaultListModel stackModel;
	private VirtualMachine vm = null;
	
	EventDispatcher dispatcher;  //  @jve:decl-index=0:
	
	private final Action debugResume = new AbstractAction("Resume") {
		public void actionPerformed(ActionEvent e) {
			dispatcher.notifyObservers(new Event(EventType.DEBUG_RESUME_REQUESTED));
		}
	};
	
	private final Action debugSuspend = new AbstractAction("Suspend") {
		public void actionPerformed(ActionEvent e) {
			dispatcher.notifyObservers(new Event(EventType.DEBUG_SUSPEND_REQUESTED));
		}
	};

	private final Action debugStepInto = new AbstractAction("Into") {
		public void actionPerformed(ActionEvent e) {
			dispatcher.notifyObservers(new Event(EventType.DEBUG_STEP_INTO_REQUESTED));
		}
	};

	private final Action debugStepOver = new AbstractAction("Over") {
		public void actionPerformed(ActionEvent e) {
			dispatcher.notifyObservers(new Event(EventType.DEBUG_STEP_OVER_REQUESTED));
		}
	};

	private final Action debugStepOut = new AbstractAction("Out") {
		public void actionPerformed(ActionEvent e) {
			dispatcher.notifyObservers(new Event(EventType.DEBUG_STEP_OUT_REQUESTED));
		}
	};

	/**
	 * This is the default constructor
	 */
	public DebugControlPanel() {
		super();
		initialize();
		setStatusAsRunning();
	}

	private void initialize() {
		GridBagConstraints threadsPanelConstraints = new GridBagConstraints();
		threadsPanelConstraints.gridx = 4;
		threadsPanelConstraints.gridy = 0;
		threadsPanelConstraints.weightx = 1;
		threadsPanelConstraints.weighty = 1;
		threadsPanelConstraints.fill = GridBagConstraints.BOTH;
		GridBagConstraints stackFramesPanelConstraints = new GridBagConstraints();
		stackFramesPanelConstraints.gridx = 5;
		stackFramesPanelConstraints.gridy = 0;
		stackFramesPanelConstraints.weightx = 1;
		stackFramesPanelConstraints.weighty = 1;
		stackFramesPanelConstraints.fill = GridBagConstraints.BOTH;
		GridBagConstraints stepPanelConstraints = new GridBagConstraints();
		stepPanelConstraints.gridx = 3;
		stepPanelConstraints.gridy = 0;
		GridBagConstraints statusPanelConstraints = new GridBagConstraints();
		statusPanelConstraints.gridx = 2;
		statusPanelConstraints.gridy = 0;
		GridBagConstraints resumeButtonConstraints = new GridBagConstraints();
		resumeButtonConstraints.gridx = 1;
		resumeButtonConstraints.gridy = 0;
		GridBagConstraints suspendButtonConstraints = new GridBagConstraints();
		suspendButtonConstraints.gridx = 0;
		suspendButtonConstraints.gridy = 0;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getSuspendButton(), suspendButtonConstraints);
		this.add(getResumeButton(), resumeButtonConstraints);
		this.add(getStatusPanel(), statusPanelConstraints);
		this.add(getStepPanel(), stepPanelConstraints);
		this.add(getThreadsPanel(), threadsPanelConstraints);
		this.add(getStackFramesPanel(), stackFramesPanelConstraints);
	}

	/**
	 * This method initializes suspendButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSuspendButton() {
		if (suspendButton == null) {
			suspendButton = new JButton(this.debugSuspend);
		}
		return suspendButton;
	}

	/**
	 * This method initializes resumeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getResumeButton() {
		if (resumeButton == null) {
			resumeButton = new JButton(this.debugResume);
		}
		return resumeButton;
	}

	/**
	 * This method initializes statusPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStatusPanel() {
		if (statusPanel == null) {
			statusLabel = new JLabel();
			statusPanel = new JPanel();
			statusPanel.setLayout(new GridBagLayout());
			statusPanel.setBorder(BorderFactory.createTitledBorder(null, "Status", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			statusPanel.add(statusLabel, new GridBagConstraints());
		}
		return statusPanel;
	}

	/**
	 * This method initializes stepPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStepPanel() {
		if (stepPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.gridy = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 0;
			stepPanel = new JPanel();
			stepPanel.setLayout(new GridBagLayout());
			stepPanel.setBorder(BorderFactory.createTitledBorder(null, "Step..", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			stepPanel.add(getStepOverButton(), gridBagConstraints4);
			stepPanel.add(getStepIntoButton(), gridBagConstraints5);
			stepPanel.add(getStepOutButton(), gridBagConstraints6);
		}
		return stepPanel;
	}

	/**
	 * This method initializes stepOverButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStepOverButton() {
		if (stepOverButton == null) {
			stepOverButton = new JButton(this.debugStepOver);
			stepOverButton.setText("Over");
		}
		return stepOverButton;
	}

	/**
	 * This method initializes stepIntoButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStepIntoButton() {
		if (stepIntoButton == null) {
			stepIntoButton = new JButton(this.debugStepInto);
			stepIntoButton.setText("Into");
		}
		return stepIntoButton;
	}

	/**
	 * This method initializes stepOutButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStepOutButton() {
		if (stepOutButton == null) {
			stepOutButton = new JButton(this.debugStepOut);
			stepOutButton.setText("Out");
		}
		return stepOutButton;
	}

	/**
	 * This method initializes threadsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getThreadsPanel() {
		if (threadsPanel == null) {
			threadsPanel = new JPanel();
			threadsPanel.setLayout(new BorderLayout());
			threadsPanel.setBorder(BorderFactory.createTitledBorder(null, "Threads", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			threadsPanel.add(getThreadScrollPane(), BorderLayout.CENTER);
		}
		return threadsPanel;
	}

	/**
	 * This method initializes stackFramesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStackFramesPanel() {
		if (stackFramesPanel == null) {
			stackFramesPanel = new JPanel();
			stackFramesPanel.setLayout(new BorderLayout());
			stackFramesPanel.setBorder(BorderFactory.createTitledBorder(null, "Stack Frames", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			stackFramesPanel.add(getStackFrameScrollPane(), BorderLayout.CENTER);
		}
		return stackFramesPanel;
	}

	/**
	 * This method initializes threadScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getThreadScrollPane() {
		if (threadScrollPane == null) {
			threadScrollPane = new JScrollPane();
			threadScrollPane.setViewportView(getThreadList());
		}
		return threadScrollPane;
	}

	/**
	 * This method initializes stackFrameScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getStackFrameScrollPane() {
		if (stackFrameScrollPane == null) {
			stackFrameScrollPane = new JScrollPane();
			stackFrameScrollPane.setViewportView(getStackFrameList());
		}
		return stackFrameScrollPane;
	}

	/**
	 * This method initializes threadList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getThreadList() {
		if (threadList == null) {
            this.threadModel = new DefaultListModel();
			threadList = new JList(this.threadModel);
			this.threadList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) return;
					
					Object item = threadList.getSelectedValue();
					if (item != null && !item.equals("")) {
						@SuppressWarnings("unchecked")
						Wrapper<ThreadReference> wrapper = (Wrapper)item;
						Event event = new Event(EventType.DEBUG_THREAD_CHANGED);
						event.setThread(new ThreadReferenceWrapper(wrapper.getContent()));
						dispatcher.notifyObservers(event);
					}
				}
            });
		}
		return threadList;
	}

	/**
	 * This method initializes stackFrameList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getStackFrameList() {
		if (stackFrameList == null) {
			this.stackModel = new DefaultListModel();
			stackFrameList = new JList(this.stackModel);
			this.stackFrameList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) return;
					
					Object item = stackFrameList.getSelectedValue();
					if (item != null && !item.equals("")) {
						@SuppressWarnings("unchecked")
						Wrapper<StackFrame> wrapper = (Wrapper)item;
						Event event = new Event(EventType.DEBUG_STACK_FRAME_CHANGED);
						event.setStackFrame(new StackFrameWrapper(wrapper.getContent()));
						dispatcher.notifyObservers(event);
					}
				}
            });
		}
		return stackFrameList;
	}

	public void processEvent(Event event) {
		switch (event.getType()) {
		case INIT:
			this.dispatcher = event.getDispatcher();
			break;
		case DEBUG_RESUMED:
			setStatusAsRunning();
			this.threadModel.removeAllElements();
			this.stackModel.removeAllElements();
			break;
		case DEBUG_SUSPENDED:
			setStatusAsSuspended();
			this.vm = (VirtualMachine) event.getVM().getVirtualMachineObject();
			updateThreadList();
			break;
		case DEBUG_THREAD_CHANGE_REQUESTED:
			setSelectedThread((ThreadReference) event.getThread().getThreadReferenceObject());
			break;
		case DEBUG_THREAD_CHANGED:
			ThreadReference thread = (ThreadReference) event.getThread().getThreadReferenceObject();
			updateStackFrameList(thread);
			break;
		case DEBUG_STACK_FRAME_CHANGE_REQUESTED:
			setSelectedStackFrame((StackFrame) event.getStackFrame().getStackFrameObject());
			break;
		case DEBUG_STACK_FRAME_CHANGED:
			break;
		case PROJECT_UPDATE:
		case CLASS_OPEN:
		case CLASS_UPDATE:
		case CLASS_REPARSE:
		case CLASS_PARSE_ERROR:
		case DISPLAY_PARAMETER_UPDATE:
		case DEBUG_ATTACH:
		case DEBUG_DETACH:
		case DEBUG_STEP_INTO_REQUESTED:
		case DEBUG_STEP_OUT_REQUESTED:
		case DEBUG_STEP_OVER_REQUESTED:
		case DEBUG_RESUME_REQUESTED:
		case DEBUG_SUSPEND_REQUESTED:
			// do nothing
			break;
		}
	}
	
	private void setStatusAsRunning() {
		this.statusLabel.setText("Running");
		this.statusLabel.setForeground(Color.GREEN.darker());
		this.resumeButton.setEnabled(false);
		this.stepIntoButton.setEnabled(false);
		this.stepOutButton.setEnabled(false);
		this.stepOverButton.setEnabled(false);
		this.suspendButton.setEnabled(true);
	}
	
	private void setStatusAsSuspended() {
		this.statusLabel.setText("Suspended");
		this.statusLabel.setForeground(Color.RED.darker());
		this.resumeButton.setEnabled(true);
		this.stepIntoButton.setEnabled(true);
		this.stepOutButton.setEnabled(true);
		this.stepOverButton.setEnabled(true);
		this.suspendButton.setEnabled(false);
	}
	
	private void updateThreadList() {
		this.threadModel.removeAllElements();
		for (ThreadReference thread : this.vm.allThreads()) {
			Wrapper<ThreadReference> wrapper = new Wrapper<ThreadReference>();
			wrapper.setContent(thread);
			wrapper.setDisplay(thread.name());
			this.threadModel.addElement(wrapper);
		}
	}
	
	private void updateStackFrameList(ThreadReference thread) {
		this.stackModel.removeAllElements();
		try {
			for (StackFrame sf : thread.frames()) {
				Wrapper<StackFrame> wrapper = new Wrapper<StackFrame>();
				wrapper.setContent(sf);
				wrapper.setDisplay(sf.location().method() + ":" + sf.location().codeIndex());
				this.stackModel.addElement(wrapper);
			}
		} catch (IncompatibleThreadStateException ite) {
			ite.printStackTrace();
		}
	}
	
	private void setSelectedThread(ThreadReference thread) {
		for (int i=0; i < this.threadModel.size(); i++) {
			@SuppressWarnings("unchecked")
			Wrapper<ThreadReference> wrapper = (Wrapper)this.threadModel.get(i);
			if (wrapper.getContent().equals(thread)) {
				this.threadList.setSelectedValue(wrapper, true);
			}
		}
	}

	private void setSelectedStackFrame(StackFrame frame) {
		for (int i=0; i < this.stackModel.size(); i++) {
			@SuppressWarnings("unchecked")
			Wrapper<StackFrame> wrapper = (Wrapper)this.stackModel.get(i);
			if (wrapper.getContent().equals(frame)) {
				this.stackFrameList.setSelectedValue(wrapper, true);
			}
		}
	}

}
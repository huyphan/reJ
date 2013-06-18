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

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.sf.rej.gui.EditorFacade;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.MainWindow;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.editor.BasicInfoEditor;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.split.ConstantPoolSplitSynchronizer;
import net.sf.rej.java.ClassFile;
import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.constantpool.DoubleInfo;
import net.sf.rej.java.constantpool.FloatInfo;
import net.sf.rej.java.constantpool.IntegerInfo;
import net.sf.rej.java.constantpool.LongInfo;
import net.sf.rej.java.constantpool.StringInfo;
import net.sf.rej.java.constantpool.UTF8Info;
import net.sf.rej.util.Range;
import net.sf.rej.util.Wrapper;

/**
 * <code>ConstantPoolTab</code> is a GUI Tab for manipulating the contents of
 * the Constant pool.
 * 
 * @author Sami Koivu
 */

public class ConstantPoolTab extends JPanel implements Tabbable, EventObserver {
	public static final long serialVersionUID = 1;
	private ClassFile cf = null;
	private boolean isOpen = false;
	private boolean upToDate = false;
	private ConstantPoolSplitSynchronizer sync;
	private Map<Object, Range> offsets;

	DefaultTableModel model = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	};

	JTable table = new JTable(this.model);

	private static final Object[] HEADERS = { "Id", "Type", "Value" };

	public ConstantPoolTab() {
		this.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(this.table);
		this.add(scrollPane, BorderLayout.CENTER);

		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				splitSynchronize();
			}
		});
		
		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				Object obj = ConstantPoolTab.this.table.getValueAt(
						ConstantPoolTab.this.table.getSelectedRow(), 2);

				if (me.getClickCount() == 2
						&& me.getButton() == MouseEvent.BUTTON1
						&& obj instanceof Wrapper) {
					Wrapper wrapper = (Wrapper) obj;
					ConstantPoolInfo cpi = (ConstantPoolInfo) wrapper
							.getContent();
					launchEditor(cpi);

				} else if (me.getButton() == MouseEvent.BUTTON3) {
					// TODO: Context menu
					// if (obj instanceof CodeRow) {
					// EditorTab.this.codeContextMenu.show(EditorTab.this.table,
					// me.getPoint().x, me.getPoint().y);
					// } else if (obj instanceof MethodDefRow) {
					// EditorTab.this.methodContextMenu.show(EditorTab.this.table,
					// me.getPoint().x, me.getPoint().y);
					// }
				}
			}
		});

	}

	public void launchEditor(final ConstantPoolInfo cpi) {
		if (cpi.getType() == ConstantPoolInfo.CLASS) {
			// TODO: constant chooser
		}

		BasicInfoEditor bie = new BasicInfoEditor(MainWindow.getInstance()) {

			@Override
			public void validate(String value)
					throws BasicInfoEditor.ValidationException {
				validateAndStore(cpi, value);
			}

		};
		switch (cpi.getType()) {
		case ConstantPoolInfo.UTF8:
			bie.invoke("Edit UTF-8 Info", cpi.getValue());
			break;
		case ConstantPoolInfo.DOUBLE:
			DoubleInfo di = (DoubleInfo) cpi;
			bie.invoke("Edit Double Info", String.valueOf(di.getDoubleValue()));
			break;
		case ConstantPoolInfo.FLOAT:
			FloatInfo fi = (FloatInfo) cpi;
			bie.invoke("Edit Float Info", String.valueOf(fi.getFloatValue()));
			break;
		case ConstantPoolInfo.INTEGER:
			IntegerInfo ii = (IntegerInfo) cpi;
			bie.invoke("Edit Integer Info", String.valueOf(ii.getIntValue()));
			break;
		case ConstantPoolInfo.LONG:
			LongInfo li = (LongInfo) cpi;
			bie.invoke("Edit Long Info", String.valueOf(li.getLongValue()));
			break;
		case ConstantPoolInfo.STRING:
			StringInfo si = (StringInfo) cpi;
			bie.invoke("Edit String Info", si.getStringValue());
			break;

		}

	}

	public void processEvent(Event event) {
		if (event.getType() == EventType.CLASS_PARSE_ERROR) {
			this.model.setRowCount(0);
			this.model.setColumnCount(0);
			this.cf = null;			
		}
		
		if (event.getType() == EventType.CLASS_OPEN || event.getType() == EventType.CLASS_REPARSE) {
			if (event.getClassFile() == null) {
				this.model.setRowCount(0);
				this.model.setColumnCount(0);
				this.cf = null;
			} else {
				this.cf = event.getClassFile();

				this.model.setColumnCount(3);
				this.model.setColumnIdentifiers(HEADERS);
			}
		}
		if (event.getType() == EventType.CLASS_OPEN || event.getType() == EventType.CLASS_UPDATE || event.getType() == EventType.CLASS_REPARSE) {
			if (this.cf != null) {
				Range constantPoolRange = this.cf.getOffsetMap().get(ClassFile.OffsetTag.CONSTANT_POOL);
				this.offsets = this.cf.getPool().getOffsetMap(constantPoolRange.getOffset());
				if (this.sync != null) {
					this.sync.setOffsets(this.offsets);
				}
			}
			
			this.upToDate = false;
			if (this.isOpen) {
				refresh();
			}
		}
		
		if (event.getType() == EventType.CLASS_OPEN) {
			// Scroll to the top
			this.table.scrollRectToVisible(new Rectangle(0,0,1,1));
		}

	}
	
	public void refresh() {
		try {
			if (this.cf != null) {
				ConstantPool cp = this.cf.getPool();
				this.model.setRowCount(cp.size());
				int i = 0;
				for (ConstantPoolInfo cpi : cp) {
					this.model.setValueAt(String.valueOf(i), i, 0);
					Wrapper<ConstantPoolInfo> wrapper = new Wrapper<ConstantPoolInfo>();
					wrapper.setContent(cpi);
					if (cpi == null) {
						this.model.setValueAt("", i, 1);
						this.model.setValueAt("[empty]", i, 2);
					} else {
						this.model.setValueAt(cpi.getTypeString(), i, 1);
						wrapper.setDisplay(cpi.getValue());
						this.model.setValueAt(wrapper, i, 2);
					}
					i++;
				}

				this.table.getColumnModel().getColumn(0).setMaxWidth(50);
				this.table.getColumnModel().getColumn(0).setMinWidth(50);
				this.table.getColumnModel().getColumn(1).setMaxWidth(130);
				this.table.getColumnModel().getColumn(1).setMinWidth(130);
			}
		} catch (Exception e) {
			SystemFacade.getInstance().handleException(e);
		}

		this.upToDate = true;
	}
	
	public String getTabTitle() {
		return "Constant Pool";
	}


	public void find() {
		// TODO: implement find
	}

	public void findNext() {
		// TODO: implement findnext
	}

	public void goTo(Link link) {
		// TODO: implement goto
	}

	public void insert() {
		int selection = JOptionPane.showOptionDialog(this, "Insert constant..",
				"Insert constant..", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, new String[] {
						"String", "Integer", "Long", "Float", "Double" },
				"String");
		switch (selection) {
		case -1:
			// no selection
			break;
		case 0: {// String
			BasicInfoEditor bie = new BasicInfoEditor(MainWindow.getInstance()) {

				@Override
				public void validate(String value)
						throws BasicInfoEditor.ValidationException {
					validateAndInsertStringInfo(value);
				}

			};
			bie.invoke("Insert String Constant", "");
			break;
		}
		case 1: {// int
			BasicInfoEditor bie = new BasicInfoEditor(MainWindow.getInstance()) {

				@Override
				public void validate(String value)
						throws BasicInfoEditor.ValidationException {
					validateAndInsertIntegerInfo(value);
				}

			};
			bie.invoke("Insert Integer Constant", "");
			break;
		}
		case 2: {// long
			BasicInfoEditor bie = new BasicInfoEditor(MainWindow.getInstance()) {

				@Override
				public void validate(String value)
						throws BasicInfoEditor.ValidationException {
					validateAndInsertLongInfo(value);
				}

			};
			bie.invoke("Insert Integer Constant", "");
			break;
		}
		case 3: {// float
			BasicInfoEditor bie = new BasicInfoEditor(MainWindow.getInstance()) {

				@Override
				public void validate(String value)
						throws BasicInfoEditor.ValidationException {
					validateAndInsertFloatInfo(value);
				}

			};
			bie.invoke("Insert Integer Constant", "");
			break;
		}
		case 4: {// double
			BasicInfoEditor bie = new BasicInfoEditor(MainWindow.getInstance()) {

				@Override
				public void validate(String value)
						throws BasicInfoEditor.ValidationException {
					validateAndInsertDoubleInfo(value);
				}

			};
			bie.invoke("Insert Integer Constant", "");
			break;
		}
		default:
			// TODO: insert field, method
			throw new RuntimeException("To be implemented");
		}
	}

	public void remove() {
		if (this.table.getSelectedRow() == this.table.getRowCount()-1) {
			EditorFacade.getInstance().removeLastConstantPoolItem(this.cf.getPool());
		} else {
			SystemFacade.getInstance().setStatus("Only last item of the pool may be removed.");
		}
		
	}

	public void redo() {
		EditorFacade.getInstance().performRedo();
	}

	public void undo() {
		EditorFacade.getInstance().performUndo();
	}

	void validateAndInsertStringInfo(String str)
			throws BasicInfoEditor.ValidationException {
		try {
			EditorFacade.getInstance().addStringInfo(this.cf.getPool(), str);
		} catch (Exception e) {
			throw new BasicInfoEditor.ValidationException(e.getMessage());
		}
	}

	void validateAndInsertLongInfo(String str) throws BasicInfoEditor.ValidationException {
		try {
			long l = Long.parseLong(str);
			EditorFacade.getInstance().addConstantPoolInfo(this.cf.getPool(), new LongInfo(l, this.cf.getPool()));
		} catch (Exception e) {
			throw new BasicInfoEditor.ValidationException(e.getMessage());
		}
	}

	void validateAndInsertFloatInfo(String str) throws BasicInfoEditor.ValidationException {
		try {
			float f = Float.parseFloat(str);
			EditorFacade.getInstance().addConstantPoolInfo(this.cf.getPool(), new FloatInfo(f, this.cf.getPool()));
		} catch (Exception e) {
			throw new BasicInfoEditor.ValidationException(e.getMessage());
		}
	}
	
	void validateAndInsertDoubleInfo(String str) throws BasicInfoEditor.ValidationException {
		try {
			double d = Double.parseDouble(str);
			EditorFacade.getInstance().addConstantPoolInfo(this.cf.getPool(), new DoubleInfo(d, this.cf.getPool()));
		} catch (Exception e) {
			throw new BasicInfoEditor.ValidationException(e.getMessage());
		}
	}
	void validateAndInsertIntegerInfo(String str) throws BasicInfoEditor.ValidationException {
		try {
			int i = Integer.parseInt(str);
			EditorFacade.getInstance().addConstantPoolInfo(this.cf.getPool(), new IntegerInfo(i, this.cf.getPool()));
		} catch (Exception e) {
			throw new BasicInfoEditor.ValidationException(e.getMessage());
		}
	}

	void validateAndStore(ConstantPoolInfo cpi, String newValue)
			throws BasicInfoEditor.ValidationException {
		// TODO: validate and save to cpi
		switch (cpi.getType()) {
		case ConstantPoolInfo.UTF8: {
			UTF8Info info = (UTF8Info) cpi;
			EditorFacade.getInstance().modifyUTF8Info(info, newValue);
			break;
		}
		case ConstantPoolInfo.DOUBLE: {
			DoubleInfo info = (DoubleInfo) cpi;
			double d = 0;
			try {
				d = Double.parseDouble(newValue);
			} catch (NumberFormatException nfe) {
				throw new BasicInfoEditor.ValidationException(
						"Not a valid double. (" + nfe.getMessage() + ")");
			}
			EditorFacade.getInstance().modifyDoubleInfo(info, d);
			break;
		}
		case ConstantPoolInfo.FLOAT: {
			FloatInfo info = (FloatInfo) cpi;
			float f = 0;
			try {
				f = Float.parseFloat(newValue);
			} catch (NumberFormatException nfe) {
				throw new BasicInfoEditor.ValidationException(
						"Not a valid float. (" + nfe.getMessage() + ")");
			}
			EditorFacade.getInstance().modifyFloatInfo(info, f);
			break;
		}
		case ConstantPoolInfo.INTEGER: {
			IntegerInfo info = (IntegerInfo) cpi;
			int i = 0;
			try {
				i = Integer.parseInt(newValue);
			} catch (NumberFormatException nfe) {
				throw new BasicInfoEditor.ValidationException(
						"Not a valid integer. (" + nfe.getMessage() + ")");
			}
			EditorFacade.getInstance().modifyIntegerInfo(info, i);
			break;
		}
		case ConstantPoolInfo.LONG: {
			LongInfo info = (LongInfo) cpi;
			long l = 0;
			try {
				l = Long.parseLong(newValue);
			} catch (NumberFormatException nfe) {
				throw new BasicInfoEditor.ValidationException(
						"Not a valid long. (" + nfe.getMessage() + ")");
			}
			EditorFacade.getInstance().modifyLongInfo(info, l);
			break;
		}
		case ConstantPoolInfo.STRING: {
			StringInfo info = (StringInfo) cpi;
			int answer = JOptionPane.showOptionDialog(this,
					"Modify underlying UTF-8 info or create a new one.",
					"Modify String Info item",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new String[] {
							"Modify", "Create new", "Cancel" }, "Modify");
			if (answer == JOptionPane.YES_OPTION) {
				// Modify UTF-8 Info
				UTF8Info ui = info.getUTF8Info();
				EditorFacade.getInstance().modifyUTF8Info(ui, newValue);
				break;

			} else if (answer == JOptionPane.NO_OPTION) {
				// Create a new UTF-8 Info
				EditorFacade.getInstance().modifyStringInfo(info, newValue);
			} else {
				// user cancelled, do nothing
			}
		}
		}
	}

	public void outline() {
	}

	public void leavingTab() {
		this.isOpen = false;
	}

	public void enteringTab() {
		this.isOpen = true;
		if (!upToDate) {
			refresh();
		}
		
		splitSynchronize();
	}

	public void setSplitSynchronizer(ConstantPoolSplitSynchronizer sync) {
		this.sync = sync;
		this.sync.setOffsets(this.offsets);
		splitSynchronize();
	}
	
	public ConstantPoolInfo getSelectedObject() {
		int selectedRow = ConstantPoolTab.this.table.getSelectedRow();
		if (selectedRow == -1) {
			return null;
		} else {
			Object obj = ConstantPoolTab.this.table.getValueAt(selectedRow, 2);
			if (obj instanceof Wrapper) {
				Wrapper wrapper = (Wrapper) obj;
				return (ConstantPoolInfo) wrapper.getContent();
			} else {
				return null;
			}
		}
	}
	
	private void splitSynchronize() {
		if (this.sync != null && isOpen) {
			ConstantPoolInfo cpi = getSelectedObject();
			this.sync.sync(cpi);
		}
	}

}

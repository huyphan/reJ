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
package net.sf.rej.gui.hexeditor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;

/**
 * A MouseListener component for the HexEditor which translates user
 * actions into the changes in the selection and content.
 * 
 * @author Sami Koivu
 */
public class HexEditorKeyListener implements KeyListener {
	
	private SelectionModel selectionModel;
	private int width = 0;
	private boolean readOnly;
	private DataProvider dataProvider = null;

	public HexEditorKeyListener(SelectionModel selectionModel) {
		this.selectionModel = selectionModel;
	}
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT: {
			this.selectionModel.setSelectedIndex(Math.max(0, this.selectionModel.getMinimumIndex()-1));
			int index = this.selectionModel.getMinimumIndex() / this.width;
			setIndex(e, index);
			break;
		}
		case KeyEvent.VK_RIGHT: {
			this.selectionModel.setSelectedIndex(Math.min(this.selectionModel.getSize()-1, this.selectionModel.getMinimumIndex()+1));			
			int index = this.selectionModel.getMinimumIndex() / this.width;
			setIndex(e, index);
			break;
		}
		case KeyEvent.VK_UP: {
			this.selectionModel.setSelectedIndex(Math.max(0, this.selectionModel.getMinimumIndex()-this.width));			
			int index = this.selectionModel.getMinimumIndex() / this.width;
			setIndex(e, index);
			break;
		}
		case KeyEvent.VK_DOWN: {
			if (this.selectionModel.getMinimumIndex() == -1) {
				this.selectionModel.setSelectedIndex(0);
			} else {
				this.selectionModel.setSelectedIndex(Math.min(this.selectionModel.getSize()-1, this.selectionModel.getMinimumIndex()+this.width));
			}
			int index = this.selectionModel.getMinimumIndex() / this.width;
			setIndex(e, index);
			break;
		}
		case KeyEvent.VK_HOME: {
			this.selectionModel.setSelectedIndex(0);
			setIndex(e, 0);
			break;
		}
		case KeyEvent.VK_END: {
			int index = this.selectionModel.getSize()-1;
			this.selectionModel.setSelectedIndex(index);
			setIndex(e, index / this.width);
			break;
		}
		case KeyEvent.VK_PAGE_UP: {
			JList list = (JList) e.getComponent();
			int count = list.getLastVisibleIndex()-list.getFirstVisibleIndex();
			this.selectionModel.setSelectedIndex(Math.max(0, this.selectionModel.getMinimumIndex()-(this.width*count)));			
			int index = this.selectionModel.getMinimumIndex() / this.width;
			setIndex(e, index);			
			break;
		}
		case KeyEvent.VK_PAGE_DOWN: {
			JList list = (JList) e.getComponent();
			int count = list.getLastVisibleIndex()-list.getFirstVisibleIndex();
			if (this.selectionModel.getMinimumIndex() == -1) {
				this.selectionModel.setSelectedIndex(0);
			} else {
				this.selectionModel.setSelectedIndex(Math.min(this.selectionModel.getSize()-1, this.selectionModel.getMinimumIndex()+(this.width*count)));
			}
			int index = this.selectionModel.getMinimumIndex() / this.width;
			setIndex(e, index);
			break;
		}
		case KeyEvent.VK_TAB: {
			this.selectionModel.setHexSelection(!this.selectionModel.getHexSelection());
			e.consume();
			e.getComponent().repaint();
			break;
		}
		}
		
	}
	
	private void setIndex(KeyEvent e, int index) {
		JList list = (JList)e.getComponent();
		list.setSelectedIndex(index);
		list.ensureIndexIsVisible(index);
		e.consume();
		list.repaint();
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_TAB) {
			return;
		}
		
		if (readOnly) {
			return; // early return
		}
		
		int index = this.selectionModel.getMinimumIndex();
		if (index >= 0) {
			if (this.selectionModel.getHexSelection()) {
				if (this.selectionModel.getSubPosition() == 0) {
					int value = getValue(e.getKeyChar());
					if (value != -1) {
						int oldValue = this.dataProvider.get(index) & 0xFF;
						int newValue = (value * 16) + (oldValue % 16);
						this.dataProvider.set(index, (byte)newValue);
						this.selectionModel.setSubPosition(1);
						setIndex(e, this.selectionModel.getMinimumIndex() / this.width);
					}
				} else {
					int value = getValue(e.getKeyChar());
					if (value != -1) {
						int oldValue = this.dataProvider.get(index) & 0xFF;
						int newValue = oldValue - (oldValue % 16) + value;
						this.dataProvider.set(index, (byte)newValue);
						this.selectionModel.setSelectedIndex(Math.min(this.selectionModel.getSize()-1, this.selectionModel.getMinimumIndex() + 1));
						setIndex(e, this.selectionModel.getMinimumIndex() / this.width);
					}
				}
			} else {
				this.dataProvider.set(index, (byte)e.getKeyChar());
				this.selectionModel.setSelectedIndex(Math.min(this.selectionModel.getSize()-1, this.selectionModel.getMinimumIndex() + 1));
				setIndex(e, this.selectionModel.getMinimumIndex() / this.width);
			}
		}
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public void setDataProvider(DataProvider provider) {
		this.dataProvider = provider;
	}
	
	private int getValue(char c) {
		switch (c) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'a':
			return 10;
		case 'b':
			return 11;
		case 'c':
			return 12;
		case 'd':
			return 13;
		case 'e':
			return 14;
		case 'f':
			return 15;
		}
		
		return -1;
	}

}

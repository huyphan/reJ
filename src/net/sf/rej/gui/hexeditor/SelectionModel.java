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

import java.util.HashSet;
import java.util.Set;

/**
 * A class that maintains the state of the selected indices in the
 * Hex Editor component. The state can be queried and set.
 * 
 * @author Sami Koivu
 */
public class SelectionModel {
	/**
	 * A set of the indices which are currently selected.
	 */
	private Set<Integer> selected = new HashSet<Integer>();
	
	/**
	 * The lowest of the selected indices.
	 */
	private int minIndex = -1;
	
	/**
	 * The highest of the selected indices.
	 */
	private int maxIndex = -1;	
	
	/**
	 * The size of the underlying data structure.
	 */
	private int size = 0;
	
	/**
	 * Sub-position. 0 if the user is currently  editing
	 * the most significant 4 bits of the byte, 1 if the user
	 * is currently editing the least significant 4 bits.
	 * Whenever the selection changes the value is set to 0.
	 */
	private int subPosition = 0;
	
	/**
	 * Whether the highlight and editing is on the hexadecimal
	 * values or the ascii counterparts.
	 */
	private boolean hexSelection = true;
	
	/**
	 * Initializes this selection model.
	 */
	public SelectionModel() {
	}
	
	/**
	 * Sets the selected index to the given value. Sub-position will
	 * be set to 0 and any old selection will be discarded.
	 * @param index new selected index.
	 */
	public void setSelectedIndex(int index) {
		this.subPosition = 0;
		this.selected.clear();
		this.selected.add(index);
		this.minIndex = index;
		this.maxIndex = index;
	}
	
	/**
	 * Returns true if the given index is currently selected, false
	 * otherwise.
	 * @param index the index to test for being selected.
	 * @return a boolean value indicating whether the the given index is selected.
	 */
	public boolean isSelected(int index) {
		return selected.contains(index);
	}
	
	/**
	 * Sets the selected indices to the given interval. Sub-position
	 * will be set to 0 and any old selection will be discarded.
	 * Defining the interval begin=0, end=3 will select the 3 first
	 * bytes.
	 * @param begin the index of the beginning of the new selection.
	 * @param end the index of the end of the new selection.
	 */
	public void setSelectedInverval(int begin, int end) {
		assert(end >= begin) : "begin must be smaller than end";
		
		this.subPosition = 0;
		this.minIndex = begin;
		this.maxIndex = end-1;
		this.selected.clear();
		
		for (int i=begin; i <end; i++) {
			this.selected.add(i);
		}
	}
	
	/**
	 * Clears the selection.
	 */
	public void clearSelection() {
		this.subPosition = 0;
		this.selected.clear();
		this.minIndex = -1;
		this.maxIndex = -1;
	}

	
	/**
	 * Returns the first selected index or -1 if there is no selection.
	 * @return the smallest index that is currently selected.
	 */
	public int getMinimumIndex() {
		return this.minIndex;
	}

	/**
	 * Returns the first selected index or -1 if there is no selection.
	 * @return the smallest index that is currently selected.
	 */
	public int getMaximumIndex() {
		return this.maxIndex;
	}

	/**
	 * Sets the size of the data with which this selection model
	 * is associated.
	 * @param size the size of the data in bytes.
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * Returns the size of the data with which this selection model
	 * is associated. 
	 * @return size of the data in bytes.
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Returns the sub-position value.
	 * @return sub-position.
	 */
	public int getSubPosition() {
		return this.subPosition;
	}
	
	/**
	 * Sets the sub-position value.
	 * @param subPosition new sub-position.
	 */
	public void setSubPosition(int subPosition) {
		assert subPosition == 0 || subPosition == 1 : "Sub-position must be 0 or 1";
		this.subPosition = subPosition;
	}

	/**
	 * Returns the hex-selection mode of this model. That is, true
	 * if the current editing should affect the hexadecimal values,
	 * false if the editing should be targeted at the ascii values.
	 * @return true if the editing is to be done on the hexadecimal
	 * values.
	 */
	public boolean getHexSelection() {
		return this.hexSelection;
	}

	/**
	 * Sets the hex-selection mode of this model. That is, true
	 * if the current editing should affect the hexadecimal values,
	 * false if the editing should be targeted at the ascii values.
	 * @param hexSelection true if the editing should be done on the
	 * hexadecimal values.
	 */
	public void setHexSelection(boolean hexSelection) {
		this.hexSelection = hexSelection;
	}

}

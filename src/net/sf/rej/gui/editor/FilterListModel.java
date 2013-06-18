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
package net.sf.rej.gui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class FilterListModel implements ListModel {

    private List<ListDataListener> listeners = new ArrayList<ListDataListener>();

    private List<Object> original = new ArrayList<Object>();
    private List<Object> filtered = new ArrayList<Object>();
    private String filter = "";

    public FilterListModel(Collection<Object> c) {
        this.original.addAll(c);
        this.filtered.addAll(this.original);
    }

    public int getSize() {
        return this.filtered.size();
    }

    public void filter() {
        int oldSize = this.filtered.size();
        String lcaseFilter = this.filter.toLowerCase();
        this.filtered.clear();
        for (int i=0; i < this.original.size(); i++) {
            Object obj = this.original.get(i);
            if(obj.toString().toLowerCase().startsWith(lcaseFilter)) {
                this.filtered.add(obj);
            }
        }
        notifyListeners(oldSize);
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Object getElementAt(int index) {
    	if (index >= this.filtered.size())
    		return null;

        return this.filtered.get(index);
    }

    public void addListDataListener(ListDataListener l) {
        this.listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        this.listeners.remove(l);
    }

    private void notifyListeners(int oldSize) {
        for (int i = 0; i < this.listeners.size(); i++) {
            ListDataListener ldl = this.listeners.get(i);
            ldl.contentsChanged(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_REMOVED, 0, oldSize));
            ldl.contentsChanged(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_ADDED, 0, this.filtered.size()));
        }
    }
}

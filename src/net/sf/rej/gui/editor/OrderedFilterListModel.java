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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class OrderedFilterListModel implements ListModel {

    private List<ListDataListener> listeners = new ArrayList<ListDataListener>();

    private List<Object> original = new ArrayList<Object>();
    private List<Object> filtered = new ArrayList<Object>();
    private String filter = "";
    private Pattern camelCasePattern = null;

    private static final Comparator<Object> ALPHABETICAL = new Comparator<Object>() {
        public int compare(Object arg0, Object arg1) {
            if (arg0 == null || arg1 == null)
                return 0;
            return arg0.toString().compareToIgnoreCase(arg1.toString());
        }
    };

	@SuppressWarnings("unchecked")
    public OrderedFilterListModel(Collection c) {
        this.original.addAll(c);
        sort();
        this.filtered.addAll(this.original);
    }

    public void sort() {
        Collections.sort(this.original, OrderedFilterListModel.ALPHABETICAL);
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
            } else if(this.camelCasePattern != null && this.camelCasePattern.matcher(obj.toString()).matches()) {
                this.filtered.add(obj);            	
            }
        }
        notifyListeners(oldSize);
    }

    public void setFilter(String filter) {
        this.filter = filter;
       	StringBuilder exp = new StringBuilder();
       	for (int i=0; i < filter.length(); i++) {
       		char c = filter.charAt(i);
       		if (Character.isUpperCase(c)) {
       			exp.append("[a-z]*");
       		}
           	
       		exp.append(c);
       	}
   		exp.append(".*");
        this.camelCasePattern = Pattern.compile(exp.toString());
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

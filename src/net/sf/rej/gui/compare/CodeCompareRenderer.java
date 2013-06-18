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
package net.sf.rej.gui.compare;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Collection;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import net.sf.rej.Imports;
import net.sf.rej.gui.editor.rendering.BytecodeRenderer;
import net.sf.rej.gui.editor.rendering.GraphicsSyntaxDrawer;
import net.sf.rej.gui.editor.rendering.WidthCalculatorDrawer;
import net.sf.rej.gui.editor.row.EditorRow;

public class CodeCompareRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private EditorRow er = null;

    // TODO: Two imports is an ugly solution. Probably the two could be combined.
    private Imports importsA;
    private Imports importsB;
    
    private Collection<EditorRow> redSet = null;
    private Collection<EditorRow> yellowSet = null;
    private Color bg = Color.WHITE;
    private boolean yellow = false;
    
    private BytecodeRenderer renderer = new BytecodeRenderer();
    
    private int width = 0;
   
    public CodeCompareRenderer() {
    }
    
    public void setRedSet(Collection<EditorRow> set) {
    	this.redSet = set;
    }
    
    public void setYellowSet(Collection<EditorRow> set) {
    	this.yellowSet = set;
    }

    @Override
    public Component getListCellRendererComponent(JList table, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        this.er = (EditorRow) value;

        yellow = false;
        if (this.redSet.contains(this.er)) {
        	this.bg = new Color(255, 100, 100);
        } else if (this.yellowSet.contains(this.er)) {
        	yellow = true;
        	this.bg = new Color(255, 255, 100);
        } else {
        	this.bg = Color.WHITE;
        }
        
        if (isSelected) {
        	this.bg = bg.darker();
        }

        setText(" ");
        WidthCalculatorDrawer wcd = new WidthCalculatorDrawer(table.getGraphics());
        this.renderer.render(this.er, wcd, yellow ? this.importsA : this.importsB);
        this.width = wcd.getMaxWidth();
        return this;
    }

    @Override
	public void paint(Graphics g) {   	
       	setBackground(bg);
        super.paint(g);
        
        if (this.er == null) {
            return;
        }

        Dimension d = this.getSize();
        GraphicsSyntaxDrawer sd = new GraphicsSyntaxDrawer(g, d);
        if (yellow) {
        	this.renderer.render(this.er, sd, this.importsB);        	
        } else {
        	this.renderer.render(this.er, sd, this.importsA);
        }
    }
    
    public void setImports(Imports importsA, Imports importsB) {
    	this.importsA = importsA;
    	this.importsB = importsB;
    }

    @Override
    public Dimension getPreferredSize() {
    	return new Dimension(this.width, super.getPreferredSize().height);
    }
    

}

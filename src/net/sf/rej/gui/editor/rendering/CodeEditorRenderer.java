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
package net.sf.rej.gui.editor.rendering;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import net.sf.rej.Imports;
import net.sf.rej.gui.editor.row.EditorRow;

public class CodeEditorRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private EditorRow er = null;
    private boolean isSelected = false;
    
    private Imports imports;
    
    private BytecodeRenderer renderer = new BytecodeRenderer();
    
    private int width = 0;

    public CodeEditorRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList table, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        this.isSelected = isSelected;
        this.er = (EditorRow) value;

        setText(" ");
        WidthCalculatorDrawer wcd = new WidthCalculatorDrawer(table.getGraphics());
        this.renderer.render(this.er, wcd, this.imports);
        this.width = wcd.getMaxWidth();
        return this;
    }

    @Override
	public void paint(Graphics g) {
    	Color bg = this.renderer.getBackgroundColor(this.er);
        if (this.isSelected) {
            bg = bg.darker();
        }
        setBackground(bg);
        super.paint(g);
        if (this.er == null) {
            return;
        }

        Dimension d = this.getSize();
        GraphicsSyntaxDrawer sd = new GraphicsSyntaxDrawer(g, d);
        this.renderer.render(this.er, sd, this.imports);
    }
    
    public void setImports(Imports imports) {
    	this.imports = imports;
    }

    @Override
    public Dimension getPreferredSize() {
    	return new Dimension(this.width, super.getPreferredSize().height);
    }
    

}

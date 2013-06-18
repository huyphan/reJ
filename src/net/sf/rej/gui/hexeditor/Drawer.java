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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Class used to draw text into a graphics object.
 * 
 * @author Sami Koivu
 */

public class Drawer {
    public static final Font PLAIN = new Font("Monospaced", Font.PLAIN, 14);
    
    private static final Color HILITE_SELECTION = new Color(255, 255, 0);
    private static final Color NO_HILITE_SELECTION = new Color(255, 255, 150);    

    private Graphics g;
    private int yPos;
    private Dimension d;
    private int offset;
    private FontMetrics fm;

    /**
     * Initializes this drawer with the given Graphics object and the
     * dimensions of the component in which the drawing takes place.
     * @param g the graphics object in which to draw.
     * @param d the dimensions of the target.
     */
    public Drawer(Graphics g, Dimension d) {
        this.g = g;
        this.d = d;
        this.yPos = d.height - 4;
        this.fm = g.getFontMetrics(PLAIN);
    }
    
    /**
     * Draws text without selections of highlighting.
     * @param text the text the draw.
     */
    public void draw(String text) {
    	draw(text, false, false);
    }
    
    /**
     * Draws text with possible selection and possible highlight.
     * @param text the text to draw.
     * @param selected true if this text is selected.
     * @param hilite true if the selection is to be highlighted.
     */
    public void draw(String text, boolean selected, boolean hilite) {
        this.g.setFont(PLAIN);
        //this.g.setColor(DEFAULT);
        int width = this.fm.stringWidth(text);
        if (selected) {
        	Color old = this.g.getColor();
        	if (hilite) {
        		this.g.setColor(HILITE_SELECTION);
        	} else {
        		this.g.setColor(NO_HILITE_SELECTION);
        	}
        	this.g.fill3DRect(offset, 0, width, d.height, true);
        	this.g.setColor(old);
        }
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += width;
    }

    /**
     * Returns the current x position of this drawer. In other words,
     * the horizontal position in which the next character is to be
     * drawn.
     * @return horizontal drawing position.
     */
	public int getOffset() {
		return this.offset;
	}

}

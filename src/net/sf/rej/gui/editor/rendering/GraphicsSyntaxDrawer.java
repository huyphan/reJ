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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


/**
 * Class used to draw syntax highlighted text into a graphics
 * object
 * 
 * @author Sami Koivu
 */

public class GraphicsSyntaxDrawer implements JavaBytecodeSyntaxDrawer {
	/*
	 * TODO: The syntaxing should be configurable
	 */
    private static final Font BOLD = new Font("Monospaced", Font.BOLD, 14);
    private static final Font PLAIN = new Font("Monospaced", Font.PLAIN, 14);
    private static final Font SMALL = new Font("Monospaced", Font.PLAIN, 10);
    private static final Color KEYWORD = new Color(127, 0, 85);
    private static final Color ANNOTATION = new Color(100, 100, 100);
    private static final Color STRING_AND_FIELD = new Color(0, 0, 192);
    private static final Color COMMENT = new Color(63, 127, 95);
    private static final Color DEFAULT = Color.black;
    private static final String INDENT = "    ";

    private Graphics g;
    private int offset = 0;
    private int yPos;
    private FontMetrics fm;

    public GraphicsSyntaxDrawer(Graphics g, Dimension d) {
        this.g = g;
        this.yPos = d.height - 4;
        this.fm = g.getFontMetrics(BOLD);
    }
    
    public void drawIndent() {
    	drawDefault(INDENT);
    }

    public void drawKeyword(String text) {
        this.g.setFont(BOLD);
        this.g.setColor(KEYWORD);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
    }

    public void drawComment(String text) {
        this.g.setFont(PLAIN);
        this.g.setColor(COMMENT);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
    }

    public void drawAnnotation(String text) {
        this.g.setFont(PLAIN);
        this.g.setColor(ANNOTATION);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
    }

    public void drawString(String text) {
        this.g.setFont(PLAIN);
        this.g.setColor(STRING_AND_FIELD);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
    }

    public void drawField(String text) {
        this.g.setFont(PLAIN);
        this.g.setColor(STRING_AND_FIELD);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
    }

    public void drawDefault(String text) {
        this.g.setFont(PLAIN);
        this.g.setColor(DEFAULT);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
    }

    public void drawDefaultOverstrike(String text) {
        this.g.setFont(PLAIN);
        this.g.setColor(DEFAULT);
        this.g.drawString(text, this.offset, this.yPos);
        int width = this.fm.stringWidth(text);
        this.g.drawLine(this.offset, yPos - 4, this.offset + width, yPos - 4);
        this.offset += width;
    }

    public void drawInstruction(String text) {
        this.g.setFont(BOLD);
        this.g.setColor(DEFAULT);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
    }

    public void drawSmall(String text, int offset) {
        this.offset = offset;
        this.g.setFont(SMALL);
        this.g.setColor(DEFAULT);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
    }

	public int getMaxWidth() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void drawBreakpoint() {
		Graphics2D g2 = (Graphics2D)this.g;
		Ellipse2D.Double ellipse = new Ellipse2D.Double(this.offset + 4, this.yPos - 6, 4, 4);
		g2.setColor(Color.RED);
		g2.fill(ellipse);
		g2.setColor(Color.RED.darker());
		g2.draw(ellipse);
	}

	public void setExecutionBackground() {
		// do nothing
	}

}

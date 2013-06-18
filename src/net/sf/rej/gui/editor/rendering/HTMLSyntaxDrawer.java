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


/**
 * Class used to draw syntax highlighted text into a graphics
 * object
 * 
 * @author Sami Koivu
 */

public class HTMLSyntaxDrawer implements JavaBytecodeSyntaxDrawer {
	
	private StringBuilder sb = new StringBuilder();
	/*
	 * TODO: The syntaxing should be configurable
	 */
    private static final Color KEYWORD = new Color(127, 0, 85);
    private static final Color ANNOTATION = new Color(100, 100, 100);
    private static final Color STRING_AND_FIELD = new Color(0, 0, 192);
    private static final Color COMMENT = new Color(63, 127, 95);
    private static final Color DEFAULT = Color.black;
    
    private String escape(String str) {
    	return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
    
    public void drawIndent() {
    	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
    }
    
    private static String toBold(String str) {
    	return "<B>" + str + "</B>";
    }
  
    private static String toColor(String str, Color c) {
		int rgb = c.getRGB() & 0xFFFFFF;
		String colorStr = ("00000" + Integer.toHexString(rgb));
		colorStr = colorStr.substring(colorStr.length()-6, colorStr.length());

		return "<FONT COLOR=\"#" + colorStr + "\">" + str + "</FONT>";
    }

    public void drawKeyword(String text) {
        sb.append(toBold(toColor(escape(text), KEYWORD)));
    }

    public void drawComment(String text) {
        sb.append(toColor(escape(text), COMMENT));
    }

    public void drawAnnotation(String text) {
        sb.append(toColor(escape(text), ANNOTATION));
    }

    public void drawString(String text) {
        sb.append(toColor(escape(text), STRING_AND_FIELD));
    }

    public void drawField(String text) {
        sb.append(toColor(escape(text), STRING_AND_FIELD));
    }

    public void drawDefault(String text) {
        sb.append(toColor(escape(text), DEFAULT));
    }

    public void drawDefaultOverstrike(String text) {
    	// TODO: overstrike is easy in HTML?
        sb.append(toColor(escape(text), DEFAULT));
    }

    public void drawInstruction(String text) {
        sb.append(toBold(toColor(escape(text), DEFAULT)));
    }

    public void drawSmall(String text, int offset) {
    	/*
        this.g.setFont(SMALL);
        this.g.setColor(DEFAULT);
        this.g.drawString(text, this.offset, this.yPos);
        this.offset += this.fm.stringWidth(text);
         */
    }
    
    public void drawLineBreak() {
    	this.sb.append("</BR>");
    }

	public void setOffset(int offset) {
	}
	
	public String getHTML() {
		return this.sb.toString();
	}

	public void drawBreakpoint() {
		// do nothing
	}

	public void setExecutionBackground() {
		// do nothing
	}

}

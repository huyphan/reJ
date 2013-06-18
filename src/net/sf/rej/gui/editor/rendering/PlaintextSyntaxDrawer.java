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



/**
 * Class used to draw syntax highlighted text into a graphics
 * object
 * 
 * @author Sami Koivu
 */

public class PlaintextSyntaxDrawer implements JavaBytecodeSyntaxDrawer {

	public StringBuilder sb = new StringBuilder();
  
    public void drawIndent() {
    	drawDefault("    ");
    }

    public void drawKeyword(String text) {
        this.sb.append(text);
    }

    public void drawComment(String text) {
        this.sb.append(text);
    }

    public void drawAnnotation(String text) {
        this.sb.append(text);
    }

    public void drawString(String text) {
        this.sb.append(text);
    }

    public void drawField(String text) {
        this.sb.append(text);
    }

    public void drawDefault(String text) {
        this.sb.append(text);
    }

    public void drawDefaultOverstrike(String text) {
        this.sb.append(text);
    }

    public void drawInstruction(String text) {
        this.sb.append(text);
    }

    public void drawSmall(String text, int offset) {
        //this.sb.append(text);
    }

	public void setOffset(int offset) {
		// do nothing
	}

	public void drawLineBreak() {
		sb.append("\n");
	}
	
	public String getText() {
		return this.sb.toString();
	}
	
	public void clear() {
		this.sb.delete(0, this.sb.length());
	}

	public void drawBreakpoint() {
		// do nothing
	}

	public void setExecutionBackground() {
		// do nothing
	}

}

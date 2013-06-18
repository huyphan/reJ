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
package net.sf.rej.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.rej.java.constantpool.ConstantPool;
import net.sf.rej.java.constantpool.ConstantPoolInfo;
import net.sf.rej.java.instruction.Label;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

/**
 * ExceptionInfo
 *
 * @author Sami Koivu
 */

public class ExceptionInfo {

    private Label startLabel;
    private Label endLabel;
    private Label handlerLabel;
    private int catchType;

    private ConstantPool pool;

    public ExceptionInfo(ByteParser parser, ConstantPool pool) {
        this.pool = pool;
        int startPc = parser.getShortAsInt();
        int endPc = parser.getShortAsInt();
        int handlerPc = parser.getShortAsInt();
        this.catchType = parser.getShortAsInt();

        // create the labels
        this.startLabel = new Label(startPc, "try-block_start(" + getType()
                + ")_" + startPc);
        this.endLabel = new Label(endPc, "try-block_end(" + getType() + ")_"
                + endPc);
        this.handlerLabel = new Label(handlerPc, "exception_handler("
                + getType() + ")_" + handlerPc);
    }

    @Override
	public String toString() {
        return "Exception: startpc " + getStartPc() + " endpc " + getEndPc()
                + " handlerpc " + getHandlerPc() + " catch type " + this.catchType
                + "(" + this.pool.get(this.catchType) + ")";
    }

    public byte[] getData() {
        ByteSerializer ser = new ByteSerializer(true);
        ser.addShort(getStartPc());
        ser.addShort(getEndPc());
        ser.addShort(getHandlerPc());
        ser.addShort(this.catchType);

        return ser.getBytes();
    }

    public int getStartPc() {
        return this.startLabel.getPosition();
    }

    public int getEndPc() {
        return this.endLabel.getPosition();
    }

    public int getHandlerPc() {
        return this.handlerLabel.getPosition();
    }

    public String getType() {
        ConstantPoolInfo cpi = this.pool.get(this.catchType);
        if (cpi == null)
            return "(#" + this.catchType + ")";
        return cpi.getValue();
    }

    public boolean isFinally() {
        return (this.catchType == 0);
    }

    public List<Label> getLabels() {
        List<Label> labels = new ArrayList<Label>();
        labels.add(this.startLabel);
        labels.add(this.endLabel);
        labels.add(this.handlerLabel);
        return labels;
    }

}

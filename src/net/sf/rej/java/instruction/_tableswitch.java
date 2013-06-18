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
package net.sf.rej.java.instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

/**
 * Access jump table by index and jump.
 * 
 * @author Sami Koivu
 */
public class _tableswitch extends Instruction {

	public static final int OPCODE = 0xaa;

	public static final String MNEMONIC = "tableswitch";

	private Label def;

	private int low;

	private int high;

	private Map<Integer, Label> offsets;

	public _tableswitch() {
		// empty constructor
	}

	@Override
	public int getOpcode() {
		return OPCODE;
	}

	@Override
	public String getMnemonic() {
		return MNEMONIC;
	}

	@Override
	public int getSize() {
		throw new RuntimeException(
				"Variant sized, getSize() must not be called, use getSize(DecompilationContext) instead.");
	}

	@Override
	public int getSize(DecompilationContext dc) {
		if (this.offsets == null) {
			ByteParser parser = dc.getParser();
			int startPos = parser.getPosition();
			parser.getByte(); // jump one byte forward -- this is the opcode
			// byte

			int pad = 0;
			while ((dc.getPosition() + pad + 1) % 4 != 0) {
				parser.getByte();
				pad++;
			}

			parser.getInt(); // def
			int low = (int) parser.getInt(); // low
			int high = (int) parser.getInt(); // high

			for (int i = low; i <= high; i++) {
				parser.getInt(); // jump offset
			}

			return parser.getPosition() - startPos;
		} else {
			int size = 1; // opcode
			while ((dc.getPosition() + size) % 4 != 0) {
				size++;
			}

			size += 12; // def, low, high
			size += ((this.high - this.low) + 1) * 4; // number of offsets
														// times
			// 4 bytes

			return size;
		}

	}

	@Override
	public void setData(byte[] data, DecompilationContext dc) {
		ByteParser parser = new ByteArrayByteParser(data);
		parser.setBigEndian(true);

		parser.getByte(); // throw away OPCODE

		while ((dc.getPosition() + parser.getPosition()) % 4 != 0) {
			parser.getByte(); // throw away bytes until we are in a position
			// divisible by 4
		}

		this.def = new Label((int) parser.getInt() + dc.getPosition(),
				"table_label_def");

		this.low = (int) parser.getInt();
		this.high = (int) parser.getInt();

		this.offsets = new HashMap<Integer, Label>();

		for (int i = this.low; i <= this.high; i++) {
			Label label = new Label((int) parser.getInt() + dc.getPosition(),
					"table_label_" + i);
			if (label.getPosition() != this.def.getPosition()) {
				this.offsets.put(Integer.valueOf(i), label);
			}
		}
	}

	@Override
	public byte[] getData(DecompilationContext dc) {
		ByteSerializer ser = new ByteSerializer(true);
		ser.addByte(OPCODE);
		while ((ser.size() + dc.getPosition()) % 4 != 0) {
			ser.addByte(0);
		}

		ser.addInt(this.def.getPosition() - dc.getPosition());
		ser.addInt(this.low);
		ser.addInt(this.high);

		for (int i = this.low; i <= this.high; i++) {
			Label l = this.offsets.get(i);
			if (l != null) {
				ser.addInt(l.getPosition() - dc.getPosition());
			} else {
				ser.addInt(this.def.getPosition() - dc.getPosition());
			}
		}

		return ser.getBytes();
	}

	@Override
	public void execute(ExecutionContext ec) {
	}

	@Override
	public List<Label> getLabels() {
		List<Label> al = new ArrayList<Label>();
		al.add(this.def);
		al.addAll(this.offsets.values());
		return al;
	}

	@Override
	public Parameters getParameters() {
		Parameters params = getParameterTypes();
		params.addValue(this.def);
		params.addValue(this.offsets);
		return params;
	}

	@Override
	public Parameters getParameterTypes() {
		return new Parameters(new ParameterType[] { ParameterType.TYPE_LABEL,
				ParameterType.TYPE_SWITCH });
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParameters(Parameters params) {
		this.def = (Label) params.getObject(0);
		this.offsets = (Map) params.getObject(1);
	}

	@Override
	public List<StackElement> getPoppedElements(DecompilationContext dc) {
		List<StackElement> elements = new ArrayList<StackElement>();
		elements.add(new StackElement("index", StackElementType.INT));
		return elements;
	}

}
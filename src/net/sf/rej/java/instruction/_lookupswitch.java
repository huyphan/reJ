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
import java.util.Map.Entry;

import net.sf.rej.util.ByteArrayByteParser;
import net.sf.rej.util.ByteParser;
import net.sf.rej.util.ByteSerializer;

/**
 * Access jump table by key match and jump.
 * 
 * @author Sami Koivu
 */
public class _lookupswitch extends Instruction {

	public static final int OPCODE = 0xab;

	public static final String MNEMONIC = "lookupswitch";

	private static final int SIZE = 1; // size varies

	private Label def;

	private Map<Integer, Label> offsets;

	public _lookupswitch() {
		// parameterless constructor
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
		return SIZE;
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
			int npairs = (int) parser.getInt();
			for (int i = 0; i < npairs; i++) {
				parser.getInt(); // match
				parser.getInt(); // offset
			}

			return parser.getPosition() - startPos;
		} else {
			int size = 1; // opcode
			while ((dc.getPosition() + size) % 4 != 0) {
				size++;
			}

			size += 8; // def, npairs
			size += this.offsets.size() * 8; // number of match/offset pairs
			// times 8 bytes

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
				"lookup_label_def");

		int npairs = (int) parser.getInt();

		this.offsets = new HashMap<Integer, Label>();

		for (int i = 0; i < npairs; i++) {
			int match = (int) parser.getInt();
			int offset = (int) parser.getInt();
			Label label = new Label(dc.getPosition() + offset, "lookup_label_"
					+ match);
			this.offsets.put(match, label);
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
		ser.addInt(this.offsets.size());

		for (Entry<Integer, Label> entry : this.offsets.entrySet()) {
			ser.addInt(entry.getKey());
			ser.addInt(entry.getValue().getPosition() - dc.getPosition());
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
		elements.add(new StackElement("key", StackElementType.INT));
		return elements;
	}

}
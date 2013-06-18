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

public class ClassVersion {
	private int majorVersion = 0;
	private int minorVersion = 0;

	public ClassVersion() {
	}
	
	public ClassVersion(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	@Override
	public String toString() {
		return this.majorVersion + "." + this.minorVersion;
	}
	
	/*
	 * The Java virtual machine implementation of Sun’s JDK release 1.0.2 supports
	 * class file format versions 45.0 through 45.3 inclusive. Sun’s JDK releases
	 * 1.1.X can support class file formats of versions in the range 45.0 through
	 * 45.65535 inclusive. For implementations of version 1.k of the Java 2 platform
	 * can support class file formats of versions in the range 45.0 through 44+k.0
	 * inclusive.
	 */
	public String getJavaVersionCompabilityString() {
		if (this.majorVersion < 45) {
			return "Invalid";
		} else if (this.majorVersion == 45 && this.minorVersion <= 3) {
			return "1.0.2";
		} else if (this.majorVersion == 45 && this.minorVersion <= 65535) {
			return "1.1.X";
		} else {
			return "1." + (this.majorVersion-44);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClassVersion)) {
			return false;
		}
		ClassVersion other = (ClassVersion) obj;
		
		return other.majorVersion == this.majorVersion && other.minorVersion == this.minorVersion;
	}
	
	@Override
	public int hashCode() {
		return this.majorVersion;
	}
	
}

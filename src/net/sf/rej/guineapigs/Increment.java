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
package net.sf.rej.guineapigs;

public class Increment {
    private int member = 0;
    private static int staticMember = 0;

    void method() {
        this.member++;
        Increment.staticMember++;
        int local = 0;
        local++;
    }

    void method2() {
        this.member = this.member + 1;
        Increment.staticMember = Increment.staticMember + 1;
        int local = 0;
        local = local + 1;
    }

    void method3() {
        this.member += 1;
        Increment.staticMember += 1;
        int local = 0;
        local += 1;
    }

}

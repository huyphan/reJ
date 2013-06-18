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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.NotSerializableException;

public class ExceptionPig {

    public void threeExceptions() throws FileNotFoundException, StringIndexOutOfBoundsException, NotSerializableException {
        switch(2) {
      		case 0:
      		    throw new FileNotFoundException();
      		case 1:
      		    throw new NotSerializableException();

        }
    }

    public void twoExceptions() throws FileNotFoundException, StringIndexOutOfBoundsException {
        switch(2) {
    		case 0:
    		    throw new FileNotFoundException();
        }
    }

    public void oneException() throws FileNotFoundException {
        switch(2) {
    		case 0:
    		    throw new FileNotFoundException();
        }
    }

    public void noExceptions() {
        try {
            try {
                throw new IOException();
            } catch(IOException exinner) {
                // do nothing
            }

        } catch(Exception exouter) {
            // do nothing
        }

        System.out.println("asdf");
        try {
            System.out.println("try");
        } catch(Exception e) {
            System.out.println("catch");
        } finally {
            System.out.println("finally");
        }
    }

}

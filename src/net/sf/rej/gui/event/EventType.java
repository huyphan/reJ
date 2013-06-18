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
package net.sf.rej.gui.event;

public enum EventType {
INIT, // system init (accompanied by EventDispatcher)

PROJECT_UPDATE, // a project has been opened (accompanied by Project or null if the project was closed)

CLASS_OPEN, // a class has been opened (accompanied by ClassFile - and filename where appropriate)
CLASS_UPDATE, // a change in the class that's currently open
CLASS_REPARSE, // the currently open class has been modified in a way that requires full reparsing of the class
CLASS_PARSE_ERROR, // the class could not be parsed

DEBUG_ATTACH, // reJ has been attached to another JVM
DEBUG_DETACH, // reJ has been detached from another JVM
DEBUG_THREAD_CHANGE_REQUESTED,
DEBUG_THREAD_CHANGED,
DEBUG_STACK_FRAME_CHANGE_REQUESTED,
DEBUG_STACK_FRAME_CHANGED,
DEBUG_SUSPEND_REQUESTED, // reJ debug suspend was requested
DEBUG_RESUME_REQUESTED, // reJ debug suspend was requested
DEBUG_SUSPENDED, // reJ debugged process was suspended (accompanied by vm)
DEBUG_RESUMED, // reJ debugged process was resumed
DEBUG_STEP_INTO_REQUESTED, // step into was requested
DEBUG_STEP_OVER_REQUESTED, // step over was requested
DEBUG_STEP_OUT_REQUESTED, // step out was requested

DISPLAY_PARAMETER_UPDATE // line identifier, etc
}

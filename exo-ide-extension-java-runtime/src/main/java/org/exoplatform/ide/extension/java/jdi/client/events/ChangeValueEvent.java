/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.java.jdi.client.events;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

/**
 * Event occurs when user tries to change variable value.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ChangeValueEvent.java Apr 28, 2012 10:05:37 AM azatsarynnyy $
 */
public class ChangeValueEvent extends GwtEvent<ChangeValueHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<ChangeValueHandler> TYPE = new GwtEvent.Type<ChangeValueHandler>();

    /** Connected debugger information. */
    private DebuggerInfo debuggerInfo;

    /** Variable whose value need to change. */
    private Variable var;

    /**
     * @param debuggerInfo
     *         connected debugger
     * @param var
     *         variable
     */
    public ChangeValueEvent(DebuggerInfo debuggerInfo, Variable var) {
        this.debuggerInfo = debuggerInfo;
        this.var = var;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ChangeValueHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ChangeValueHandler handler) {
        handler.onChangeValue(this);
    }

    /**
     * Returns the connected debugger information.
     *
     * @return debugger information
     */
    public DebuggerInfo getDebuggerInfo() {
        return debuggerInfo;
    }

    /**
     * Get the variable whose value need to change.
     *
     * @return variable
     */
    public Variable getVariable() {
        return var;
    }

}

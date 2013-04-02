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

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: EvaluateExpressionEvent.java May 7, 2012 12:25:37 PM azatsarynnyy $
 */
public class EvaluateExpressionEvent extends GwtEvent<EvaluateExpressionHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<EvaluateExpressionHandler> TYPE = new GwtEvent.Type<EvaluateExpressionHandler>();

    /** Connected debugger information. */
    private DebuggerInfo debuggerInfo;

    /**
     * @param debuggerInfo
     *         connected debugger
     */
    public EvaluateExpressionEvent(DebuggerInfo debuggerInfo) {
        this.debuggerInfo = debuggerInfo;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EvaluateExpressionHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EvaluateExpressionHandler handler) {
        handler.onEvaluateExpression(this);
    }

    /**
     * Returns the connected debugger information.
     *
     * @return debugger information
     */
    public DebuggerInfo getDebuggerInfo() {
        return debuggerInfo;
    }

}

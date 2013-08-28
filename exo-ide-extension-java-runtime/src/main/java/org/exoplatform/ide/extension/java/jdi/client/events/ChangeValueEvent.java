/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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

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

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class DebuggerConnectedEvent extends GwtEvent<DebuggerConnectedHandler> {

    /** Type used to register event. */
    public static final GwtEvent.Type<DebuggerConnectedHandler> TYPE = new GwtEvent.Type<DebuggerConnectedHandler>();

    /** VFS id. */
    private DebuggerInfo debuggerInfo;

    /** @param debuggerInfo */
    public DebuggerConnectedEvent(DebuggerInfo debuggerInfo) {
        this.debuggerInfo = debuggerInfo;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DebuggerConnectedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(DebuggerConnectedHandler handler) {
        handler.onDebuggerConnected(this);
    }

    public DebuggerInfo getDebuggerInfo() {
        return debuggerInfo;
    }

}

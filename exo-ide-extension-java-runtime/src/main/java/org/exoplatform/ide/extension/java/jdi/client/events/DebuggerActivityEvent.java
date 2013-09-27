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

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class DebuggerActivityEvent extends GwtEvent<DebuggerActivityHandler> {
    public static final GwtEvent.Type<DebuggerActivityHandler> TYPE = new GwtEvent.Type<DebuggerActivityHandler>();

    boolean state;

    public DebuggerActivityEvent(boolean state) {
        this.state = state;
    }

    @Override
    public Type<DebuggerActivityHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DebuggerActivityHandler handler) {
        handler.onDebuggerActivityChanged(this);
    }

    public boolean getState() {
        return state;
    }
}

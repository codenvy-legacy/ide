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

import org.exoplatform.ide.extension.java.jdi.client.EditorBreakPoint;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 3:42:30 PM Mar 28, 2012 evgen $
 */
public class BreakPointsUpdatedEvent extends GwtEvent<BreakPointsUpdatedHandler> {

    public static final GwtEvent.Type<BreakPointsUpdatedHandler> TYPE = new Type<BreakPointsUpdatedHandler>();

    private Map<String, Set<EditorBreakPoint>> breakPoints;

    /** @param breakPoints */
    public BreakPointsUpdatedEvent(Map<String, Set<EditorBreakPoint>> breakPoints) {
        super();
        this.breakPoints = breakPoints;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<BreakPointsUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(BreakPointsUpdatedHandler handler) {
        handler.onBreakPointsUpdated(this);
    }

    /** @return the breakPoints */
    public Map<String, Set<EditorBreakPoint>> getBreakPoints() {
        return breakPoints;
    }

}

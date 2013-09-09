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

import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class BreakPointAddedEvent extends GwtEvent<BreakPointAddedHandler> {

    private BreakPoint breakPoint;

    public BreakPointAddedEvent(BreakPoint breakPoint) {
        this.breakPoint = breakPoint;
    }

    public static final GwtEvent.Type<BreakPointAddedHandler> TYPE = new GwtEvent.Type<BreakPointAddedHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<BreakPointAddedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(BreakPointAddedHandler handler) {
        handler.onAddBreakPoint(this);
    }

    public BreakPoint getBreakPoint() {
        return breakPoint;
    }

}

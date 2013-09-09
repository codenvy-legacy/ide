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
package org.exoplatform.ide.extension.maven.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class BuildProjectStopEvent extends GwtEvent<BuildProjectStopHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<BuildProjectStopHandler> TYPE = new Type<BuildProjectStopHandler>();

    /**
     *
     */
    public BuildProjectStopEvent() {
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public GwtEvent.Type<BuildProjectStopHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(BuildProjectStopHandler handler) {
        handler.onBuildProjectStopEvent(this);
    }
}

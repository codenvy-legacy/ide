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
package org.exoplatform.ide.extension.openshift.client.domain;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to create new domain. Implement {@link CreateDomainHandler} to handle event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 6, 2011 2:52:28 PM anya $
 */
public class CreateDomainEvent extends GwtEvent<CreateDomainHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<CreateDomainHandler> TYPE = new GwtEvent.Type<CreateDomainHandler>();

    /** Indicates that windows is showed from application info list, that after update namespace will be fired ShowApplicationListEvent */
    private boolean fromUserInfo;

    public CreateDomainEvent() {
    }

    public CreateDomainEvent(boolean fromUserInfo) {
        this.fromUserInfo = fromUserInfo;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CreateDomainHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(CreateDomainHandler handler) {
        handler.onCreateDomain(this);
    }

    public boolean isFromUserInfo() {
        return fromUserInfo;
    }

    public void setFromUserInfo(boolean fromUserInfo) {
        this.fromUserInfo = fromUserInfo;
    }
}

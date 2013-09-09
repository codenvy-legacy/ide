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
package org.exoplatform.ide.extension.cloudbees.client.delete;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when CloudBees application is deleted.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 8, 2011 2:39:55 PM anya $
 */
public class ApplicationDeletedEvent extends GwtEvent<ApplicationDeletedHandler> {

    /** Type used to register event. */
    public static final GwtEvent.Type<ApplicationDeletedHandler> TYPE = new GwtEvent.Type<ApplicationDeletedHandler>();

    /** Deleted application's id. */
    private String applicationId;

    /**
     * @param applicationId
     *         deleted application id
     */
    public ApplicationDeletedEvent(String applicationId) {
        this.applicationId = applicationId;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationDeletedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ApplicationDeletedHandler handler) {
        handler.onApplicationDeleted(this);
    }

    /** @return {@link String} id of the deleted application */
    public String getApplicationId() {
        return applicationId;
    }
}

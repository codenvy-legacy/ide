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
 * Event, which set to control to delete application.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationEvent.java Jul 1, 2011 12:56:44 PM vereshchaka $
 */
public class DeleteApplicationEvent extends GwtEvent<DeleteApplicationHandler> {

    private String appId;

    private String appTitle;

    /**
     *
     */
    public DeleteApplicationEvent() {
    }

    /**
     * @param appId
     * @param appTitle
     */
    public DeleteApplicationEvent(String appId, String appTitle) {
        super();
        this.appId = appId;
        this.appTitle = appTitle;
    }

    /** Type used to register this event. */
    public static final GwtEvent.Type<DeleteApplicationHandler> TYPE = new GwtEvent.Type<DeleteApplicationHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DeleteApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(DeleteApplicationHandler handler) {
        handler.onDeleteApplication(this);
    }

    /** @return the appId */
    public String getAppId() {
        return appId;
    }

    /** @return the appTitle */
    public String getAppTitle() {
        return appTitle;
    }

}

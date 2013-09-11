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
package org.exoplatform.ide.extension.cloudbees.client.update;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UpdateApplicationEvent.java Oct 10, 2011 5:07:26 PM vereshchaka $
 */
public class UpdateApplicationEvent extends GwtEvent<UpdateApplicationHandler> {
    private String appId;

    private String appTitle;

    /**
     *
     */
    public UpdateApplicationEvent() {
    }

    /**
     * @param appId
     * @param appTitle
     */
    public UpdateApplicationEvent(String appId, String appTitle) {
        super();
        this.appId = appId;
        this.appTitle = appTitle;
    }

    /** Type used to register this event. */
    public static final GwtEvent.Type<UpdateApplicationHandler> TYPE = new GwtEvent.Type<UpdateApplicationHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(UpdateApplicationHandler handler) {
        handler.onUpdateApplication(this);
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

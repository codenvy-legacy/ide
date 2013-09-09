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
package org.exoplatform.ide.extension.cloudbees.client.info;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;

/**
 * Event, which set to control to show application info.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationInfoEvent.java Jun 30, 2011 4:57:08 PM vereshchaka $
 */
public class ApplicationInfoEvent extends GwtEvent<ApplicationInfoHandler> {

    private ApplicationInfo appInfo;

    /**
     *
     */
    public ApplicationInfoEvent() {
    }

    /** @param appInfo */
    public ApplicationInfoEvent(ApplicationInfo appInfo) {
        super();
        this.appInfo = appInfo;
    }

    /** Type used to register this event. */
    public static final GwtEvent.Type<ApplicationInfoHandler> TYPE = new GwtEvent.Type<ApplicationInfoHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationInfoHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ApplicationInfoHandler handler) {
        handler.onShowApplicationInfo(this);
    }

    /** @return the appInfo */
    public ApplicationInfo getAppInfo() {
        return appInfo;
    }

}

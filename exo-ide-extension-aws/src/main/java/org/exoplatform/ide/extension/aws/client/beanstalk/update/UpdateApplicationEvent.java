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
package org.exoplatform.ide.extension.aws.client.beanstalk.update;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;

/**
 * Event occurs when user tries to update application.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 19, 2012 4:31:32 PM anya $
 */
public class UpdateApplicationEvent extends GwtEvent<UpdateApplicationHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<UpdateApplicationHandler> TYPE = new GwtEvent.Type<UpdateApplicationHandler>();

    private ApplicationUpdatedHandler applicationUpdatedHandler;

    private String vfsId;

    private String projectId;

    private ApplicationInfo applicationInfo;

    public UpdateApplicationEvent(String vfsId, String projectId, ApplicationInfo applicationInfo,
                                  ApplicationUpdatedHandler applicationUpdatedHandler) {
        this.vfsId = vfsId;
        this.projectId = projectId;
        this.applicationInfo = applicationInfo;
        this.applicationUpdatedHandler = applicationUpdatedHandler;
    }

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

    /** @return the applicationUpdatedHandler */
    public ApplicationUpdatedHandler getApplicationUpdatedHandler() {
        return applicationUpdatedHandler;
    }

    /** @return the vfsId */
    public String getVfsId() {
        return vfsId;
    }

    /** @return the projectId */
    public String getProjectId() {
        return projectId;
    }

    /** @return the applicationInfo */
    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}

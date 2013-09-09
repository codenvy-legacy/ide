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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;

/**
 * Event occurs, when user tries to delete application's version.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 4:52:19 PM anya $
 */
public class DeleteVersionEvent extends GwtEvent<DeleteVersionHandler> {

    /** Type, used to register event. */
    public static final GwtEvent.Type<DeleteVersionHandler> TYPE = new GwtEvent.Type<DeleteVersionHandler>();

    private String vfsId;

    private String projectId;

    private ApplicationVersionInfo version;

    private VersionDeletedHandler versionDeletedHandler;

    public DeleteVersionEvent(String vfsId, String projectId, ApplicationVersionInfo version,
                              VersionDeletedHandler versionDeletedHandler) {
        this.projectId = projectId;
        this.vfsId = vfsId;
        this.version = version;
        this.versionDeletedHandler = versionDeletedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DeleteVersionHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(DeleteVersionHandler handler) {
        handler.onDeleteVersion(this);
    }

    /** @return the vfsId */
    public String getVfsId() {
        return vfsId;
    }

    /** @return the projectId */
    public String getProjectId() {
        return projectId;
    }

    /** @return the version */
    public ApplicationVersionInfo getVersion() {
        return version;
    }

    /** @return the versionDeletedHandler */
    public VersionDeletedHandler getVersionDeletedHandler() {
        return versionDeletedHandler;
    }
}

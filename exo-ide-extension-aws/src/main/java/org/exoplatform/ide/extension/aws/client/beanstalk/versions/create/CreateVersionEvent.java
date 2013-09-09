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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.create;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Event occurs, when user tries to create new version.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 11:44:58 AM anya $
 */
public class CreateVersionEvent extends GwtEvent<CreateVersionHandler> {
    public static final GwtEvent.Type<CreateVersionHandler> TYPE = new GwtEvent.Type<CreateVersionHandler>();

    private String vfsId;

    private ProjectModel project;

    private String applicationName;

    private VersionCreatedHandler versionCreatedHandler;

    public CreateVersionEvent(String vfsId, ProjectModel project, String applicationName,
                              VersionCreatedHandler versionCreatedHandler) {
        this.vfsId = vfsId;
        this.project = project;
        this.applicationName = applicationName;
        this.versionCreatedHandler = versionCreatedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CreateVersionHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(CreateVersionHandler handler) {
        handler.onCreateVersion(this);
    }

    /** @return the vfsId */
    public String getVfsId() {
        return vfsId;
    }

    /** @return the project */
    public ProjectModel getProject() {
        return project;
    }

    /** @return the applicationName */
    public String getApplicationName() {
        return applicationName;
    }

    /** @return the versionCreatedHandler */
    public VersionCreatedHandler getVersionCreatedHandler() {
        return versionCreatedHandler;
    }
}

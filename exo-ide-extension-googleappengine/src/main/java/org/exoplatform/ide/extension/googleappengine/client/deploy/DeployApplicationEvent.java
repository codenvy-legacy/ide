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
package org.exoplatform.ide.extension.googleappengine.client.deploy;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Event occurs, when user tries to deploy application to Google App engine.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 16, 2012 4:47:12 PM anya $
 */
public class DeployApplicationEvent extends GwtEvent<DeployApplicationHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<DeployApplicationHandler> TYPE = new GwtEvent.Type<DeployApplicationHandler>();

    private ProjectModel project;

    public DeployApplicationEvent() {
        this.project = null;
    }

    public DeployApplicationEvent(ProjectModel project) {
        this.project = project;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DeployApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(DeployApplicationHandler handler) {
        handler.onDeployApplication(this);
    }

    /** @return the project */
    public ProjectModel getProject() {
        return project;
    }
}

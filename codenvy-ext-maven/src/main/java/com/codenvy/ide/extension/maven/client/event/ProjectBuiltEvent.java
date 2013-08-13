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
package com.codenvy.ide.extension.maven.client.event;

import com.codenvy.ide.extension.maven.shared.BuildStatus;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when project has built by maven builder.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ProjectBuiltEvent.java Apr 3, 2012 12:23:44 PM azatsarynnyy $
 */
public class ProjectBuiltEvent extends GwtEvent<ProjectBuiltHandler> {
    /** Status of build. */
    private BuildStatus status;

    /** Type used to register this event. */
    public static final GwtEvent.Type<ProjectBuiltHandler> TYPE = new Type<ProjectBuiltHandler>();

    /**
     * @param status
     *         status of build
     */
    public ProjectBuiltEvent(BuildStatus status) {
        this.status = status;
    }

    /** {@inheritDoc} */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ProjectBuiltHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(ProjectBuiltHandler handler) {
        handler.onProjectBuilt(this);
    }

    /**
     * Returns the status of build project.
     *
     * @return the build status
     */
    public BuildStatus getBuildStatus() {
        return status;
    }
}
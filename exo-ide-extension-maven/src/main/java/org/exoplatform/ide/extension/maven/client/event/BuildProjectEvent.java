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
package org.exoplatform.ide.extension.maven.client.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Event occurs, when user tries to build project by maven builder.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectEvent.java Feb 17, 2012 4:04:56 PM azatsarynnyy $
 */
public class BuildProjectEvent extends GwtEvent<BuildProjectHandler> {
    /** Project for build. */
    private ProjectModel project;

    private final boolean publish;

    private final boolean force;

    public BuildProjectEvent() {
        this(false);
    }

    /**
     * If <code>publish</code> artifact will be in public repository after build.
     * By default set to false
     *
     * @param publish
     */
    public BuildProjectEvent(boolean publish) {
        this(null, publish);
    }

    /**
     * If <code>publish</code> artifact will be in public repository after build.
     * <code>force</code> project will be build  even if it not change from last build
     * By default set to false
     *
     * @param publish
     */
    public BuildProjectEvent(boolean publish, boolean force) {
        this(null, publish, force);
    }

    /** @param project */
    public BuildProjectEvent(ProjectModel project) {
        this(project, false);
    }

    /**
     * If <code>publish</code> artifact will be in public repository after build.
     * By default set to false
     *
     * @param project
     * @param publish
     */
    public BuildProjectEvent(ProjectModel project, boolean publish) {
        this(project, publish, false);
    }

    public BuildProjectEvent(ProjectModel project, boolean publish, boolean force) {
        this.project = project;
        this.publish = publish;
        this.force = force;
    }

    /** Type used to register this event. */
    public static final GwtEvent.Type<BuildProjectHandler> TYPE = new GwtEvent.Type<BuildProjectHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<BuildProjectHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(BuildProjectHandler handler) {
        handler.onBuildProject(this);
    }

    /**
     * Get the project for build.
     *
     * @return the project
     */
    public ProjectModel getProject() {
        return project;
    }

    public boolean isPublish() {
        return publish;
    }

    public boolean isForce() {
        return force;
    }
}

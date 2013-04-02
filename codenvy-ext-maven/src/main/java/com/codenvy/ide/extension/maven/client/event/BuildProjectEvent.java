/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.maven.client.event;

import com.codenvy.ide.resources.model.Project;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to build project by maven builder.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectEvent.java Feb 17, 2012 4:04:56 PM azatsarynnyy $
 */
public class BuildProjectEvent extends GwtEvent<BuildProjectHandler> {
    /** Project for build. */
    private Project project;

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
    public BuildProjectEvent(Project project) {
        this(project, false);
    }

    /**
     * If <code>publish</code> artifact will be in public repository after build.
     * By default set to false
     *
     * @param project
     * @param publish
     */
    public BuildProjectEvent(Project project, boolean publish) {
        this(project, publish, false);
    }

    public BuildProjectEvent(Project project, boolean publish, boolean force) {
        this.project = project;
        this.publish = publish;
        this.force = force;
    }

    /** Type used to register this event. */
    public static final GwtEvent.Type<BuildProjectHandler> TYPE = new GwtEvent.Type<BuildProjectHandler>();

    /** {@inheritDoc} */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<BuildProjectHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(BuildProjectHandler handler) {
        handler.onBuildProject(this);
    }

    /**
     * Get the project for build.
     *
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    public boolean isPublish() {
        return publish;
    }

    public boolean isForce() {
        return force;
    }
}
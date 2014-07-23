/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.event;

import com.codenvy.api.project.shared.dto.ProjectReference;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that should be fired in order to open some project.
 *
 * @author Artem Zatsarynnyy
 */
public class OpenProjectEvent extends GwtEvent<OpenProjectHandler> {

    public static Type<OpenProjectHandler> TYPE = new Type<>();
    private final ProjectReference project;

    /**
     * Creates a new open project event.
     *
     * @param project
     *         project to open
     */
    public OpenProjectEvent(ProjectReference project) {
        this.project = project;
    }

    @Override
    public Type<OpenProjectHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return project to open */
    public ProjectReference getProject() {
        return project;
    }

    @Override
    protected void dispatch(OpenProjectHandler handler) {
        handler.onOpenProject(this);
    }
}

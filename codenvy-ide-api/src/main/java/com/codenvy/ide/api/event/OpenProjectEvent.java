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

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that should be fired in order to open a project.
 *
 * @author Artem Zatsarynnyy
 */
public class OpenProjectEvent extends GwtEvent<OpenProjectHandler> {

    public static Type<OpenProjectHandler> TYPE = new Type<>();
    private final String projectName;

    /**
     * Creates an event to initiate opening the specified project.
     *
     * @param projectName
     *         name of the project to open
     */
    public OpenProjectEvent(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public Type<OpenProjectHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Returns name of the project to open.
     *
     * @return name of the project to open
     */
    public String getProjectName() {
        return projectName;
    }

    @Override
    protected void dispatch(OpenProjectHandler handler) {
        handler.onOpenProject(this);
    }
}

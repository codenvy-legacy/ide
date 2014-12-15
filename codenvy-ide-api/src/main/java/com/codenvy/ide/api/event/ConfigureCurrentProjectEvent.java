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
 * An event that should be fired in order to configure the currently opened project.
 *
 * @author Artem Zatsarynnyy
 */
public class ConfigureCurrentProjectEvent extends GwtEvent<ConfigureCurrentProjectHandler> {
    /** Type class used to register this event. */
    public static Type<ConfigureCurrentProjectHandler> TYPE = new Type<>();
    private final boolean problemsOnly;

    /** Creates new {@link ConfigureCurrentProjectEvent}. */
    public ConfigureCurrentProjectEvent() {
        this(false);
    }

    /**
     * Creates new {@link ConfigureCurrentProjectEvent}.
     *
     * @param problemsOnly
     *         {@code true} - only project with problems should be configured, {@code false} - any project
     */
    public ConfigureCurrentProjectEvent(boolean problemsOnly) {
        this.problemsOnly = problemsOnly;
    }

    @Override
    public Type<ConfigureCurrentProjectHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ConfigureCurrentProjectHandler handler) {
        handler.onConfigureCurrentProject(this);
    }

    /** @return {@code true} - only project with problems should be configured, {@code false} - any project */
    public boolean isProblemsOnly() {
        return problemsOnly;
    }
}

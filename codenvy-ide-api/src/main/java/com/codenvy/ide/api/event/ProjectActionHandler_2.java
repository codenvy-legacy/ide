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

import com.google.gwt.event.shared.EventHandler;

/**
 * TODO: rename handler
 * A handler for handling {@link ProjectActionEvent_2}.
 *
 * @author Artem Zatsarynnyy
 */
public interface ProjectActionHandler_2 extends EventHandler {
    /**
     * Called when someone is going to open a project.
     *
     * @param event
     *         the fired {@link ProjectActionEvent_2}
     */
    void onOpenProject(ProjectActionEvent_2 event);

    /**
     * Called when someone is going to close the currently opened project.
     *
     * @param event
     *         the fired {@link ProjectActionEvent_2}
     */
    void onCloseProject(ProjectActionEvent_2 event);
}

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
 * Resource API fires ProjectAction Events when project any kind of operations that
 * changes the project invoked. Those are opening, closing, changing the description.
 *
 * @author Nikolay Zamosenchuk
 */
public interface ProjectActionHandler extends EventHandler {
    /**
     * Project opened
     *
     * @param event
     */
    void onProjectOpened(ProjectActionEvent event);

    /**
     * Project closed
     *
     * @param event
     */
    void onProjectClosed(ProjectActionEvent event);

    /**
     * Project Description Changed
     *
     * @param event
     */
    void onProjectDescriptionChanged(ProjectActionEvent event);
}

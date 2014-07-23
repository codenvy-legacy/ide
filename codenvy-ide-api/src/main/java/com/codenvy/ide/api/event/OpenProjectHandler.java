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
 * A handler for handling {@link OpenProjectEvent}.
 *
 * @author Artem Zatsarynnyy
 */
public interface OpenProjectHandler extends EventHandler {
    /**
     * Called when {@link OpenProjectEvent} is fired.
     *
     * @param event
     *         the {@link OpenProjectEvent} that contains project to open
     */
    void onOpenProject(OpenProjectEvent event);
}

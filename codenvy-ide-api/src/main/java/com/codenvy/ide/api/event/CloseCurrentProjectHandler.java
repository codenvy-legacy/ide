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
 * A handler for handling {@link CloseCurrentProjectEvent}.
 *
 * @author Artem Zatsarynnyy
 */
public interface CloseCurrentProjectHandler extends EventHandler {
    /**
     * Called when {@link CloseCurrentProjectEvent} is fired.
     *
     * @param event
     *         the fired {@link CloseCurrentProjectEvent}
     */
    void onClose(CloseCurrentProjectEvent event);
}

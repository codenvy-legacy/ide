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
 * Handles ResourceChangedEvent
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ResourceChangedHandler extends EventHandler {
    /**
     * Resource created
     *
     * @param event
     */
    void onResourceCreated(ResourceChangedEvent event);

    /**
     * Resource deleted
     *
     * @param event
     */
    void onResourceDeleted(ResourceChangedEvent event);

    /**
     * Resource renamed
     *
     * @param event
     */
    void onResourceRenamed(ResourceChangedEvent event);

    /**
     * Resource moved
     *
     * @param event
     */
    void onResourceMoved(ResourceChangedEvent event);
    
    /**
     * Resource tree refreshed
     * 
     * @param event
     */
    void onResourceTreeRefreshed(ResourceChangedEvent event);
}

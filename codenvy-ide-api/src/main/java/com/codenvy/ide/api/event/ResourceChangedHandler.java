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

}

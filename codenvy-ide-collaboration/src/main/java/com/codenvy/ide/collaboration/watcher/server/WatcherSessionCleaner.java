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
package com.codenvy.ide.collaboration.watcher.server;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WatcherSessionCleaner implements HttpSessionListener {
    /** {@inheritDoc} */
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    /** {@inheritDoc} */
    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        ExoContainer container = getContainer();
        if (container != null) {
            VfsWatcher vfsWatcher = (VfsWatcher)container.getComponentInstanceOfType(VfsWatcher.class);
            vfsWatcher.sessionDestroyed(httpSessionEvent.getSession().getId());
        }
    }

    protected ExoContainer getContainer() {
        return ExoContainerContext.getCurrentContainerIfPresent();
    }
}

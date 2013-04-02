/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

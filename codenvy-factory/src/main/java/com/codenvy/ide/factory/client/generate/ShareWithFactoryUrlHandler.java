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
package com.codenvy.ide.factory.client.generate;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ShareWithFactoryUrlEvent} event.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ShareWithFactoryUrlHandler.java Jun 11, 2013 11:26:11 AM azatsarynnyy $
 */
public interface ShareWithFactoryUrlHandler extends EventHandler {
    /**
     * Perform actions, when user tries to share opened project with Factory URL.
     * 
     * @param event {@link ShareWithFactoryUrlEvent}
     */
    void onShare(ShareWithFactoryUrlEvent event);
}

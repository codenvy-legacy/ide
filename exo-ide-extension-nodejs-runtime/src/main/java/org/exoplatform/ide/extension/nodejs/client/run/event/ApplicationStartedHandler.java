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
package org.exoplatform.ide.extension.nodejs.client.run.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ApplicationStartedEvent} event.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: ApplicationStoppedEvent.java Apr 18, 2013 5:12:23 PM vsvydenko $
 *
 */
public interface ApplicationStartedHandler extends EventHandler {
    /**
     * Perform actions, when Node.js application has started.
     *
     * @param event
     */
    void onApplicationStarted(ApplicationStartedEvent event);
}

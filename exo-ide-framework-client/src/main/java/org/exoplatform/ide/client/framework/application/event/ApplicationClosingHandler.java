/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.application.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ApplicationClosingEvent} event.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: Apr 20, 2012 11:27:00 AM azatsarynnyy $
 * 
 */
public interface ApplicationClosingHandler extends EventHandler
{
   /**
    * Perform actions, before the browser window closes
    * or navigates to a different site.
    * 
    * @param event the event
    */
   void onApplicationClosing(ApplicationClosingEvent event);
}

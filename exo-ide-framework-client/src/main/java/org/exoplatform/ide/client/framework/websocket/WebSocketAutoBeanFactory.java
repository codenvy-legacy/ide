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
package org.exoplatform.ide.client.framework.websocket;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketAutoBeanFactory.java Jul 13, 2012 5:25:39 PM azatsarynnyy $
 *
 */
public interface WebSocketAutoBeanFactory extends AutoBeanFactory
{
   /**
    * A factory method for a WebSocket message bean.
    * 
    * @return an {@link AutoBean} of type {@link WebSocketMessage}
    */
   AutoBean<WebSocketMessage> webSocketMessage();

   /**
    * A factory method for a WebSocket message bean which contains description of the exception.
    * 
    * @return an {@link AutoBean} of type {@link WebSocketExceptionMessage}
    */
   AutoBean<WebSocketExceptionMessage> webSocketExceptionMessage();
}

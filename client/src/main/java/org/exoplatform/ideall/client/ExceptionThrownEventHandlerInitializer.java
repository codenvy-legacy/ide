/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.handler.ExceptionThrownHandlerImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ExceptionThrownEventHandlerInitializer
{

   private static HandlerRegistration handler = null;

   public static void initialize(HandlerManager eventBus)
   {
//      if (eventBus.getHandlerCount(ExceptionThrownEvent.TYPE) > 0)
//      {
//         Window.alert("present " + eventBus.getHandlerCount(ExceptionThrownEvent.TYPE));
//         return;
//      }

      if (handler != null) {
         return;
      }
      
      if (GWT.isScript()) {
         handler = eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandlerImpl());
      } else {
         /*
          * shell mode
          */
         handler = eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandlerImpl());
         //handler = eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandlerImplEx());         
      }
   }

   public static void clear()
   {
      if (handler == null)
      {
         return;
      }

      handler.removeHandler();
      handler = null;
   }

}

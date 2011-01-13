/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ExceptionThrownEventHandler implements ExceptionThrownHandler, EnableStandartErrorsHandlingHandler
{
   private boolean showErrors = true;

   public ExceptionThrownEventHandler(HandlerManager eventBus)
   {
      eventBus.addHandler(ExceptionThrownEvent.TYPE, this);
      eventBus.addHandler(EnableStandartErrorsHandlingEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      if (showErrors)
      {
         IDEExceptionThrownEventHandler.handlerEvent(event);
      }
   }

   /**
    * @see org.exoplatform.ide.client.event.EnableStandartErrorsHandlingHandler#onEnableStandartErrorsHandling(org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent)
    */
   public void onEnableStandartErrorsHandling(EnableStandartErrorsHandlingEvent event)
   {
      showErrors = event.isEnable();
   }

}

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
package org.exoplatform.ideall.client.module.edit;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.event.edit.GoToLineEvent;
import org.exoplatform.ideall.client.event.edit.GoToLineHandler;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.module.edit.event.FindTextEvent;
import org.exoplatform.ideall.client.module.edit.event.FindTextHandler;
import org.exoplatform.ideall.client.module.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ideall.client.module.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ideall.client.module.navigation.action.GoToLineForm;
import org.exoplatform.ideall.client.search.text.FindTextForm;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class FileEditModuleEventHandler implements RegisterEventHandlersHandler, ShowLineNumbersHandler,
   FindTextHandler, GoToLineHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   protected Handlers handlers;

   public FileEditModuleEventHandler(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.removeHandler(RegisterEventHandlersEvent.TYPE);

      handlers.addHandler(ShowLineNumbersEvent.TYPE, this);
      handlers.addHandler(FindTextEvent.TYPE, this);
      handlers.addHandler(GoToLineEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ideall.client.event.edit.ShowLineNumbersHandler#onShowLineNumbers(org.exoplatform.ideall.client.event.edit.ShowLineNumbersEvent)
    */
   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      context.setShowLineNumbers(event.isShowLineNumber());
      CookieManager.getInstance().storeLineNumbers(context);
   }

   /**
    * @see org.exoplatform.ideall.client.event.edit.FindTextHandler#onFindText(org.exoplatform.ideall.client.event.edit.FindTextEvent)
    */
   public void onFindText(FindTextEvent event)
   {
      new FindTextForm(eventBus, context);
   }

   public void onGoToLine(GoToLineEvent event)
   {
      if (context.getActiveFile() != null)
      {
         new GoToLineForm(eventBus, context);
      }
   }

}

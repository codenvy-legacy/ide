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
package org.exoplatform.ide.client.module.development;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ide.client.module.development.event.ShowOutlineHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class DevelopmentModuleEventHandler implements RegisterEventHandlersHandler, ShowOutlineHandler, ApplicationSettingsReceivedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;
   
   private ApplicationSettings applicationSettings;

   public DevelopmentModuleEventHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.addHandler(ShowOutlineEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.outline.event.ShowOutlineHandler#onShowOutline(org.exoplatform.ide.client.outline.event.ShowOutlineEvent)
    */
   public void onShowOutline(ShowOutlineEvent event)
   { 
      applicationSettings.setValue("outline", new Boolean(event.isShow()), Store.COOKIES);
//      applicationSettings.setStoredIn("outline", Store.COOKIES);
      //CookieManager.setShowOutline(event.isShow());
   }

}

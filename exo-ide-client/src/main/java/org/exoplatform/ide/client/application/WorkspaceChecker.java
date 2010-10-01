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
package org.exoplatform.ide.client.application;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.module.vfs.api.Folder;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WorkspaceChecker implements ExceptionThrownHandler, ItemPropertiesReceivedHandler
{

   private HandlerManager eventBus;

   private String entryPoint;

   private Handlers handlers;

   private ApplicationSettings applicationSettings;

   public WorkspaceChecker(HandlerManager eventBus, String entryPoint, ApplicationSettings applicationSettings)
   {
      this.eventBus = eventBus;
      this.entryPoint = entryPoint;
      this.applicationSettings = applicationSettings;
      handlers = new Handlers(eventBus);

      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);

      Folder rootFolder = new Folder(entryPoint);
      VirtualFileSystem.getInstance().getProperties(rootFolder);
   }

   public void onError(ExceptionThrownEvent event)
   {
      event.getError().printStackTrace();

      //      ServerException e = (ServerException)event.getError();

      handlers.removeHandlers();

      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
      Dialogs.getInstance().showError("Entry point <b>" + entryPoint + "</b> not found!");
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
      applicationSettings.setValue("entry-point", event.getItem().getHref(), Store.COOKIES);
      eventBus.fireEvent(new EntryPointChangedEvent(event.getItem().getHref()));
      new ApplicationStateLoader(eventBus, applicationSettings);
   }

}

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
package org.exoplatform.ideall.client.application;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.ideall.client.ExceptionThrownEventHandlerInitializer;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WorkspaceChecker implements ExceptionThrownHandler, ItemPropertiesReceivedHandler
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   public WorkspaceChecker(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      ExceptionThrownEventHandlerInitializer.clear();
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);

      Folder rootFolder = new Folder(context.getEntryPoint());
      VirtualFileSystem.getInstance().getProperties(rootFolder);
   }

   public void onError(ExceptionThrownEvent event)
   {
      System.out.println("error: " + event.getError());      
      event.getError().printStackTrace();
      
      ServerException e = (ServerException)event.getError();
      System.out.println("STATUS: " + e.getHTTPStatus());
      System.out.println("STATUS TEXT: " + e.getStatusText());
      
      handlers.removeHandlers();
      
      ExceptionThrownEventHandlerInitializer.initialize(eventBus);
      
      Dialogs.getInstance().showError("Entry point <b>" + context.getEntryPoint() + "</b> not found!");
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      handlers.removeHandlers();
      ExceptionThrownEventHandlerInitializer.initialize(eventBus);

      System.out.println("items received for: " + event.getItem().getHref());
      context.setEntryPoint(event.getItem().getHref());

      new ApplicationStateLoader(eventBus, context).loadState();
   }

}

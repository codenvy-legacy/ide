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
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ideall.client.ExceptionThrownEventHandlerInitializer;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.module.vfs.api.Folder;
import org.exoplatform.ideall.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.module.vfs.api.event.ItemPropertiesReceivedHandler;

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

   private String entryPoint;

   private Handlers handlers;
   
   private ApplicationContext context;

   public WorkspaceChecker(HandlerManager eventBus, String entryPoint, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.entryPoint = entryPoint;
      this.context = context;
      handlers = new Handlers(eventBus);

      ExceptionThrownEventHandlerInitializer.clear();
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);

      Folder rootFolder = new Folder(entryPoint);
      VirtualFileSystem.getInstance().getProperties(rootFolder);
   }

   public void onError(ExceptionThrownEvent event)
   {
      event.getError().printStackTrace();
      
      ServerException e = (ServerException)event.getError();
      
      handlers.removeHandlers();
      
      ExceptionThrownEventHandlerInitializer.initialize(eventBus);
      
      Dialogs.getInstance().showError("Entry point <b>" + entryPoint + "</b> not found!");
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      handlers.removeHandlers();
      ExceptionThrownEventHandlerInitializer.initialize(eventBus);
      
      new ApplicationStateLoader(eventBus, context).loadState();
   }

}

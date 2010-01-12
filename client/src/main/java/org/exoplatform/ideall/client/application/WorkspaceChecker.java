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

import org.exoplatform.gwt.commons.exceptions.ExceptionThrownEvent;
import org.exoplatform.gwt.commons.exceptions.ExceptionThrownHandler;
import org.exoplatform.gwt.commons.smartgwt.dialogs.Dialogs;
import org.exoplatform.ideall.client.ExceptionThrownEventHandlerInitializer;
import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.Workspace;
import org.exoplatform.ideall.client.model.data.DataService;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesReceivedHandler;

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

      String path = "/" + context.getRepository() + "/" + context.getWorkspace();
      Workspace workspace = new Workspace(path);
      DataService.getInstance().getProperties(workspace);
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
      ExceptionThrownEventHandlerInitializer.initialize(eventBus);
      Dialogs.showError("Workspace <b>" + context.getRepository() + "/" + context.getWorkspace() + "</b> not found!");
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      handlers.removeHandlers();
      ExceptionThrownEventHandlerInitializer.initialize(eventBus);
      new ApplicationStateLoader(eventBus, context).loadState();
   }

}

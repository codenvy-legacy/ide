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
import org.exoplatform.ideall.client.ExceptionThrownEventHandlerInitializer;
import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.data.DataService;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedHandler;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationStateLoader implements ItemPropertiesReceivedHandler, FileContentReceivedHandler,
   ExceptionThrownHandler
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   public ApplicationStateLoader(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
   }

   public void loadState()
   {
      ExceptionThrownEventHandlerInitializer.clear();

      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

      preloadNextFile();
   }

   private File fileToLoad;

   protected void preloadNextFile()
   {
      try {
         if (context.getPreloadFiles().size() == 0)
         {
            fileToLoad = null;
            handlers.removeHandlers();
            ExceptionThrownEventHandlerInitializer.initialize(eventBus);

            context.setInitialized(true);
            eventBus.fireEvent(new InitializeApplicationEvent());
            return;
         }

         fileToLoad = context.getPreloadFiles().values().iterator().next();
         context.getPreloadFiles().remove(fileToLoad.getPath());
         DataService.getInstance().getProperties(fileToLoad);
         
      } catch (Exception exc) {
         exc.printStackTrace();
      }      
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      fileToLoad.setNewFile(false);
      fileToLoad.setContentChanged(false);
      DataService.getInstance().getFileContent(fileToLoad);
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      context.getOpenedFiles().put(fileToLoad.getPath(), fileToLoad);
      preloadNextFile();
   }

   public void onError(ExceptionThrownEvent event)
   {
      preloadNextFile();
   }

}

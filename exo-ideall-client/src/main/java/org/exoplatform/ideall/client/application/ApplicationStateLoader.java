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
import org.exoplatform.ideall.client.ExceptionThrownEventHandlerInitializer;
import org.exoplatform.ideall.client.framework.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.module.vfs.api.File;
import org.exoplatform.ideall.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.module.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ideall.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.module.vfs.api.event.ItemPropertiesReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

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
   
   private void initializeApplication() {
      new Timer() {
         @Override
         public void run()
         {
            eventBus.fireEvent(new RegisterEventHandlersEvent());
            
            new Timer() {
               @Override
               public void run()
               {
                  try
                  {
                     eventBus.fireEvent(new InitializeApplicationEvent());
                  }
                  catch (Throwable e)
                  {
                     e.printStackTrace();
                  }                        
               }
               
            }.schedule(10);
         }
      }.schedule(10);      
   }

   protected void preloadNextFile()
   {
      try
      {
         if (context.getPreloadFiles().size() == 0)
         {
            fileToLoad = null;
            handlers.removeHandlers();

            ExceptionThrownEventHandlerInitializer.initialize(eventBus);
            
            initializeApplication();
            return;
         }

         fileToLoad = context.getPreloadFiles().values().iterator().next();
         context.getPreloadFiles().remove(fileToLoad.getHref());
         VirtualFileSystem.getInstance().getProperties(fileToLoad);
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      fileToLoad.setNewFile(false);
      fileToLoad.setContentChanged(false);
      VirtualFileSystem.getInstance().getContent(fileToLoad);
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      context.getOpenedFiles().put(fileToLoad.getHref(), fileToLoad);
      preloadNextFile();
   }

   public void onError(ExceptionThrownEvent event)
   {
      context.getOpenedFiles().remove(fileToLoad.getHref());
      preloadNextFile();
   }

}

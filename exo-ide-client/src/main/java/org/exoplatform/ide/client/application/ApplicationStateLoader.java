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
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeApplicationEvent;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

   private Handlers handlers;

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private ApplicationSettings applicationSettings;

   private List<String> filesToLoad;

   public ApplicationStateLoader(HandlerManager eventBus, ApplicationSettings applicationSettings)
   {
      this.eventBus = eventBus;
      this.applicationSettings = applicationSettings;
      handlers = new Handlers(eventBus);

      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));

      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

      filesToLoad = applicationSettings.getValueAsList("opened-files");      
      if (filesToLoad == null)
      {
         filesToLoad = new ArrayList<String>();
         applicationSettings.setValue("opened-files", filesToLoad, Store.REGISTRY);
      }

      preloadNextFile();
   }

   private File fileToLoad;

   private void initializeApplication()
   {
      new Timer()
      {
         @Override
         public void run()
         {
            eventBus.fireEvent(new RegisterEventHandlersEvent());

            new Timer()
            {
               @Override
               public void run()
               {
                  try
                  {
                     String activeFile = applicationSettings.getValueAsString("active-file");
                     eventBus.fireEvent(new InitializeApplicationEvent(openedFiles, activeFile));
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
         if (filesToLoad.size() == 0)
         {
            fileToLoad = null;
            handlers.removeHandlers();

            eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
            initializeApplication();
            return;
         }

         String href = filesToLoad.get(0);
         
         fileToLoad = new File(href);
         filesToLoad.remove(0);
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
      openedFiles.put(fileToLoad.getHref(), fileToLoad);
      preloadNextFile();
   }

   public void onError(ExceptionThrownEvent event)
   {
      openedFiles.remove(fileToLoad.getHref());
      preloadNextFile();
   }

}

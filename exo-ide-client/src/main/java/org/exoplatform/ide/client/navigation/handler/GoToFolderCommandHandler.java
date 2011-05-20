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
package org.exoplatform.ide.client.navigation.handler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.navigation.event.GoToFolderEvent;
import org.exoplatform.ide.client.navigation.event.GoToFolderHandler;

import java.util.ArrayList;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GoToFolderCommandHandler implements GoToFolderHandler, 
   EntryPointChangedHandler, EditorActiveFileChangedHandler
{

   private HandlerManager eventBus;

   private String pathToOpen;

   private ArrayList<String> pathes;

   private String entryPoint;

   private File activeFile;

   public GoToFolderCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      eventBus.addHandler(GoToFolderEvent.TYPE, this);
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * Go To Folder command handler
    * 
    */
   public void onGoToFolder(GoToFolderEvent event)
   {
      if (activeFile == null)
      {
         return;
      }

      String workingPath = activeFile.getHref();

      String entryPoint = this.entryPoint.substring(0, this.entryPoint.lastIndexOf("/"));
      entryPoint = entryPoint.substring(0, entryPoint.lastIndexOf("/"));
      workingPath = workingPath.substring(entryPoint.length(), workingPath.length());

      while (workingPath.startsWith("/"))
      {
         workingPath = workingPath.substring(1);
      }

      workingPath = workingPath.substring(0, workingPath.lastIndexOf("/"));
      String[] p = workingPath.split("/");
      pathes = new ArrayList<String>();
      pathToOpen = entryPoint + "/" + p[0] + "/";
      if (p.length > 1)
      {
         for (int i = 1; i < p.length; i++)
         {
            pathes.add(p[i]);
         }
      }

      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(pathToOpen)));
      openNextFolder(new Folder(pathToOpen));
   }
   
   public void openNextFolder(Folder folderToOpen)
   {
      /*
       * Folder content received handler.
       * Get subfolder content here
       */
      VirtualFileSystem.getInstance().getChildren(folderToOpen, new AsyncRequestCallback<Folder>()
      {
         
         @Override
         protected void onSuccess(Folder result)
         {
            new Timer()
            {
               @Override
               public void run()
               {
                  if (pathes.size() > 0)
                  {
                     String name = pathes.get(0);
                     pathes.remove(0);
                     pathToOpen += name + "/";
                     eventBus.fireEvent(new RefreshBrowserEvent(new Folder(pathToOpen)));
                     openNextFolder(new Folder(pathToOpen));
                  }
                  else
                  {
                     // try to select file.........
                     eventBus.fireEvent(new SelectItemEvent(activeFile.getHref()));
                  }
               }
            }.schedule(100);
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            exception.printStackTrace();
            eventBus.fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed.<br>Parent folder not found."));
         }
      });
   }

}

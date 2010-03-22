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
package org.exoplatform.ideall.client.command;

import java.util.ArrayList;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ideall.client.event.browse.GoToFolderEvent;
import org.exoplatform.ideall.client.event.browse.GoToFolderHandler;
import org.exoplatform.ideall.client.event.browse.SetFocusOnItemEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.ChildrenReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ChildrenReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GoToFolderCommandHandler implements GoToFolderHandler, ChildrenReceivedHandler,
   ExceptionThrownHandler
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private String pathToOpen;

   private ArrayList<String> pathes;

   public GoToFolderCommandHandler(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(GoToFolderEvent.TYPE, this);
   }

   /**
    * Go To Folder command handler
    * 
    */
   public void onGoToFolder(GoToFolderEvent event)
   {
      if (context.getActiveFile() == null)
      {
         return;
      }

      String workingPath = context.getActiveFile().getPath();
      while (workingPath.startsWith("/"))
      {
         workingPath = workingPath.substring(1);
      }
      workingPath = workingPath.substring(0, workingPath.lastIndexOf("/"));

      String p[] = workingPath.split("/");
      pathes = new ArrayList<String>();
      pathToOpen = "/" + p[0] + "/" + p[1];

      if (p.length > 2)
      {
         for (int i = 2; i < p.length; i++)
         {
            pathes.add(p[i]);
         }
      }

      handlers.addHandler(ChildrenReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      VirtualFileSystem.getInstance().getChildren(new Folder(pathToOpen));
   }

   /**
    * Folder content received handler.
    * Get subfolder content here
    * 
    */
   public void onChildrenReceived(ChildrenReceivedEvent event)
   {
      if (pathes.size() > 0)
      {
         String name = pathes.get(0);
         pathes.remove(0);
         pathToOpen += "/" + name;

         VirtualFileSystem.getInstance().getChildren(new Folder(pathToOpen));
      }
      else
      {
         // try to select file.........
         handlers.removeHandlers();
         eventBus.fireEvent(new SetFocusOnItemEvent(context.getActiveFile().getPath()));
      }

   }

   /**
    * Handling any errors here!
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

}

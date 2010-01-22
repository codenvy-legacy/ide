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
package org.exoplatform.ideall.client.common;

import java.util.ArrayList;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.event.browse.GoToFolderEvent;
import org.exoplatform.ideall.client.event.browse.GoToFolderHandler;
import org.exoplatform.ideall.client.event.browse.SetFocusOnItemEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.data.DataService;
import org.exoplatform.ideall.client.model.data.event.FolderContentReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.FolderContentReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GoToFolderCommandHandler implements GoToFolderHandler, FolderContentReceivedHandler
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   public GoToFolderCommandHandler(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(GoToFolderEvent.TYPE, this);
   }

   private String pathToOpen;

   private ArrayList<String> pathes;

   public void onGoToFolder(GoToFolderEvent event)
   {
      System.out.println("GoToFolderCommandHandler.onGoToFolder()");

      if (context.getActiveFile() == null)
      {
         System.out.println("no any file selected!");
         return;
      }

      String workingPath = context.getActiveFile().getPath();
      while (workingPath.startsWith("/"))
      {
         workingPath = workingPath.substring(1);
      }
      workingPath = workingPath.substring(0, workingPath.lastIndexOf("/"));
      System.out.println("working path: " + workingPath);

      String p[] = workingPath.split("/");
      pathes = new ArrayList<String>();
      pathToOpen = "/" + p[0] + "/" + p[1];

      System.out.println("path to open > " + pathToOpen);

      if (p.length > 2)
      {
         for (int i = 2; i < p.length; i++)
         {
            pathes.add(p[i]);
         }
      }
      System.out.println("pathes length > " + pathes.size());

      handlers.addHandler(FolderContentReceivedEvent.TYPE, this);
      DataService.getInstance().getFolderContent(pathToOpen);
   }

   public void onFolderContentReceivedEx(FolderContentReceivedEvent event)
   {
      System.out.println("folder content received...... opening next folder............");

      if (pathes.size() > 0)
      {
         String name = pathes.get(0);
         pathes.remove(0);
         pathToOpen += "/" + name;

         System.out.println("now path to open > " + pathToOpen);
         DataService.getInstance().getFolderContent(pathToOpen);
      }
      else
      {
         // try to select file.........
         handlers.removeHandlers();         

         eventBus.fireEvent(new SetFocusOnItemEvent(context.getActiveFile().getPath()));

      }

   }

}

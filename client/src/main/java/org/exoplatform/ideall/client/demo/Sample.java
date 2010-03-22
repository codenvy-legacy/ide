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
package org.exoplatform.ideall.client.demo;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.ChildrenReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ChildrenReceivedHandler;
import org.exoplatform.ideall.client.model.vfs.webdav.WebDavVirtualFileSystem;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Sample implements ChildrenReceivedHandler, ExceptionThrownHandler
{

   private HandlerManager eventBus;

   public Sample()
   {
      HandlerManager eventBus = new HandlerManager(null);
      new WebDavVirtualFileSystem(eventBus);

      eventBus.addHandler(ChildrenReceivedEvent.TYPE, this);
      eventBus.addHandler(ExceptionThrownEvent.TYPE, this);
      
      /*
       * Get children
       */
      String webDavPath = "http://....";

      Folder folder = new Folder(webDavPath);
      VirtualFileSystem.getInstance().getChildren(folder);
   }

   public void onChildrenReceived(ChildrenReceivedEvent event)
   {
      // children received
      Folder folder = event.getFolder();
      for (Item item : folder.getChildren())
      {
         System.out.println("> " + item.getPath());
      }
   }

   public void onError(ExceptionThrownEvent event)
   {
      Window.alert("Error! " + event.getError().getMessage());
   }

}

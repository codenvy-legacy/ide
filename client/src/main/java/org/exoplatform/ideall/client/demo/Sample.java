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
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.vfs.webdav.WebDavVirtualFileSystem;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Sample implements FileContentSavedHandler, ExceptionThrownHandler
{

   public Sample()
   {
      HandlerManager eventBus = new HandlerManager(null);
      new EmptyLoader();
      new WebDavVirtualFileSystem(eventBus);

      eventBus.addHandler(FileContentSavedEvent.TYPE, this);
      eventBus.addHandler(ExceptionThrownEvent.TYPE, this);

      /*
       * Create folder
       */
      String path = "http://host:port/some path/some file";

      File file = new File(path);
      VirtualFileSystem.getInstance().getFileContent(file);
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      Window.alert("Content of the file " + event.getFile().getPath() + " saved successfully!");
   }

   public void onError(ExceptionThrownEvent event)
   {
      Window.alert("Error! " + event.getError().getMessage());
   }

}

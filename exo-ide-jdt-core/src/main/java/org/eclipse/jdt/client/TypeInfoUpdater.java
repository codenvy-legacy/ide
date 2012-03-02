/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.event.ItemMovedEvent;
import org.exoplatform.ide.vfs.client.event.ItemMovedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:19:16 PM 34360 evgen $
 * 
 */
public class TypeInfoUpdater implements FileSavedHandler, ItemDeletedHandler, ItemMovedHandler
{

   /**
    * Default Maven 'sourceDirectory' value
    */
   private static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

   /**
    * 
    */
   public TypeInfoUpdater()
   {
      IDE.addHandler(FileSavedEvent.TYPE, this);
      IDE.addHandler(ItemDeletedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event.FileSavedEvent)
    */
   @Override
   public void onFileSaved(FileSavedEvent event)
   {
      if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA))
      {
         deleteTypeFormStorage(event.getFile(), true);
      }
   }

   /**
    * Delete type info from storage and receive new if needed.
    * @param file The Java file that edited or deleted.
    * @param isNeedUpdate is need to receive new type info for file 
    */
   private void deleteTypeFormStorage(FileModel file, boolean isNeedUpdate)
   {
      ProjectModel project = file.getProject();
      if (project == null)
         return;
      String srcPath;
      if (project.hasProperty("sourceFolder"))
      {
         srcPath = (String)project.getPropertyValue("sourceFolder");
      }
      else
      {
         srcPath = DEFAULT_SOURCE_FOLDER;
      }
      String fqn = file.getPath().substring((project.getPath() + "/" + srcPath).length() + 1);
      fqn = fqn.substring(0, fqn.lastIndexOf('.'));
      fqn = fqn.replaceAll("/", ".");
      TypeInfoStorage.get().removeTypeInfo(fqn);
      if(isNeedUpdate)
         NameEnvironment.loadTypeInfo(fqn, project.getId());
   }

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent)
    */
   @Override
   public void onItemDeleted(ItemDeletedEvent event)
   {
      if (event.getItem().getMimeType().equals(MimeType.APPLICATION_JAVA))
      {
         deleteTypeFormStorage((FileModel)event.getItem(), false);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemMovedHandler#onItemMoved(org.exoplatform.ide.vfs.client.event.ItemMovedEvent)
    */
   @Override
   public void onItemMoved(ItemMovedEvent event)
   {
      // TODO Auto-generated method stub
      
   }

}

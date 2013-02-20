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
package org.exoplatform.ide.client.framework.project.api;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class IDEProject extends ProjectModel
{

   public interface LoadCompleteHandler
   {
      void onLoadComplete(Throwable error);
   }

   public IDEProject(ProjectModel project)
   {
      super(project);
   }

   public List<Item> getChildren(Folder parent) throws Exception
   {
      if (parent instanceof FolderModel)
      {
         return ((FolderModel)parent).getChildren().getItems();
      }
      else if (parent instanceof ProjectModel)
      {
         return ((ProjectModel)parent).getChildren().getItems();
      }
      else
      {
         throw new Exception("Item " + parent.getPath() + " is not a folder");
      }
   }

   public Item getChildByName(Folder parent, String name) throws Exception
   {
      for (Item item : getChildren(parent))
      {
         if (item.getName().equals(name))
         {
            return item;
         }
      }

      throw new Exception("Item " + name + " not found in folder " + parent.getPath());
   }

   public Item getResource(Folder parent, String path) throws Exception
   {
      while (path.startsWith("/"))
      {
         path = path.substring(1);
      }

      while (path.endsWith("/"))
      {
         path = path.substring(0, path.length() - 1);
      }

      String[] parts = path.split("/");
      for (int i = 0; i < parts.length; i++)
      {
         if (i == parts.length - 1)
         {
            return getChildByName(parent, parts[i]);
         }
         else
         {
            parent = (Folder)getChildByName(parent, parts[i]);
         }
      }

      throw new Exception("Item " + name + " not found in folder " + parent.getPath());
   }

   public Item getResource(String absolutePath) throws Exception
   {
      if (!absolutePath.startsWith(getPath()))
      {
         throw new Exception("Item is out of the project's scope. Project : " + getName() + ", item path is : "
            + absolutePath);
      }

      if (absolutePath.equals(getPath()))
      {
         return this;
      }

      absolutePath = absolutePath.substring(getPath().length());
      String[] parts = absolutePath.split("/");
      Folder parent = this;
      for (int i = 1; i < parts.length; i++)
      {
         Item child = getResource(parent, parts[i]);
         if (i < parts.length - 1 && !(child instanceof Folder))
         {
            throw new Exception("Item " + child.getPath() + " is not a folder");
         }

         if (i == parts.length - 1)
         {
            return child;
         }

         parent = (Folder)child;
      }

      throw new Exception("Item " + absolutePath + " not found");
   }

   private void validateResource(Item item) throws Exception
   {
      if (item == null)
      {
         throw new Exception("Resource is null.");
      }

      if (item.getId().equals(getId()))
      {
         return;
      }

      if (!(item instanceof ItemContext))
      {
         throw new Exception("Item is not implements ItemContext. Parent and Project not set.");
      }

      ItemContext itemContext = (ItemContext)item;

      if (itemContext.getParent() == null || !itemContext.getParent().getPath().startsWith(getPath()))
      {
         throw new Exception("Item has no parent.  Project : " + getName() + ", item path is : " + item.getPath());
      }
   }

   public void refresh(final FolderModel folder, final AsyncCallback<Folder> callback)
   {
      try
      {
         validateResource(folder);
         FolderModel target = (FolderModel)getResource(folder.getPath());

         try
         {
            FolderTreeUnmarshaller unmarshaller = new FolderTreeUnmarshaller(target, this);
            VirtualFileSystem.getInstance().getTree(target.getId(), new AsyncRequestCallback<Folder>(unmarshaller)
            {
               @Override
               protected void onSuccess(Folder result)
               {
                  callback.onSuccess(result);
               }

               @Override
               protected void onFailure(Throwable e)
               {
                  callback.onFailure(e);
               }
            });
         }
         catch (Exception e)
         {
            callback.onFailure(e);
         }
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

}

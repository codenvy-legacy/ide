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

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemListImpl;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class FolderTreeUnmarshaller implements Unmarshallable<Folder>
{

   private static final String CHILDREN = "children";

   private static final String TYPE = "itemType";

   private static final String MIME_TYPE = "mimeType";

   private static final String ITEM = "item";

   private ProjectModel project;

   private final FolderModel folder;

   public FolderTreeUnmarshaller(FolderModel folder, ProjectModel project)
   {
      this.folder = folder;
      this.project = project;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONObject object = JSONParser.parseLenient(response.getText()).isObject();

         JSONValue item = object.get(ITEM);
         ItemList<Item> children = getChildren(object.get(CHILDREN));
         folder.setChildren(children);

         updateProjectAndParent(project.getChildren(), project, project);
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
         throw new UnmarshallerException("Can't parse JSON response.");
      }
   }

   private void updateProjectAndParent(ItemList<Item> items, FolderModel parent, ProjectModel project)
   {
      for (Item item : items.getItems())
      {
         if (item instanceof FileModel)
         {
            ((FileModel)item).setProject(project);
            ((FileModel)item).setParent(parent);
         }
         else if (item instanceof ProjectModel)
         {
            ((ProjectModel)item).setParent(parent);
            ((ProjectModel)item).setProject(project);
            updateProjectAndParent(((ProjectModel)item).getChildren(), (ProjectModel)item, (ProjectModel)item);
         }
         else if (item instanceof FolderModel)
         {
            ((FolderModel)item).setParent(parent);
            ((FolderModel)item).setProject(project);
            updateProjectAndParent(((FolderModel)item).getChildren(), (FolderModel)item, project);
         }

      }

   }

   private ItemList<Item> getChildren(JSONValue children)
   {
      ItemList<Item> itemList = new ItemListImpl<Item>();

      if (children.isArray() == null)
      {
         return itemList;
      }

      if (children.isNull() != null)
      {
         return itemList;
      }

      JSONArray itemsArray = children.isArray();
      for (int i = 0; i < itemsArray.size(); i++)
      {
         JSONObject itemObject = itemsArray.get(i).isObject();

         JSONObject item = itemObject.get(ITEM).isObject();

         String mimeType = null;
         if (item.get(MIME_TYPE).isString() != null)
            mimeType = item.get(MIME_TYPE).isString().stringValue();

         ItemType type = null;

         if (item.get(TYPE).isNull() == null)
         {
            type = ItemType.valueOf(item.get(TYPE).isString().stringValue());
         }

         if (ItemType.PROJECT == type)
         {
            if (Project.PROJECT_MIME_TYPE.equals(mimeType))
            {
               try
               {
                  ProjectModel projectModel = new ProjectModel(item);
                  IDEProject project = ProjectBuilder.createProject(projectModel);

                  itemList.getItems().add(project);
                  project.setChildren(getChildren(itemObject.get(CHILDREN)));
               }
               catch (Exception e)
               {
                  e.printStackTrace();
                  System.out.println("Invalid JSON " + item.toString());
               }

            }
         }
         else if (ItemType.FOLDER == type)
         {
            try
            {
               FolderModel folder = new FolderModel(item);
               itemList.getItems().add(folder);
               folder.setChildren(getChildren(itemObject.get(CHILDREN)));
            }
            catch (Exception e)
            {
               e.printStackTrace();
               System.out.println("Invalid JSON " + item.toString());
            }
         }
         else
         {
            try
            {
               FileModel file = new FileModel(item);
               itemList.getItems().add(file);
            }
            catch (Exception e)
            {
               e.printStackTrace();
               System.out.println("Invalid JSON " + item.toString());
            }

         }
      }

      return itemList;
   }

   @Override
   public Folder getPayload()
   {
      return folder;
   }

}

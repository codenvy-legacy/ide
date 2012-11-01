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
package org.eclipse.jdt.client.packaging;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class FolderTreeUnmarshaller implements Unmarshallable<FolderModel>
{

   private static final String CHILDREN = "children";

   private static final String TYPE = "itemType";

   private static final String MIME_TYPE = "mimeType";

   private static final String ITEM = "item";

   private final FolderModel folder;
   
   private final ProjectModel parentProject;

   public FolderTreeUnmarshaller(FolderModel folder, ProjectModel parentProject)
   {
      this.folder = folder;
      this.parentProject = parentProject;
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
         ItemList<Item> children = getChildren(object.get(CHILDREN));
         folder.setChildren(children);
         setProjectAndParent(children, folder);

         //         if (folder instanceof FolderModel)
         //         {
         //            ((FolderModel)folder).setChildren(children);
         //         }
         //         else if (folder instanceof ProjectModel)
         //         {
         //            ((ProjectModel)folder).setChildren(children);
         //         }
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
         //String message = "Can't parse item " + response.getText();
         throw new UnmarshallerException("Can't parse JSON response.");
      }
   }
   
   private void setProjectAndParent(ItemList<Item> items, FolderModel parentFolder)
   {
      for (Item item : items.getItems())
      {
//         System.out.println("traverse item > " + item.getPath());
         
         if (item instanceof ItemContext)
         {
            ((ItemContext)item).setProject(parentProject);
            ((ItemContext)item).setParent(parentFolder);
         }
         
         if (item instanceof FolderModel)
         {
            FolderModel folder = (FolderModel)item;
            setProjectAndParent(folder.getChildren(), folder);
         }
      }
   }

   private ItemList<Item> getChildren(JSONValue children)
   {
      ItemList<Item> itemList = new ItemList<Item>();

      if (children.isArray() == null)
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
               ProjectModel project = new ProjectModel(item);
               itemList.getItems().add(project);
               project.setChildren(getChildren(itemObject.get(CHILDREN)));
            }
         }
         else if (ItemType.FOLDER == type)
         {
            FolderModel folder = new FolderModel(item);
            itemList.getItems().add(folder);
            folder.setChildren(getChildren(itemObject.get(CHILDREN)));
         }
         else
         {
            FileModel file = new FileModel(item);
            itemList.getItems().add(file);
         }
      }

      return itemList;
   }

   @Override
   public FolderModel getPayload()
   {
      return folder;
   }

}

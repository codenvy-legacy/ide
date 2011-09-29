/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: FilderContentUnmarshaller Feb 2, 2011 2:59:31 PM evgen $
 *
 */
public class ChildrenUnmarshaller implements Unmarshallable<List<Item>>
{

   /**
    * Item type
    */
   private static final String TYPE = "itemType";

   /**
    * Item mime type
    */
   private static final String MIME_TYPE = "mimeType";

   private final List<Item> items;

   /**
    * @param items
    */
   public ChildrenUnmarshaller(final List<Item> items)
   {
      super();
      this.items = items;
      this.items.clear();
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONValue jsonValue = JSONParser.parseLenient(response.getText());
         parseItems(jsonValue.isObject().get("items").isArray());
      }
      catch (Exception exc)
      {
         exc.printStackTrace();

         String message = "Can't parse folder content at <b>" + "id" + "</b>! ";
         throw new UnmarshallerException(message);
      }
   }

   @Override
   public List<Item> getPayload()
   {
      return this.items;
   }

   /**
    * Parse JSON Array to List of Item
    * @param itemsArray JSON array
    * @return list of children items
    */
   private void parseItems(JSONArray itemsArray)
   {
      //ArrayList<Item> items = new ArrayList<Item>();
      //      items.clear();

      for (int i = 0; i < itemsArray.size(); i++)
      {
         JSONObject object = itemsArray.get(i).isObject();
         ItemType type = ItemType.valueOf(object.get(TYPE).isString().stringValue());
         String mimeType = object.get(MIME_TYPE).isString().stringValue();

         if (type == ItemType.FOLDER)
         {
            if (Project.PROJECT_MIME_TYPE.equals(mimeType))
            {
               items.add(new ProjectModel(object));
            }
            else
            {
               items.add(new FolderModel(object));
            }
         }
         else
            items.add(new FileModel(object));
      }

      //this.folder.getChildren().setItems(items);
   }

}

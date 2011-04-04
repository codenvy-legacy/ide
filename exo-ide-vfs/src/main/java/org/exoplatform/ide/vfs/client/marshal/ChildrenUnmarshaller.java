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

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.model.File;
import org.exoplatform.ide.vfs.client.model.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.ArrayList;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: FilderContentUnmarshaller Feb 2, 2011 2:59:31 PM evgen $
 *
 */
public class ChildrenUnmarshaller implements Unmarshallable
{

   /**
    * Item type
    */
   private static final String TYPE = "itemType";
   
   private Folder folder;

   /**
    * @param items
    */
   public ChildrenUnmarshaller(Folder folder)
   {
      super();
      this.folder = folder;
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

   /**
    * Parse JSON Array to List of Item
    * @param itemsArray JSON array
    * @return list of children items
    */
   private void parseItems(JSONArray itemsArray)
   {
      ArrayList<Item> items = new ArrayList<Item>();
      
      for (int i = 0; i < itemsArray.size(); i++)
      {
         JSONObject object = itemsArray.get(i).isObject();         
         ItemType type = ItemType.valueOf(object.get(TYPE).isString().stringValue());         
         
         if (type == ItemType.FOLDER)
            items.add(new Folder(object));
         else
            items.add(new File(object));
      }
      
      this.folder.getChildren().setItems(items);
   }

}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.ItemType;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: FilderContentUnmarshaller Feb 2, 2011 2:59:31 PM evgen $
 *
 */
public class ChildrenUnmarshaller implements Unmarshallable
{

   /**
    * 
    */
   private static final String LOCKED = "locked";

   /**
    * 
    */
   private static final String LAST_MODIFICATION_DATE = "lastModificationDate";

   /**
    * 
    */
   private static final String CONTENT_TYPE = "contentType";

   /**
    * 
    */
   private static final String VERSION_ID = "versionId";

   /**
    * 
    */
   private static final String CONTENT_LENGTH = "length";

   /**
    * Item type
    */
   private static final String TYPE = "type";

   /**
    * {@link Link} rel
    */
   private static final String REL = "rel";

   /**
    * {@link Link} href
    */
   private static final String HREF = "href";

   /**
    * Item map of {@link Link}
    */
   private static final String LINKS = "links";

   /**
    * Item path
    */
   private static final String PATH = "path";

   /**
    * Item name
    */
   private static final String NAME = "name";

   /**
    * Item creation date
    */
   private static final String CREATION_DATE = "creationDate";

   /**
    * Item Id
    */
   private static final String ID = "id";

   private ItemList<Item> items;

   private String id;

   /**
    * @param items
    */
   public ChildrenUnmarshaller(String id, ItemList<Item> items)
   {
      super();
      this.items = items;
      this.id = id;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseFolderContent(response.getText());
      }
      catch (Exception exc)
      {
         exc.printStackTrace();

         String message = "Can't parse folder content at <b>" + id + "</b>!";
         throw new UnmarshallerException(message);
      }
   }

   /**
    * Parse response
    * @param text response string
    */
   private void parseFolderContent(String text)
   {
      JSONValue jsonValue = JSONParser.parse(text);
      this.items.getItems().addAll(parseItems(jsonValue.isObject().get("items").isArray()));

   }

   /**
    * Parse JSON Array to List of Item
    * @param itemsArray JSON array
    * @return list of children items
    */
   private List<Item> parseItems(JSONArray itemsArray)
   {
      List<Item> items = new ArrayList<Item>();
      for (int i = 0; i < itemsArray.size(); i++)
      {
         JSONObject object = itemsArray.get(i).isObject();
         ItemType type = ItemType.valueOf(object.get(TYPE).isString().stringValue());
         if (type == ItemType.FOLDER)
         {
            Folder folder = new Folder();
            folder.setItemType(type);
            parseBaseFields(object, folder);
            
            items.add(folder);
         }
         else
         {
            File file = new File();
            file.setItemType(type);
            parseBaseFields(object, file);

            if (object.containsKey(CONTENT_LENGTH))
               file.setLength((long)object.get(CONTENT_LENGTH).isNumber().doubleValue());

            if (object.containsKey(VERSION_ID))
               file.setVersionId(object.get(VERSION_ID).isString().stringValue());

            if (object.containsKey(CONTENT_TYPE))
               file.setContentType(object.get(CONTENT_TYPE).isString().stringValue());

            if (object.containsKey(LOCKED))
               file.setLocked(object.get(LOCKED).isBoolean().booleanValue());

            if (object.containsKey(LAST_MODIFICATION_DATE))
               file.setLastModificationDate((long)object.get(LAST_MODIFICATION_DATE).isNumber().doubleValue());

            items.add(file);
         }

      }
      return items;
   }

   /**
    * Parse base fields (id, creationDate, name, path, links) from JSON Object
    * @param object JSON Object that represent Item
    * @param item Item 
    */
   private void parseBaseFields(JSONObject object, Item item)
   {
      if (object.containsKey(ID))
         item.setId(object.get(ID).isString().stringValue());

      if (object.containsKey(CREATION_DATE))
         item.setCreationDate((long)object.get(CREATION_DATE).isNumber().doubleValue());

      if (object.containsKey(NAME))
         item.setName(object.get(NAME).isString().stringValue());

      if (object.containsKey(PATH))
         item.setPath(object.get(PATH).isString().stringValue());
      
      if (object.containsKey(LINKS))
         addLinks(object.get(LINKS).isObject(), item.getLinks());
   }

   /**
    * Parse links filed
    * @param linksObject JSON Object that represent map of of {@link Link}
    * @param links field of Item
    */
   private void addLinks(JSONObject linksObject, Map<String, Link> links)
   {
      for (String key : linksObject.keySet())
      {
         JSONObject l = linksObject.get(key).isObject();
         Link link = new Link();
         if (l.containsKey(HREF))
            link.setHref(l.get(HREF).isString().stringValue());
         if (l.containsKey(TYPE))
            link.setType(l.get(TYPE).isString().stringValue());

         if (l.containsKey(REL))
            link.setRel(l.get(REL).isString().stringValue());

         links.put(key, link);
      }
   }

}

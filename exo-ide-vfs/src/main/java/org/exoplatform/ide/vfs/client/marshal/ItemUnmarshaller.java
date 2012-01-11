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

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 30, 2011 evgen $
 *
 */
public class ItemUnmarshaller implements Unmarshallable<ItemWrapper>
{

   private ItemWrapper wrapper;

   /**
    * Item type
    */
   private static final String TYPE = "itemType";

   /**
    * Item mime type
    */
   private static final String MIME_TYPE = "mimeType";

   /**
    * @param item
    */
   public ItemUnmarshaller(ItemWrapper item)
   {
      this.wrapper = item;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONValue val = JSONParser.parseLenient(response.getText());
         JSONObject object = val.isObject();
         ItemType type = ItemType.valueOf(object.get(TYPE).isString().stringValue());
         String mimeType = null;
         if (object.get(MIME_TYPE).isString() != null)
            mimeType = object.get(MIME_TYPE).isString().stringValue();

         if (type == ItemType.FOLDER)
         {
            if (Project.PROJECT_MIME_TYPE.equals(mimeType))
            {
               wrapper.setItem(new ProjectModel(object));
            }
            else
            {
               wrapper.setItem(new FolderModel(object));
            }
         }
         else
         {
            FileModel file = new FileModel(object);
            if (wrapper.getItem() != null)
            {
               FolderModel parent = ((FileModel)wrapper.getItem()).getParent();
               ProjectModel project = ((FileModel)wrapper.getItem()).getProject();
               file.setParent(parent);
               file.setProject(project);
            }
            wrapper.setItem(file);
         }
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse item.");
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public ItemWrapper getPayload()
   {
      return wrapper;
   }

}

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
package org.exoplatform.ide.client.model.template.marshal;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FileTemplateList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: FileTemplateListUnmarshaller.java Jul 27, 2011 2:56:00 PM vereshchaka $
 *
 */
public class FileTemplateListUnmarshaller implements Unmarshallable
{

   private FileTemplateList fileTemplateList;

   public FileTemplateListUnmarshaller(FileTemplateList fileTemplates)
   {
      this.fileTemplateList = fileTemplates;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      List<FileTemplate> fileTemplates =
         (fileTemplateList.getFileTemplates() == null) ? new ArrayList<FileTemplate>() : fileTemplateList
            .getFileTemplates();

      JavaScriptObject json = build(response.getText());
      JSONArray jsonArray = new JSONArray(json);

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONValue value = jsonArray.get(i);
         FileTemplate fileTemplate = parseObject(value.isObject());
         fileTemplates.add(fileTemplate);
      }
   }
   
   private FileTemplate parseObject(JSONObject jsonObject)
   {
      String name = null;
      String description = null;
      String mimeType = null;
      String content = null;
      boolean isDefault = false;
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals("name"))
         {
            name = jsonValue.isString().stringValue();
         }
         else if (key.equals("description"))
         {
            if (jsonValue.isString() != null)
            {
               description = jsonValue.isString().stringValue();
            }
         }
         else if (key.equals("mimeType"))
         {
            if (jsonValue.isString() != null)
            {
               mimeType = jsonValue.isString().stringValue();
            }
         }
         else if (key.equals("content"))
         {
            if (jsonValue.isString() != null)
            {
               content = jsonValue.isString().stringValue();
            }
         }
         else if (key.equals("isDefault"))
         {
            if (jsonValue.isBoolean() != null)
            {
               isDefault = jsonValue.isBoolean().booleanValue();
            }
         }
      }
      return new FileTemplate(mimeType, name, description, content, isDefault);
   }

   public static native JavaScriptObject build(String json) /*-{
                                                            return eval('(' + json + ')');      
                                                            }-*/;

}

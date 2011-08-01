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

import com.google.gwt.json.client.JSONArray;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.client.model.template.FileTemplate;

import java.util.List;

/**
 * Marshal file template list.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: FileTemplateListMarshaller.java Aug 1, 2011 10:11:31 AM vereshchaka $
 *
 */
public class FileTemplateListMarshaller implements Marshallable
{
   
   private List<FileTemplate> fileTemplates;
   
   public FileTemplateListMarshaller(List<FileTemplate> fileTemplates)
   {
      this.fileTemplates = fileTemplates;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONArray jsonArray = new JSONArray();
      int i = 0;
      for (FileTemplate fileTemplate : fileTemplates)
      {
         JSONObject jsonObject = new JSONObject();
         if (fileTemplate == null)
         {
            return jsonObject.toString();
         }

         jsonObject.put("name", new JSONString(fileTemplate.getName()));
         final String description = fileTemplate.getDescription() == null ? "" : fileTemplate.getDescription();
         jsonObject.put("description", new JSONString(description));
         jsonObject.put("mimeType", new JSONString(fileTemplate.getMimeType()));
         jsonObject.put("content", new JSONString(fileTemplate.getContent()));
         jsonObject.put("isDefault", new JSONString(String.valueOf(fileTemplate.isDefault())));
         jsonArray.set(i++, jsonObject);
      }
      
      return jsonArray.toString();

   }

}

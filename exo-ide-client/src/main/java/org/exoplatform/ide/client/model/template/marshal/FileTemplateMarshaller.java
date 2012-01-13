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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.client.model.template.FileTemplate;

/**
 * Marshal FileTemplate object to json.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: FileTemplateMarshaller.java Jul 27, 2011 12:40:34 PM vereshchaka $
 * 
 */
public class FileTemplateMarshaller implements Marshallable
{

   private FileTemplate fileTemplate;

   public FileTemplateMarshaller(FileTemplate fileTemplate)
   {
      this.fileTemplate = fileTemplate;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
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

      return jsonObject.toString();
   }

}

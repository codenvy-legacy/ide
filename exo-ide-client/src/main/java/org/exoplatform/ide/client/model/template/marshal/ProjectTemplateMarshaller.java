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
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectTemplateMarshaller.java Jul 28, 2011 6:45:53 PM vereshchaka $
 *
 */
public class ProjectTemplateMarshaller implements Marshallable
{
   private ProjectTemplate projectTemplate;

   public ProjectTemplateMarshaller(ProjectTemplate projectTemplate)
   {
      this.projectTemplate = projectTemplate;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONObject jsonObject = new JSONObject();
      if (projectTemplate == null)
      {
         return jsonObject.toString();
      }

      jsonObject.put("name", new JSONString(projectTemplate.getName()));
      final String description = projectTemplate.getDescription() == null ? "" : projectTemplate.getDescription();
      jsonObject.put("description", new JSONString(description));
      final String type = projectTemplate.getType() == null ? "" : projectTemplate.getType();
      jsonObject.put("type", new JSONString(type));
      jsonObject.put("isDefault", new JSONString(String.valueOf(projectTemplate.isDefault())));
      if (!projectTemplate.getChildren().isEmpty())
      {
         jsonObject.put("children", getChildrenJsonArray(projectTemplate.getChildren()));
      }

      return jsonObject.toString();
   }

   private JSONArray getChildrenJsonArray(List<Template> childrenList)
   {
      JSONArray childrenJsonArray = new JSONArray();
      for (int i = 0; i < childrenList.size(); i++)
      {
         Template template = childrenList.get(i);
         if (template instanceof FileTemplate)
         {
            JSONObject fileJsonObj = new JSONObject();
            fileJsonObj.put("childType", new JSONString("file"));
            fileJsonObj.put("name", new JSONString(template.getName()));
            fileJsonObj.put("fileName", new JSONString(((FileTemplate)template).getFileName()));
            childrenJsonArray.set(i, fileJsonObj);
         }
         else if (template instanceof FolderTemplate)
         {
            JSONObject folderJsonObj = new JSONObject();
            folderJsonObj.put("childType", new JSONString("folder"));
            folderJsonObj.put("name", new JSONString(template.getName()));
            if (!((FolderTemplate)template).getChildren().isEmpty())
            {
               folderJsonObj.put("children", getChildrenJsonArray(((FolderTemplate)template).getChildren()));
            }
            childrenJsonArray.set(i, folderJsonObj);
         }
      }
      return childrenJsonArray;
   }

}

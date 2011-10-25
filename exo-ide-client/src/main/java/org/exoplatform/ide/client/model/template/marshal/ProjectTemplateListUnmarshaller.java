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
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplateList;
import org.exoplatform.ide.client.model.template.Template;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectTemplateListUnmarshaller.java Jul 28, 2011 1:09:44 PM vereshchaka $
 *
 */
public class ProjectTemplateListUnmarshaller implements Unmarshallable
{

   private ProjectTemplateList projectTemplateList;
   
   public ProjectTemplateListUnmarshaller(ProjectTemplateList projectTemplateList)
   {
      this.projectTemplateList = projectTemplateList;
   }
   
   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      List<ProjectTemplate> projectTemplates =
         (projectTemplateList.getProjectTemplates() == null) ? new ArrayList<ProjectTemplate>() : projectTemplateList
            .getProjectTemplates();

      JavaScriptObject json = build(response.getText());
      JSONArray jsonArray = new JSONArray(json);

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONValue value = jsonArray.get(i);
         ProjectTemplate projectTemplate = parseObject(value.isObject());
         projectTemplates.add(projectTemplate);
      }
   }
   
   private ProjectTemplate parseObject(JSONObject jsonObject)
   {
      ProjectTemplate projectTemplate = new ProjectTemplate(null);
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals("name"))
         {
            projectTemplate.setName(jsonValue.isString().stringValue());
         }
         else if (key.equals("description"))
         {
            if (jsonValue.isString() != null)
            {
               projectTemplate.setDescription(jsonValue.isString().stringValue());
            }
         }
         else if (key.equals("type"))
         {
            if (jsonValue.isString() != null)
            {
               projectTemplate.setType(jsonValue.isString().stringValue());
            }
         }
         else if (key.equals("children"))
         {
            if (jsonValue.isArray() != null)
            {
               projectTemplate.setChildren(parseChildrenListValue(jsonValue.isArray()));
            }
         }
         else if (key.equals("default"))
         {
            if (jsonValue.isBoolean() != null)
            {
               projectTemplate.setDefault(jsonValue.isBoolean().booleanValue());
            }
         }
      }
      return projectTemplate;
   }
   
   private List<Template> parseChildrenListValue(JSONArray array)
   {
      List<Template> templates = new ArrayList<Template>();
      
      for (int i = 0; i < array.size(); i++)
      {
         JSONObject obj = array.get(i).isObject();
         if (obj.get("childType").isString().stringValue().equals("file"))
         {
            //parse file
            templates.add(parseFile(obj));
         }
         else
         {
            //parse folder
            templates.add(parseFolder(obj));
         }
      }
      return templates;
   }
   
   private Template parseFile(JSONObject jsonObject)
   {
      FileTemplate fileTemplate = new FileTemplate(null, null);
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals("name"))
         {
            fileTemplate.setName(jsonValue.isString().stringValue());
         }
         else if (key.equals("fileName"))
         {
            fileTemplate.setFileName(jsonValue.isString().stringValue());
         }
      }
      return fileTemplate;
   }
   
   private Template parseFolder(JSONObject jsonObject)
   {
      FolderTemplate folderTemplate = new FolderTemplate();
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals("name"))
         {
            folderTemplate.setName(jsonValue.isString().stringValue());
         }
         else if (key.equals("children"))
         {
            if (jsonValue.isArray() != null)
            {
               folderTemplate.setChildren(parseChildrenListValue(jsonValue.isArray()));
            }
         }
      }
      return folderTemplate;
   }
   
   public static native JavaScriptObject build(String json) /*-{
   return eval('(' + json + ')');      
   }-*/;


}

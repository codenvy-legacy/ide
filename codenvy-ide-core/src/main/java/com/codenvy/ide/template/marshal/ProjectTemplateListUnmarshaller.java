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
package com.codenvy.ide.template.marshal;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.template.ProjectTemplate;
import com.codenvy.ide.template.TemplateAutoBeanFactory;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectTemplateListUnmarshaller.java Jul 28, 2011 1:09:44 PM vereshchaka $
 * 
 */
public class ProjectTemplateListUnmarshaller implements Unmarshallable<List<ProjectTemplate>>
{
   private List<ProjectTemplate> projectTemplates;

   public ProjectTemplateListUnmarshaller(List<ProjectTemplate> projectTemplates)
   {
      this.projectTemplates = projectTemplates;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();
      if (jsonArray == null)
      {
         return;
      }

      for (int i = 0; i < jsonArray.size(); i++)
      {
         String payload = jsonArray.get(i).isObject().toString();

         AutoBean<ProjectTemplate> projectTemplateBean =
            AutoBeanCodex.decode(TemplateAutoBeanFactory.AUTO_BEAN_FACTORY, ProjectTemplate.class, payload);
         projectTemplates.add(projectTemplateBean.as());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<ProjectTemplate> getPayload()
   {
      return projectTemplates;
   }
}

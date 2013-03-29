/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.api.wizard.newproject;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;

/**
 * AbstractCreateProjectPresenter is an abstract base implementation of CreateProjectHandler.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class AbstractCreateProjectPresenter implements CreateProjectHandler
{
   private JsonStringMap<String> params = JsonCollections.createStringMap();

   private String projectName;

   /**
    * {@inheritDoc}
    */
   @Override
   public void addParam(String name, String value)
   {
      params.put(name, value);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getParam(String name)
   {
      return params.get(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getProjectName()
   {
      return projectName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setProjectName(String name)
   {
      projectName = name;
   }
}
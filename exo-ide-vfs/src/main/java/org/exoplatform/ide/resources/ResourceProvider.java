/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.resources;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.properties.Property;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 7, 2012  
 */
public interface ResourceProvider
{
   public void getProject(String name, AsyncCallback<Project> callback) throws ResourceException;

   @SuppressWarnings("rawtypes")
   public void createProject(String name, JsonArray<Property> properties, AsyncCallback<Project> callback)
      throws ResourceException;

   public ModelProvider getModelProvider(String primaryNature);

   public void registerModelProvider(String primaryNature, ModelProvider modelProvider) throws ResourceException;

}
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
import org.exoplatform.ide.resources.event.StartableExtension;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.properties.Property;

/**
 * Public interface of Resources API is presented by {@link ResourceProvider}. This class is designed to provide
 * an access to IDE resources, such as Files, Folders, Projects and derivatives entities. Project is the root
 * entry point that provides an access to actual resources. <br/>
 * 
 * Project encapsulates the model of the resources used in it. It can be classic Files and Folders, as well as
 * custom Package instances, the derivatives of the Folder, custom RubyScript instance, the derivatives of the 
 * File. Concrete model of the Project is called Project Model and it is presented by Project class. <br/>
 * 
 * Project Models are created in relation to Project Primary nature. When project is created from scratch,
 * primary nature is retrieved from properties provided, then corresponding ModelProvider invoked to create
 * empty Project that is initialized with JSon data retrieved from REST VFS service.  
 * 
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ResourceProvider extends StartableExtension
{
   /**
    * Returns active project or null if none opened
    * 
    * @return active project or null if none. 
    */
   public Project getActiveProject();

   /**
    * Reads already stored project. Model Provider will be invoked to deserialized Project Model corresponding to 
    * Primary Nature stored in Project's Properties.
    * 
    * @param name 
    * @param callback
    * @throws ResourceException
    */
   public void getProject(String name, AsyncCallback<Project> callback) throws ResourceException;

   /**
    * Creates new empty project based on provided properties (Project Description).
    * 
    * @param name
    * @param properties
    * @param callback
    * @throws ResourceException
    */
   @SuppressWarnings("rawtypes")
   public void createProject(String name, JsonArray<Property> properties, AsyncCallback<Project> callback)
      throws ResourceException;

   /**
    * Registers ModelProvider instance for given Primary Project Nature
    * 
    * @param primaryNature
    * @param modelProvider
    * @throws ResourceException
    */
   public void registerModelProvider(String primaryNature, ModelProvider modelProvider) throws ResourceException;

   /**
    * Retrieves Model Provider instance for given Primary Nature, of Generic Model Provider is none found. 
    * 
    * @param primaryNature
    * @return
    */
   public ModelProvider getModelProvider(String primaryNature);

}
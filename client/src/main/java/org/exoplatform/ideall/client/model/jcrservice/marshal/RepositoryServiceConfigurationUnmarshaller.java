/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.ideall.client.model.jcrservice.marshal;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ideall.client.model.jcrservice.bean.Repository;
import org.exoplatform.ideall.client.model.jcrservice.bean.RepositoryServiceConfiguration;
import org.exoplatform.ideall.client.model.jcrservice.bean.Workspace;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RepositoryServiceConfigurationUnmarshaller implements Unmarshallable
{

   public static final String DEFAULT_REPOSITORY_NAME = "defaultRepositoryName";

   public static final String REPOSITORIES = "repositories";

   public static final String NAME = "name";

   public static final String SYSTEM_WORKSPACE_NAME = "systemWorkspaceName";

   public static final String WORKSPACE_ENTRIES = "workspaceEntries";

   private RepositoryServiceConfiguration configuration;

   private HandlerManager eventBus;

   public RepositoryServiceConfigurationUnmarshaller(HandlerManager eventBus,
      RepositoryServiceConfiguration configuration)
   {
      this.configuration = configuration;
      this.eventBus = eventBus;
   }

   public void unmarshal(String body)
   {
      try
      {
         parseReposytoryServiceConfiguration(body);
      }
      catch (Exception exc)
      {
         String message = "Can't parse repository service configuration";
         eventBus.fireEvent(new ExceptionThrownEvent(new Exception(message)));
      }
   }

   private void parseReposytoryServiceConfiguration(String body)
   {
      JSONObject json = JSONParser.parse(body).isObject();

      String defaultRepositoryName = json.get(DEFAULT_REPOSITORY_NAME).isString().stringValue();
      configuration.setDefaultRepositoryName(defaultRepositoryName);

      JSONArray repositoryEntries = json.get(REPOSITORIES).isArray();
      configuration.getRepositories().clear();
      for (int i = 0; i < repositoryEntries.size(); i++)
      {
         JSONObject repositoryJSON = repositoryEntries.get(i).isObject();
         Repository repository = parseRepositoryConfiguration(repositoryJSON);
         configuration.getRepositories().add(repository);
      }
   }

   private Repository parseRepositoryConfiguration(JSONObject repositoryJSON)
   {
      Repository repository = new Repository();
      String name = repositoryJSON.get(NAME).isString().stringValue();
      String systemWorkspace = repositoryJSON.get(SYSTEM_WORKSPACE_NAME).isString().stringValue();
      repository.setName(name);
      repository.setSystemWorkspaceName(systemWorkspace);

      JSONArray workspaceEntries = repositoryJSON.get(WORKSPACE_ENTRIES).isArray();
      for (int i = 0; i < workspaceEntries.size(); i++)
      {
         JSONObject workspaceEntryJSON = workspaceEntries.get(i).isObject();
         Workspace workspace = parseWorkspace(workspaceEntryJSON);
         repository.getWorkspaces().add(workspace);
      }

      return repository;
   }

   private Workspace parseWorkspace(JSONObject workspaceJSON)
   {
      String name = workspaceJSON.get(NAME).isString().stringValue();
      Workspace workspace = new Workspace(name);
      return workspace;
   }

}

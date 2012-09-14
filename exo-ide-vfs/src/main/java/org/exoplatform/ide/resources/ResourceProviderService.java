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

import com.google.gwt.core.client.GWT;

import com.google.web.bindery.event.shared.EventBus;

import com.google.inject.Inject;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.resources.event.ExtensionInitializedEvent;
import org.exoplatform.ide.resources.marshal.JSONSerializer;
import org.exoplatform.ide.resources.marshal.ProjectModelProviderAdapter;
import org.exoplatform.ide.resources.marshal.ProjectModelUnmarshaller;
import org.exoplatform.ide.resources.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.GenericModelProvider;
import org.exoplatform.ide.resources.model.Link;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.properties.Property;

/**
 * Implementation of Resource Provider
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ResourceProviderService implements ResourceProvider
{

   /**
    * Used for compatibility with IDE-VFS 1.x 
    */
   private static final String DEPRECATED_PROJECT_TYPE = "deprecated.project.type";

   public static final String GENERIC_PROJECT = "project.generic";

   /**
    * Fully qualified URL to root folder of VFS
    */
   private final String workspaceURL;

   private Loader loader;

   private final JsonStringMap<ModelProvider> modelProviders;

   protected VirtualFileSystemInfo vfsInfo;

   @SuppressWarnings("unused")
   private boolean initialized = false;

   private EventBus eventBus;

   private Project activeProject;

   /**
    * Resources API for client application.
    * It deals with VFS to retrieve the content of  the files 
    * @throws ResourceException 
    */
   @Inject
   public ResourceProviderService(EventBus eventBus) throws ResourceException
   {
      this.workspaceURL = "http://127.0.0.1:8888/rest/ide/vfs/dev-monit";
      this.modelProviders = JsonCollections.<ModelProvider> createStringMap();
      this.modelProviders.put(GENERIC_PROJECT, new GenericModelProvider());
      // TODO 
      this.loader = new EmptyLoader();
      this.eventBus = eventBus;
      //init();
   }

   @Override
   public void start() throws ResourceException
   {
      AsyncRequestCallback<VirtualFileSystemInfo> internalCallback =
         new AsyncRequestCallback<VirtualFileSystemInfo>(new VFSInfoUnmarshaller(new VirtualFileSystemInfo()))
         {
            @Override
            protected void onSuccess(VirtualFileSystemInfo result)
            {
               vfsInfo = result;
               initialized = true;
               eventBus.fireEvent(new ExtensionInitializedEvent(ResourceProviderService.this));
               //IDE.fireEvent(new VfsChangedEvent(result));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               //    Dialogs.getInstance().showError("Workspace " + vfsId + " not found.");
               exception.printStackTrace();
               GWT.log("ResourceProviderService:ailed to start resource provider" + exception.getMessage() + ">"
                  + exception);
            }
         };

      this.vfsInfo = internalCallback.getPayload();
      try
      {
         AsyncRequest.build(RequestBuilder.GET, workspaceURL).send(internalCallback);
      }
      catch (RequestException e)
      {
         throw new ResourceException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void getProject(final String name, final AsyncCallback<Project> callback) throws ResourceException
   {

      // initialize empty project object
      //Project newProject = new Project(name, parentProject, properties);
      ProjectModelProviderAdapter adapter = new ProjectModelProviderAdapter(this);

      // create internal wrapping Request Callback with proper Unmarshaller
      AsyncRequestCallback<ProjectModelProviderAdapter> internalCallback =
         new AsyncRequestCallback<ProjectModelProviderAdapter>(new ProjectModelUnmarshaller(adapter))
         {
            @Override
            protected void onSuccess(ProjectModelProviderAdapter result)
            {
               Project project = result.getProject();
               project.setParent(vfsInfo.getRoot());
               activeProject = project;
               callback.onSuccess(project);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               callback.onFailure(exception);
            }
         };

      String param = "propertyFilter=*&itemType=" + Project.TYPE;
      try
      {
         AsyncRequest
            .build(RequestBuilder.GET, vfsInfo.getRoot().getLinkByRelation(Link.REL_CHILDREN).getHref() + "?" + param)
            .loader(loader).send(internalCallback);
      }
      catch (RequestException e)
      {
         throw new ResourceException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("rawtypes")
   @Override
   public void createProject(String name, JsonArray<Property> properties, final AsyncCallback<Project> callback)
      throws ResourceException
   {
      final Folder rootFolder = vfsInfo.getRoot();
      // initialize empty project object
      ProjectModelProviderAdapter adapter = new ProjectModelProviderAdapter(this);

      // create internal wrapping Request Callback with proper Unmarshaller
      AsyncRequestCallback<ProjectModelProviderAdapter> internalCallback =
         new AsyncRequestCallback<ProjectModelProviderAdapter>(new ProjectModelUnmarshaller(adapter))
         {
            @Override
            protected void onSuccess(ProjectModelProviderAdapter result)
            {
               Project project = result.getProject();
               project.setParent(rootFolder);
               project.setProject(project);
               activeProject = project;
               callback.onSuccess(project);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               callback.onFailure(exception);
            }
         };

      // perform request
      String url = rootFolder.getLinkByRelation(Link.REL_CREATE_PROJECT).getHref();
      url = URL.decode(url).replace("[name]", name);
      // DEPRECATED type not used anymore in 2.0
      url = url.replace("[type]", DEPRECATED_PROJECT_TYPE);
      url = URL.encode(url);
      loader.setMessage("Creating new project...");
      try
      {
         AsyncRequest.build(RequestBuilder.POST, url)
            .data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(properties).toString())
            .header(HTTPHeader.CONTENT_TYPE, "application/json").loader(loader).send(internalCallback);
      }
      catch (RequestException e)
      {
         throw new ResourceException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void registerModelProvider(String primaryNature, ModelProvider modelProvider) throws ResourceException
   {
      modelProviders.put(primaryNature, modelProvider);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ModelProvider getModelProvider(String primaryNature)
   {
      // TODO Project must contain at least GENERIC primary nature
      if (primaryNature != null)
      {
         ModelProvider modelProvider = modelProviders.get(primaryNature);
         if (modelProvider != null)
         {
            return modelProvider;
         }
      }
      return modelProviders.get(GENERIC_PROJECT);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Project getActiveProject()
   {
      return activeProject;
   }

}

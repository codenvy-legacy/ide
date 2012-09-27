/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero GeneralLicense
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU GeneralLicense for more details.
 *
 * You should have received a copy of the GNU GeneralLicense
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.resources.model;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.event.ProjectActionEvent;
import org.exoplatform.ide.core.event.ResourceChangedEvent;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.loader.EmptyLoader;
import org.exoplatform.ide.loader.Loader;
import org.exoplatform.ide.resources.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.resources.marshal.FileUnmarshaller;
import org.exoplatform.ide.resources.marshal.FolderUnmarshaller;
import org.exoplatform.ide.resources.marshal.JSONDeserializer;
import org.exoplatform.ide.resources.marshal.JSONSerializer;
import org.exoplatform.ide.resources.marshal.StringUnmarshaller;
import org.exoplatform.ide.rest.AsyncRequest;
import org.exoplatform.ide.rest.AsyncRequestCallback;
import org.exoplatform.ide.rest.HTTPHeader;
import org.exoplatform.ide.rest.MimeType;

import java.util.Date;

/**
 * Represents Project  model. Responsinble for deserialization of JSon String to generate it' own project model
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class Project extends Folder
{
   public static final String PROJECT_MIME_TYPE = "text/vnd.ideproject+directory";

   public static final String TYPE = "project.generic";

   protected ProjectDescription description = new ProjectDescription(this);

   /** Properties. */
   @SuppressWarnings("rawtypes")
   protected JsonArray<Property> properties;

   protected ResourceProvider provider;

   private Loader loader;

   private final EventBus eventBus;

   /**
    * Constructor for empty project. Used for serialization only.
    * 
    * Not intended to be used by client.
    */
   public Project(EventBus eventBus)
   {
      this(null, null, PROJECT_MIME_TYPE, null, new Date().getTime(), null, JsonCollections.<Link> createStringMap(),
         eventBus);
   }

   /**
    * Not intended to be used by client.
    * 
    * @param name
    * @param parentId
    * @param properties
    */
   @SuppressWarnings("rawtypes")
   public Project(String name, Folder parent, JsonArray<Property> properties, EventBus eventBus)
   {
      this(null, name, PROJECT_MIME_TYPE, parent, 0, properties, JsonCollections.<Link> createStringMap(), eventBus);
   }

   /**
    * Internal constructor for sub-classing
    * 
    */
   @SuppressWarnings("rawtypes")
   protected Project(String id, String name, String mimeType, //String path, 
      Folder parent, long creationDate, JsonArray<Property> properties, JsonStringMap<Link> links, EventBus eventBus)
   {
      super(id, name, TYPE, mimeType, parent, creationDate, links);
      this.properties = properties;
      this.eventBus = eventBus;
      // TODO : receive it in some way
      this.loader = new EmptyLoader();
   }

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   public void init(JSONObject itemObject)
   {
      id = itemObject.get("id").isString().stringValue();
      name = itemObject.get("name").isString().stringValue();
      mimeType = itemObject.get("mimeType").isString().stringValue();
      //path = itemObject.get("path").isString().stringValue();
      //parentId = itemObject.get("parentId").isString().stringValue();
      creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
      properties = (JsonArray)JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
      links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
      //projectType = (itemObject.get("projectType") != null) ? itemObject.get("projectType").isString().stringValue() : null;
      // TODO Unmarshall children 
      this.persisted = true;
   }

   public ProjectDescription getDescription()
   {
      return description;
   }

   /**
    * Other properties.
    *
    * @return properties. If there is no properties then empty list returned, never <code>null</code>
    */
   @SuppressWarnings("rawtypes")
   public JsonArray<Property> getProperties()
   {
      if (properties == null)
      {
         properties = JsonCollections.<Property> createArray();
      }
      return properties;
   }

   /**
    * Get single property with specified name.
    *
    * @param name name of property
    * @return property or <code>null</code> if there is not property with specified name
    */
   @SuppressWarnings("rawtypes")
   public Property getProperty(String name)
   {
      JsonArray<Property> props = getProperties();
      for (int i = 0; i < props.size(); i++)
      {
         Property p = props.get(i);
         if (p.getName().equals(name))
         {
            return p;
         }
      }
      return null;
   }

   /**
    * Check does item has property with specified name.
    *
    * @param name name of property
    * @return <code>true</code> if item has property <code>name</code> and <code>false</code> otherwise
    */
   public boolean hasProperty(String name)
   {
      return getProperty(name) != null;
   }

   /**
    * Get value of property <code>name</code>. It is shortcut for:
    * <pre>
    *    String name = ...
    *    Item item = ...
    *    Property property = item.getProperty(name);
    *    Object value;
    *    if (property != null)
    *       value = property.getValue().get(0);
    *    else
    *       value = null;
    * </pre>
    *
    * @param name property name
    * @return value of property with specified name or <code>null</code>
    */
   @SuppressWarnings("rawtypes")
   public Object getPropertyValue(String name)
   {
      Property p = getProperty(name);
      if (p != null)
      {
         return p.getValue().get(0);
      }
      return null;
   }

   /**
    * Get set of property values
    *
    * @param name property name
    * @return set of property values or <code>null</code> if property does not exists
    * @see #getPropertyValue(String)
    */
   @SuppressWarnings({"rawtypes", "unchecked"})
   public JsonArray getPropertyValues(String name)
   {
      Property p = getProperty(name);
      if (p != null)
      {
         JsonArray values = JsonCollections.createArray();
         values.addAll(p.getValue());
         return values;
      }
      return null;
   }

   // management methods

   /**
    * Create new file.
    * 
    * @param parent
    * @param name
    * @param content
    * @param mimeType
    * @param callback
    * @throws ResourceException
    */
   public void createFile(final Folder parent, String name, String content, String mimeType,
      final AsyncCallback<File> callback)
   {
      try
      {
         checkItemValid(parent);

         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<File> internalCallback = new AsyncRequestCallback<File>(new FileUnmarshaller(new File()))
         {
            @Override
            protected void onSuccess(File newFile)
            {
               // initialize file after unmarshaling
               File file = newFile;
               // add to the list of items
               file.setParent(parent);
               //parent.addChild(file);
               // set proper parent project
               file.setProject(Project.this);
               eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newFile));
               callback.onSuccess(newFile);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               callback.onFailure(exception);
            }
         };

         String url = parent.getLinkByRelation(Link.REL_CREATE_FILE).getHref();
         url = URL.decode(url).replace("[name]", name);
         url = URL.encode(url);
         loader.setMessage("Creating new file...");
         AsyncRequest.build(RequestBuilder.POST, url).data(content).header(HTTPHeader.CONTENT_TYPE, mimeType)
            .loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * Create new Folder.
    * 
    * @param parent
    * @param name
    * @param callback
    * @throws ResourceException
    */
   public void createFolder(final Folder parent, String name, final AsyncCallback<Folder> callback)
   {
      try
      {

         checkItemValid(parent);

         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<Folder> internalCallback =
            new AsyncRequestCallback<Folder>(new FolderUnmarshaller(new Folder()))
            {
               @Override
               protected void onSuccess(Folder newFolder)
               {
                  // initialize file after unmarshaling
                  Folder folder = newFolder;
                  // add to the list of items
                  folder.setParent(parent);
                  //parent.addChild(folder);
                  // set proper parent project
                  folder.setProject(Project.this);
                  eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newFolder));
                  callback.onSuccess(newFolder);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         String url = parent.getLinkByRelation(Link.REL_CREATE_FOLDER).getHref();
         String urlString = URL.decode(url).replace("[name]", name);
         urlString = URL.encode(urlString);
         loader.setMessage("Creating new folder...");
         AsyncRequest.build(RequestBuilder.POST, urlString).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * Delete child resource
    * 
    * @param resource
    * @param callback
    * @throws ResourceException
    */
   public void deleteChild(final Resource resource, final AsyncCallback<Void> callback)
   {
      try
      {
         checkItemValid(resource);
         final Folder parent = resource.getParent();
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>()
         {
            @Override
            protected void onSuccess(Void result)
            {
               // remove from the list of child
               parent.removeChild(resource);
               eventBus.fireEvent(ResourceChangedEvent.createResourceDeletedEvent(resource));
               callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               callback.onFailure(exception);
            }
         };

         // TODO check with lock
         String url = resource.getLinkByRelation(Link.REL_DELETE).getHref();

         if (File.TYPE.equals(resource.getResourceType()) && ((File)resource).isLocked())
         {
            url = URL.decode(url).replace("[lockToken]", ((File)resource).getLock().getLockToken());
         }
         loader.setMessage("Deleting item...");
         AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * @param file
    * @param callback
    * @throws ResourceException
    */
   public void getContent(File file, final AsyncCallback<File> callback)
   {
      try
      {
         checkItemValid(file);

         // content already present
         if (file.getContent() != null)
         {
            callback.onSuccess(file);
         }

         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<File> internalCallback =
            new AsyncRequestCallback<File>(new FileContentUnmarshaller(file))
            {
               @Override
               protected void onSuccess(File result)
               {
                  callback.onSuccess(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         String url = file.getLinkByRelation(Link.REL_CONTENT).getHref();
         loader.setMessage("Loading content...");
         AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * @param file
    * @param callback
    * @throws ResourceException
    */
   public void updateContent(File file, final AsyncCallback<File> callback)
   {
      try
      {

         checkItemValid(file);
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<File> internalCallback =
            new AsyncRequestCallback<File>(new FileContentUnmarshaller(file))
            {
               @Override
               protected void onSuccess(File result)
               {
                  callback.onSuccess(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         // TODO check with lock
         String url = file.getLinkByRelation(Link.REL_CONTENT).getHref();
         url += (file.isLocked()) ? "?lockToken=" + file.getLock().getLockToken() : "";
         loader.setMessage("Updating content...");
         AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.CONTENT_TYPE, file.getMimeType())
            .data(file.getContent()).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * @param file
    * @param callback
    * @throws ResourceException
    */
   public void lock(File file, final AsyncCallback<String> callback)
   {
      try
      {
         checkItemValid(file);
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<StringBuilder> internalCallback =
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller())
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  callback.onSuccess(result.toString());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         String url = file.getLinkByRelation(Link.REL_LOCK).getHref();
         loader.setMessage("Locking file...");
         AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * @param file
    * @param lockToken
    * @param callback
    * @throws ResourceException
    */
   public void unlock(File file, String lockToken, final AsyncCallback<Void> callback)
   {
      try
      {
         checkItemValid(file);
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>()
         {
            @Override
            protected void onSuccess(Void result)
            {
               callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               callback.onFailure(exception);
            }
         };

         String url = file.getLinkByRelation(Link.REL_UNLOCK).getHref();
         url = URL.decode(url).replace("[lockToken]", lockToken);
         loader.setMessage("Unlocking file...");
         AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * @param source
    * @param destination
    * @param lockToken
    * @param callback
    * @throws ResourceException
    */
   public void move(final Resource source, final Folder destination, String lockToken,
      final AsyncCallback<Resource> callback)
   {
      try
      {
         checkItemValid(source);
         checkItemValid(destination);

         AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>()
         {
            @Override
            protected void onSuccess(Void result)
            {
               // TODO : check consistency
               source.setParent(destination);
               eventBus.fireEvent(ResourceChangedEvent.createResourceMovedEvent(source));
               callback.onSuccess(source);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               callback.onFailure(exception);
            }
         };

         // TODO check with locks
         String url = source.getLinkByRelation(Link.REL_MOVE).getHref();
         url = URL.decode(url).replace("[parentId]", destination.getId());
         if (File.TYPE.equals(source.getResourceType()) && ((File)source).isLocked())
         {
            url = URL.decode(url).replace("[lockToken]", ((File)source).getLock().getLockToken());
         }
         url = URL.encode(url);
         loader.setMessage("Moving item...");
         AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * @param source
    * @param destination
    * @param callback
    * @throws ResourceException
    */
   public void copy(final Resource source, final Folder destination, final AsyncCallback<Resource> callback)

   {
      callback.onFailure(new Exception("copy not supported"));
   }

   /**
   * @param item
   * @param mediaType
   * @param newname
   * @param lockToken
   * @param callback
   * @throws RequestException
   */
   public void rename(final Resource resource, final String newname, String lockToken,
      final AsyncCallback<Resource> callback)
   {
      try
      {
         checkItemValid(resource);

         // internal call back
         AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>()
         {
            @Override
            protected void onSuccess(Void result)
            {
               // TODO : check consistency
               resource.setName(newname);
               eventBus.fireEvent(ResourceChangedEvent.createResourceRenamedEvent(resource));
               callback.onSuccess(resource);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               callback.onFailure(exception);
            }
         };

         String url = resource.getLinkByRelation(Link.REL_RENAME).getHref();
         url = URL.decode(url);
         url = url.replace("mediaType=[mediaType]", "");
         url =
            (newname != null && !newname.isEmpty()) ? url.replace("[newname]", newname) : url.replace(
               "newname=[newname]", "");

         if (File.TYPE.equals(resource.getResourceType()) && ((File)resource).isLocked())
         {
            url = URL.decode(url).replace("[lockToken]", ((File)resource).getLock().getLockToken());
         }

         url = url.replace("?&", "?");
         url = url.replaceAll("&&", "&");
         url = URL.encode(url);
         loader.setMessage("Renaming item...");
         AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * @param callback
    * @throws ResourceException
    */
   public void flushProjectProperties(final AsyncCallback<Project> callback)
   {
      try
      {
         AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>()
         {
            @Override
            protected void onSuccess(Void result)
            {
               eventBus.fireEvent(ProjectActionEvent.createProjectDescriptionChangedEvent(Project.this));
               callback.onSuccess(Project.this);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               callback.onFailure(exception);
            }
         };

         String url = this.getLinkByRelation(Link.REL_SELF).getHref();
         loader.setMessage("Updating item...");
         AsyncRequest.build(RequestBuilder.POST, url)
            .data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(getProperties()).toString())
            .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
            .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * 
    * @param source
    * @param destination
    * @param callback
    * @throws ResourceException
    */
   public void search(final AsyncCallback<JsonArray<Resource>> callback)
   {
      callback.onFailure(new Exception("Operation not currently supported"));
   }

   // ====================================================================================================

   /**
    * Check if resource belongs to this project
    * 
    * @param resource
    * @throws ResourceException
    */
   private void checkItemValid(final Resource resource) throws Exception
   {
      if (resource == null)
      {
         throw new Exception("Resource is null.");
      }
      if (resource.getProject() != this)
      {
         throw new Exception("Resource is out of the project's scope. Project : " + getName() + ", resource path is : "
            + resource.getPath());
      }
   }
}

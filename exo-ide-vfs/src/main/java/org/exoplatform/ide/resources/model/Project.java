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
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.resources.ResourceException;
import org.exoplatform.ide.resources.ResourceProvider;
import org.exoplatform.ide.resources.marshal.FileUnmarshaller;
import org.exoplatform.ide.resources.marshal.FolderUnmarshaller;
import org.exoplatform.ide.resources.marshal.JSONDeserializer;
import org.exoplatform.ide.resources.properties.Property;

import java.util.Date;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
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

   /**
    * Constructor for empty project. Used for serialization only.
    * 
    * Not intended to be used by client.
    */
   public Project()
   {
      this(null, null, PROJECT_MIME_TYPE, null, null, new Date().getTime(), null, JsonCollections
         .<Link> createStringMap());
   }

   /**
    * Not intended to be used by client.
    * 
    * @param name
    * @param parentId
    * @param properties
    */
   @SuppressWarnings("rawtypes")
   public Project(String name, Folder parent, JsonArray<Property> properties)
   {
      this(null, name, PROJECT_MIME_TYPE, null, parent, 0, properties, JsonCollections.<Link> createStringMap());
   }

   /**
    * Internal constructor for sub-classing
    * 
    */
   @SuppressWarnings("rawtypes")
   protected Project(String id, String name, String mimeType, String path, Folder parent, long creationDate,
      JsonArray<Property> properties, JsonStringMap<Link> links)
   {
      super(id, name, TYPE, mimeType, path, parent, creationDate, links);
      this.properties = properties;
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
      path = itemObject.get("path").isString().stringValue();
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
    */
   public void createFile(final Folder parent, String name, String content, String mimeType,
      final AsyncCallback<File> callback) throws ResourceException
   {
      checkValidParent(parent);

      // create internal wrapping Request Callback with proper Unmarshaller
      AsyncRequestCallback<File> internalCallback = new AsyncRequestCallback<File>(new FileUnmarshaller(new File()))
      {
         @Override
         protected void onSuccess(File result)
         {
            // initialize file after unmarshaling
            File file = result;
            file.setParent(parent);
            file.setProject(Project.this);
            callback.onSuccess(result);
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
      try
      {
         AsyncRequest.build(RequestBuilder.POST, url).data(content).header(HTTPHeader.CONTENT_TYPE, mimeType)
            .loader(loader).send(internalCallback);
      }
      catch (RequestException e)
      {
         throw new ResourceException(e);
      }
   }

   /**
    * @param parent
    * @throws ResourceException
    */
   private void checkValidParent(final Folder parent) throws ResourceException
   {
      if (parent.getProject() != this)
      {
         throw new ResourceException("Parent folder is out of the project's scope.");
      }
   }

   /**
    * Create new Folder.
    */
   public void createFolder(final Folder parent, String name, final AsyncCallback<Folder> callback) throws ResourceException
   {
      
      checkValidParent(parent);

      // create internal wrapping Request Callback with proper Unmarshaller
      AsyncRequestCallback<Folder> internalCallback = new AsyncRequestCallback<Folder>(new FolderUnmarshaller(new Folder()))
      {
         @Override
         protected void onSuccess(Folder result)
         {
            // initialize file after unmarshaling
            Folder folder = result;
            folder.setParent(parent);
            folder.setProject(Project.this);
            callback.onSuccess(result);
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
      try
      {
         AsyncRequest.build(RequestBuilder.POST, urlString).loader(loader).send(internalCallback);
      }
      catch (RequestException e)
      {
         throw new ResourceException(e);
      }
   }
}

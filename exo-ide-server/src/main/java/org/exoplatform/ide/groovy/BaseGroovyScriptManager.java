/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.groovy;

import org.everrest.core.ResourcePublicationException;
import org.everrest.groovy.BaseResourceId;
import org.everrest.groovy.ResourceId;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BaseGroovyScriptManager.java 3720 2010-12-23 15:06:33Z
 *          aparfonov $
 * @deprecated
 */
// Contains all method what we do not need any more in GroovyScript2RestLoader
// but need to keep for back compatibility.
public abstract class BaseGroovyScriptManager
{
   protected GroovyResourcePublisher groovyPublisher;

   public BaseGroovyScriptManager(GroovyResourcePublisher groovyPublisher)
   {
      this.groovyPublisher = groovyPublisher;
   }

   public boolean isLoaded(ResourceId key)
   {
      return groovyPublisher.isPublished(key);
   }

   public boolean isLoaded(String key)
   {
      return isLoaded(new BaseResourceId(key));
   }

   public boolean isLoaded(URL url)
   {
      return isLoaded(new BaseResourceId(url.toString()));
   }

   public Response load(String repository, String workspace, String path, boolean state)
   {
      return load(repository, workspace, path, state, null);
   }

   public abstract Response load(String repository, String workspace, String path, boolean state, List<String> sources,
      List<String> files, MultivaluedMap<String, String> properties);

   public Response load(String repository, String workspace, String path, boolean state,
      MultivaluedMap<String, String> properties)
   {
      return load(repository, workspace, path, state, null, null, properties);
   }

   public boolean loadScript(ResourceId key, String name, InputStream stream) throws IOException
   {
      try
      {
         //TODO check params
         groovyPublisher.publishPerRequest(stream, key, null, null, null);
         return true;
      }
      catch (ResourcePublicationException e)
      {
         return false;
      }
   }

   public boolean loadScript(String key, InputStream stream) throws IOException
   {
      return loadScript(key, null, stream);
   }

   public boolean loadScript(String key, String name, InputStream stream) throws IOException
   {
      return loadScript(new BaseResourceId(key), name, stream);
   }

   public boolean loadScript(URL url) throws IOException
   {
      ResourceId key = new BaseResourceId(url.toString());
      try
      {
         //TODO check params
         groovyPublisher.publishPerRequest(new BufferedInputStream(url.openStream()), key, null, null, null);
         return true;
      }
      catch (ResourcePublicationException e)
      {
         return true;
      }
   }

   public boolean unloadScript(ResourceId key)
   {
      return null != groovyPublisher.unpublishResource(key);
   }

   public boolean unloadScript(String key)
   {
      return unloadScript(new BaseResourceId(key));
   }

   public void unloadScript(URL url)
   {
      unloadScript(new BaseResourceId(url.toString()));
   }

   public Response validateScript(String name, final InputStream script)
   {
      return validateScript(name, script, null, null);
   }

   public abstract Response validateScript(String name, InputStream script, List<String> sources, List<String> files);
}

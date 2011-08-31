/*
 * Copyright (C) 2009 eXo Platform SAS.
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

import org.everrest.core.ObjectFactory;
import org.everrest.core.resource.AbstractResourceDescriptor;
import org.everrest.groovy.ResourceId;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: GroovyScript2RestUpdateListener.java 34445 2009-07-24 07:51:18Z
 *          dkatayev $
 */
@SuppressWarnings("deprecation")
public class GroovyScript2RestUpdateListener implements EventListener
{
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(GroovyScript2RestUpdateListener.class);

   /** Repository name. */
   private final String repository;

   /** Workspace name. */
   private final String workspace;

   /** See {@link GroovyScript2RestLoader}. */
   private final GroovyScript2RestLoader groovyScript2RestLoader;

   /** See {@link Session}. */
   private final Session session;

   /**
    * @param repository repository name
    * @param workspace workspace name
    * @param groovyScript2RestLoader See {@link GroovyScript2RestLoader}
    * @param session JCR session
    */
   public GroovyScript2RestUpdateListener(String repository, String workspace,
      GroovyScript2RestLoader groovyScript2RestLoader, Session session)
   {
      this.repository = repository;
      this.workspace = workspace;
      this.groovyScript2RestLoader = groovyScript2RestLoader;
      this.session = session;
   }

   /**
    * {@inheritDoc}
    */
   public void onEvent(EventIterator eventIterator)
   {
      // waiting for Event.PROPERTY_ADDED, Event.PROPERTY_REMOVED,
      // Event.PROPERTY_CHANGED
      try
      {
         while (eventIterator.hasNext())
         {
            Event event = eventIterator.nextEvent();
            String path = event.getPath();

            if (path.endsWith("/jcr:data"))
            {
               // jcr:data removed 'exo:groovyResourceContainer' then unbind resource
               if (event.getType() == Event.PROPERTY_REMOVED)
               {
                  unloadScript(path.substring(0, path.lastIndexOf('/')));
               }
               else if (event.getType() == Event.PROPERTY_ADDED || event.getType() == Event.PROPERTY_CHANGED)
               {
                  Node node = session.getItem(path).getParent();
                  if (node.getProperty("exo:autoload").getBoolean())
                     loadScript(node);
               }
            }
         }
      }
      catch (Exception e)
      {
         LOG.error("Process event failed. ", e);
      }
   }

   /**
    * Load script form supplied node.
    *
    * @param node JCR node
    * @throws Exception if any error occurs
    */
   private void loadScript(Node node) throws Exception
   {
      ResourceId key = new NodeScriptKey(repository, workspace, node);
      ObjectFactory<AbstractResourceDescriptor> resource =
         groovyScript2RestLoader.groovyPublisher.unpublishResource(key);
      if (resource != null)
      {
        //TODO check params:
         groovyScript2RestLoader.groovyPublisher.publishPerRequest(node.getProperty("jcr:data").getStream(), key,
            resource.getObjectModel().getProperties(), null, null);
      }
      else
      {
         //TODO check params:
         groovyScript2RestLoader.groovyPublisher.publishPerRequest(node.getProperty("jcr:data").getStream(), key, null, null, null);
      }
   }

   /**
    * Unload script.
    *
    * @param path unified JCR node path
    * @throws Exception if any error occurs
    */
   private void unloadScript(String path) throws Exception
   {
      ResourceId key = new NodeScriptKey(repository, workspace, path);
      groovyScript2RestLoader.groovyPublisher.unpublishResource(key);
   }

}

/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.groovy.server;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.JsonWriter;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.bean.Dependency;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class ProjectCreationListener implements EventListener
{
   /**
    * 
    */
   private static final String EXOIDE_CLASSPATH = "exoide:classpath";

   private static final Log LOG = ExoLogger.getLogger(ProjectCreationListener.class);

   private static final String DEFAULT_JAR_FILES_LIST = "codeassistant/jar-files.txt";

   private static final Set<String> groovyProjects = new HashSet<String>();

   private static String dependencys;

   static
   {
      groovyProjects.add("eXo");
   }

   /**
    * 
    */
   public ProjectCreationListener()
   {
      if (dependencys == null)
      {
         InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_JAR_FILES_LIST);
         JsonParser p = new JsonParser();
         try
         {
            p.parse(in);
            Dependency[] dep = (Dependency[])ObjectBuilder.createArray(Dependency[].class, p.getJsonObject());
            JsonValue jsonArray = JsonGenerator.createJsonArray(dep);
            ByteArrayOutputStream ou = new ByteArrayOutputStream();
            JsonWriter w = new JsonWriter(ou);
            jsonArray.writeTo(w);
            w.flush();
            dependencys = ou.toString();
         }
         catch (JsonException e)
         {
            LOG.error("Can't parse dependency", e);
         }
         finally
         {
            if (in != null)
               try
               {
                  in.close();
               }
               catch (IOException skip)
               {
               }
         }
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.observation.EventListener#handleEvent(org.exoplatform.ide.vfs.server.observation.ChangeEvent)
    */
   @Override
   public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = event.getVirtualFileSystem();
      Item project = vfs.getItem(event.getItemId(), PropertyFilter.ALL_FILTER);
      if (!groovyProjects.contains(project.getPropertyValue("vfs:projectType")))
         return;

      if (!project.hasProperty(EXOIDE_CLASSPATH))
      {
         List<Property> properties = Arrays.asList(new Property(EXOIDE_CLASSPATH, dependencys));
         vfs.updateItem(project.getId(), properties, null);
      }

   }
}

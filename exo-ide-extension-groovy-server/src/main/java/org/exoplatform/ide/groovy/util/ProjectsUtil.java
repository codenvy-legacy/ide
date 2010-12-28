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
package org.exoplatform.ide.groovy.util;

import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 24, 2010 $
 *
 */
public class ProjectsUtil
{
   public static final String PROJECTS_CONTEXT = "/IDE/projects";

   public static final String PROJECT = "project";

   public static final String BASE_LOCATION = "base-location";

   public static final String CLASSPATH_LOCATION = "classpath-location";

   /**
    * Get projects' information from registry.
    * 
    * @param registryService registry service
    * @param sessionProviderService session provider service
    * @return map of projects with key - project location and value - class path file's location
    */
   public static Map<String, String> getProjects(RegistryService registryService,
      SessionProviderService sessionProviderService)
   {
      Map<String, String> projects = new HashMap<String, String>();
      RegistryEntry registryEntry;
      try
      {
         //Get "projects" registry entry:
         registryEntry =
            registryService.getEntry(sessionProviderService.getSessionProvider(null), RegistryService.EXO_APPLICATIONS
               + PROJECTS_CONTEXT);
      }
      catch (PathNotFoundException pathNotFoundException)
      {
         //No "projects" entry in registry, return empty projects map:
         return projects;
      }
      catch (RepositoryException e)
      {
         return projects;
      }
      //Get entries with projects:
      NodeList projectNodes = registryEntry.getDocument().getElementsByTagName(PROJECT);
      for (int i = 0; i < projectNodes.getLength(); i++)
      {
         org.w3c.dom.Node projectNode = projectNodes.item(i);
         String baseLocation = "";
         String classpathLocation = "";
         for (int j = 0; j < projectNode.getChildNodes().getLength(); j++)
         {
            org.w3c.dom.Node node = projectNode.getChildNodes().item(j);
            String value = (node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : "";
            try
            {
               value = URLDecoder.decode(value, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
               System.out.println("Registry value will not be decoded: " + value);
               e.printStackTrace();
            }
            if (BASE_LOCATION.equals(node.getNodeName()))
            {
               baseLocation = value;
            }
            else if (CLASSPATH_LOCATION.equals(node.getNodeName()))
            {
               classpathLocation = value;
            }
         }
         projects.put(baseLocation, classpathLocation);
      }
      return projects;
   }

   /**
    * Get location of class path file, if script pointed be fileHref parameter
    * is a part of one of the pointed projects.
    * 
    * @param projects projects
    * @param fileHref script location
    * @return {@link String} location of class path file
    */
   public static String getClasspathLocation(Map<String, String> projects, String fileHref)
   {
      String dependencyLocation = null;
      for (String projectLocation : projects.keySet())
      {
         if (fileHref.startsWith(projectLocation))
         {
            dependencyLocation = projects.get(projectLocation);
            break;
         }
      }
      return dependencyLocation;
   }
}

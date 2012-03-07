/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.shell.server.rest;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 1, 2011 evgen $
 * 
 */
public class ShellApplication extends Application
{
   private Set<Class<?>> classes = new HashSet<Class<?>>();

   private final Set<Object> objects = new HashSet<Object>();

   public ShellApplication(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      String entryPoint = readValueParam(initParams, "defaultEntryPoint");
      boolean discoverable = Boolean.parseBoolean(readValueParam(initParams, "discoverable"));
      String workspace = readValueParam(initParams, "workspace");
      String config = readValueParam(initParams, "config");

      objects.add(new ShellConfigurationService(vfsRegistry, entryPoint, discoverable, workspace, config));

      classes.add(CRaSHService.class);
      classes.add(CLIResourcesService.class);
   }

   /**
    * @see javax.ws.rs.core.Application#getClasses()
    */
   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   /**
    * @see javax.ws.rs.core.Application#getSingletons()
    */
   @Override
   public Set<Object> getSingletons()
   {
      return objects;
   }

   /**
    * Read value param from init params.
    * 
    * @param initParams
    * @param paramName
    * @return value param or null if value not found.
    */
   private static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
            return vp.getValue();
      }
      return null;
   }

}

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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.PackageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class UpdateInvoker
{

   private static final Logger LOG = LoggerFactory.getLogger(UpdateInvoker.class);

   private final InfoStorage infoStorage;

   private List<Dependency> dependencies;

   private File dependencyFolder;

   /**
    * @param infoStorage
    * @param dependencies
    * @param dependencyFolder
    * @param queue 
    */
   public UpdateInvoker(InfoStorage infoStorage, List<Dependency> dependencies, File dependencyFolder)
   {
      this.infoStorage = infoStorage;
      this.dependencies = dependencies;
      this.dependencyFolder = dependencyFolder;
   }

   public void execute()
   {
      try
      {
         DataWriter writer = infoStorage.getWriter();

         for (Dependency dep : dependencies)
         {
            Set<String> packages = new TreeSet<String>();
            String jarName = getJarName(dep);
            if (LOG.isDebugEnabled())
               LOG.debug("Load info from: " + jarName);
            try
            {
               File jarFile = new File(dependencyFolder, jarName);
               List<TypeInfo> typeInfos = JarParser.parse(jarFile);
               packages.addAll(PackageParser.parse(jarFile));
               writer.addTypeInfo(typeInfos, dep.toString());
               writer.addPackages(packages, dep.toString());
            }
            catch (IOException e)
            {
               if (LOG.isDebugEnabled())
                  LOG.debug("Can't open: " + jarName, e);
            }
         }
      }
      catch (IOException e)
      {
         LOG.error("Can't index dependency", e);
      }
      finally
      {
         UpdateUtil.delete(dependencyFolder);
      }
   }

   /**
    * @param dep
    * @return
    */
   private String getJarName(Dependency dep)
   {
      StringBuilder b = new StringBuilder();
      b.append(dep.getArtifactID()).append('-').append(dep.getVersion()).append('.').append(dep.getType());
      return b.toString();
   }
}

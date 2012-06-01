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

import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class DockUpdateInvoker implements UpdateInvoker
{
   private static final Logger LOG = LoggerFactory.getLogger(DockUpdateInvoker.class);

   private final InfoStorage infoStorage;

   private final List<Dependency> dependencies;

   private final File dependencyFolder;

   /**
    * @param infoStorage
    * @param dependencies
    * @param createDependencys
    */
   public DockUpdateInvoker(InfoStorage infoStorage, List<Dependency> dependencies, File dependencyFolder)
   {
      this.infoStorage = infoStorage;
      this.dependencies = dependencies;
      this.dependencyFolder = dependencyFolder;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.UpdateInvoker#execute()
    */
   @Override
   public void execute()
   {
      QDoxJavaDocExtractor javaDocExtractor = new QDoxJavaDocExtractor();
      try
      {
         DataWriter writer = infoStorage.getWriter();
         for (Dependency dep : dependencies)
         {

            String artifact = dep.toString();
            if (infoStorage.isJavaDockForArtifactExist(artifact))
               continue;

            String jarName = getJarName(dep);
            File jarFile = new File(dependencyFolder, jarName);
            
            if (LOG.isDebugEnabled())
               LOG.debug("Load javadoc from: " + jarName);
            
            if (!jarFile.exists())
               continue;

            InputStream zipStream = new FileInputStream(jarFile);
            try
            {
               Map<String, String> javaDocs = javaDocExtractor.extractZip(zipStream);
               writer.addJavaDocs(javaDocs, artifact);
            }
            finally
            {
               zipStream.close();
            }
         }
      }
      catch (IOException e)
      {
         LOG.error("Can't index javadoc for", e);
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
      b.append(dep.getArtifactID()).append('-').append(dep.getVersion()).append('-').append("sources")
         .append('.').append(dep.getType());
      return b.toString();
   }

}

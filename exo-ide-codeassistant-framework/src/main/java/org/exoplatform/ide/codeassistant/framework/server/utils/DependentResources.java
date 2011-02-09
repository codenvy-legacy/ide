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
package org.exoplatform.ide.codeassistant.framework.server.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for source files and folders with sources, 
 * which are taken from classpath information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 27, 2010 $
 *
 */
public class DependentResources
{
   /**
    * Paths to source files.
    */
   private List<String> fileSources;

   /**
    * Paths to folders with source files.
    */
   private List<String> folderSources;

   /**
    * @param groovyClassPath
    */
   public DependentResources(GroovyClassPath groovyClassPath)
   {
      fileSources = new ArrayList<String>();
      folderSources = new ArrayList<String>();

      //Separate files and folders:
      for (GroovyClassPathEntry entry : groovyClassPath.getEntries())
      {
         if (entry.getKind().equals("dir"))
         {
            folderSources.add(entry.getPath());
         }
         else if (entry.getKind().equals("file"))
         {
            fileSources.add(entry.getPath());
         }
      }
   }

   /**
    * @return the fileSources
    */
   public List<String> getFileSources()
   {
      if (fileSources == null)
      {
         fileSources = new ArrayList<String>();
      }
      return fileSources;
   }

   /**
    * @return the folderSources
    */
   public List<String> getFolderSources()
   {
      if (folderSources == null)
      {
         folderSources = new ArrayList<String>();
      }
      return folderSources;
   }
}

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
package org.exoplatform.ide.codeassistant.framework.server.impl.storage;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class FileFinder
{

   private static final Log LOG = ExoLogger.getLogger(FileFinder.class);

   private List<String> fileList = new ArrayList<String>();

   public FileFinder(String path)
   {
      if (path.indexOf(";") >= 0)
      {
         String[] pathes = path.split(";");
         for (String p : pathes)
         {
            if (!p.isEmpty())
            {
               findForFiles(p);
            }
         }
      }
      else
      {
         findForFiles(path);
      }
   }

   public List<String> getFileList()
   {
      return fileList;
   }

   private void findForFiles(String fileMask)
   {
      fileMask = fileMask.trim();

      try
      {
         File file = new File(fileMask);
         if (file.isFile() && file.exists())
         {
            fileList.add(fileMask);
         }
         else
         {
            findForFilesByMask(fileMask);
         }

      }
      catch (Exception e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e);
      }

   }

   private void findForFilesByMask(String fileMask)
   {
      if (fileMask.contains("/"))
      {
         try
         {
            String directory = fileMask.substring(0, fileMask.lastIndexOf("/"));
            String fileNamePattern = fileMask.substring(fileMask.lastIndexOf("/") + 1);

            File directoryContent = new File(directory);
            String[] files = directoryContent.list(new Filter(fileNamePattern));

            if (files == null)
               return;

            for (String f : files)
            {
               fileList.add(directory + File.separator + f);
            }
         }
         catch (Exception e)
         {
            if (LOG.isDebugEnabled())
               LOG.error(e);
         }
      }
      else
      {
         LOG.info("This case is not implemented!");
      }
   }

   private class Filter implements FilenameFilter
   {

      private String startWith;

      private String endWith;

      private String fileName;

      public Filter(String fileNamePattern)
      {
         if (fileNamePattern.contains("*"))
         {
            startWith = fileNamePattern.substring(0, fileNamePattern.indexOf("*"));
            endWith = fileNamePattern.substring(fileNamePattern.lastIndexOf("*") + 1);
         }
         else
         {
            fileName = fileNamePattern;
         }
      }

      @Override
      public boolean accept(File dir, String name)
      {
         if (fileName != null)
         {
            if (fileName.equalsIgnoreCase(name))
            {
               return true;
            }
         }
         else
         {
            if (name.startsWith(startWith) && name.endsWith(endWith))
            {
               return true;
            }
         }

         return false;
      }

   }

}

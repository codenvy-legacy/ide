/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.exoplatform.ide.client.services.FileSystemService;
import org.exoplatform.ide.shared.model.File;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple RPC File System service for testing purposes, to be replaced with eXo IDE REST 
 * Services. 
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 26, 2012  
 */
public class FileSystemSeriveImpl extends RemoteServiceServlet implements FileSystemService
{

   private static final String HOME = "/home/exo/java/ws/";

   private static final long serialVersionUID = 1L;

   /**
   * {@inheritDoc}
   */
   public String getFileContent(String fileName) throws IOException
   {
      // No real need to close the BufferedReader/InputStreamReader
      // as they're only wrapping the stream
      FileInputStream stream = new FileInputStream(HOME + fileName);
      try
      {
         Reader reader = new BufferedReader(new InputStreamReader(stream));
         StringBuilder builder = new StringBuilder();
         char[] buffer = new char[8192];
         int read;
         while ((read = reader.read(buffer, 0, buffer.length)) > 0)
         {
            builder.append(buffer, 0, read);
         }
         return builder.toString();
      }
      finally
      {
         // Potential issue here: if this throws an IOException,
         // it will mask any others. Normally I'd use a utility
         // method which would log exceptions and swallow them
         stream.close();
      }
   }

   /**
    * {@inheritDoc}
    */
   public File[] getFileList()
   {

      java.io.File file = new java.io.File(HOME);
      String[] list;
      if (file.exists())
      {
         list = file.list();

         List<File> fileList = new ArrayList<File>();
         for (int i = 0; i < list.length; i++)
         {
            String fileName = list[i];
            if (!new java.io.File(fileName).isDirectory())
            {
               fileList.add(new File(fileName));
            }
         }
         return fileList.toArray(new File[fileList.size()]);
      }
      return new File[0];

   }
}

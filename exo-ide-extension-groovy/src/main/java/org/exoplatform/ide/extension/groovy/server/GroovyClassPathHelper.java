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
package org.exoplatform.ide.extension.groovy.server;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPathEntry;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GroovyClassPathHelper
{
   public static GroovyClassPath getGroovyClassPath(String projectId, VirtualFileSystem vfs)
      throws VirtualFileSystemException, JsonException
   {
      Folder project = (Folder)vfs.getItem(projectId, PropertyFilter.NONE_FILTER);
      InputStream classPathStream = null;
      try
      {
         JsonParser jsonParser = new JsonParser();
         jsonParser.parse(vfs.getContent(project.createPath(".groovyclasspath"), null).getStream());
         JsonValue jsonValue = jsonParser.getJsonObject();
         GroovyClassPath classPath = ObjectBuilder.createObject(GroovyClassPath.class, jsonValue);
         return classPath;
      }
      catch (ItemNotFoundException e)
      {
         return null;
      }
      finally
      {
         if (classPathStream != null)
         {
            try
            {
               classPathStream.close();
            }
            catch (IOException ignored)
            {
            }
         }
      }
   }

   public static SourceFolder[] getSourceFolders(GroovyClassPath classPath)
   {
      GroovyClassPathEntry[] classPathEntries = classPath.getEntries();
      List<SourceFolder> src = new ArrayList<SourceFolder>();
      for (int i = 0; i < classPathEntries.length; i++)
      {
         GroovyClassPathEntry entry = classPathEntries[i];
         if ("dir".equals(entry.getKind()))
         {
            URI folderURI = URI.create(entry.getPath());

            // If there is no scheme use 'ide+vfs'.
            if (folderURI.getScheme() == null)
            {
               String fragment = folderURI.getFragment();
               if (!fragment.endsWith("/"))
               {
                  fragment = fragment + "/";
               }
               String path = folderURI.getPath(); // virtual file system ID
               if (!path.startsWith("/"))
               {
                  path = "/" + path;
               }
               try
               {
                  folderURI = new URI("ide+vfs", null, null, -1, path, null, fragment);
               }
               catch (URISyntaxException e)
               {
                  throw new RuntimeException(e); // Should not happen.
               }
            }

            try
            {
               src.add(new SourceFolder(folderURI.toURL()));
            }
            catch (MalformedURLException e)
            {
               throw new RuntimeException("Invalid URL of class path item " + entry.getPath());
            }
         }
      }
      return src.toArray(new SourceFolder[src.size()]);
   }

   public static SourceFile[] getSourceFiles(GroovyClassPath classPath)
   {
      GroovyClassPathEntry[] classPathEntries = classPath.getEntries();
      List<SourceFile> files = new ArrayList<SourceFile>();
      for (int i = 0; i < classPathEntries.length; i++)
      {
         GroovyClassPathEntry entry = classPathEntries[i];
         if ("file".equals(entry.getKind()))
         {
            URI fileURI = URI.create(entry.getPath());

            // If there is no scheme use 'ide+vfs'.
            if (fileURI.getScheme() == null)
            {
               String path = fileURI.getPath(); // virtual file system ID
               if (!path.startsWith("/"))
               {
                  path = "/" + path;
               }
               try
               {
                  fileURI = new URI("ide+vfs", null, null, -1, path, null, fileURI.getFragment());
               }
               catch (URISyntaxException e)
               {
                  throw new RuntimeException(e); // Should not happen.
               }
            }

            try
            {
               files.add(new SourceFile(fileURI.toURL()));
            }
            catch (MalformedURLException e)
            {
               throw new RuntimeException("Invalid URL of class path item " + entry.getPath());
            }
         }
      }
      return files.toArray(new SourceFile[files.size()]);
   }

   private GroovyClassPathHelper()
   {
   }
}

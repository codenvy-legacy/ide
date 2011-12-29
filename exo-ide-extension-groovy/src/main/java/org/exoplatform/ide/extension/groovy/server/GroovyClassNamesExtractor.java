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
package org.exoplatform.ide.extension.groovy.server;

import org.everrest.groovy.ExtendedGroovyClassLoader;
import org.everrest.groovy.GroovyClassLoaderProvider;
import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;
import org.exoplatform.ide.codeassistant.framework.server.extractors.TypeInfoExtractor;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extracting meta information from given Groovy classes (methods & constructors) to the
 * bean object that can be transform to JSON
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyClassNamesExtractor Jan 18, 2011 9:30:38 AM evgen $
 */
public class GroovyClassNamesExtractor
{
   private GroovyClassLoaderProvider classLoaderProvider;

   private VirtualFileSystem vfs;

   public GroovyClassNamesExtractor(VirtualFileSystem vfs, GroovyClassLoaderProvider classLoaderProvider)
   {
      this.vfs = vfs;
      this.classLoaderProvider = classLoaderProvider;

   }

   /**
    * Get list of Groovy class names in project classpath.
    *
    * @param prefix of class name
    * @param groovyClassPath classpath
    * @return {@link List} of {@link ShortTypeInfo}
    * @throws MalformedURLException
    * @throws URISyntaxException
    */
   public List<ShortTypeInfo> getClassNames(String prefix, GroovyClassPath groovyClassPath) throws MalformedURLException,
      URISyntaxException, VirtualFileSystemException
   {

      SourceFolder[] folders = GroovyClassPathHelper.getSourceFolders(groovyClassPath);
      SourceFile[] files = GroovyClassPathHelper.getSourceFiles(groovyClassPath);

      List<SourceFile> result = new ArrayList<SourceFile>();
      for (SourceFolder f : folders)
      {
         // Convert to URI. Need to have decoded path.
         URI theUri = URI.create(f.getPath().toString());
         if ("ide+vfs".equals(theUri.getScheme()))
         {
            // TODO : If possible avoid to use JCR specific attributes in search statement.
            String sql = "SELECT * FROM nt:file WHERE jcr:path LIKE '" + theUri.getFragment() + "%'";
            ItemList<Item> itemList = vfs.search(sql, -1, 0);

            for (Item item : itemList.getItems())
            {
               if (
                  (item.getName().endsWith(".groovy")
                     || item.getName().endsWith(".grs")
                     || item.getName().endsWith(".cmtc")
                  ) && item.getName().startsWith(prefix))
               {
                  result.add(new SourceFile(new URI("ide+vfs", "/" + vfs.getInfo().getId(), item.getPath()).toURL()));
               }
            }
         }
      }
      if (files != null)
      {
         result.addAll(Arrays.asList(files));
      }

      ExtendedGroovyClassLoader classLoader = folders != null && folders.length > 0
         ? classLoaderProvider.getGroovyClassLoader(folders)
         : classLoaderProvider.getGroovyClassLoader();

      classLoader.parseClasses(result.toArray(new SourceFile[result.size()]));
      Class<?>[] classes = classLoader.getLoadedClasses();
      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();

      for (Class<?> clazz : classes)
      {
         if (clazz.getName().contains("$"))
         {
            continue;
         }
         if (clazz.getSimpleName().startsWith(prefix))
         {
            types.add(TypeInfoExtractor.extract(clazz));
         }
      }

      return types;
   }

   /**
    * Get type info for specific FQN.
    *
    * @param fqn of type
    * @param groovyClassPath Classpath for current project
    * @return type info
    * @throws MalformedURLException
    * @throws URISyntaxException
    * @throws VirtualFileSystemException
    */
   public TypeInfo getClassInfo(String fqn, GroovyClassPath groovyClassPath) throws MalformedURLException,
      URISyntaxException, VirtualFileSystemException
   {
      SourceFolder[] folders = GroovyClassPathHelper.getSourceFolders(groovyClassPath);
      SourceFile[] files = GroovyClassPathHelper.getSourceFiles(groovyClassPath);

      List<SourceFile> result = new ArrayList<SourceFile>();

      for (SourceFolder f : folders)
      {
         // Convert to URI. Need to have decoded path.
         URI theUri = URI.create(f.getPath().toString());
         if ("ide+vfs".equals(theUri.getScheme()))
         {
            // TODO : If possible avoid to use JCR specific attributes in search statement.
            String sql = "SELECT * FROM nt:file WHERE jcr:path LIKE '" + theUri.getFragment() + "%'";
            ItemList<Item> search = vfs.search(sql, -1, 0);
            for (Item item : search.getItems())
            {
               if (item.getName().endsWith(".groovy")
                  || item.getName().endsWith(".grs")
                  || item.getName().endsWith(".cmtc"))
               {
                  result.add(new SourceFile(new URI("ide+vfs", "/" + vfs.getInfo().getId(), item.getPath()).toURL()));
               }
            }
         }

      }
      if (files != null)
      {
         result.addAll(Arrays.asList(files));
      }

      ExtendedGroovyClassLoader classLoader = folders != null && folders.length > 0
         ? classLoaderProvider.getGroovyClassLoader(folders)
         : classLoaderProvider.getGroovyClassLoader();

      classLoader.parseClasses(result.toArray(new SourceFile[result.size()]));
      Class<?>[] classes = classLoader.getLoadedClasses();
      for (Class<?> clazz : classes)
      {
         if (clazz.getName().equals(fqn))
         {
            return TypeInfoExtractor.extract(clazz);
         }
      }

      return null;
   }

}

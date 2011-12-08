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
import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;
import org.exoplatform.ide.codeassistant.framework.server.extractors.TypeInfoExtractor;
import org.exoplatform.ide.codeassistant.framework.server.utils.DependentResources;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.groovy.JcrGroovyClassLoaderProvider;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;

/**
 * Extracting meta information from given Groovy classes (methods & constructors) to the 
 * bean object that can be transform to JSON
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyClassNamesExtractor Jan 18, 2011 9:30:38 AM evgen $
 *
 */
public class GroovyClassNamesExtractor
{
   private JcrGroovyClassLoaderProvider classLoaderProvider;

   private ExtendedGroovyClassLoader classLoader;

   private VirtualFileSystem vfs;

   public GroovyClassNamesExtractor(VirtualFileSystem vfs)
   {
      this.vfs = vfs;
      classLoaderProvider = new JcrGroovyClassLoaderProvider();

   }

   /**
    * Get list of Groovy class names in project classpath
    * @param prefix of class name
    * @param resources classpath
    * @return {@link List} of {@link ShortTypeInfo} 
    * @throws MalformedURLException
    * @throws URISyntaxException
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   public List<ShortTypeInfo> getClassNames(String prefix, DependentResources resources) throws MalformedURLException,
      URISyntaxException, VirtualFileSystemException
   {

      SourceFolder[] src = createSourceFolder(resources.getFolderSources());
      SourceFile[] srcFiles = createSourceFiles(resources.getFileSources());

      List<SourceFile> nodeFiles = new ArrayList<SourceFile>();
      for (SourceFolder f : src)
      {
         UnifiedNodeReference ref = new UnifiedNodeReference(f.getPath());

         String p = ref.getPath();
         String sql = "SELECT * FROM nt:file WHERE jcr:path LIKE '" + p + "%'";
         ItemList<Item> itemList = vfs.search(sql, -1, 0);

         for (Item nod : itemList.getItems())
         {
            if ((nod.getName().endsWith(".groovy") || nod.getName().endsWith(".grs") || nod.getName().endsWith(".cmtc"))
               && nod.getName().startsWith(prefix))
            {
               String path = "jcr://" + ref.getRepository() + "/" + ref.getWorkspace() + "#" + nod.getPath();
               URL url = new URL(null, path, UnifiedNodeReference.getURLStreamHandler());
               nodeFiles.add(new SourceFile(url));
            }
         }

      }
      if (srcFiles != null)
         nodeFiles.addAll(Arrays.asList(srcFiles));

      this.classLoader =
         src != null ? classLoaderProvider.getGroovyClassLoader(src) : classLoaderProvider.getGroovyClassLoader();
      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();

      classLoader.parseClasses(nodeFiles.toArray(new SourceFile[0]));
      Class<?>[] classes = classLoader.getLoadedClasses();
      for (Class<?> clazz : classes)
      {
         if(clazz.getName().contains("$"))
            continue;
         if (clazz.getSimpleName().startsWith(prefix))
            types.add(TypeInfoExtractor.extract(clazz));
      }

      return types;
   }

   /**
    * Convert {@link List} source folder paths to {@link SourceFolder} array
    * @param sources {@link List} of source folders paths
    * @return array of {@link SourceFolder}
    */
   private SourceFolder[] createSourceFolder(List<String> sources)
   {
      SourceFolder[] src = null;
      if (sources.size() > 0)
      {
         src = new SourceFolder[sources.size()];
         for (int i = 0; i < sources.size(); i++)
         {
            try
            {
               String str = sources.get(i);
               URL url = null;
               if (str.startsWith("jcr://"))
                  url = new URL(null, str, UnifiedNodeReference.getURLStreamHandler());
               else
                  url = new URL(str);
               src[i] = new SourceFolder(url);
            }
            catch (MalformedURLException e)
            {
               e.printStackTrace();
            }
         }
      }
      return src;
   }

   /**
    * Convert {@link List} to {@link SourceFile} array
    * @param fileSrc path for source file
    * @return array of {@link SourceFile}
    * @throws MalformedURLException
    */
   private SourceFile[] createSourceFiles(List<String> fileSrc) throws MalformedURLException
   {
      SourceFile[] srcFiles = null;
      if (!fileSrc.isEmpty())
      {
         srcFiles = new SourceFile[fileSrc.size()];
         for (int i = 0; i < fileSrc.size(); i++)
         {
            String str = fileSrc.get(i);
            URL url = null;
            if (str.startsWith("jcr://"))
               url = new URL(null, str, UnifiedNodeReference.getURLStreamHandler());
            else
               url = new URL(str);
            srcFiles[i] = new SourceFile(url);
         }
      }
      return srcFiles;
   }

   /**
    * Get type info for specific FQN
    * @param fqn of type
    * @param resources Classpath for current project
    * @return type info
    * @throws MalformedURLException
    * @throws URISyntaxException
    * @throws VirtualFileSystemException 
    */
   public TypeInfo getClassInfo(String fqn, DependentResources resources) throws MalformedURLException,
      URISyntaxException, VirtualFileSystemException
   {
      SourceFolder[] src = createSourceFolder(resources.getFolderSources());
      SourceFile[] srcFiles = createSourceFiles(resources.getFileSources());
      List<SourceFile> nodeFiles = new ArrayList<SourceFile>();

      for (SourceFolder f : src)
      {
         UnifiedNodeReference ref = new UnifiedNodeReference(f.getPath());

         String p = ref.getPath();
         String sql = "SELECT * FROM nt:file WHERE jcr:path LIKE '" + p + "%'";
         ItemList<Item> search = vfs.search(sql, -1, 0);
         for (Item nod : search.getItems())
         {
            if (nod.getName().endsWith(".groovy") || nod.getName().endsWith(".grs") || nod.getName().endsWith(".cmtc"))
            {
               String path = "jcr://" + ref.getRepository() + "/" + ref.getWorkspace() + "#" + nod.getPath();
               URL url = new URL(null, path, UnifiedNodeReference.getURLStreamHandler());
               nodeFiles.add(new SourceFile(url));
            }
         }

      }
      if (srcFiles != null)
         nodeFiles.addAll(Arrays.asList(srcFiles));

      this.classLoader =
         src != null ? classLoaderProvider.getGroovyClassLoader(src) : classLoaderProvider.getGroovyClassLoader();

      classLoader.parseClasses(nodeFiles.toArray(new SourceFile[0]));
      Class<?>[] classes = classLoader.getLoadedClasses();
      for (Class<?> clazz : classes)
      {
         if (clazz.getName().equals(fqn))
            return TypeInfoExtractor.extract(clazz);
      }

      return null;
   }

}

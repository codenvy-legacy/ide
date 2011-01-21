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
package org.exoplatform.ide.groovy.codeassistant.extractors;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.exoplatform.ide.groovy.codeassistant.bean.ShortTypeInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;
import org.exoplatform.ide.groovy.util.DependentResources;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.exoplatform.services.jcr.ext.script.groovy.JcrGroovyClassLoaderProvider;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.rest.ext.groovy.ExtendedGroovyClassLoader;
import org.exoplatform.services.rest.ext.groovy.GroovyClassLoaderProvider;
import org.exoplatform.services.rest.ext.groovy.SourceFile;
import org.exoplatform.services.rest.ext.groovy.SourceFolder;

/**
 * Extracting meta information from given Groovy classes (methods & constructors) to the 
 * bean object that can be transform to JSON
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyClassNamesExtractor Jan 18, 2011 9:30:38 AM evgen $
 *
 */
public class GroovyClassNamesExtractor
{

   private final RepositoryService repositoryService;

   private final ThreadLocalSessionProviderService sessionProviderService;

   private GroovyClassLoaderProvider classLoaderProvider;

   private ExtendedGroovyClassLoader classLoader;


   public GroovyClassNamesExtractor(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
      this.classLoaderProvider = new JcrGroovyClassLoaderProvider();

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
      URISyntaxException, RepositoryException, RepositoryConfigurationException
   {

      SourceFolder[] src = createSourceFolder(resources.getFolderSources());
      SourceFile[] srcFiles = createSourceFiles(resources.getFileSources());

      List<SourceFile> nodeFiles = new ArrayList<SourceFile>();
         for (SourceFolder f : src)
         {
            UnifiedNodeReference ref = new UnifiedNodeReference(f.getPath());
            Session session =
               getSession(repositoryService, sessionProviderService, ref.getRepository(), ref.getWorkspace());
            String p = ref.getPath();
            String sql = "SELECT * FROM nt:file WHERE jcr:path LIKE '" + p + "%'";
            Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
            QueryResult result = q.execute();

            NodeIterator iterator = result.getNodes();
            while (iterator.hasNext())
            {
               Node nod = (NodeImpl)iterator.nextNode();
               if ((nod.getName().endsWith(".groovy") || nod.getName().endsWith(".grs"))
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
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   public TypeInfo getClassInfo(String fqn, DependentResources resources) throws MalformedURLException,
      URISyntaxException, RepositoryException, RepositoryConfigurationException
   {
      SourceFolder[] src = createSourceFolder(resources.getFolderSources());
      SourceFile[] srcFiles = createSourceFiles(resources.getFileSources());
      List<SourceFile> nodeFiles = new ArrayList<SourceFile>();

      for (SourceFolder f : src)
      {
         UnifiedNodeReference ref = new UnifiedNodeReference(f.getPath());
         Session session =
            getSession(repositoryService, sessionProviderService, ref.getRepository(), ref.getWorkspace());
         String p = ref.getPath();
         String sql = "SELECT * FROM nt:file WHERE jcr:path LIKE '" + p + "%'";
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();

         NodeIterator iterator = result.getNodes();
         while (iterator.hasNext())
         {
            Node nod = (NodeImpl)iterator.nextNode();
            if (nod.getName().endsWith(".groovy") || nod.getName().endsWith(".grs"))
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

   /**
    * Get Session for specific repository and workspace
    * 
    * @param repositoryService repository service
    * @param sessionProviderService session provider service
    * @param repoName repository's name
    * @param repoPath path to file in repository
    * @return {@link Session} created JCR session
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   protected static Session getSession(RepositoryService repositoryService,
      SessionProviderService sessionProviderService, String repoName, String workspace) throws RepositoryException,
      RepositoryConfigurationException
   {
      ManageableRepository repo = repositoryService.getRepository(repoName);
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      if (sp == null)
         throw new RepositoryException("SessionProvider is not properly set. Make the application calls"
            + "SessionProviderService.setSessionProvider(..) somewhere before ("
            + "for instance in Servlet Filter for WEB application)");
      return sp.getSession(workspace, repo);
   }

}

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
package org.exoplatform.ide.extension.chromattic.server;

import org.chromattic.dataobject.CompilationSource;
import org.chromattic.dataobject.DataObjectService;
import org.chromattic.dataobject.NodeTypeFormat;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;
import org.exoplatform.ide.codeassistant.framework.server.utils.DependentResources;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.groovy.JcrGroovyCompiler;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.ext.resource.NodeRepresentationService;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.exoplatform.services.jcr.impl.core.nodetype.NodeTypeManagerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/ide/chromattic/")
public class RestDataObjectService
{

   /**
    * WebDav context uses for selecting jcr path of resource which is adressed
    * via WebDav or IDE Virtual File System service (server)
    */
   public static final String WEBDAV_CONTEXT = "/jcr/";

   private RepositoryService repositoryService;

   public static final String GROOVY_CLASSPATH = ".groovyclasspath";

   private JcrGroovyCompiler compiler;

   private VirtualFileSystemRegistry vfsRegistry;

   public RestDataObjectService(DataObjectService dataObjectService, RepositoryService repositoryService,
      NodeRepresentationService nodeRepresentationService, JcrGroovyCompiler compiler,
      VirtualFileSystemRegistry vfsRegistry)
   {
      this.repositoryService = repositoryService;
      this.compiler = compiler;
      this.vfsRegistry = vfsRegistry;
   }

   /**
    * Compile Data Object Service and return Node Type definition.
    * 
    * @param uriInfo - UriInfo
    * @param location - Resource URL
    * @return
    * @throws IOException
    * @throws URISyntaxException
    * @throws RepositoryException 
    * @throws VirtualFileSystemException 
    * @throws JsonException 
    * @throws RepositoryConfigurationException 
    * @throws PermissionDeniedException 
    * @throws ItemNotFoundException 
    */
   @POST
   @Path("/generate-nodetype-definition")
   public String getNodeTypeDefinition(@QueryParam("id") String id, @QueryParam("vfsid") String vfsid,
      @QueryParam("projectid") String projectid, @QueryParam("nodeTypeFormat") NodeTypeFormat format)
      throws IOException, URISyntaxException, RepositoryException, VirtualFileSystemException, JsonException,
      RepositoryConfigurationException
   {

      if (id == null)
         throw new IllegalArgumentException("You must specify item ID.");
      //TODO
      String repository = getCurrentRepository();
      CompilationSource compilationSource = null;
      SourceFolder[] sources = null;
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      String path = vfs.getItem(id, PropertyFilter.NONE_FILTER).getPath();

      if (projectid != null)
      {
         DependentResources dependentResources = getDependentResources(projectid, vfs);

         if (dependentResources != null && dependentResources.getFolderSources().size() > 0)
         {
            //TODO only first one dir is taken at the moment
            String dependencySource = dependentResources.getFolderSources().get(0);
            UnifiedNodeReference sourceReference = new UnifiedNodeReference(dependencySource);
            compilationSource =
               new CompilationSource(sourceReference.getRepository(), sourceReference.getWorkspace(),
                  sourceReference.getPath());
            String dep = dependentResources.getFolderSources().get(0);
            sources = new SourceFolder[]{new SourceFolder((new UnifiedNodeReference(dep)).getURL())};
         }
         else
         {
            compilationSource = new CompilationSource(repository, vfsid, path);
         }
      }
      else
      {
         compilationSource = new CompilationSource(repository, vfsid, path);
      }

      SourceFile[] sourceFile =
         new SourceFile[]{new SourceFile((new UnifiedNodeReference(repository, vfsid, path)).getURL())};

      List<String> nodeReferences = new ArrayList<String>();
      URL[] urls = compiler.getDependencies(sources, sourceFile);
      for (int i = 0; i < urls.length; i++)
      {
         if (MimeTypeResolver.resolve(new UnifiedNodeReference(urls[i]), "application/x-chromattic+groovy"))
            nodeReferences.add(new UnifiedNodeReference(urls[i]).getPath());
      }

      return new DataObjectCompiler(compiler, compilationSource, nodeReferences.toArray(new String[nodeReferences
         .size()])).generateSchema(format);
   }

   @POST
   @Path("/register-nodetype/{format}/{alreadyExistsBehaviour}")
   public void registerNodeType(@PathParam("format") NodeTypeFormat format,
      @PathParam("alreadyExistsBehaviour") Integer alreadyExistsBehaviour, InputStream nodeTypeDefinition)
      throws RepositoryException, RepositoryConfigurationException

   {
      NodeTypeManagerImpl nodeTypeManager = (NodeTypeManagerImpl)getRepository().getNodeTypeManager();
      switch (format)
      {
         case EXO :
            nodeTypeManager.registerNodeTypes(nodeTypeDefinition, alreadyExistsBehaviour, NodeTypeDataManager.TEXT_XML);
            break;
         case CND :
            nodeTypeManager.registerNodeTypes(nodeTypeDefinition, alreadyExistsBehaviour,
               NodeTypeDataManager.TEXT_X_JCR_CND);
            break;
         default :
            throw new RepositoryException("Unsupported content type:" + format.name());
      }

   }

   private DependentResources getDependentResources(String projectid, VirtualFileSystem vfs)
      throws VirtualFileSystemException, JsonException, RepositoryException, RepositoryConfigurationException
   {
      Folder project = (Folder)vfs.getItem(projectid, PropertyFilter.NONE_FILTER);
      try
      {
         JsonParser jsonParser = new JsonParser();
         jsonParser.parse(vfs.getContent(project.createPath(GROOVY_CLASSPATH), null).getStream());
         JsonValue jsonValue = jsonParser.getJsonObject();
         GroovyClassPath classPath = ObjectBuilder.createObject(GroovyClassPath.class, jsonValue);
         return new DependentResources(getCurrentRepository(), classPath);
      }
      catch (ItemNotFoundException e)
      {
         return null;
      }
   }

   // Temporary method. Remove after refactoring of GroovyScript2RestLoader.
   private String getCurrentRepository() throws RepositoryException, RepositoryConfigurationException
   {
      return getRepository().getConfiguration().getName();
   }

   private ManageableRepository getRepository() throws RepositoryException, RepositoryConfigurationException
   {
      return repositoryService.getCurrentRepository() == null ? repositoryService.getDefaultRepository()
         : repositoryService.getCurrentRepository();
   }

}

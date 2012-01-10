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

import org.chromattic.dataobject.NodeTypeFormat;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.extension.groovy.server.GroovyClassPathHelper;
import org.exoplatform.ide.extension.groovy.server.IDEGroovyCompiler;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
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
   private RepositoryService repositoryService;
   private VirtualFileSystemRegistry vfsRegistry;
   private IDEGroovyCompiler compiler;

   public RestDataObjectService(RepositoryService repositoryService,
                                IDEGroovyCompiler compiler,
                                VirtualFileSystemRegistry vfsRegistry)
   {
      this.repositoryService = repositoryService;
      this.compiler = compiler;
      this.vfsRegistry = vfsRegistry;
   }

   /**
    * Compile Data Object Service and return Node Type definition.
    *
    * @param id ID of groovy source that contains node type description
    * @param vfsid ID of virtual file system
    * @param projectId ID of IDE project
    * @param format format of node type
    * @return node type definition as string. Format or representation depends on <code>format</code> parameter.
    * @throws VirtualFileSystemException
    * @throws JsonException
    * @throws IOException
    */
   @POST
   @Path("/generate-nodetype-definition")
   public String getNodeTypeDefinition(@QueryParam("id") String id,
                                       @QueryParam("vfsid") String vfsid,
                                       @QueryParam("projectid") String projectId,
                                       @QueryParam("nodeTypeFormat") NodeTypeFormat format)
      throws VirtualFileSystemException, JsonException, IOException
   {
      if (null == id)
      {
         throw new IllegalArgumentException("Id of groovy source item may not be null.  ");
      }

      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      GroovyClassPath classPath = null;
      if (null != projectId)
      {
         classPath = GroovyClassPathHelper.getGroovyClassPath(projectId, vfs);
      }

      SourceFile[] files;
      if (null != classPath)
      {
         // Merge all source files available in class path.
         SourceFile[] fromClassPath = GroovyClassPathHelper.getSourceFiles(classPath);
         files = new SourceFile[fromClassPath.length + 1];
         files[0] = new SourceFile(URI.create("ide+vfs:/" + vfsid + "#" + id).toURL());
         System.arraycopy(fromClassPath, 0, files, 1, fromClassPath.length);
      }
      else
      {
         files = new SourceFile[]{new SourceFile(URI.create("ide+vfs:/" + vfsid + "#" + id).toURL())};
      }

      SourceFolder[] src = null;
      if (null != classPath)
      {
         src = GroovyClassPathHelper.getSourceFolders(classPath);
      }
      // Get all required compilation dependencies.
      // Compiler try to load all dependencies from specified source folder
      // or(and) from pre-configured source libraries if any.
      URL[] all = compiler.getDependencies(src, files);

      // Now need to filter 'chromattic' object model files.
      List<SourceFile> model = new ArrayList<SourceFile>();
      for (int i = 0; i < all.length; i++)
      {
         URLConnection connection = all[i].openConnection();
         if ("application/x-chromattic+groovy".equals(connection.getContentType()))
         {
            model.add(new SourceFile(all[i]));
         }
      }
      return new DataObjectCompiler(compiler, src, model.toArray(new SourceFile[model.size()])).generateSchema(format);
   }

   @POST
   @Path("/register-nodetype/{format}/{alreadyExistsBehaviour}")
   public void registerNodeType(@PathParam("format") NodeTypeFormat format,
                                @PathParam("alreadyExistsBehaviour") Integer alreadyExistsBehaviour,
                                InputStream nodeTypeDefinition)
      throws RepositoryException
   {
      switch (format)
      {
         case EXO:
            getRepository().getNodeTypeManager()
               .registerNodeTypes(nodeTypeDefinition, alreadyExistsBehaviour, NodeTypeDataManager.TEXT_XML);
            break;
         case CND:
            getRepository().getNodeTypeManager()
               .registerNodeTypes(nodeTypeDefinition, alreadyExistsBehaviour, NodeTypeDataManager.TEXT_X_JCR_CND);
            break;
         default:
            throw new RepositoryException("Unsupported content type:" + format.name());
      }
   }

   private ManageableRepository getRepository() throws RepositoryException
   {
      return repositoryService.getCurrentRepository();
   }
}

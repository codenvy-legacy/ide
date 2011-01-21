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
package org.exoplatform.ide.chromattic;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.chromattic.dataobject.CompilationSource;
import org.chromattic.dataobject.DataObjectService;
import org.chromattic.dataobject.NodeTypeFormat;
import org.exoplatform.ide.groovy.util.DependentResources;
import org.exoplatform.ide.groovy.util.GroovyScriptServiceUtil;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.resource.JcrURLConnection;
import org.exoplatform.services.jcr.ext.resource.NodeRepresentationService;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.exoplatform.services.jcr.ext.script.groovy.JcrGroovyCompiler;
import org.exoplatform.services.jcr.impl.core.nodetype.NodeTypeManagerImpl;
import org.exoplatform.services.rest.ext.filter.UriNormalizationFilter;
import org.exoplatform.services.rest.ext.groovy.SourceFile;
import org.exoplatform.services.rest.ext.groovy.SourceFolder;

@Path("/ide/chromattic/")
public class RestDataObjectService
{

   /**
    * WebDav context uses for selecting jcr path of resource which is 
    * adressed via WebDav or IDE Virtual File System service (server)
    */
   public static final String WEBDAV_CONTEXT = "/jcr/";

   private DataObjectService dataObjectService;

   private RepositoryService repositoryService;

   private ThreadLocalSessionProviderService sessionProviderService;

   private JcrGroovyCompiler compiler;

   public RestDataObjectService(DataObjectService dataObjectService, RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService, NodeRepresentationService nodeRepresentationService)
   {
      this.dataObjectService = dataObjectService;
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
      this.compiler = new JcrGroovyCompiler();
   }

   /**
    * Compile Data Object Service and return Node Type definition.
    * 
    * @param uriInfo - UriInfo
    * @param location - Resource URL 
    * @return
    * @throws IOException 
    * @throws URISyntaxException 
    */
   @POST
   @Path("/generate-nodetype-definition")
   public String getNodeTypeDefinition(@Context UriInfo uriInfo, @QueryParam("do-location") String location,
      @QueryParam("nodeTypeFormat") NodeTypeFormat format) throws PathNotFoundException, IOException,
      URISyntaxException
   {
      String[] jcrLocation = parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);
      if (location == null)
      {
         throw new IllegalArgumentException("You must specify location of the source script.");
      }
      if (jcrLocation == null)
      {
         throw new PathNotFoundException("Location of script " + location + " not found. ");
      }
      String repository = jcrLocation[0];
      String workspace = jcrLocation[1];

      String pp = jcrLocation[2];
      if (pp.startsWith("/"))
      {
         pp = pp.substring(1);
      }
      String path = "/" + jcrLocation[2];

      //TODO: 
      CompilationSource compilationSource;
      DependentResources dependentResources =
         GroovyScriptServiceUtil.getDependentResource(location, uriInfo.getBaseUri().toASCIIString(),
            repositoryService, sessionProviderService);
      if (dependentResources != null && dependentResources.getFolderSources().size() > 0)
      {
         //TODO only first one dir is taken at the moment
         String dependentSource = dependentResources.getFolderSources().get(0);
         dependentSource = dependentSource.replace("jcr://", "");
         String[] pathParts = dependentSource.split("/");
         if (pathParts == null || pathParts.length < 3)
         {
            throw new PathNotFoundException("Location of dependency  " + dependentSource + " not found. ");
         }
         String dependenceWorkspace =
            (pathParts[1].endsWith("#") ? pathParts[1].substring(0, pathParts[1].length() - 1) : pathParts[1]);
         String repwork = pathParts[0] + "/" + pathParts[1];
         compilationSource =
            new CompilationSource(pathParts[0], dependenceWorkspace, dependentSource.replace(repwork, ""));
      }
      else
      {
         compilationSource = new CompilationSource(repository, workspace, path);
      }

      String dep = dependentResources.getFolderSources().get(0);
      SourceFolder[] sources = {new SourceFolder((new UnifiedNodeReference(dep)).getURL())};

      SourceFile[] sourceFile = {new SourceFile((new UnifiedNodeReference(repository, workspace, path)).getURL())};

      List<String> nodeReferences = new ArrayList<String>();

      URL[] urls = compiler.getDependencies(sources, sourceFile);

      for (int i = 0; i < urls.length; i++)
      {
         if (MimeTypeResolver.resolve(new UnifiedNodeReference(urls[i]), "application/x-chromattic+groovy"))
         {
            nodeReferences.add(new UnifiedNodeReference(urls[i]).getPath());
         }
      }

      return dataObjectService.generateSchema(format, compilationSource,
         nodeReferences.toArray(new String[nodeReferences.size()]));
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

   /**
    * @param baseUri base URI
    * @param location location of groovy script
    * @return array of {@link String}, which elements contain repository name, workspace name and 
    * path the path to JCR node that contains groovy script to be deployed
    */
   private String[] parseJcrLocation(String baseUri, String location)
   {
      baseUri += WEBDAV_CONTEXT;
      if (!location.startsWith(baseUri))
      {
         return null;
      }

      String[] elements = new String[3];
      location = location.substring(baseUri.length());
      elements[0] = location.substring(0, location.indexOf('/'));
      location = location.substring(location.indexOf('/') + 1);
      elements[1] = location.substring(0, location.indexOf('/'));
      elements[2] = location.substring(location.indexOf('/') + 1);
      return elements;
   }

   private ManageableRepository getRepository() throws RepositoryException, RepositoryConfigurationException
   {
      return repositoryService.getCurrentRepository() == null ? repositoryService.getDefaultRepository()
         : repositoryService.getCurrentRepository();
   }

}

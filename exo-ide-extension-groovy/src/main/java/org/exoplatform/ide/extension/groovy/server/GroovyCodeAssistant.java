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
import org.exoplatform.ide.codeassistant.framework.server.utils.DependentResources;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistant;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 23, 2011 10:32:31 AM evgen $
 *
 */
public class GroovyCodeAssistant extends CodeAssistant
{

   private VirtualFileSystemRegistry vfsRegistry;

   /** See {@link RepositoryService}. */
   private RepositoryService repositoryService;

   /** Logger. */
   static final Log LOG = ExoLogger.getLogger(GroovyCodeAssistant.class);

   /**
    * @param storage
    */
   public GroovyCodeAssistant(CodeAssistantStorage storage, VirtualFileSystemRegistry vfsRegistry,
      RepositoryService repositoryService)
   {
      super(storage);
      this.vfsRegistry = vfsRegistry;
      this.repositoryService = repositoryService;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#getJavaDocFromProject(java.lang.String)
    */
   @Override
   protected String getClassJavaDocFromProject(String fqn, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      //we don't support this feature now
      throw new CodeAssistantException(404, "Not found");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findClassesInPackage(java.lang.String, java.lang.String, java.lang.String)
    */

   @Override
   protected TypeInfo getClassByFqnFromProject(String fqn, String projectId, String vfsId)
      throws VirtualFileSystemException, CodeAssistantException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      DependentResources dependentResources;
      try
      {
         dependentResources = getDependentResources(projectId, vfs);
         if (dependentResources != null)
         {
            TypeInfo classInfo = new GroovyClassNamesExtractor(vfs).getClassInfo(fqn, dependentResources);
            return classInfo;
         }
      }
      catch (JsonException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }

      return null;
   }

   @Override
   protected List<ShortTypeInfo> getTypesByNamePrefixFromProject(String className, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);

      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      try
      {
         DependentResources dependentResources = getDependentResources(projectId, vfs);

         if (dependentResources != null)
         {
            types = new GroovyClassNamesExtractor(vfs).getClassNames(className, dependentResources);
         }
      }
      catch (JsonException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      return types;
   }

   @Override
   protected List<ShortTypeInfo> getTypesByFqnPrefixInProject(String prefix, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   protected List<ShortTypeInfo> getByTypeFromProject(JavaType type, String prefix, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public List<ShortTypeInfo> getClassesFromProject(String fileId, String projectId, String vfsId)
      throws VirtualFileSystemException, CodeAssistantException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistant#getMemberJavaDocFromProject(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   protected String getMemberJavaDocFromProject(String fqn, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      //we don't support this feature now
      throw new CodeAssistantException(404, "Not found");
   }

   private DependentResources getDependentResources(String projectid, VirtualFileSystem vfs)
      throws VirtualFileSystemException, JsonException
   {
      Folder project = (Folder)vfs.getItem(projectid, PropertyFilter.NONE_FILTER);
      try
      {
         JsonParser jsonParser = new JsonParser();
         jsonParser.parse(vfs.getContent(project.createPath(GroovyScriptService.GROOVY_CLASSPATH), null).getStream());
         JsonValue jsonValue = jsonParser.getJsonObject();
         GroovyClassPath classPath = ObjectBuilder.createObject(GroovyClassPath.class, jsonValue);
         return new DependentResources(getCurrentRepository(), classPath);
      }
      catch (ItemNotFoundException e)
      {
         return null;
      }
   }

   private String getCurrentRepository()
   {
      try
      {
         return repositoryService.getCurrentRepository().getConfiguration().getName();
      }
      catch (RepositoryException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

}

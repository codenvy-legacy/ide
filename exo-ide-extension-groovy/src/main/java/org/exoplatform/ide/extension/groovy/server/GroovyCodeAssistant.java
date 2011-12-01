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

import org.exoplatform.ide.codeassistant.jvm.CodeAssistant;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 23, 2011 10:32:31 AM evgen $
 *
 */
public class GroovyCodeAssistant extends CodeAssistant
{

   /**
    * @param storage
    */
   public GroovyCodeAssistant(CodeAssistantStorage storage)
   {
      super(storage);
   }



   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#getJavaDocFromProject(java.lang.String)
    */
   @Override
   protected String getClassJavaDocFromProject(String fqn, String projectId, String vfsId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      //TODO
      throw new CodeAssistantException(404, "Not found");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findClassesInPackage(java.lang.String, java.lang.String, java.lang.String)
    */

   @Override
   protected TypeInfo getClassByFqnFromProject(String fqn, String projectId, String vfsId)
      throws VirtualFileSystemException, CodeAssistantException
   {
      return null;
   }

   @Override
   protected List<ShortTypeInfo> getTypesByNamePrefixFromProject(String className, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      return null;
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
      //TODO
      throw new CodeAssistantException(404, "Not found");
   }

   //   /**
   //    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#getClassByFQNFromProject(java.lang.String, java.lang.String, java.lang.String)
   //    */
   //   @Override
   //   public TypeInfo getClassByFQNFromProject(String fqn, String location) throws CodeAssistantException
   //   {
   //      try
   //      {
   //         if (location != null)
   //         {
   //            DependentResources dependentResources =
   //               GroovyScriptServiceUtil.getDependentResource(location, repositoryService);
   //            if (dependentResources != null)
   //            {
   //               TypeInfo classInfo =
   //                  new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassInfo(fqn,
   //                     dependentResources);
   //               if (classInfo == null)
   //                  throw new CodeAssistantException(404, "Class info for " + fqn + " not found");
   //               return classInfo;
   //            }
   //
   //         }
   //      }
   //      catch (MalformedURLException e)
   //      {
   //         if (LOG.isDebugEnabled())
   //            e.printStackTrace();
   //         throw new CodeAssistantException(500, e.getMessage());
   //      }
   //      catch (URISyntaxException e)
   //      {
   //         if (LOG.isDebugEnabled())
   //            e.printStackTrace();
   //         throw new CodeAssistantException(500, e.getMessage());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         if (LOG.isDebugEnabled())
   //            e.printStackTrace();
   //         throw new CodeAssistantException(404, e.getMessage());
   //      }
   //      catch (RepositoryConfigurationException e)
   //      {
   //         if (LOG.isDebugEnabled())
   //            e.printStackTrace();
   //         throw new CodeAssistantException(404, e.getMessage());
   //      }
   //      return null;
   //   }

   //   /**
   //    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findFQNsByClassNameInProject(java.lang.String, java.lang.String, java.lang.String)
   //    */
   //   @Override
   //   public List<ShortTypeInfo> findFQNsByClassNameInProject(String className, String location)
   //      throws CodeAssistantException
   //   {
   //      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
   //      try
   //      {
   //         if (location != null && !location.isEmpty())
   //         {
   //            DependentResources dependentResources =
   //               GroovyScriptServiceUtil.getDependentResource(location, repositoryService);
   //            if (dependentResources != null)
   //            {
   //               types =
   //                  new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassNames(className,
   //                     dependentResources);
   //            }
   //         }
   //         return types;
   //      }
   //      catch (MalformedURLException e)
   //      {
   //         if (LOG.isDebugEnabled())
   //            e.printStackTrace();
   //         throw new CodeAssistantException(400, e.getMessage());
   //      }
   //      catch (URISyntaxException e)
   //      {
   //         if (LOG.isDebugEnabled())
   //            e.printStackTrace();
   //         throw new CodeAssistantException(400, e.getMessage());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         if (LOG.isDebugEnabled())
   //            e.printStackTrace();
   //         throw new CodeAssistantException(404, e.getMessage());
   //      }
   //      catch (RepositoryConfigurationException e)
   //      {
   //         if (LOG.isDebugEnabled())
   //            e.printStackTrace();
   //         throw new CodeAssistantException(404, e.getMessage());
   //      }
   //   }

   // /**
   // * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findFQNsByPrefix(java.lang.String, java.lang.String, java.lang.String)
   // */
   //@Override
   //public List<ShortTypeInfo> findFQNsByPrefixInProject(String prefix, String location) throws CodeAssistantException
   //{
   //   List<ShortTypeInfo> groovyClass = null;
   //   try
   //   {
   //      if (location != null && !location.isEmpty())
   //      {
   //
   //         DependentResources dependentResources =
   //            GroovyScriptServiceUtil.getDependentResource(location, repositoryService);
   //         if (dependentResources != null)
   //         {
   //            groovyClass =
   //               new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassNames(prefix,
   //                  dependentResources);
   //
   //         }
   //      }
   //   }
   //   catch (RepositoryException e)
   //   {
   //      if (LOG.isDebugEnabled())
   //         e.printStackTrace();
   //      //TODO:need fix status code
   //      throw new CodeAssistantException(404, e.getMessage());
   //   }
   //   catch (RepositoryConfigurationException e)
   //   {
   //      if (LOG.isDebugEnabled())
   //         e.printStackTrace();
   //      //TODO:need fix status code
   //      throw new CodeAssistantException(404, e.getMessage());
   //   }
   //   catch (MalformedURLException e)
   //   {
   //      if (LOG.isDebugEnabled())
   //         e.printStackTrace();
   //      throw new CodeAssistantException(400, e.getMessage());
   //   }
   //   catch (URISyntaxException e)
   //   {
   //      if (LOG.isDebugEnabled())
   //         e.printStackTrace();
   //      throw new CodeAssistantException(400, e.getMessage());
   //   }
   //
   //   return groovyClass;
   //}

}

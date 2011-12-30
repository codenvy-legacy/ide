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
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistant;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 23, 2011 10:32:31 AM evgen $
 *
 */
public class GroovyCodeAssistant extends CodeAssistant
{
   private VirtualFileSystemRegistry vfsRegistry;

   private IDEGroovyClassLoaderProvider groovyClassLoaderProvider;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(GroovyCodeAssistant.class);

   /**
    * @param storage
    */
   public GroovyCodeAssistant(CodeAssistantStorage storage,
                              VirtualFileSystemRegistry vfsRegistry,
                              IDEGroovyClassLoaderProvider groovyClassLoaderProvider)
   {
      super(storage);
      this.vfsRegistry = vfsRegistry;
      this.groovyClassLoaderProvider = groovyClassLoaderProvider;
   }

   @Override
   protected String getClassJavaDocFromProject(String fqn, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      //we don't support this feature now
      throw new CodeAssistantException(404, "Not found");
   }

   @Override
   protected TypeInfo getClassByFqnFromProject(String fqn, String projectId, String vfsId)
      throws VirtualFileSystemException, CodeAssistantException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      try
      {
         GroovyClassPath groovyClassPath = GroovyClassPathHelper.getGroovyClassPath(projectId, vfs);
         if (null != groovyClassPath)
         {
            TypeInfo classInfo = new GroovyClassNamesExtractor(vfs, groovyClassLoaderProvider).getClassInfo(fqn, groovyClassPath);
            return classInfo;
         }
      }
      catch (JsonException e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e.getMessage(), e);
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e.getMessage(), e);
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e.getMessage(), e);
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
         GroovyClassPath groovyClassPath = GroovyClassPathHelper.getGroovyClassPath(projectId, vfs);
         if (null != groovyClassPath)
         {
            types = new GroovyClassNamesExtractor(vfs, groovyClassLoaderProvider)
               .getClassNames(className, GroovyClassPathHelper.getGroovyClassPath(projectId, vfs));
         }
      }
      catch (JsonException e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e.getMessage(), e);
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e.getMessage(), e);
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e.getMessage(), e);
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
}

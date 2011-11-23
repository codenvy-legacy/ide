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

import org.exoplatform.ide.codeassistant.api.CodeAssistant;
import org.exoplatform.ide.codeassistant.api.CodeAssistantException;
import org.exoplatform.ide.codeassistant.api.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.JavaType;
import org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.Where;
import org.exoplatform.ide.codeassistant.api.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.api.TypeInfo;
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
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findClassByFQN(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   protected TypeInfo findClassByFQN(String fqn, String projectId, String vfsId) throws VirtualFileSystemException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findFQNsByClassNameInProject(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   protected List<ShortTypeInfo> findFQNsByClassNameInProject(String className, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findFQNsByPrefixInProject(java.lang.String, org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.Where, java.lang.String, java.lang.String)
    */
   @Override
   protected List<ShortTypeInfo> findFQNsByPrefixInProject(String prefix, Where where, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findByTypeInProject(org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.JavaType, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   protected List<ShortTypeInfo> findByTypeInProject(JavaType type, String prefix, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#getJavaDocFromProject(java.lang.String)
    */
   @Override
   protected String getJavaDocFromProject(String fqn, String projectId, String vfsId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      //TODO
      throw new CodeAssistantException(404, "Not found");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findClassesInPackage(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> findClassesInPackage(String fileId, String projectId, String vfsId)
      throws VirtualFileSystemException, CodeAssistantException
   {
      // TODO Auto-generated method stub
      return null;
   }

}

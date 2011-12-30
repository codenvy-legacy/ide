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
package org.exoplatform.ide.extension.java.server;


import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 28, 2011 4:34:52 PM evgen $
 *
 */
public class JavaDocBuilderVfsTest extends JavaDocBase
{

   @Test
   public void projectParserConstruntorTest() throws CodeAssistantException, VirtualFileSystemException
   {
      TypeInfo clazz =
         javaCa.getClassByFqnFromProject("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", project.getId(),
            VFS_ID);
      Assert.assertEquals("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", clazz.getName());
      Assert.assertEquals(1, clazz.getModifiers());
      List<MethodInfo> methods = clazz.getMethods();
      List<MethodInfo> constructors = new ArrayList<MethodInfo>();
      for (MethodInfo methodInfo : methods)
      {
         if (methodInfo.isConstructor())
            constructors.add(methodInfo);
      }
      Assert.assertEquals(2, constructors.size());
      MethodInfo info = constructors.get(0);
      Assert.assertEquals("AutoCompletionManager", info.getName());
      Assert.assertEquals("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", info.getDeclaringClass());
      Assert.assertEquals("com.google.gwt.event.shared.HandlerManager", info.getParameterTypes().get(0));
   }

   @Test
   public void projectParserFieldsTest() throws CodeAssistantException, VirtualFileSystemException
   {
      TypeInfo clazz =
         javaCa.getClassByFqnFromProject("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", project.getId(),
            VFS_ID);
      Assert.assertEquals("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", clazz.getName());
      List<FieldInfo> fields = clazz.getFields();
      Assert.assertEquals(6, fields.size());
      FieldInfo info = fields.get(0);
      Assert.assertNotNull(info.getName());
      Assert.assertNotNull(info.getType());
      Assert.assertNotNull(info.getDeclaringClass());
      Assert.assertNotNull(info.getModifiers());
   }

   @Test
   public void projectParserMethodsTest() throws CodeAssistantException, VirtualFileSystemException
   {
      TypeInfo clazz =
         javaCa.getClassByFqnFromProject("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", project.getId(),
            VFS_ID);

      List<MethodInfo> methods = clazz.getMethods();
      Assert.assertNotNull(methods);
      Assert.assertEquals(18, methods.size());
      MethodInfo methodInfo = methods.get(0);
      Assert.assertNotNull(methodInfo.getName());
      Assert.assertNotNull(methodInfo.getModifiers());
      Assert.assertNotNull(methodInfo.getParameterTypes());
      Assert.assertNotNull(methodInfo.getDeclaringClass());
   }
}

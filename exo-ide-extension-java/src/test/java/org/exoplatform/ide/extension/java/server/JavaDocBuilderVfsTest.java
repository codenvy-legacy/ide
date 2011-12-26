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
import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.junit.Assert;
import org.junit.Test;

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
      Assert.assertEquals("AutoCompletionManager", clazz.getName());
      Assert.assertEquals(1, clazz.getModifiers());
      Assert.assertEquals(2, clazz.getConstructors().length);
      RoutineInfo info = clazz.getConstructors()[0];
      Assert.assertEquals("AutoCompletionManager", info.getName());
      Assert.assertEquals("(HandlerManager)", info.getParameterTypes());
      Assert.assertEquals("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", info.getDeclaringClass());
      Assert.assertEquals("(com.google.gwt.event.shared.HandlerManager)", info.getGenericParameterTypes());
   }

   @Test
   public void projectParserFieldsTest() throws CodeAssistantException, VirtualFileSystemException
   {
      TypeInfo clazz =
         javaCa.getClassByFqnFromProject("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", project.getId(),
            VFS_ID);
      Assert.assertEquals("AutoCompletionManager", clazz.getName());
      FieldInfo[] fields = clazz.getFields();
      Assert.assertEquals(6, fields.length);
      FieldInfo info = fields[0];
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

      MethodInfo[] methods = clazz.getMethods();
      Assert.assertNotNull(methods);
      Assert.assertEquals(16, methods.length);
      MethodInfo methodInfo = methods[0];
      Assert.assertNotNull(methodInfo.getName());
      Assert.assertNotNull(methodInfo.getModifiers());
      Assert.assertNotNull(methodInfo.getParameterTypes());
      Assert.assertNotNull(methodInfo.getReturnType());
      Assert.assertNotNull(methodInfo.getDeclaringClass());
      Assert.assertNotNull(methodInfo.getGenericParameterTypes());
   }
}

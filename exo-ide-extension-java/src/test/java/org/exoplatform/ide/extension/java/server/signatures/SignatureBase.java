/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server.signatures;

import com.thoughtworks.qdox.model.JavaClass;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.extension.java.server.parser.JavaDocBuilderVfs;
import org.exoplatform.ide.extension.java.server.parser.JavaTypeToTypeInfoConverter;
import org.exoplatform.ide.extension.java.server.parser.VfsClassLibrary;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.junit.Before;
import org.mockito.Mock;

import java.io.StringReader;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:34:24 PM Mar 13, 2012 evgen $
 * 
 */
public class SignatureBase
{

   protected JavaDocBuilderVfs javaDocBuilderVfs;

   @Mock
   private VirtualFileSystem vfs;
   
   @Mock
   protected CodeAssistantStorage storage;

   @Before
   public void createParser()
   {
      VfsClassLibrary vfsClassLibrary = new VfsClassLibrary(vfs);
      vfsClassLibrary.addClassLoader(ClassLoader.getSystemClassLoader());
      javaDocBuilderVfs = new JavaDocBuilderVfs(vfs, vfsClassLibrary);

   }

   /**
    * @param b
    * @return
    */
   protected TypeInfo getTypeInfo(StringBuilder b, String classFqn)
   {
      StringReader reader = new StringReader(b.toString());
      javaDocBuilderVfs.addSource(reader);
      JavaClass clazz = javaDocBuilderVfs.getClassByName(classFqn);
      TypeInfo typeInfo = new JavaTypeToTypeInfoConverter(storage).convert(clazz);
      return typeInfo;
   }

}
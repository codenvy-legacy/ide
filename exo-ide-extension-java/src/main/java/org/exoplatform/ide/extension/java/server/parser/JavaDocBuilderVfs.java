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
package org.exoplatform.ide.extension.java.server.parser;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

import org.exoplatform.ide.extension.java.server.parser.scanner.FileSuffixFilter;
import org.exoplatform.ide.extension.java.server.parser.scanner.FolderScanner;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 28, 2011 3:07:14 PM evgen $
 *
 */
public class JavaDocBuilderVfs extends JavaDocBuilder
{
   /**
    * 
    */
   private static final long serialVersionUID = 2801488236934185900L;

   private VirtualFileSystem vfs;

   /**
    * @param vfs
    */
   public JavaDocBuilderVfs(VirtualFileSystem vfs, VfsClassLibrary library)
   {
      super(library);
      this.vfs = vfs;
   }

   @Override
   protected JavaClass createSourceClass(String name)
   {
      InputStream sourceFile = ((VfsClassLibrary)getClassLibrary()).getSourceFileContent(name);
      if (sourceFile != null)
      {
         JavaSource source = addSource(new InputStreamReader(sourceFile), name);
         for (int index = 0; index < source.getClasses().length; index++)
         {
            JavaClass clazz = source.getClasses()[index];
            if (name.equals(clazz.getFullyQualifiedName()))
            {
               return clazz;
            }
         }
         return source.getNestedClassByName(name);
      }
      return null;
   }

   public void addSourceTree(Folder folder)
   {
      FolderScanner scanner = new FolderScanner(folder, vfs);
      scanner.addFilter(new FileSuffixFilter(".java"));
      List<Item> list = scanner.scan();
      for (Item i : list)
      {
         try
         {
            addSource(new InputStreamReader(vfs.getContent(i.getId()).getStream()));
         }
         catch (VirtualFileSystemException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
}

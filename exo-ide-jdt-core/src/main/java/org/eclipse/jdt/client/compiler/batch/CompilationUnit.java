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
package org.eclipse.jdt.client.compiler.batch;

import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.internal.compiler.env.ICompilationUnit;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z evgen $
 * 
 */
public class CompilationUnit implements ICompilationUnit
{
   public char[] contents;

   public char[] fileName;

   public char[] mainTypeName;

   String encoding;

   // a specific destination path for this compilation unit; coding is
   // aligned with Main.destinationPath:
   // == null: unspecified, use whatever value is set by the enclosing
   // context, id est Main;
   // == Main.NONE: absorbent element, do not output class files;
   // else: use as the path of the directory into which class files must
   // be written.

   public CompilationUnit(char[] contents, String fileName, String encoding)
   {
      this(contents, fileName, encoding, null);
   }

   /**
    * @param contents
    * @param string
    * @param encoding
    */
   public CompilationUnit(char[] contents, String fileName, String encoding, String destinationPath)
   {
      this.contents = contents;
      char[] fileNameCharArray = fileName.toCharArray();
      if (CharOperation.indexOf('\\', fileNameCharArray) != -1)
      {
         CharOperation.replace(fileNameCharArray, '\\', '/');
      }

      this.fileName = fileNameCharArray;
      int start = CharOperation.lastIndexOf('/', fileNameCharArray) + 1;

      int end = CharOperation.lastIndexOf('.', fileNameCharArray);
      if (end == -1)
      {
         end = fileNameCharArray.length;
      }

      this.mainTypeName = CharOperation.subarray(fileNameCharArray, start, end);
      this.encoding = encoding;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.IDependent#getFileName()
    */
   @Override
   public char[] getFileName()
   {
      return fileName;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.ICompilationUnit#getContents()
    */
   @Override
   public char[] getContents()
   {
      return contents;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.ICompilationUnit#getMainTypeName()
    */
   @Override
   public char[] getMainTypeName()
   {
      return mainTypeName;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.ICompilationUnit#getPackageName()
    */
   @Override
   public char[][] getPackageName()
   {
      return null;
   }

}

/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.internal.core;

import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.client.internal.compiler.util.Util;
/**
 * A basic implementation of <code>ICompilationUnit</code>
 * for use in the <code>SourceMapper</code>.
 * @see ICompilationUnit
 */
public class BasicCompilationUnit implements ICompilationUnit
{
   protected char[] contents;

   // Note that if this compiler ICompilationUnit's content is known in advance, the fileName is not used to retrieve this content.
   // Instead it is used to keep enough information to recreate the IJavaElement corresponding to this compiler ICompilationUnit.
   // Thus the fileName can be a path to a .class file, or even a path in a .jar to a .class file.
   // (e.g. /P/lib/mylib.jar|org/eclipse/test/X.class)
   protected char[] fileName;

   protected char[][] packageName;

   protected char[] mainTypeName;

   protected String encoding;

   public BasicCompilationUnit(char[] contents, char[][] packageName, String fileName)
   {
      this.contents = contents;
      this.fileName = fileName.toCharArray();
      this.packageName = packageName;
   }

   public BasicCompilationUnit(char[] contents, char[][] packageName, String fileName, String encoding)
   {
      this(contents, packageName, fileName);
      this.encoding = encoding;
   }

//   public BasicCompilationUnit(char[] contents, char[][] packageName, String fileName, IJavaElement javaElement)
//   {
//      this(contents, packageName, fileName);
//      initEncoding(javaElement);
//   }

//   /*
//    * Initialize compilation unit encoding.
//    * If we have a project, then get file name corresponding IFile and retrieve its encoding using
//    * new API for encoding.
//    * In case of a class file, then go through project in order to let the possibility to retrieve
//    * a corresponding source file resource.
//    * If we have a compilation unit, then get encoding from its resource directly...
//    */
//   private void initEncoding(IJavaElement javaElement)
//   {
//      if (javaElement != null)
//      {
//         this.encoding = "UTF-8";
//      }
//      else
//      {
//         this.encoding = null;
//      }
//   }

   public char[] getContents()
   {
      if (this.contents != null)
         return this.contents; // answer the cached source
      
      return CharOperation.NO_CHAR;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.IDependent#getFileName()
    */
   public char[] getFileName()
   {
      return this.fileName;
   }

   public char[] getMainTypeName()
   {
      if (this.mainTypeName == null)
      {
         int start = CharOperation.lastIndexOf('/', this.fileName) + 1;
         if (start == 0 || start < CharOperation.lastIndexOf('\\', this.fileName))
            start = CharOperation.lastIndexOf('\\', this.fileName) + 1;
         int separator = CharOperation.indexOf('|', this.fileName) + 1;
         if (separator > start) // case of a .class file in a default package in a jar
            start = separator;

         int end = CharOperation.lastIndexOf('$', this.fileName);
         if (end == -1 || !Util.isClassFileName(this.fileName))
         {
            end = CharOperation.lastIndexOf('.', this.fileName);
            if (end == -1)
               end = this.fileName.length;
         }

         this.mainTypeName = CharOperation.subarray(this.fileName, start, end);
      }
      return this.mainTypeName;
   }

   public char[][] getPackageName()
   {
      return this.packageName;
   }

   public String toString()
   {
      return "CompilationUnit: " + new String(this.fileName); //$NON-NLS-1$
   }
}

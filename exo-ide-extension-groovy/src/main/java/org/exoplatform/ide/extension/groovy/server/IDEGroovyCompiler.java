/**
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;
import org.exoplatform.ide.extension.groovy.server.IDEGroovyClassLoaderProvider.IDEGroovyClassLoader;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * IDEGroovyCompiler can load source code of groovy script from IDE virtual file
 * system and parse it via GroovyClassLoader.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: IDEGroovyCompiler.java 4455 2011-05-31 06:54:06Z dkuleshov $
 */
public class IDEGroovyCompiler
{
   protected final IDEGroovyClassLoaderProvider classLoaderProvider;

   public IDEGroovyCompiler(IDEGroovyClassLoaderProvider classLoaderProvider)
   {
      this.classLoaderProvider = classLoaderProvider;
   }

   public IDEGroovyCompiler()
   {
      this(new IDEGroovyClassLoaderProvider());
   }

   /**
    * Compile Groovy source that located in <code>files</code>. Compiled sources
    * can be dependent to each other and dependent to Groovy sources that are
    * accessible for this compiler and with additional Groovy sources
    * <code>src</code>. <b>NOTE</b> To be able load Groovy source files from
    * specified folders the following rules must be observed:
    * <ul>
    * <li>Groovy source files must be located in folder with respect to package
    * structure</li>
    * <li>Name of Groovy source files must be the same as name of class located
    * in file</li>
    * <li>Groovy source file must have extension '.groovy'</li>
    * </ul>
    * 
    * @param src
    *           additional Groovy source location that should be added in
    *           class-path when compile <code>files</code>
    * @param files
    *           Groovy sources to be compiled
    * @return result of compilation
    * @throws IOException
    *            if any i/o errors occurs
    */
   public Class<?>[] compile(SourceFolder[] src, SourceFile[] files) throws IOException
   {
      return doCompile((IDEGroovyClassLoader)classLoaderProvider.getGroovyClassLoader(src), files);
   }

   /**
    * Compile Groovy source that located in <code>files</code>. Compiled sources
    * can be dependent to each other and dependent to Groovy sources that are
    * accessible for this compiler.
    * 
    * @param files
    *           Groovy sources to be compiled
    * @return result of compilation
    * @throws IOException
    *            if any i/o errors occurs
    */
   public Class<?>[] compile(SourceFile[] files) throws IOException
   {
      return doCompile((IDEGroovyClassLoader)classLoaderProvider.getGroovyClassLoader(), files);
   }

   @SuppressWarnings("rawtypes")
   private Class<?>[] doCompile(final IDEGroovyClassLoader cl, final SourceFile[] files)
   {
      Class[] classes = AccessController.doPrivileged(new PrivilegedAction<Class[]>()
      {
         public Class[] run()
         {
            return cl.parseClasses(files);
         }
      });
      return classes;
   }

   /**
    * Get URLs of classes (stored in VFS only) required to compile sources
    * <code>files</code>. Result array includes URLs of <code>files</code> plus
    * URLs of other required sources if they can be found in class-path.
    * 
    * @param sources
    *           additional Groovy source location that should be added in
    *           class-path when analyze <code>files</code>
    * @param files
    *           set of sources for analyzing
    * @return URLs
    * @throws IOException
    *            if any i/o errors occurs
    */
   public URL[] getDependencies(final SourceFolder[] sources, final SourceFile[] files) throws IOException
   {
      try
      {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<URL[]>()
         {
            public URL[] run() throws IOException
            {
               return ((IDEGroovyClassLoader)classLoaderProvider.getGroovyClassLoader()).findDependencies(sources,
                  files);
            }
         });
      }
      catch (PrivilegedActionException e)
      {
         Throwable cause = e.getCause();
         throw (IOException)cause;
      }
   }
}

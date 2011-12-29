/*
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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyResourceLoader;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.io.ReaderSource;
import org.codehaus.groovy.control.io.URLReaderSource;
import org.everrest.groovy.ExtendedGroovyClassLoader;
import org.everrest.groovy.GroovyClassLoaderProvider;
import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: IDEGroovyClassLoaderProvider.java 3702 2010-12-22 10:24:13Z
 *          aparfonov $
 */
public class IDEGroovyClassLoaderProvider extends GroovyClassLoaderProvider
{

   public static class IDEGroovyClassLoader extends ExtendedGroovyClassLoader
   {
      public IDEGroovyClassLoader(ClassLoader classLoader)
      {
         super(classLoader);
      }

      public IDEGroovyClassLoader(GroovyClassLoader parent)
      {
         super(parent);
      }

      /**
       * {@inheritDoc}
       */
      protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource cs)
      {
         return new IDECompilationUnit(config, cs, this);
      }

      public URL[] findDependencies(final SourceFolder[] sources, final SourceFile[] files) throws IOException
      {
         return findDependencies(sources, files, Phases.SEMANTIC_ANALYSIS, null);
      }

      @SuppressWarnings("rawtypes")
      private URL[] findDependencies(SourceFolder[] sources, SourceFile[] files, int phase, CompilerConfiguration config)
         throws IOException
      {
         CodeSource cs = new CodeSource(getCodeSource(), (java.security.cert.Certificate[])null);
         IDEGroovyResourceLoader actualLoader = null;
         URL[] roots = null;
         if (sources != null && sources.length > 0)
         {
            roots = new URL[sources.length];
            for (int i = 0; i < sources.length; i++)
            {
               roots[i] = sources[i].getPath();
            }
            actualLoader = new IDEGroovyResourceLoader(roots);
         }

         final HierarchicalResourceLoader loader =
            new HierarchicalResourceLoader(actualLoader, IDEGroovyClassLoader.this.getResourceLoader());

         CompilationUnit cunit =
            new IDECompilationUnit(config, cs, new IDEGroovyClassLoader(
               IDEGroovyClassLoaderProvider.class.getClassLoader())
            {
               public GroovyResourceLoader getResourceLoader()
               {
                  return loader;
               }
            });

         for (int i = 0; i < files.length; i++)
         {
            cunit.addSource(files[i].getPath());
         }
         cunit.compile(phase);

         List classNodes = cunit.getAST().getClasses();
         List<URL> dependencies = new ArrayList<URL>(classNodes.size());
         for (Iterator iter = classNodes.iterator(); iter.hasNext();)
         {
            ClassNode classNode = (ClassNode)iter.next();
            ModuleNode module = classNode.getModule();
            if (module != null)
            {
               SourceUnit currentSunit = module.getContext();
               if (currentSunit instanceof IDESourceUnit)
               {
                  dependencies.add(((IDESourceUnit)currentSunit).getUrl());
               }
            }
         }

         return dependencies.toArray(new URL[dependencies.size()]);
      }
   }

   public static class IDECompilationUnit extends CompilationUnit
   {
      public IDECompilationUnit()
      {
         super();
      }

      public IDECompilationUnit(CompilerConfiguration configuration, CodeSource security, GroovyClassLoader loader,
         GroovyClassLoader transformLoader)
      {
         super(configuration, security, loader, transformLoader);
      }

      public IDECompilationUnit(CompilerConfiguration configuration, CodeSource security, GroovyClassLoader loader)
      {
         super(configuration, security, loader);
      }

      public IDECompilationUnit(CompilerConfiguration configuration)
      {
         super(configuration);
      }

      public IDECompilationUnit(GroovyClassLoader loader)
      {
         super(loader);
      }

      /**
       * @see org.codehaus.groovy.control.CompilationUnit#addSource(java.net.URL)
       */
      @Override
      public SourceUnit addSource(URL url)
      {
         return addSource(new IDESourceUnit(url, configuration, classLoader, getErrorCollector()));
      }
   }

   /** Adapter for ide+vfs like URLs. */
   public static class IDESourceUnit extends SourceUnit
   {
      private URL url;

      public IDESourceUnit(File source, CompilerConfiguration configuration, GroovyClassLoader loader, ErrorCollector er)
      {
         super(source, configuration, loader, er);
      }

      public IDESourceUnit(String name, ReaderSource source, CompilerConfiguration flags, GroovyClassLoader loader,
         ErrorCollector er)
      {
         super(name, source, flags, loader, er);
      }

      public IDESourceUnit(String name, String source, CompilerConfiguration configuration, GroovyClassLoader loader,
         ErrorCollector er)
      {
         super(name, source, configuration, loader, er);
      }

      public IDESourceUnit(URL source, CompilerConfiguration configuration, GroovyClassLoader loader, ErrorCollector er)
      {
         // IDE VFS local path (or ID) is in fragment of URL:
         // ide+vfs://${vfsID}#${/path} or ${ID}
         super("ide+vfs".equals(source.getProtocol()) //
            ? source.getRef() //
            : source.getPath(), new URLReaderSource(source, configuration), configuration, loader, er);
         this.url = source;
      }

      public URL getUrl()
      {
         return url;
      }
   }

   private static class HierarchicalResourceLoader implements GroovyResourceLoader
   {
      private final GroovyResourceLoader actual;

      private final GroovyResourceLoader parent;

      HierarchicalResourceLoader(GroovyResourceLoader actual, GroovyResourceLoader parent) throws MalformedURLException
      {
         this.actual = actual;
         this.parent = parent;
      }

      public URL loadGroovySource(String filename) throws MalformedURLException
      {
         URL resource = null;
         if (actual != null)
         {
            resource = actual.loadGroovySource(filename);
         }
         if (resource == null && parent != null)
         {
            resource = parent.loadGroovySource(filename);
         }
         return resource;
      }
   }

   /* ========================================================================== */

   public IDEGroovyClassLoaderProvider()
   {
      super(AccessController.doPrivileged(new PrivilegedAction<IDEGroovyClassLoader>()
      {
         public IDEGroovyClassLoader run()
         {
            return new IDEGroovyClassLoader(IDEGroovyClassLoaderProvider.class.getClassLoader());
         }
      }));
   }

   public ExtendedGroovyClassLoader getGroovyClassLoader(SourceFolder[] sources) throws MalformedURLException
   {
      if (sources == null || sources.length == 0)
      {
         return getGroovyClassLoader();
      }

      URL[] roots = new URL[sources.length];
      for (int i = 0; i < sources.length; i++)
      {
         roots[i] = sources[i].getPath();
      }

      final GroovyClassLoader parent = getGroovyClassLoader();
      IDEGroovyClassLoader classLoader = AccessController.doPrivileged(new PrivilegedAction<IDEGroovyClassLoader>()
      {
         public IDEGroovyClassLoader run()
         {
            return new IDEGroovyClassLoader(parent);
         }
      });
      classLoader.setResourceLoader(new IDEGroovyResourceLoader(roots));
      return classLoader;
   }
}
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
package org.exoplatform.ide.groovy;

import groovy.lang.GroovyClassLoader;

import org.codehaus.groovy.control.CompilationFailedException;
import org.everrest.core.DependencySupplier;
import org.everrest.core.ResourceBinder;
import org.everrest.groovy.ExtendedGroovyClassLoader;
import org.everrest.groovy.GroovyClassLoaderProvider;
import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 30, 2011 2:49:20 PM anya $
 * 
 */
public class GroovyResourcePublisher extends org.everrest.groovy.GroovyResourcePublisher
{

   /**
    * @param binder
    * @param classLoaderProvider
    * @param dependencies
    */
   protected GroovyResourcePublisher(ResourceBinder binder, GroovyClassLoaderProvider classLoaderProvider,
      DependencySupplier dependencies)
   {
      super(binder, classLoaderProvider, dependencies);
   }

   /**
    * Validate does stream contain Groovy source code which is conforms with requirement to JAX-RS resource.
    * 
    * @param in Groovy source stream
    * @param name script name. This name will be used by GroovyClassLoader to identify script, e.g. specified name will be used in
    *           error message in compilation of Groovy fails. If this parameter is <code>null</code> then GroovyClassLoader will
    *           use automatically generated name
    * @param src additional path to Groovy sources
    * @param files Groovy source files to be added in build path directly
    * @throws MalformedScriptException if source has errors or there is no required JAX-RS annotation
    */
   public void validateResource(final InputStream in, final String name, final SourceFolder[] src,
      final SourceFile[] files) throws MalformedScriptException
   {
      // Class<?> rc;
      try
      {
         // rc =
         AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>()
         {
            public Class<?> run() throws MalformedURLException
            {
               ExtendedGroovyClassLoader cl =
                  (src == null) ? classLoaderProvider.getGroovyClassLoader() : classLoaderProvider
                     .getGroovyClassLoader(src);
               return cl.parseClass(in, (name != null && name.length() > 0) ? name : cl.generateScriptName(), files);
            }
         });
      }
      catch (PrivilegedActionException e)
      {
         Throwable cause = e.getCause();
         // MalformedURLException
         throw new IllegalArgumentException(cause.getMessage());
      }
      catch (CompilationFailedException e)
      {
         throw new MalformedScriptException(e.getMessage());
      }
      // / XXX : Temporary disable resource class validation. Just try to compile
      // class and assume resource class is OK if compilation is successful.
      /*
       * try { new AbstractResourceDescriptorImpl(rc).accept(ResourceDescriptorValidator.getInstance()); } catch (RuntimeException
       * e) { // FIXME : Need have proper exception for invalid resources in 'exo.ws.rest.core'. throw new
       * MalformedScriptException(e.getMessage()); }
       */
   }

   /**
    * Validate does stream contain Groovy source code which is conforms with requirement to JAX-RS resource.
    * 
    * @param in Groovy source stream
    * @param name script name. This name will be used by GroovyClassLoader to identify script, e.g. specified name will be used in
    *           error message in compilation of Groovy fails. If this parameter is <code>null</code> then GroovyClassLoader will
    *           use automatically generated name
    * @throws MalformedScriptException if source has errors or there is no required JAX-RS annotation
    */
   public void validateResource(InputStream in, String name) throws MalformedScriptException
   {
      validateResource(in, name, null, null);
   }

   /**
    * Validate does <code>source</code> contain Groovy source code which is conforms with requirement to JAX-RS resource.
    * 
    * @param source Groovy source code as String
    * @param charset source string charset. May be <code>null</code> than default charset will be in use
    * @param name script name. This name will be used by GroovyClassLoader to identify script, e.g. specified name will be used in
    *           error message in compilation of Groovy fails. If this parameter is <code>null</code> then GroovyClassLoader will
    *           use automatically generated name
    * @param src additional path to Groovy sources
    * @param files Groovy source files to be added in build path directly
    * @throws MalformedScriptException if source has errors or there is no required JAX-RS annotation
    */
   public final void validateResource(String source, String charset, String name, SourceFolder[] src, SourceFile[] files)
      throws MalformedScriptException
   {
      validateResource(source, charset == null ? DEFAULT_CHARSET : Charset.forName(charset), name, src, files);
   }

   /**
    * Validate does <code>source</code> contain Groovy source code which is conforms with requirement to JAX-RS resource.
    * 
    * @param source Groovy source code as String
    * @param name script name. This name will be used by GroovyClassLoader to identify script, e.g. specified name will be used in
    *           error message in compilation of Groovy fails. If this parameter is <code>null</code> then GroovyClassLoader will
    *           use automatically generated name
    * @param src additional path to Groovy sources
    * @param files Groovy source files to be added in build path directly
    * @throws MalformedScriptException if source has errors or there is no required JAX-RS annotation
    */
   public final void validateResource(String source, String name, SourceFolder[] src, SourceFile[] files)
      throws MalformedScriptException
   {
      validateResource(source, DEFAULT_CHARSET, name, src, files);
   }

   /**
    * Validate does <code>source</code> contain Groovy source code which is conforms with requirement to JAX-RS resource.
    * 
    * @param source Groovy source code as String
    * @param name script name. This name will be used by GroovyClassLoader to identify script, e.g. specified name will be used in
    *           error message in compilation of Groovy fails. If this parameter is <code>null</code> then GroovyClassLoader will
    *           use automatically generated name
    * @throws MalformedScriptException if source has errors or there is no required JAX-RS annotation
    */
   public final void validateResource(String source, String name) throws MalformedScriptException
   {
      validateResource(source, DEFAULT_CHARSET, name, null, null);
   }

   private void validateResource(String source, Charset charset, String name, SourceFolder[] src, SourceFile[] files)
      throws MalformedScriptException
   {
      byte[] bytes = source.getBytes(charset);
      validateResource(new ByteArrayInputStream(bytes), name, src, files);
   }

   /**
    * @return get underling groovy class loader
    */
   public GroovyClassLoader getGroovyClassLoader()
   {
      return classLoaderProvider.getGroovyClassLoader();
   }
}

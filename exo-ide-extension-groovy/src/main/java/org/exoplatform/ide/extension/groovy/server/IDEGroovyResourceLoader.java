/*
 * Copyright (C) 2009 eXo Platform SAS.
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

import org.everrest.groovy.DefaultGroovyResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Loader that able load groovy source files from IDE virtual file system.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class IDEGroovyResourceLoader extends DefaultGroovyResourceLoader
{
   /**
    * Extension of groovy source file. Groovy source file must have one of
    * extensions from this array to be processed by current
    * GroovyResourceLoader. It is possible to configure set of supported
    * extension by adding it in file
    * 'META-INF/org.exoplatform.ide.groovy.source.extensions'. Such file must be
    * available for context class loader.
    */
   private static final String[] IDE_GROOVY_SOURCE_EXTENSIONS = getGroovySourceFileExtensions();

   private static String[] getGroovySourceFileExtensions()
   {
      Set<String> extensions = new LinkedHashSet<String>();
      extensions.add(".groovy"); // always add '.groovy' extension
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream extensionsFile =
         classLoader.getResourceAsStream("META-INF/services/org.exoplatform.ide.groovy.source.extensions");
      if (extensionsFile != null)
      {
         BufferedReader r = null;
         try
         {
            r = new BufferedReader(new InputStreamReader(extensionsFile, "utf-8"));
            String line = null;
            while ((line = r.readLine()) != null)
            {
               int comment = line.indexOf('#');
               if (comment == 0)
               {
                  continue;
               }
               if (comment > 0)
               {
                  line = line.substring(0, comment);
               }
               line = line.trim();
               if (line.length() > 0)
               {
                  extensions.add(line);
               }
            }
         }
         catch (IOException ioe)
         {
            throw new RuntimeException(ioe.getMessage(), ioe);
         }
         finally
         {
            try
            {
               if (r != null)
               {
                  r.close();
               }
               extensionsFile.close();
            }
            catch (IOException ignored)
            {
               // Ignore errors when close resource.
            }
         }
      }
      return extensions.toArray(new String[extensions.size()]);
   }

   private static URL[] normalizeURL(URL[] src) throws MalformedURLException
   {
      URL[] res = new URL[src.length];
      for (int i = 0; i < src.length; i++)
      {
         if ("ide+vfs".equals(src[i].getProtocol()))
         {
            String ref = src[i].getRef();
            if (ref == null)
            {
               ref = "/";
            }
            else if (ref.charAt(ref.length() - 1) != '/')
            {
               ref = ref + "/";
            }
            res[i] = new URL(src[i], "#" + ref);
         }
         else
         {
            res[i] = src[i];
         }
      }
      return res;
   }

   public IDEGroovyResourceLoader(URL[] roots) throws MalformedURLException
   {
      super(normalizeURL(roots));
   }

   public IDEGroovyResourceLoader(URL root) throws MalformedURLException
   {
      this(new URL[]{root});
   }

   @Override
   protected URL createURL(URL root, String filename) throws MalformedURLException
   {
      return ("ide+vfs".equals(root.getProtocol())) //
         ? new URL(root, "#" + root.getRef() + filename) //
         : new URL(root, filename);
   }

   @Override
   protected String[] getSourceFileExtensions()
   {
      return IDE_GROOVY_SOURCE_EXTENSIONS;
   }
}
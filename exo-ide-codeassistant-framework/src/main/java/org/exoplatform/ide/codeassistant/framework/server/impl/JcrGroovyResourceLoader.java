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
package org.exoplatform.ide.codeassistant.framework.server.impl;

import org.everrest.groovy.DefaultGroovyResourceLoader;
import org.exoplatform.services.jcr.ext.resource.JcrURLConnection;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * JCR groovy resource resolver.
 * @author vetal
 *
 */
public class JcrGroovyResourceLoader extends DefaultGroovyResourceLoader
{
   private static final Log LOG = ExoLogger.getLogger(JcrGroovyResourceLoader.class);

   private static URL[] normalizeJcrURL(URL[] src) throws MalformedURLException
   {
      URL[] res = new URL[src.length];
      for (int i = 0; i < src.length; i++)
      {
         if ("jcr".equals(src[i].getProtocol()))
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

   public JcrGroovyResourceLoader(URL[] roots) throws MalformedURLException
   {
      super(normalizeJcrURL(roots));
   }

   public JcrGroovyResourceLoader(URL root) throws MalformedURLException
   {
      this(new URL[]{root});
   }

   /**
    * @see org.exoplatform.services.rest.ext.groovy.DefaultGroovyResourceLoader#getResource(java.lang.String)
    */
   @Override
   protected URL getResource(String filename) throws MalformedURLException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Process file: " + filename);
      return super.getResource(filename);
   }
   
   /**
    * @see org.exoplatform.services.rest.ext.groovy.DefaultGroovyResourceLoader#createURL(java.net.URL,java.lang.String)
    */
   
   protected URL createURL(URL root, String filename) throws MalformedURLException
   {
      return ("jcr".equals(root.getProtocol())) ? new URL(root, "#" + root.getRef() + filename)
      : new URL(root, filename);
   }

   /**
    * @see org.exoplatform.services.rest.ext.groovy.DefaultGroovyResourceLoader#checkResource(java.net.URL)
    */
   @Override
   protected boolean checkResource(URL resource)
   {
      URLConnection connection = null;
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Try to load resource from URL : " + resource);

         connection = resource.openConnection();
         connection.getInputStream().close();

         return true;
      }
      catch (IOException e)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Can't open URL : " + resource);

         return false;
      }
      finally
      {
         if (connection != null && resource != null && "jcr".equals(resource.getProtocol()))
            ((JcrURLConnection)connection).disconnect();
      }
   }
}
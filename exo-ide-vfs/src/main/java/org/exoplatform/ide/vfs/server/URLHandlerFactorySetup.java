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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Setup {@link URLStreamHandlerFactory} to be able use URL for access to virtual file system. It is not possible to provide
 * correct {@link URLStreamHandler} by system property 'java.protocol.handler.pkgs'. Bug in Oracle JDK:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4648098
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class URLHandlerFactorySetup
{
   private static final Log LOG = ExoLogger.getExoLogger(URLHandlerFactorySetup.class);

   public synchronized static void setup(VirtualFileSystemRegistry registry)
   {
      try
      {
         new URL("ide+vfs", "", "");
      }
      catch (MalformedURLException mue)
      {
         // URL with protocol 'ide+vfs' is not supported yet. Need register URLStreamHandlerFactory.

         if (LOG.isDebugEnabled())
         {
            LOG.debug("--> Try setup URLStreamHandlerFactory for protocol 'ide+vfs'. ");
         }
         try
         {
            // Get currently installed URLStreamHandlerFactory.
            Field factoryField = URL.class.getDeclaredField("factory");
            factoryField.setAccessible(true);
            URLStreamHandlerFactory currentFactory = (URLStreamHandlerFactory)factoryField.get(null);

            if (LOG.isDebugEnabled())
            {
               LOG.debug("--> Current instance of URLStreamHandlerFactory: " //
                  + (currentFactory != null ? currentFactory.getClass().getName() : null));
            }

            //
            URLStreamHandlerFactory vfsURLFactory = new VirtualFileSystemURLHandlerFactory(currentFactory, registry);
            factoryField.set(null, vfsURLFactory);
         }
         catch (SecurityException se)
         {
            throw new VirtualFileSystemRuntimeException(se.getMessage(), se);
         }
         catch (NoSuchFieldException nfe)
         {
            throw new VirtualFileSystemRuntimeException(nfe.getMessage(), nfe);
         }
         catch (IllegalAccessException ae)
         {
            throw new VirtualFileSystemRuntimeException(ae.getMessage(), ae);
         }

         // Check 'ide+vfs' again. From not it should be possible to use such URLs.
         // At the same time we force URL to remember our protocol handler.
         // URL knows about it even if the URLStreamHandlerFactory is changed.

         try
         {
            new URL("ide+vfs", "", "");

            //
            if (LOG.isDebugEnabled())
            {
               LOG.debug("--> URLStreamHandlerFactory installed. ");
            }
         }
         catch (MalformedURLException e)
         {
            throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
         }
      }
   }

   protected URLHandlerFactorySetup()
   {
   }
}

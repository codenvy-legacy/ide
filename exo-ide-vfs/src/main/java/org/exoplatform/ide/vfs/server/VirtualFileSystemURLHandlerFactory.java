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

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Factory for URLStreamHandler to <code>ide+vfs</code> protocol.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class VirtualFileSystemURLHandlerFactory implements URLStreamHandlerFactory
{
   private final URLStreamHandlerFactory delegate;

   private final VirtualFileSystemRegistry registry;

   /**
    * @param delegate factory which we should ask to create URLStreamHandler if current factory does not support requested
    *           protocol.
    * @param registry set of all available virtual file systems
    */
   public VirtualFileSystemURLHandlerFactory(URLStreamHandlerFactory delegate, VirtualFileSystemRegistry registry)
   {
      this.delegate = delegate;
      this.registry = registry;
   }

   /**
    * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
    */
   @Override
   public URLStreamHandler createURLStreamHandler(String protocol)
   {
      if ("ide+vfs".equals(protocol))
      {
         return new VirtualFileSystemResourceHandler(registry);
      }
      else if (delegate != null)
      {
         delegate.createURLStreamHandler(protocol);
      }
      return null;
   }
}

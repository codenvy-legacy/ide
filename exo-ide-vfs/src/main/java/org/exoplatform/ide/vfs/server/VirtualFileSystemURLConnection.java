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

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

/**
 * Connection to virtual file system. Instances of this class are not safe to be
 * used by multiple concurrent threads.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class VirtualFileSystemURLConnection extends URLConnection
{
   private final VirtualFileSystemRegistry registry;

   private VirtualFileSystem vfs;

   private Item item;

   /**
    * @param url
    *           the URL
    * @param registry
    *           virtual file system registry
    */
   public VirtualFileSystemURLConnection(URL url, VirtualFileSystemRegistry registry)
   {
      super(check(url)); // Be sure URL is correct.
      this.registry = registry;
   }

   private static final URL check(URL url)
   {
      if (!"ide+vfs".equals(url.getProtocol()))
      {
         throw new IllegalArgumentException("Invalid URL: " + url);
      }
      return url;
   }

   /**
    * @see java.net.URLConnection#connect()
    */
   @Override
   public void connect() throws IOException
   {
      final URL theUrl = getURL();
      String path = theUrl.getPath();
      final String vfsId = (path == null || "/".equals(path)) //
         ? null //
         : (path.startsWith("/")) //
            ? path.substring(1) //
            : path;
      try
      {
         vfs = registry.getProvider(vfsId).newInstance(null);
         final String itemIdentifier = theUrl.getRef();
         item = (itemIdentifier.startsWith("/")) //
            ? vfs.getItemByPath(itemIdentifier, null, PropertyFilter.NONE_FILTER) //
            : vfs.getItem(itemIdentifier, PropertyFilter.NONE_FILTER);
      }
      catch (VirtualFileSystemException e)
      {
         throw new IOException(e.getMessage(), e);
      }
      connected = true;
   }

   public void disconnect()
   {
      item = null;
      vfs = null;
      connected = false;
   }

   /**
    * @see java.net.URLConnection#getContentLength()
    */
   @Override
   public int getContentLength()
   {
      try
      {
         if (!connected)
         {
            connect();
         }
         if (item.getItemType() == ItemType.FILE)
         {
            return (int)((File)item).getLength();
         }
      }
      catch (IOException e)
      {
      }
      return -1;
   }

   /**
    * @see java.net.URLConnection#getContentType()
    */
   @Override
   public String getContentType()
   {
      try
      {
         if (!connected)
         {
            connect();
         }
         return item.getMimeType();
      }
      catch (IOException e)
      {
      }
      return null;
   }

   /**
    * @see java.net.URLConnection#getLastModified()
    */
   @Override
   public long getLastModified()
   {
      try
      {
         if (!connected)
         {
            connect();
         }
         if (item.getItemType() == ItemType.FILE)
         {
            return ((File)item).getLastModificationDate();
         }
      }
      catch (IOException e)
      {
      }
      return 0;
   }

   /**
    * @see java.net.URLConnection#getContent()
    */
   @Override
   public Object getContent() throws IOException
   {
      if (!connected)
      {
         connect();
      }
      return item;
   }

   /**
    * @see java.net.URLConnection#getContent(java.lang.Class[])
    */
   @SuppressWarnings("rawtypes")
   @Override
   public Object getContent(Class[] classes) throws IOException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see java.net.URLConnection#getInputStream()
    */
   @Override
   public InputStream getInputStream() throws IOException
   {
      if (!connected)
      {
         connect();
      }
      try
      {
         if (item.getItemType() == ItemType.FILE)
         {
            ContentStream content = vfs.getContent(item.getId());
            return content.getStream();
         }
         // Folder. Show plain list of child.
         ItemList<Item> children = vfs.getChildren(item.getId(), -1, 0, PropertyFilter.NONE_FILTER);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         Writer w = new OutputStreamWriter(out);
         for (Item i : children.getItems())
         {
            w.write(i.getName());
            w.write('\n');
         }
         w.flush();
         w.close();
         InputStream in = new ByteArrayInputStream(out.toByteArray());
         return in;
      }
      catch (VirtualFileSystemException e)
      {
         throw new IOException(e.getMessage(), e);
      }
   }
}
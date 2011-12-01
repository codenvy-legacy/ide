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
package org.exoplatform.ide.extension.java.server.parser;

import com.thoughtworks.qdox.model.ClassLibrary;

import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 28, 2011 3:08:29 PM evgen $
 *
 */
public class VfsClassLibrary extends ClassLibrary
{

   private VirtualFileSystem vfs;

   private List<Folder> sourceFolders = new ArrayList<Folder>();

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(VfsClassLibrary.class);

   /**
    * @param vfs
    */
   public VfsClassLibrary(VirtualFileSystem vfs)
   {
      this.vfs = vfs;
   }

   /**
    * 
    */
   private static final long serialVersionUID = -4177400811232878566L;

   public void addSourceFolder(Folder folder)
   {
      sourceFolders.add(folder);
   }

   public InputStream getSourceFileContent(String className)
   {
      String mainClassName = className.split("\\$")[0];
      String path = mainClassName.replace('.', '/') + ".java";
      for (Folder f : sourceFolders)
      {

         try
         {
            Item i = vfs.getItemByPath(f + "/" + path, null, PropertyFilter.NONE_FILTER);
            if (i instanceof File)
            {
               return vfs.getContent(i.getId()).getStream();
            }
         }
         catch (ItemNotFoundException e)
         {
            if (LOG.isDebugEnabled())
               LOG.debug(e);
            continue;
         }
         catch (PermissionDeniedException e)
         {
            if (LOG.isWarnEnabled())
               LOG.warn(e);
         }
         catch (VirtualFileSystemException e)
         {
            if (LOG.isWarnEnabled())
               LOG.warn(e);
         }

      }
      return null;
   }

}

/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.data.event;

import org.exoplatform.ideall.client.model.File;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class FileContentSavedEvent extends GwtEvent<FileContentSavedHandler>
{

   public static final GwtEvent.Type<FileContentSavedHandler> TYPE = new GwtEvent.Type<FileContentSavedHandler>();

   private File file;

   private String path;
   
   private boolean isNewFile;
   
   private boolean isSaveAs;

   public FileContentSavedEvent(File file, String path, boolean isNewFile, boolean isSaveAs)
   {
      this.file = file;
      this.path = path;
      this.isNewFile = isNewFile;
      this.isSaveAs = isSaveAs;
   }

   /**
    * @return the file
    */
   public File getFile()
   {
      return file;
   }

   /**
    * @return the path
    */
   public String getPath()
   {
      return path;
   }

   @Override
   protected void dispatch(FileContentSavedHandler handler)
   {
      handler.onFileContentSaved(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<FileContentSavedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the isNewFile
    */
   public boolean isNewFile()
   {
      return isNewFile;
   }

   /**
    * @return the isSaveAs
    */
   public boolean isSaveAs()
   {
      return isSaveAs;
   }
   
}

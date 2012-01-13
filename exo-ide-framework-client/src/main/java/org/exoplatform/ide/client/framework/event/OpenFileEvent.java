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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class OpenFileEvent extends GwtEvent<OpenFileHandler>
{

   public static final GwtEvent.Type<OpenFileHandler> TYPE = new GwtEvent.Type<OpenFileHandler>();

   private FileModel file;

   private String fileId;

   private String editor;

   private int ignoreErrorsCount = 0;

   /**
    * Check is lock file.
    */
   private boolean lockFile = true;

   public OpenFileEvent(FileModel file)
   {
      this.file = file;
   }

   public OpenFileEvent(FileModel file, boolean lockFile)
   {
      this.file = file;
      this.lockFile = lockFile;
   }

   public OpenFileEvent(FileModel file, boolean lockFile, int ignoreErrorsCount)
   {
      this.file = file;
      this.lockFile = lockFile;
      this.ignoreErrorsCount = ignoreErrorsCount;
   }

   public OpenFileEvent(String fileId)
   {
      this.fileId = fileId;
   }

   public OpenFileEvent(FileModel file, String editor)
   {
      this(file);
      this.editor = editor;
   }

   public OpenFileEvent(FileModel file, String editor, boolean lockFile)
   {
      this(file);
      this.editor = editor;
      this.lockFile = lockFile;
   }

   public FileModel getFile()
   {
      return file;
   }

   public String getEditor()
   {
      return editor;
   }

   public String getFileId()
   {
      return fileId;
   }

   public boolean isLockFile()
   {
      return lockFile;
   }

   @Override
   protected void dispatch(OpenFileHandler hendler)
   {
      hendler.onOpenFile(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<OpenFileHandler> getAssociatedType()
   {
      return TYPE;
   }

   public int getIgnoreErrorsCount()
   {
      return ignoreErrorsCount;
   }

}

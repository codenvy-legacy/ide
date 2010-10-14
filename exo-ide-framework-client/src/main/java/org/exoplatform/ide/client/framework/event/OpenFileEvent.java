/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.client.framework.module.vfs.api.File;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileEvent extends GwtEvent<OpenFileHandler>
{

   public static final GwtEvent.Type<OpenFileHandler> TYPE = new GwtEvent.Type<OpenFileHandler>();

   private File file;
   
   private String href;
   
   private String editor;
   
   private int ignoreErrorsCount = 0; 
   
   /**
    * Check is lock file.
    */
   private boolean lockFile = true;

   public OpenFileEvent(File file)
   {
      this.file = file;
   }
   
   public OpenFileEvent(File file, boolean lockFile)
   {
      this.file = file;
      this.lockFile = lockFile;
   }
   
   public OpenFileEvent(File file, boolean lockFile, int ignoreErrorsCount)
   {
      this.file = file;
      this.lockFile = lockFile;
      this.ignoreErrorsCount = ignoreErrorsCount;
   }
   
   public OpenFileEvent(String href)
   {
      this.href = href;
   }

   public OpenFileEvent(File file, String editor)
   {
      this(file);
      this.editor = editor;
   }
   
   public OpenFileEvent(File file, String editor, boolean lockFile)
   {
      this(file);
      this.editor = editor;
      this.lockFile = lockFile;
   }

   public File getFile()
   {
      return file;
   }

   public String getEditor()
   {
      return editor;
   }
   
   public String getHref()
   {
      return href;
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

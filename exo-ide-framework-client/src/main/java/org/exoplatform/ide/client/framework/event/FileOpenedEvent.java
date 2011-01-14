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

import org.exoplatform.ide.client.framework.vfs.File;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class FileOpenedEvent extends GwtEvent<FileOpenedHandler>
{

   public static final GwtEvent.Type<FileOpenedHandler> TYPE = new GwtEvent.Type<FileOpenedHandler>();

   private File file;

   private String editor;

   public FileOpenedEvent(File file)
   {
      this.file = file;
   }

   public FileOpenedEvent(File file, String editor)
   {
      this(file);
      this.editor = editor;
   }

   public File getFile()
   {
      return file;
   }

   public String getEditor()
   {
      return editor;
   }
   
   @Override
   protected void dispatch(FileOpenedHandler handler)
   {
      handler.onFileOpened(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<FileOpenedHandler> getAssociatedType()
   {
      return TYPE;
   }

}

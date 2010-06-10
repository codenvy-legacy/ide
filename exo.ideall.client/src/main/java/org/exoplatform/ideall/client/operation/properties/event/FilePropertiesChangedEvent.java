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

package org.exoplatform.ideall.client.operation.properties.event;

import org.exoplatform.ideall.vfs.api.File;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Changed by The eXo Platform SAS        .
 * @version $Id: $
 */

public class FilePropertiesChangedEvent extends GwtEvent<FilePropertiesChangedHandler>
{

   public static GwtEvent.Type<FilePropertiesChangedHandler> TYPE = new GwtEvent.Type<FilePropertiesChangedHandler>();

   private File file;

   public FilePropertiesChangedEvent(File file)
   {
      this.file = file;
   }

   @Override
   protected void dispatch(FilePropertiesChangedHandler handler)
   {
      handler.onFilePropertiesChanged(this);
   }

   @Override
   public GwtEvent.Type<FilePropertiesChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   public File getFile()
   {
      return file;
   }

}

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
package org.exoplatform.ide.client.upload.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="dmitry.ndp@gmail.com">Dmitry Nochevnov</a>
 * @version $
 */

public class FilePathSelectedEvent extends GwtEvent<FilePathSelectedHandler>
{

   public static final GwtEvent.Type<FilePathSelectedHandler> TYPE = new GwtEvent.Type<FilePathSelectedHandler>();

   public FilePathSelectedEvent()
   {
   }

   @Override
   protected void dispatch(FilePathSelectedHandler handler)
   {
      handler.onFilePathSelected(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<FilePathSelectedHandler> getAssociatedType()
   {
      return TYPE;
   }

}

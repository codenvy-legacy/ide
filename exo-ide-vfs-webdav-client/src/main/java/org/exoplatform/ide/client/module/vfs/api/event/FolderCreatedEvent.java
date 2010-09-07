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

package org.exoplatform.ide.client.module.vfs.api.event;

import org.exoplatform.ide.client.module.vfs.api.Folder;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class FolderCreatedEvent extends GwtEvent<FolderCreatedHandler>
{

   public static final GwtEvent.Type<FolderCreatedHandler> TYPE = new GwtEvent.Type<FolderCreatedHandler>();

   private Folder folder;

   public FolderCreatedEvent(Folder folder)
   {
      this.folder = folder;
   }

   @Override
   protected void dispatch(FolderCreatedHandler handler)
   {
      handler.onFolderCreated(this);
   }

   @Override
   public GwtEvent.Type<FolderCreatedHandler> getAssociatedType()
   {
      return TYPE;
   }

   public Folder getFolder()
   {
      return folder;
   }

}

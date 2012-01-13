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
package org.exoplatform.ide.client.framework.navigation.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.shared.Folder;

/**
 * Event occurs when folder content is refreshed in browser tree. It is needed to known when the content in browser tree is
 * updated in extensions. Implement {@link FolderRefreshedHandler} handler to process the event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 6, 2011 4:10:23 PM anya $
 * 
 */
public class FolderRefreshedEvent extends GwtEvent<FolderRefreshedHandler>
{

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<FolderRefreshedHandler> TYPE = new GwtEvent.Type<FolderRefreshedHandler>();

   /**
    * Refreshed folder.
    */
   private Folder folder;

   /**
    * @param folder refreshed folder
    */
   public FolderRefreshedEvent(Folder folder)
   {
      this.folder = folder;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<FolderRefreshedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(FolderRefreshedHandler handler)
   {
      handler.onFolderRefreshed(this);
   }

   /**
    * @return the folder
    */
   public Folder getFolder()
   {
      return folder;
   }
}

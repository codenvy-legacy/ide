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
package org.exoplatform.ide.client.versioning.event;

import org.exoplatform.ide.client.module.vfs.api.Version;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired to display the content of the certain version.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 11, 2010 $
 *
 */
public class ShowVersionEvent extends GwtEvent<ShowVersionHandler>
{

   public static final GwtEvent.Type<ShowVersionHandler> TYPE = new GwtEvent.Type<ShowVersionHandler>();

   private Version version;

   public ShowVersionEvent(Version version)
   {
      this.version = version;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ShowVersionHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ShowVersionHandler handler)
   {
      handler.onShowVersion(this);
   }

   public Version getVersion()
   {
      return version;
   }
}

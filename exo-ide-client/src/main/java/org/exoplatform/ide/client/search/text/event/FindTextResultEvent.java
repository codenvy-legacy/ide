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
package org.exoplatform.ide.client.search.text.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired when results from text find in file is received.
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FindTextResultEvent extends GwtEvent<FindTextResultHandler>
{
   
   public static final GwtEvent.Type<FindTextResultHandler> TYPE = new GwtEvent.Type<FindTextResultHandler>();
   
   private boolean found;
   
   /**
    * @param found
    */
   public FindTextResultEvent(boolean found){
      this.found = found;
   }
   
   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(FindTextResultHandler handler)
   {
      handler.onFindTextResult(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<FindTextResultHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the found
    */
   public boolean isFound()
   {
      return found;
   }
}

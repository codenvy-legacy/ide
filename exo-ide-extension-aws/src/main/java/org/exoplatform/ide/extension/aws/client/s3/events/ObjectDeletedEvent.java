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

package org.exoplatform.ide.extension.aws.client.s3.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ObjectDeletedEvent extends GwtEvent<ObjectDeletedHandler>
{
   
   private String id;
   
   public ObjectDeletedEvent()
   {
      // TODO Auto-generated constructor stub
   }
   
   

   public ObjectDeletedEvent(String id)
   {
      this.id = id;
   }


   public String getId()
   {
      return id;
   }
   
   public static final GwtEvent.Type<ObjectDeletedHandler> TYPE = new GwtEvent.Type<ObjectDeletedHandler>();

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ObjectDeletedHandler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   protected void dispatch(ObjectDeletedHandler handler)
   {
      handler.onUploadFile(this);
   }

}

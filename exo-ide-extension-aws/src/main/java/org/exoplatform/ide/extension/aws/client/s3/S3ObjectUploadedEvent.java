/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when object uploaded to the AWS S3.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3ObjectUploadedEvent.java Sep 26, 2012 vetal $
 *
 */
public class S3ObjectUploadedEvent extends GwtEvent<S3ObjectUploadedHandler>
{

   /**
    * Type used to register the event.
    */
   public static final GwtEvent.Type<S3ObjectUploadedHandler> TYPE = new GwtEvent.Type<S3ObjectUploadedHandler>();

  
   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<S3ObjectUploadedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(S3ObjectUploadedHandler handler)
   {
      handler.onS3ObjectUploaded(this);
   }
}

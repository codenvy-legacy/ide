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
package org.exoplatform.ide.client.framework.documentation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fire this to add documentation for specific media file.
 * <br>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RegisterDocumentationEvent Jan 24, 2011 11:11:39 AM evgen $
 *
 */
public class RegisterDocumentationEvent extends GwtEvent<RegisterDocumentationHandler>
{

   public static GwtEvent.Type<RegisterDocumentationHandler> TYPE = new Type<RegisterDocumentationHandler>();

   private String mimeType;

   private String url;

   /**
    * @param mimeType type of file
    * @param url to documentation
    */
   public RegisterDocumentationEvent(String mimeType, String url)
   {
      this.mimeType = mimeType;
      this.url = url;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RegisterDocumentationHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RegisterDocumentationHandler handler)
   {
      handler.onRegisterDocumentation(this);
   }

   /**
    * @return the mimeType of file
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @return the url to the documentation
    */
   public String getUrl()
   {
      return url;
   }

}

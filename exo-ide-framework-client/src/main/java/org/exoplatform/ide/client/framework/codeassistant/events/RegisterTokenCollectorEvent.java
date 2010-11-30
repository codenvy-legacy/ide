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
package org.exoplatform.ide.client.framework.codeassistant.events;

import org.exoplatform.ide.client.framework.codeassistant.api.TokenCollectorExt;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 26, 2010 2:43:50 PM evgen $
 *
 */
public class RegisterTokenCollectorEvent extends GwtEvent<RegisterTokenCollectorHandler>
{

   public static GwtEvent.Type<RegisterTokenCollectorHandler> TYPE = new Type<RegisterTokenCollectorHandler>();

   private TokenCollectorExt collector;
   
   private String mimeType;

   /**
    * @param collector
    */
   public RegisterTokenCollectorEvent(String mimeType, TokenCollectorExt collector)
   {
      this.mimeType = mimeType;
      this.collector = collector;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RegisterTokenCollectorHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RegisterTokenCollectorHandler handler)
   {
      handler.onRegisterTokenCollector(this);
   }

   /**
    * @return the collector
    */
   public TokenCollectorExt getCollector()
   {
      return collector;
   }

   /**
    * @return the mimeType
    */
   public String getMimeType()
   {
      return mimeType;
   }

}

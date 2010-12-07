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

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenCollectorExt;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenWidgetFactory;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 6, 2010 3:08:14 PM evgen $
 *
 */
public class RegisterAutocompleteEvent extends GwtEvent<RegisterAutocompleteHandler>
{

   public static GwtEvent.Type<RegisterAutocompleteHandler> TYPE = new Type<RegisterAutocompleteHandler>();

   private String mimeType;

   private TokenWidgetFactory<TokenExt> factory;

   private TokenCollectorExt collector;

   /**
    * @param mimeType MimeType for add autocompletion 
    * @param factory {@link TokenWidgetFactory}
    * @param collector {@link TokenCollectorExt} 
    */
   public RegisterAutocompleteEvent(String mimeType, TokenWidgetFactory<TokenExt> factory, TokenCollectorExt collector)
   {
      this.mimeType = mimeType;
      this.factory = factory;
      this.collector = collector;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RegisterAutocompleteHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RegisterAutocompleteHandler handler)
   {
      handler.onRegisterAutocomplete(this);
   }

   /**
    * @return the mimeType
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @return the factory
    */
   public TokenWidgetFactory<TokenExt> getFactory()
   {
      return factory;
   }

   /**
    * @return the collector
    */
   public TokenCollectorExt getCollector()
   {
      return collector;
   }

}

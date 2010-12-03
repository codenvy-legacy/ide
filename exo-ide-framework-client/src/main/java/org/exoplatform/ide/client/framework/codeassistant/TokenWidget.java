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
package org.exoplatform.ide.client.framework.codeassistant;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 4:13:18 PM evgen $
 *
 */
public abstract class TokenWidget<T> extends Composite implements HasClickHandlers, HasMouseOverHandlers,
   HasDoubleClickHandlers
{

   protected T token;

   public TokenWidget(T token)
   {
      this.token = token;
   }

   /**
    * @return the token
    */
   public T getToken()
   {
      return token;
   }
   
   
   /**
    * @return name of token
    */
   public abstract String getTokenName();
   
   
   /**
    * @return String that will inserted in editor 
    */
   public abstract String getTokenValue();
   
   /**
    * 
    */
   public abstract void setSelectedStyle();

   
   /**
    * 
    */
   public abstract void setOveredStyle();

   /**
    * 
    */
   public abstract void setDefaultStyle();


   /**
    * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
    */
   public HandlerRegistration addClickHandler(ClickHandler handler)
   {

      return addDomHandler(handler, ClickEvent.getType());
   }

   /**
    * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
    */
   public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
   {
      return addDomHandler(handler, MouseOverEvent.getType());
   }

   /**
    * @see com.google.gwt.event.dom.client.HasDoubleClickHandlers#addDoubleClickHandler(com.google.gwt.event.dom.client.DoubleClickHandler)
    */
   public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
   {
      return addDomHandler(handler, DoubleClickEvent.getType());
   }

}

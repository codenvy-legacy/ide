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
package org.exoplatform.ide.client.module.groovy.codeassistant.event;

import org.exoplatform.ide.client.framework.codeassistant.api.ImportDeclarationTokenCollector;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RegisterImportTokenCollectorEvent Jan 24, 2011 5:15:26 PM evgen $
 *
 */
public class RegisterImportTokenCollectorEvent extends GwtEvent<RegisterImportTokenCollectorHandler>
{
   public static final GwtEvent.Type<RegisterImportTokenCollectorHandler> TYPE =
      new Type<RegisterImportTokenCollectorHandler>();

   private String mimeType;

   private ImportDeclarationTokenCollector collector;

   /**
    * @param mimeType
    * @param collector
    */
   public RegisterImportTokenCollectorEvent(String mimeType, ImportDeclarationTokenCollector collector)
   {
      this.mimeType = mimeType;
      this.collector = collector;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RegisterImportTokenCollectorHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RegisterImportTokenCollectorHandler handler)
   {
      handler.onRegisterImportTokenCollector(this);
   }

   /**
    * @return the mimeType
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @return the collector
    */
   public ImportDeclarationTokenCollector getCollector()
   {
      return collector;
   }

}

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
package org.exoplatform.ide.extension.cloudbees.client.update;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UpdateApplicationEvent.java Oct 10, 2011 5:07:26 PM vereshchaka $
 */
public class UpdateApplicationEvent extends GwtEvent<UpdateApplicationHandler>
{
   private String appId;

   private String appTitle;

   /**
    * 
    */
   public UpdateApplicationEvent()
   {
   }

   /**
    * @param appId
    * @param appTitle
    */
   public UpdateApplicationEvent(String appId, String appTitle)
   {
      super();
      this.appId = appId;
      this.appTitle = appTitle;
   }

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<UpdateApplicationHandler> TYPE = new GwtEvent.Type<UpdateApplicationHandler>();

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<UpdateApplicationHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(UpdateApplicationHandler handler)
   {
      handler.onUpdateApplication(this);
   }

   /**
    * @return the appId
    */
   public String getAppId()
   {
      return appId;
   }

   /**
    * @return the appTitle
    */
   public String getAppTitle()
   {
      return appTitle;
   }

}

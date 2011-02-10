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
package org.exoplatform.ide.extension.netvibes.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired, when try to do deploy UWA widget to Netvibes Ecosystem.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 29, 2010 $
 *
 */
public class DeployUwaWidgetEvent extends GwtEvent<DeployUwaWidgetHandler>
{
   /**
    * Type class used to register events with the HandlerManager. 
    */
   public static final GwtEvent.Type<DeployUwaWidgetHandler> TYPE = new GwtEvent.Type<DeployUwaWidgetHandler>();
   
   
   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<DeployUwaWidgetHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(DeployUwaWidgetHandler handler)
   {
      handler.onDeployUwaWidget(this);
   }

}

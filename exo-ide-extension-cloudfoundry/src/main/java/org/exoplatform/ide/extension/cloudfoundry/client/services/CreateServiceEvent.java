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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to create service.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 16, 2012 12:31:52 PM anya $
 * 
 */
public class CreateServiceEvent extends GwtEvent<CreateServiceHandler>
{
   /**
    * Type, used to register the event.
    */
   public static final GwtEvent.Type<CreateServiceHandler> TYPE = new GwtEvent.Type<CreateServiceHandler>();

   /**
    * Handler for successful provisioned service creation.
    */
   private ProvisionedServiceCreatedHandler provisionedServiceCreatedHandler;

   /**
    * @param provisionedServiceCreatedHandler handler for successful provisioned service creation
    */
   public CreateServiceEvent(ProvisionedServiceCreatedHandler provisionedServiceCreatedHandler)
   {
      this.provisionedServiceCreatedHandler = provisionedServiceCreatedHandler;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<CreateServiceHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(CreateServiceHandler handler)
   {
      handler.onCreateService(this);
   }

   /**
    * @return the provisionedServiceCreatedHandler
    */
   public ProvisionedServiceCreatedHandler getProvisionedServiceCreatedHandler()
   {
      return provisionedServiceCreatedHandler;
   }
}

/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.jcrservice.event;

import org.exoplatform.ideall.client.model.jcrservice.bean.RepositoryServiceConfiguration;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RepositoryConfigurationReceivedEvent extends GwtEvent<RepositoryConfigurationReceivedHandler>
{

   public static final GwtEvent.Type<RepositoryConfigurationReceivedHandler> TYPE =
      new GwtEvent.Type<RepositoryConfigurationReceivedHandler>();

   private RepositoryServiceConfiguration configuration;

   public RepositoryConfigurationReceivedEvent(RepositoryServiceConfiguration configuration)
   {
      this.configuration = configuration;
   }

   public RepositoryServiceConfiguration getConfiguration()
   {
      return configuration;
   }

   @Override
   protected void dispatch(RepositoryConfigurationReceivedHandler handler)
   {
      handler.onRepositoryConfigurationReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RepositoryConfigurationReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

}

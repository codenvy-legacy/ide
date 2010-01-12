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
package org.exoplatform.ideall.client.model.jcrservice;

import org.exoplatform.gwt.commons.rest.AsyncRequest;
import org.exoplatform.gwt.commons.rest.AsyncRequestCallback;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.jcrservice.bean.RepositoryServiceConfiguration;
import org.exoplatform.ideall.client.model.jcrservice.event.RepositoryConfigurationReceivedEvent;
import org.exoplatform.ideall.client.model.jcrservice.marshal.RepositoryServiceConfigurationUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RepositoryServiceImpl extends RepositoryService
{

   public static final String CONTEXT = "/jcr-service";

   public static final String URL_REPOSITORYSERVICECONFIG = CONTEXT + "/repository-service-configuration";

   private HandlerManager eventBus;

   public RepositoryServiceImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   @Override
   public void getRepositoryServiceConfiguration()
   {
      String url = Configuration.getInstance().getContext() + URL_REPOSITORYSERVICECONFIG;

      RepositoryServiceConfiguration configuration = new RepositoryServiceConfiguration();

      RepositoryConfigurationReceivedEvent event = new RepositoryConfigurationReceivedEvent(configuration);
      RepositoryServiceConfigurationUnmarshaller unmarshaller =
         new RepositoryServiceConfigurationUnmarshaller(configuration);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

}

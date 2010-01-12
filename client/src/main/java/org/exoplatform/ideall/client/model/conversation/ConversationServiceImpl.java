/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.model.conversation;

import org.exoplatform.gwt.commons.rest.AsyncRequest;
import org.exoplatform.gwt.commons.rest.AsyncRequestCallback;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.conversation.event.UserInfoReceivedEvent;
import org.exoplatform.ideall.client.model.conversation.marshal.UserInfoUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ConversationServiceImpl extends ConversationService
{

   private static final String CONTEXT = "/conversation-state";

   private static final String WHOAMI = "/whoami";

   private HandlerManager eventBus;

   public ConversationServiceImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   @Override
   public void getUserInfo()
   {
      String url = Configuration.getInstance().getContext() + CONTEXT + WHOAMI;

      UserInfo userInfo = new UserInfo(UserInfo.DEFAULT_USER_NAME);
      UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller(userInfo);
      UserInfoReceivedEvent event = new UserInfoReceivedEvent(userInfo);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

}

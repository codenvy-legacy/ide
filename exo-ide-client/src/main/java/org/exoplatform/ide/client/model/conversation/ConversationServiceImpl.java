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
package org.exoplatform.ide.client.model.conversation;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.model.conversation.marshal.UserInfoUnmarshaller;

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

   private static final String CONVERSATION_SERVICE_CONTEXT = "/conversation-state";

   private static final String WHOAMI = "/whoami";

   private HandlerManager eventBus;

   private Loader loader;
   
   private String restServiceContext;

   public ConversationServiceImpl(HandlerManager eventBus, Loader loader, String restServiceContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;
   }

   @Override
   public void getUserInfo()
   {
      String url = restServiceContext + CONVERSATION_SERVICE_CONTEXT + WHOAMI;

      UserInfo userInfo = new UserInfo(UserInfo.DEFAULT_USER_NAME);
      UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller(userInfo);
      UserInfoReceivedEvent event = new UserInfoReceivedEvent(userInfo);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}

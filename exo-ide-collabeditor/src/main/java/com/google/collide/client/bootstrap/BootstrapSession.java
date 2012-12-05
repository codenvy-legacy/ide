// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.client.bootstrap;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;

/**
 * Bootstrap information for the client.
 *
 * <p>This gets injected to the page with the initial page request.
 *
 * <p>It contains a description of the files contained in the project workspace,
 * as well as user identification information and a sessionID token that is sent
 * along with subsequent requests.
 *
 */
public final class BootstrapSession implements UserInfoReceivedHandler
{

   private static BootstrapSession instance;

   private String userUd;

   /**
    * Use this method to obtain an instance of the Session object.
    */
   public static BootstrapSession getBootstrapSession()
   {
      return instance;
   };

   public BootstrapSession(HandlerManager eventBus)
   {
      eventBus.addHandler(UserInfoReceivedEvent.TYPE, this);
      instance = this;
   }

   /**
    * @return The active client ID for the current tab.
    */
   public String getActiveClientId()
   {
      return userUd;
   }

   /**
    * @return The user's handle. This is his name or email.
    */
   public String getUsername()
   {
      return userUd;
   }

   /**
    * @return The user's unique ID (obfuscated GAIA ID).
    */
   public String getUserId()
   {
      return userUd;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userUd = event.getUserInfo().getName();
   }
}

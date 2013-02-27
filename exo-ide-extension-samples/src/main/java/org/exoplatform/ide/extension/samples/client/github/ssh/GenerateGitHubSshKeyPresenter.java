/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.github.ssh;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.samples.client.oauth.OAuthLoginEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.RefreshKeysEvent;
import org.exoplatform.ide.git.client.github.GitHubClientService;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class GenerateGitHubSshKeyPresenter implements UserInfoReceivedHandler, GenerateGitHubKeyHandler
{
   private UserInfo userInfo;

   EmptyLoader loader;

   public GenerateGitHubSshKeyPresenter()
   {
      IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
      IDE.addHandler(GenerateGitHubKeyEvent.TYPE, this);
      loader = new EmptyLoader();
   }

   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      this.userInfo = event.getUserInfo();
   }

   @Override
   public void onGenerateGitHubSshKey(GenerateGitHubKeyEvent event)
   {
      if (userInfo != null)
      {
         loader.show();
         getToken(userInfo.getName());
      }
   }

   private void generateGitHubKey()
   {
      try
      {
         AsyncRequestCallback<Void> callback = new AsyncRequestCallback<Void>()
         {
            @Override
            protected void onSuccess(Void result)
            {
               loader.hide();
               IDE.fireEvent(new RefreshKeysEvent());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               loader.hide();
               IDE.fireEvent(new OAuthLoginEvent());
            }
         };

         String url = Utils.getRestContext() + "/ide/github/ssh/generate";
         AsyncRequest.build(RequestBuilder.POST, url).loader(new EmptyLoader()).send(callback);
      }
      catch (RequestException e)
      {
         loader.hide();
         Dialogs.getInstance().showError("Upload key to github failed.");
      }
   }

   private void getToken(String user)
   {
      try
      {
         GitHubClientService.getInstance().getUserToken(user,
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {

               @Override
               protected void onSuccess(StringBuilder result)
               {
                  if (result == null || result.toString().isEmpty())
                  {
                     loader.hide();
                     processUnauthorized();
                  }
                  else
                  {
                     generateGitHubKey();
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  processUnauthorized();
               }
            });
      }
      catch (RequestException e)
      {
         loader.hide();
         IDE.fireEvent(new OAuthLoginEvent());
      }
   }

   private void processUnauthorized()
   {
      IDE.fireEvent(new OAuthLoginEvent());
   }
}

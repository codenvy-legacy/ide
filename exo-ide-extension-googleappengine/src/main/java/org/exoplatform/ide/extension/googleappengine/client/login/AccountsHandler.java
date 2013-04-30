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
package org.exoplatform.ide.extension.googleappengine.client.login;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.googleappengine.client.GaeTools;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 14, 2012 11:44:01 AM anya $
 */
public class AccountsHandler implements LogoutHandler {

    public AccountsHandler() {
        IDE.addHandler(LogoutEvent.TYPE, this);
        isLogged();
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.login.LogoutHandler#onLogout(org.exoplatform.ide.extension
     * .googleappengine.client.login.LogoutEvent) */
    @Override
    public void onLogout(LogoutEvent event) {
        try {
            GoogleAppEngineClientService.getInstance().logout(new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION.logoutSuccess(), Type.INFO));
                    IDE.fireEvent(new SetLoggedUserStateEvent(false));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus()) {
                            IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION.logoutNotLogged(),
                                                          Type.INFO));
                            IDE.fireEvent(new SetLoggedUserStateEvent(false));
                            return;
                        }
                    }
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public static void isLogged() {
        AutoBean<GaeUser> user = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.user();
        AutoBeanUnmarshaller<GaeUser> unmarshaller = new AutoBeanUnmarshaller<GaeUser>(user);
        try {
            GoogleAppEngineClientService.getInstance().getLoggedUser(
                    new GoogleAppEngineAsyncRequestCallback<GaeUser>(unmarshaller) {

                        @Override
                        protected void onSuccess(GaeUser result) {
                            IDE.fireEvent(new SetLoggedUserStateEvent(GaeTools.isAuthenticatedInAppEngine(result.getToken())));
                        }

                        /**
                         * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback#onFailure(java
                         * .lang.Throwable)
                         */
                        @Override
                        protected void onFailure(Throwable exception) {
                        }
                    });
        } catch (RequestException e) {
        }
    }

}

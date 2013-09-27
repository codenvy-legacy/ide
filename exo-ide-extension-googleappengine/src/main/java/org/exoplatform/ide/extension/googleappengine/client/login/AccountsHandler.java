/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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

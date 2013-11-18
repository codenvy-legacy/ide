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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.googleappengine.client.GaeTools;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;

/**
 * Presenter for log in Google App Engine operation. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 18, 2012 12:19:01 PM anya $
 */
public class LoginPresenter implements LoginHandler, ViewClosedHandler, JsPopUpOAuthWindow.Callback {
    interface Display extends IsView {
        /**
         * Get Go button click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getGoButton();

        void setLoginLocation(String href);
    }

    private Display display;

    public LoginPresenter() {
        IDE.addHandler(LoginEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /**
     * Bind display with presenter.
     *
     * @param d
     */
    public void bindDisplay(Display d) {
        this.display = d;

        display.getGoButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doLogin();
            }
        });
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginHandler#onLogin(org.exoplatform.ide.extension.googleappengine
     * .client.login.LoginEvent)
     */
    @Override
    public void onLogin(LoginEvent event) {
        new JsPopUpOAuthWindow().withOauthProvider("google")
                                .withScope("https://www.googleapis.com/auth/appengine.admin")
                                .withCallback(this)
                                .login();
    }

    @Override
    public void oAuthFinished(int authenticationStatus) {
        if (authenticationStatus == 2) {
            IDE.fireEvent(new SetLoggedUserStateEvent(true));
        }
    }

    private void doLogin() {
        isUserLogged();
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void isUserLogged() {
        AutoBean<GaeUser> user = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.user();
        AutoBeanUnmarshaller<GaeUser> unmarshaller = new AutoBeanUnmarshaller<GaeUser>(user);
        try {
            GoogleAppEngineClientService.getInstance().getLoggedUser(
                    new GoogleAppEngineAsyncRequestCallback<GaeUser>(unmarshaller) {
                        @Override
                        protected void onSuccess(GaeUser result) {
                            boolean isLogged =
                                    GaeTools.isAuthenticatedInAppEngine(result.getToken());
                            IDE.fireEvent(new SetLoggedUserStateEvent(isLogged));
                            if (!isLogged) {
                                if (display != null) {
                                    IDE.getInstance().closeView(display.asView().getId());
                                }
                            }
                        }

                        /**
                         * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback#onFailure(java
                         *      .lang.Throwable)
                         */
                        @Override
                        protected void onFailure(Throwable exception) {
                            if (exception instanceof UnauthorizedException) {
                                IDE.fireEvent(new ExceptionThrownEvent(exception));
                                return;
                            }
                            IDE.fireEvent(new SetLoggedUserStateEvent(true));
                            if (display != null) {
                                IDE.getInstance().closeView(display.asView().getId());
                            }
                            // Window.open(url, "_blank", null);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

}

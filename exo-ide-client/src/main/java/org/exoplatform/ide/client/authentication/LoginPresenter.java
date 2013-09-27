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
package org.exoplatform.ide.client.authentication;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Frame;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.util.Utils;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 */
public class LoginPresenter implements ViewClosedHandler, ExceptionThrownHandler, InitializeServicesHandler,
                                       UserInfoReceivedHandler {

    /** LoginDialog's display. */
    public interface Display extends IsView {

        HasClickHandlers getLoginButton();

        HasClickHandlers getLoginGoogleButton();

        HasClickHandlers getLoginGitHubButton();

        HasClickHandlers getCancelButton();

        void setLoginButtonEnabled(boolean enabled);

        TextFieldItem getLoginField();

        TextFieldItem getPasswordField();

    }

    /** Display's instance. */
    private Display display;

    /** Login. */
    private String login;

    /** Password. */
    private String password;

    /** Creates a new instance of LoginDialog. */
    public LoginPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ExceptionThrownEvent.TYPE, this);
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.addHandler(UserInfoReceivedEvent.TYPE, this);

        /*
         * // Uncomment this to show Image at the top of IDE to see how the Login Window looks. Image showLoginImage = new
         * Image(IDEImageBundle.INSTANCE.browser()); showLoginImage.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
         * RootPanel.get().add(showLoginImage, 300, 0); showLoginImage.addClickHandler(new ClickHandler() {
         * @Override public void onClick(ClickEvent event) { showLoginDialog(null); } });
         */

    }

    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        // nothing to do
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /**
     * Creates and shows new Login View.
     *
     * @param asyncRequest
     */
    private void showLoginDialog(final AsyncRequest asyncRequest, final Response response) {
        Window window = new Window("Login");
        window.add(new Frame(Utils.getRestContext() + Utils.getWorkspaceName() + "/configuration/init"));
        window.setHeight(435);
        window.setWidth(580);
        window.showCentered();

//        if (display != null) {
//            return;
//        }
//
//        display = GWT.create(Display.class);
//
//
//        display.getLoginGoogleButton().addClickHandler(new ClickHandler() {
//
//            @Override
//            public void onClick(ClickEvent event) {
//                String authUrl = Utils.getAuthorizationContext()
//                                 + "/" + Utils.getWorkspaceName() + "/oauth/authenticate?oauth_provider=google&mode=federated_login"
//                                 + "&scope=https://www.googleapis.com/auth/userinfo.profile"
//                                 + "&scope=https://www.googleapis.com/auth/userinfo.email"
//                                 + "&redirect_after_login="
//                                 + Utils.getAuthorizationPageURL();
//
//                JsPopUpOAuthWindow authWindow = new JsPopUpOAuthWindow(authUrl, Utils.getAuthorizationErrorPageURL(), 980, 500, null);
//                authWindow.loginWithOAuth();
//                IDE.getInstance().closeView(display.asView().getId());
//
//            }
//        });
//
//        display.getLoginGitHubButton().addClickHandler(new ClickHandler() {
//
//            @Override
//            public void onClick(ClickEvent event) {
//                String authUrl = Utils.getAuthorizationContext()
//                                 + "/" + Utils.getWorkspaceName() + "/oauth/authenticate?oauth_provider=github&mode=federated_login"
//                                 + "&scope=user&scope=repo&redirect_after_login="
//                                 + Utils.getAuthorizationPageURL();
//                JsPopUpOAuthWindow authWindow = new JsPopUpOAuthWindow(authUrl, Utils.getAuthorizationErrorPageURL(), 980, 500, null);
//                authWindow.loginWithOAuth();
//                IDE.getInstance().closeView(display.asView().getId());
//
//            }
//        });
//
//        display.getLoginButton().addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                try {
//                    doLogin(asyncRequest);
//                } catch (Exception e) {
//                    Log.info("Exception > " + e.getMessage());
//                }
//            }
//        });
//
//        display.getCancelButton().addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                IDE.getInstance().closeView(display.asView().getId());
//            }
//        });
//
//        KeyPressHandler textFieldsKeyPressHandler = new KeyPressHandler() {
//            @Override
//            public void onKeyPress(KeyPressEvent event) {
//                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
//                    try {
//                        doLogin(asyncRequest);
//                    } catch (Exception e) {
//                        Log.info("Exception > " + e.getMessage());
//                    }
//                }
//            }
//        };
//
//        display.getLoginField().addValueChangeHandler(valueChangeHandler);
//        display.getLoginField().addKeyPressHandler(textFieldsKeyPressHandler);
//        display.getPasswordField().addValueChangeHandler(valueChangeHandler);
//        display.getPasswordField().addKeyPressHandler(textFieldsKeyPressHandler);
//
//        display.getLoginField().setValue(login);
//        display.getPasswordField().setValue(password);
//
//        if (!GWT.isScript() && login == null && password == null) {
//            display.getLoginField().setValue("ide");
//            display.getPasswordField().setValue("codenvy123");
//        }
//
//        checkForLoginButtonEnabled();
//
//        IDE.getInstance().openView(display.asView());
    }

    /** Handle changing of the text in text fields. */
    ValueChangeHandler<String> valueChangeHandler = new ValueChangeHandler<String>() {
        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
            checkForLoginButtonEnabled();
        }
    };

    /** Checks for text in the text fields and enables or disables Login button. */
    private void checkForLoginButtonEnabled() {
        if (display.getLoginField().getValue() == null || display.getLoginField().getValue().trim().isEmpty()
            || display.getPasswordField().getValue() == null || display.getPasswordField().getValue().trim().isEmpty()) {
            display.setLoginButtonEnabled(false);
            return;
        }

        display.setLoginButtonEnabled(true);

    }

    /** Do Login. */
    private void doLogin(AsyncRequest asyncRequest) {
        login = display.getLoginField().getValue().trim();
        password = display.getPasswordField().getValue().trim();
        display.getLoginField().setValue(login);
        display.getPasswordField().setValue(password);

        hiddenLoadAuthorizationPage(asyncRequest);
    }

    private void hiddenLoadAuthorizationPage(final AsyncRequest asyncRequest) {
        String authorizationPageURL = Utils.getAuthorizationPageURL();
        try {
            if (authorizationPageURL == null) {
                throw new Exception(org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.confMissingVariable("authorizationPageURL"));
            }
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, authorizationPageURL);
            requestBuilder.setCallback(new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    sendLoginRequest(asyncRequest);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    Dialogs.getInstance().showError("Can not log in!");
                }
            });
            requestBuilder.send();

        } catch (Exception e) {
            Dialogs.getInstance().showError("Can not log in!");

            Log.info("Exception > " + e.getMessage());
        }
    }

    private void sendLoginRequest(final AsyncRequest asyncRequest) {
        StringBuffer postBuilder = new StringBuffer();
        postBuilder.append("j_username=");
        postBuilder.append(URL.encodeQueryString(login));
        postBuilder.append("&j_password=");
        postBuilder.append(URL.encodeQueryString(password));

        try {
            String securityCheckURL = Utils.getSecurityCheckURL();
            if (securityCheckURL == null) {
                throw new Exception(org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.confMissingVariable("securityCheckURL"));
            }
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, securityCheckURL);
            requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded");
            requestBuilder.sendRequest(postBuilder.toString(), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    IDE.getInstance().closeView(display.asView().getId());
                    if (asyncRequest != null) {
                        try {
                            asyncRequest.send(asyncRequest.getCallback());
                        } catch (RequestException e) {
                            Dialogs.getInstance().showError("Can not log in!");
                        }
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    Dialogs.getInstance().showError("Can not log in!");
                }
            });

        } catch (Exception e) {
            Dialogs.getInstance().showError("Can not log in!");
            Log.info("Exception > " + e.getMessage());
        }
    }

    @Override
    public void onError(ExceptionThrownEvent event) {
        Throwable exception = event.getException();
        if (exception instanceof UnauthorizedException) {
            UnauthorizedException unauthorizedException = (UnauthorizedException)exception;
            AsyncRequest asyncRequest = unauthorizedException.getRequest();
            showLoginDialog(asyncRequest, unauthorizedException.getResponse());
            return;
        }
    }

    @Override
    public void onUserInfoReceived(UserInfoReceivedEvent event) {
        login = event.getUserInfo().getName();
    }

}

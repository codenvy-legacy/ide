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
package org.exoplatform.ide.extension.appfog.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.AppfogLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;

/**
 * Presenter for login view. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class LoginPresenter implements LoginHandler, ViewClosedHandler {
    interface Display extends IsView {
        /**
         * Get login button click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getLoginButton();

        /**
         * Get cancel button click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get email field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getEmailField();

        /**
         * Get password field.
         *
         * @return {@link HasValue}
         */
        TextFieldItem getPasswordField();

        /**
         * Get target select item.
         *
         * @return
         */
        HasValue<String> getTargetSelectField();

        /**
         * Get the label, where error message will be displayed.
         *
         * @return
         */
        HasValue<String> getErrorLabelField();

        /**
         * Change the enable state of the login button.
         *
         * @param enabled
         */
        void enableLoginButton(boolean enabled);

        /** Give focus to login field. */
        void focusInEmailField();

        /**
         * Set the list of available targets.
         *
         * @param target
         */
        void setTargetValues(String target);
    }

    private static final AppfogLocalizationConstant lb = AppfogExtension.LOCALIZATION_CONSTANT;

    private Display display;

    private LoggedInHandler loggedIn;

    private LoginCanceledHandler loginCanceled;

    /** The last server, that user logged in. */
    private String server;

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

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (loginCanceled != null) {
                    loginCanceled.onLoginCanceled();
                }
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getLoginButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doLogin();
            }
        });

        display.getEmailField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableLoginButton(isFieldsFullFilled());
            }
        });

        display.getPasswordField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableLoginButton(isFieldsFullFilled());
            }
        });

        display.getPasswordField().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 && isFieldsFullFilled()) {
                    doLogin();
                }
            }
        });

    }

    /**
     * Check whether necessary fields are fullfilled.
     *
     * @return if <code>true</code> all necessary fields are fullfilled
     */
    private boolean isFieldsFullFilled() {
        return (display.getEmailField().getValue() != null && !display.getEmailField().getValue().isEmpty()
                && display.getPasswordField().getValue() != null && !display.getPasswordField().getValue().isEmpty());
    }

    @Override
    public void onLogin(LoginEvent event) {
        loggedIn = event.getLoggedIn();
        loginCanceled = event.getLoginCanceled();
        if (event.getLoginUrl() != null) {
            server = event.getLoginUrl();
            if (!server.startsWith("http")) {
                server = "https://" + server;
            }
        }
        if (display == null) {
            Display display = GWT.create(Display.class);
            bindDisplay(display);
            IDE.getInstance().openView(display.asView());
            display.enableLoginButton(false);
            display.focusInEmailField();
            display.setTargetValues(AppfogExtension.DEFAULT_SERVER);
            getSystemInformation();
        }
    }

    /** Get Cloud Foundry system information to fill the login field, if user is logged in. */
    protected void getSystemInformation() {
        try {
            AutoBean<SystemInfo> systemInfo = AppfogExtension.AUTO_BEAN_FACTORY.systemInfo();
            AutoBeanUnmarshaller<SystemInfo> unmarshaller = new AutoBeanUnmarshaller<SystemInfo>(systemInfo);
            AppfogClientService.getInstance().getSystemInfo(AppfogExtension.DEFAULT_SERVER,
                                                            new AppfogAsyncRequestCallback<SystemInfo>(unmarshaller, loggedIn, loginCanceled) {
                                                                @Override
                                                                protected void onSuccess(SystemInfo result) {
                                                                    display.getEmailField().setValue(result.getUser());
                                                                }
                                                            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    protected void doLogin() {
        final String enteredServer = display.getTargetSelectField().getValue();
        final String email = display.getEmailField().getValue();
        final String password = display.getPasswordField().getValue();

        try {
            AppfogClientService.getInstance().login(enteredServer, email, password,
                                                    new AsyncRequestCallback<String>() {

                                                        @Override
                                                        protected void onSuccess(String result) {
                                                            server = enteredServer;
                                                            IDE.fireEvent(new OutputEvent(lb.loginSuccess(), Type.INFO));
                                                            if (loggedIn != null) {
                                                                loggedIn.onLoggedIn();
                                                            }
                                                            IDE.getInstance().closeView(display.asView().getId());
                                                        }

                                                        /**
                                                         * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure
                                                         * (java.lang.Throwable)
                                                         */
                                                        @Override
                                                        protected void onFailure(Throwable exception) {
                                                            if (exception instanceof ServerException) {
                                                                ServerException serverException = (ServerException)exception;
                                                                if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                                                                    && serverException.getMessage() != null
                                                                    && serverException.getMessage().contains("Can't access target.")) {
                                                                    display.getErrorLabelField().setValue(lb.loginViewErrorUnknownTarget());
                                                                    return;
                                                                } else if (HTTPStatus.OK != serverException.getHTTPStatus() &&
                                                                           serverException.getMessage() != null
                                                                           && serverException.getMessage()
                                                                                             .contains("Operation not permitted")) {
                                                                    display.getErrorLabelField()
                                                                           .setValue(lb.loginViewErrorInvalidUserOrPassword());
                                                                    return;
                                                                } else if (HTTPStatus.FORBIDDEN == serverException.getHTTPStatus()
                                                                           && serverException.getMessage() != null
                                                                           && serverException.getMessage().contains("Invalid Password")) {
                                                                    display.getErrorLabelField()
                                                                           .setValue(lb.loginViewErrorInvalidUserOrPassword());
                                                                    return;
                                                                }
                                                                // otherwise will be called method from superclass.
                                                            }
                                                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                        }
                                                    });
        } catch (RequestException e) {
            Window.alert(e.getMessage());
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            loggedIn = null;
            loginCanceled = null;
        }
    }
}

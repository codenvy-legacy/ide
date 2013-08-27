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
package org.exoplatform.ide.extension.aws.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 4:10:17 PM anya $
 */
public class LoginPresenter implements LoginHandler, ViewClosedHandler {
    interface Display extends IsView {
        TextFieldItem getAccessKey();

        TextFieldItem getSecretKey();

        HasClickHandlers getLoginButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getLoginResult();

        void enableLoginButton(boolean enable);

        void focusInAccessKey();

    }

    private Display display;

    private LoggedInHandler loggedInHandler;

    private LoginCanceledHandler loginCanceledHandler;

    public LoginPresenter() {
        IDE.getInstance().addControl(new SwitchAccountControl());

        IDE.addHandler(LoginEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
                if (loginCanceledHandler != null) {
                    loginCanceledHandler.onLoginCanceled();
                    loginCanceledHandler = null;
                }
            }
        });

        display.getLoginButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doLogin();
            }
        });

        display.getAccessKey().addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 && isFieldsFullFilled()) {
                    doLogin();
                }
            }
        });

        display.getSecretKey().addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 && isFieldsFullFilled()) {
                    doLogin();
                }
            }
        });

        display.getAccessKey().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableLoginButton(isFieldsFullFilled());
            }
        });

        display.getSecretKey().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableLoginButton(isFieldsFullFilled());
            }
        });
    }

    /** @return {@link Boolean} <code>true</code> if fields are full filled */
    private boolean isFieldsFullFilled() {
        return (display.getAccessKey().getValue() != null && !display.getAccessKey().getValue().isEmpty()
                && display.getSecretKey().getValue() != null && !display.getSecretKey().getValue().isEmpty());
    }

    /** Perform login operation. */
    public void doLogin() {
        try {
            BeanstalkClientService.getInstance().login(display.getAccessKey().getValue(),
                                                       display.getSecretKey().getValue(), new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.getInstance().closeView(display.asView().getId());
                    IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.loginSuccess(), Type.INFO));
                    if (loggedInHandler != null) {
                        loggedInHandler.onLoggedIn();
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    display.getLoginResult().setValue(AWSExtension.LOCALIZATION_CONSTANT.loginErrorInvalidKeyValue());
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.login.LoginHandler#onLogin(org.exoplatform.ide.extension.aws.client.login.LoginEvent)
     */
    @Override
    public void onLogin(LoginEvent event) {
        this.loggedInHandler = event.getLoggedInHandler();
        this.loginCanceledHandler = event.getLoginCanceledHandler();

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.getLoginResult().setValue("");
        display.enableLoginButton(false);
    }
}

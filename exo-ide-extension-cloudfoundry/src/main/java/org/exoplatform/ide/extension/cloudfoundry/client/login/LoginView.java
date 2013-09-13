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
package org.exoplatform.ide.extension.cloudfoundry.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.PasswordTextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * View for login to CloudFoundry.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 26, 2011 10:54:35 AM anya $
 */
public class LoginView extends ViewImpl implements LoginPresenter.Display {
    private static final String ID = "ideLoginView";

    private static final int WIDTH = 400;

    private static final int HEIGHT = 180;

    private static final String LOGIN_BUTTON_ID = "ideLoginViewLoginButton";

    private static final String CANCEL_BUTTON_ID = "ideLoginViewCancelButton";

    private static final String EMAIL_FIELD_ID = "ideLoginViewEmailField";

    private static final String PASSWORD_FIELD_ID = "ideLoginViewPasswordField";

    private static final String TARGET_FIELD_ID = "ideLoginViewTargetField";

    /** UI binder for this view. */
    private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

    interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
    }

    /** Field to select target (domain, server), where to login. */
    @UiField
    ComboBoxField targetField;

    /** Email field. */
    @UiField
    TextBox emailField;

    /** Password field. */
    @UiField
    PasswordTextInput passwordField;

    /** Login button. */
    @UiField
    ImageButton loginButton;

    /** Cancel button. */
    @UiField
    ImageButton cancelButton;

    @UiField
    Label errLabel;

    public LoginView() {
        super(ID, ViewType.MODAL, CloudFoundryExtension.LOCALIZATION_CONSTANT.loginViewTitle(), null, WIDTH, HEIGHT,
              false);
        add(uiBinder.createAndBindUi(this));

        targetField.setName(TARGET_FIELD_ID);
        emailField.setName(EMAIL_FIELD_ID);
        passwordField.setName(PASSWORD_FIELD_ID);
        loginButton.setButtonId(LOGIN_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#getLoginButton() */
    @Override
    public HasClickHandlers getLoginButton() {
        return loginButton;
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#getEmailField() */
    @Override
    public HasValue<String> getEmailField() {
        return emailField;
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#getPasswordField() */
    @Override
    public TextFieldItem getPasswordField() {
        return passwordField;
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#enableLoginButton(boolean) */
    @Override
    public void enableLoginButton(boolean enabled) {
        loginButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#focusInEmailField() */
    @Override
    public void focusInEmailField() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                emailField.setFocus(true);
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.login.LoginPresenter.Display#getTargetSelectField() */
    @Override
    public HasValue<String> getTargetSelectField() {
        return targetField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.login.LoginPresenter.Display#setTargetValues(java.lang.String[]) */
    @Override
    public void setTargetValues(String[] targets) {
        targetField.setValueMap(targets);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.login.LoginPresenter.Display#getErrorLabelField() */
    @Override
    public HasValue<String> getErrorLabelField() {
        return errLabel;
    }
}

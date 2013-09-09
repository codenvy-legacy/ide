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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.PasswordTextInput;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoginView extends ViewImpl implements org.exoplatform.ide.client.authentication.LoginPresenter.Display {

    private static final String ID = "ideLoginView";

    private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

    interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
    }

    @UiField
    ImageButton loginButton;

    @UiField
    ImageButton loginGoogleButton;

    @UiField
    ImageButton loginGitHubButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    TextInput loginField;

    @UiField
    PasswordTextInput passwordField;

    @UiField
    VerticalPanel verticalPanel;

    public LoginView() {
        super(ID, "modal", "Login", new Image(IDEImageBundle.INSTANCE.ok()), 540, 290);
        add(uiBinder.createAndBindUi(this));
        passwordField.setName("ideLoginViewPasswordField");
        loginGoogleButton.setImage(new Image("http://www.google.com/favicon.ico"));
        loginGitHubButton.setImage(new Image(IDEImageBundle.INSTANCE.gitHubIconSmall()));
        verticalPanel.add(createLogoLayout());
    }

    @Override
    public HasClickHandlers getLoginButton() {
        return loginButton;
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    @Override
    public void setLoginButtonEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
    }

    @Override
    public TextFieldItem getLoginField() {
        return loginField;
    }

    @Override
    public TextFieldItem getPasswordField() {
        return passwordField;
    }

    @Override
    public HasClickHandlers getLoginGoogleButton() {
        return loginGoogleButton;
    }

    @Override
    public HasClickHandlers getLoginGitHubButton() {
        return loginGitHubButton;
    }

    private HorizontalPanel createLogoLayout() {
        HorizontalPanel logoLayout = new HorizontalPanel();
        logoLayout.setWidth("100%");
        logoLayout.setHeight(84 + "px");
        String style = logoLayout.getElement().getAttribute("style");
        style += "background : url(\"" + Images.Logos.ABOUT_BG + "\") repeat-x scroll 0 0 transparent;";
        logoLayout.getElement().setAttribute("style", style);
        Image logoImage = new Image();
        logoImage.setUrl(Images.Logos.ABOUT_LOGO);
        logoImage.getElement().setAttribute("style", "padding-left: 35px; padding-top: 34px;");
        logoLayout.add(logoImage);
        return logoLayout;
    }
}

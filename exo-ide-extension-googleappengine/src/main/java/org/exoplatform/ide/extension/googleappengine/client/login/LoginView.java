/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.LinkButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

/**
 * View for log in Google App Engine.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 18, 2012 12:36:27 PM anya $
 */
public class LoginView extends ViewImpl implements LoginPresenter.Display {
    private static final String ID = "ideLoginView";

    private static final int WIDTH = 440;

    private static final int HEIGHT = 170;

    private static final String LABEL_ID = "ideLoginViewLabel";

    private static final String GO_BUTTON_ID = "ideLoginViewGoButton";

    private static final String CANCEL_BUTTON_ID = "ideLoginViewCancelButton";

    /** UI binder for this view. */
    private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

    interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
    }

    @UiField
    Label label;

    /** Go button. */
    @UiField
    LinkButton goButton;

    public LoginView() {
        super(ID, ViewType.MODAL, GoogleAppEngineExtension.GAE_LOCALIZATION.loginViewTitle(), new Image(
                GAEClientBundle.INSTANCE.googleAppEngine()), WIDTH, HEIGHT, true);
        add(uiBinder.createAndBindUi(this));
        label.setID(LABEL_ID);
        goButton.setId(GO_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#getGoButton() */
    @Override
    public HasClickHandlers getGoButton() {
        return goButton;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#setLoginLocation(java.lang.String) */
    @Override
    public void setLoginLocation(String href) {
        goButton.setHref(href);
    }
}

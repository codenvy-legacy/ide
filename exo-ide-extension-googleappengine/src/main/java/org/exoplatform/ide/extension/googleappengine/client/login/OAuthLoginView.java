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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 30, 2012 10:28:36 AM anya $
 */
public class OAuthLoginView extends ViewImpl {

    private static final String           ID               = "ideGaeOAuthLoginView";

    private static final int              WIDTH            = 450;

    private static final int              HEIGHT           = 180;

    private static final String           LABEL_ID         = "ideOAuthLoginViewLabel";

    private static final String           AUTH_BUTTON_ID   = "ideOAuthLoginViewAuthButton";

    private static final String           CANCEL_BUTTON_ID = "ideOAuthLoginViewCancelButton";

    private static OAuthLoginViewUiBinder uiBinder         = GWT.create(OAuthLoginViewUiBinder.class);

    interface OAuthLoginViewUiBinder extends UiBinder<Widget, OAuthLoginView> {
    }

    @UiField
    ImageButton oauthButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    Label       label;

    public OAuthLoginView() {
        super(ID, ViewType.MODAL, GoogleAppEngineExtension.GAE_LOCALIZATION.loginOAuthTitle(), new Image(GAEClientBundle.INSTANCE.login()),
              WIDTH, HEIGHT, false);
        Widget widget = uiBinder.createAndBindUi(this);
        label.setID(LABEL_ID);
        oauthButton.setId(AUTH_BUTTON_ID);
        cancelButton.setId(CANCEL_BUTTON_ID);
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(ID);
            }

        });

        oauthButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String authUrl = Utils.getAuthorizationContext()
                                 + "/ide/oauth/authenticate?oauth_provider=google"
                                 + "&scope=https://www.googleapis.com/auth/appengine.admin"
                                 + "&userId=" + IDE.user.getName() + "&redirect_after_login=/ide/" + Utils.getWorkspaceName();
                JsPopUpOAuthWindow authWindow = new JsPopUpOAuthWindow(authUrl, Utils.getAuthorizationErrorPageURL(), 450, 500, null);
                authWindow.loginWithOAuth();
                IDE.getInstance().closeView(ID);
            }
        });
        add(widget);
        IDE.getInstance().openView(this);
    }
}

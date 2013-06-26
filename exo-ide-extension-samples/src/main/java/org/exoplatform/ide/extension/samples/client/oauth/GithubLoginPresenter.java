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
package org.exoplatform.ide.extension.samples.client.oauth;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 30, 2012 10:26:25 AM anya $
 */
public class GithubLoginPresenter implements GithubLoginHandler, ViewClosedHandler, JsPopUpOAuthWindow.JsPopUpOAuthWindowCallback {

    interface Display extends IsView {
        HasClickHandlers getLoginButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getLabel();
    }

    private Display display;

    public GithubLoginPresenter() {
        IDE.addHandler(GithubLoginEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getLoginButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String authUrl = Utils.getAuthorizationContext()
                                 + "/ide/oauth/authenticate?oauth_provider=github"
                                 + "&scope=user&userId=" + IDE.user.getName()
                                 + "&scope=repo&redirect_after_login=/w/" + Utils.getWorkspaceName();
                JsPopUpOAuthWindow authWindow = new JsPopUpOAuthWindow(authUrl, Utils.getAuthorizationErrorPageURL(), 980, 500,
                                                                       GithubLoginPresenter.this);
                authWindow.loginWithOAuth();
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /**
     * @see org.exoplatform.ide.extension.samples.client.oauth.GithubLoginHandler#onGithubLogin(org.exoplatform.ide.extension.samples
     *      .client.oauth.GithubLoginEvent)
     */
    @Override
    public void onGithubLogin(GithubLoginEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }
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

    @Override
    public void oAuthFinished(int authenticationStatus) {
        if (authenticationStatus == 2) {
            IDE.fireEvent(new GithubLoginFinishedEvent());
        }
    }
}

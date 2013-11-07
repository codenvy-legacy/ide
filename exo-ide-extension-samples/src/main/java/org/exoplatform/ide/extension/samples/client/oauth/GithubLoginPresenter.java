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
                                 + "&scope=user&userId=" + IDE.user.getUserId()
                                 + "&scope=repo&redirect_after_login=/ide/" + Utils.getWorkspaceName();
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

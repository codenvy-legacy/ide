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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 30, 2012 10:28:36 AM anya $
 */
public class GithubLoginView extends ViewImpl implements GithubLoginPresenter.Display {

    private static final String            ID               = "ideGithubLoginView";

    private static final int               WIDTH            = 450;

    private static final int               HEIGHT           = 180;

    private static final String            LABEL_ID         = "ideGithubLoginViewLabel";

    private static final String            AUTH_BUTTON_ID   = "ideGithubLoginViewAuthButton";

    private static final String            CANCEL_BUTTON_ID = "ideGithubLoginViewCancelButton";

    private static GithubLoginViewUiBinder uiBinder         = GWT.create(GithubLoginViewUiBinder.class);

    interface GithubLoginViewUiBinder extends UiBinder<Widget, GithubLoginView> {
    }

    @UiField
    ImageButton oauthButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    Label       label;

    public GithubLoginView() {
        super(ID, ViewType.MODAL, SamplesExtension.LOCALIZATION_CONSTANT.loginOAuthTitle(),
              new Image(
                        SamplesClientBundle.INSTANCE.gitHub()), WIDTH, HEIGHT, true);
        add(uiBinder.createAndBindUi(this));
        label.setID(LABEL_ID);
        oauthButton.setId(AUTH_BUTTON_ID);
        cancelButton.setId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.samples.client.oauth.GithubLoginPresenter.Display#getLoginButton() */
    @Override
    public HasClickHandlers getLoginButton() {
        return oauthButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.oauth.GithubLoginPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.oauth.GithubLoginPresenter.Display#getLabel() */
    @Override
    public HasValue<String> getLabel() {
        return label;
    }
}

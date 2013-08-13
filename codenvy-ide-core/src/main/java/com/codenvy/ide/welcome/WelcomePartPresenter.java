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
package com.codenvy.ide.welcome;

import com.codenvy.ide.api.parts.WelcomePart;
import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Simple Welcome Page
 * TODO : reimplement MVP
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class WelcomePartPresenter extends AbstractPartPresenter implements WelcomePart {

    private static WelcomePartPresenterUiBinder uiBinder = GWT.create(WelcomePartPresenterUiBinder.class);

    private Element element;

    interface WelcomePartPresenterUiBinder extends UiBinder<Element, WelcomePartPresenter> {
    }

    @Inject
    public WelcomePartPresenter() {
        element = uiBinder.createAndBindUi(this);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        HTML h = new HTML();
        h.getElement().appendChild(element);
        h.setSize("100%", "100%");
        container.setWidget(h);
    }

    /** @see com.codenvy.ide.api.ui.workspace.PartPresenter#getTitle() */
    @Override
    public String getTitle() {
        return "Welcome";
    }

    /** @see com.codenvy.ide.api.ui.workspace.PartPresenter#getTitleImage() */
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    /** @see com.codenvy.ide.api.ui.workspace.PartPresenter#getTitleToolTip() */
    @Override
    public String getTitleToolTip() {
        return "This is Welcome page, it shows general information about Project Development.";
    }

}

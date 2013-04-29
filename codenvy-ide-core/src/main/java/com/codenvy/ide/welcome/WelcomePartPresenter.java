/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
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

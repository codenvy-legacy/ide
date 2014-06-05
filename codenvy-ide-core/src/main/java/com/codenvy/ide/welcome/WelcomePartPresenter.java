/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.welcome;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.WelcomeItemAction;
import com.codenvy.ide.api.parts.WelcomePart;
import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;


/**
 * Simple Welcome Page.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class WelcomePartPresenter extends AbstractPartPresenter implements WelcomePart, WelcomePartView.ActionDelegate {
    private WelcomePartView          view;
    private Resources                resources;
    private Array<WelcomeItemAction> actions;

    /**
     * Create presenter.
     *
     * @param view
     * @param resources
     */
    @Inject
    public WelcomePartPresenter(WelcomePartView view, Resources resources) {
        this.view = view;
        this.view.setDelegate(this);
        this.resources = resources;
        this.actions = Collections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Welcome";
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.welcome();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "This is Welcome page, it shows general information about Project Development.";
    }

    /** {@inheritDoc} */
    @Override
    public void addItem(@NotNull WelcomeItemAction action) {
        actions.add(action);
        view.addItem(action.getTitle(), action.getCaption(), action.getIcon(), actions.size() - 1);
    }

    /** {@inheritDoc} */
    @Override
    public void onItemClicked(int itemIndex) {
        WelcomeItemAction action = actions.get(itemIndex);
        action.execute();
    }
}
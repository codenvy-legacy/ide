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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.ui.action.ActionGroup;
import com.codenvy.ide.api.ui.action.ActionPlaces;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Manages Toolbar items, changes item state and other.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ToolbarPresenter implements Presenter, ToolbarView.ActionDelegate {
    private ToolbarView view;

    /**
     * Create presenter.
     *
     * @param view
     */
    @Inject
    public ToolbarPresenter(ToolbarView view) {
        this.view = view;
        this.view.setDelegate(this);
    }

    public void bindMainGroup(ActionGroup group) {
        view.setAddSeparatorFirst(true);
        view.setPlace(ActionPlaces.MAIN_TOOLBAR);
        view.setActionGroup(group);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
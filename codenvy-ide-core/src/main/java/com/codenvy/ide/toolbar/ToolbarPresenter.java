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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.ui.action.ActionGroup;
import com.codenvy.ide.api.ui.action.ActionManager;
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
    private ToolbarView   view;
    private ActionManager actionManager;

    /**
     * Create presenter.
     *
     * @param view
     */
    @Inject
    public ToolbarPresenter(ToolbarView view, ActionManager actionManager) {
        this.view = view;
        this.actionManager = actionManager;
        view.setDelegate(this);
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
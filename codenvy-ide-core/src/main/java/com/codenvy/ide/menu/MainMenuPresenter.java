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
package com.codenvy.ide.menu;

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Manages Main Menu Items, their runtime visibility and enabled state.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class MainMenuPresenter implements Presenter, MainMenuView.ActionDelegate {

    private final EventBus eventBus;

    private final MainMenuView view;

    /**
     * Main Menu Presenter requires Event Bus to listen to Expression Changed Event
     * and View implementation
     *
     * @param eventBus
     * @param view
     */
    @Inject
    public MainMenuPresenter(EventBus eventBus, MainMenuView view) {
        this.eventBus = eventBus;
        this.view = view;
        view.setDelegate(this);
        bind();
    }

    /** Bind event handlers to Event Bus */
    private void bind() {
    }


    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

}
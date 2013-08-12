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
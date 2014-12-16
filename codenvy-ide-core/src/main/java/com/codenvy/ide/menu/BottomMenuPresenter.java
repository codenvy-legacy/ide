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
package com.codenvy.ide.menu;

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Manages Bottom Menu Items, their runtime visibility and enabled state.
 *
 * @author Oleksii Orel
 */
@Singleton
public class BottomMenuPresenter implements Presenter, BottomMenuView.ActionDelegate {

    private final BottomMenuView view;

    /**
     * Bottom Menu Presenter requires View implementation
     *
     * @param view
     */
    @Inject
    public BottomMenuPresenter(BottomMenuView view) {
        this.view = view;
        this.view.setDelegate(this);
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
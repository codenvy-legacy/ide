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
package com.codenvy.ide.contexmenu;

import com.codenvy.ide.api.ui.action.ActionPlaces;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Manages Context Menu Items, their runtime visibility and enabled state.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ContextMenuPresenter implements ContextMenuView.ActionDelegate {
    private ContextMenuView view;

    /**
     * Create presenter.
     *
     * @param view
     */
    @Inject
    public ContextMenuPresenter(ContextMenuView view) {
        this.view = view;
        this.view.setDelegate(this);
        this.view.setPlace(ActionPlaces.MAIN_CONTEXT_MENU);
    }

    /**
     * Show menu in specified position.
     *
     * @param x
     *         the x-position on the browser window's client area.
     * @param y
     *         the y-position on the browser window's client area.
     */
    public void show(int x, int y) {
        view.show(x, y);
    }
}
/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.actions.find;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class FindActionPresenter implements Presenter, FindActionView.ActionDelegate {

    private FindActionView view;
    private ActionManager actionManager;

    @Inject
    public FindActionPresenter(FindActionView view, ActionManager actionManager) {
        this.view = view;
        this.actionManager = actionManager;
        view.setDelegate(this);
    }

    @Override
    public void go(AcceptsOneWidget container) {

    }

    public void show() {
        view.show();

    }
}

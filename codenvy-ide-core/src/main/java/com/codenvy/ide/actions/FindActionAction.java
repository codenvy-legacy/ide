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
package com.codenvy.ide.actions;

import com.codenvy.ide.actions.find.FindActionPresenter;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action fo find action action
 * @author Evgen Vidolob
 */
@Singleton
public class FindActionAction extends Action {

    private FindActionPresenter presenter;

    @Inject
    public FindActionAction(FindActionPresenter presenter) {
        super("Find Action","Find Action by name", null);
        this.presenter = presenter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.show();
    }
}

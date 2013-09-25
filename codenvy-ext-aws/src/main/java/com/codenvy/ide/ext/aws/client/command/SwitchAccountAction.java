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
package com.codenvy.ide.ext.aws.client.command;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.aws.client.AWSResource;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to show Switch Account window.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class SwitchAccountAction extends Action {
    private LoginPresenter presenter;

    /**
     * Create action.
     *
     * @param presenter
     * @param resource
     */
    @Inject
    public SwitchAccountAction(LoginPresenter presenter, AWSResource resource) {
        super("Switch Account...", "Login on Amazon Web Services", resource.switchAccount());
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog();
    }
}

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
package com.codenvy.ide.ext.openshift.client.actions;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftResources;
import com.codenvy.ide.ext.openshift.client.domain.CreateDomainPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Command for "PaaS/OpenShift/Change Domain..." action.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class ChangeDomainAction extends Action {

    private CreateDomainPresenter presenter;

    @Inject
    public ChangeDomainAction(CreateDomainPresenter presenter, OpenShiftResources resources) {
        super("Change Domain...", "Change domain name on openshift.com", resources.domainEdit());
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog(null);
    }
}

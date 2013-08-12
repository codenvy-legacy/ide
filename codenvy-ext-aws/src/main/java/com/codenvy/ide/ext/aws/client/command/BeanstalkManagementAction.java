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

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.aws.client.AWSResource;
import com.codenvy.ide.ext.aws.client.beanstalk.manage.ManageApplicationPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to show Manage Application window.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class BeanstalkManagementAction extends Action {
    private ResourceProvider           resourceProvider;
    private ManageApplicationPresenter presenter;

    /**
     * Create action.
     *
     * @param resource
     * @param resourceProvider
     * @param presenter
     */
    @Inject
    public BeanstalkManagementAction(AWSResource resource, ResourceProvider resourceProvider, ManageApplicationPresenter presenter) {
        super("Elastic Beanstalk Application...", "Manage Elastic Beanstalk application", resource.manageApplication());
        this.resourceProvider = resourceProvider;
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation()
         .setVisible(resourceProvider.getActiveProject() != null && resourceProvider.getActiveProject().hasProperty("aws-application"));
    }
}

/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.client.action;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.factory.client.FactoryLocalizationConstant;
import com.codenvy.ide.factory.client.FactoryResources;
import com.codenvy.ide.factory.client.share.ShareFactoryPresenter;
import com.google.inject.Inject;

/**
 * Action for creating Factory and sharing it.
 * 
 * @author Ann Shumilova
 */
public class ShareFactoryAction extends Action {
    private final ResourceProvider      resourceProvider;
    private final AnalyticsEventLogger  eventLogger;
    private final ShareFactoryPresenter presenter;

    @Inject
    public ShareFactoryAction(ShareFactoryPresenter presenter,
                              ResourceProvider resourceProvider,
                              FactoryResources resources,
                              FactoryLocalizationConstant locale,
                              AnalyticsEventLogger eventLogger) {
        super(locale.factoryUrlAction(), locale.factoryUrlAction(), null, resources.shareFactory());
        this.resourceProvider = resourceProvider;
        this.eventLogger = eventLogger;
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Share factory");
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        e.getPresentation().setEnabled(activeProject != null);
    }

}

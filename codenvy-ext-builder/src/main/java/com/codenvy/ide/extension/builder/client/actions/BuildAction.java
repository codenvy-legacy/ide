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
package com.codenvy.ide.extension.builder.client.actions;

import com.codenvy.ide.api.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.codenvy.ide.extension.builder.client.build.BuildProjectPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to build current project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuildAction extends Action {

    private final ResourceProvider      resourceProvider;
    private final BuildProjectPresenter presenter;
    private final AnalyticsEventLogger  eventLogger;

    @Inject
    public BuildAction(BuildProjectPresenter presenter, BuilderResources resources,
                       BuilderLocalizationConstant localizationConstant, ResourceProvider resourceProvider,
                       AnalyticsEventLogger eventLogger) {
        super(localizationConstant.buildProjectControlTitle(),
              localizationConstant.buildProjectControlDescription(), null, resources.build());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Build project");
        presenter.buildActiveProject();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabled(resourceProvider.getActiveProject() != null);
    }
}

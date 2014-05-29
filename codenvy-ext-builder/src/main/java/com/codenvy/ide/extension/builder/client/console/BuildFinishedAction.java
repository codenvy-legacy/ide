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
package com.codenvy.ide.extension.builder.client.console;

import com.codenvy.api.builder.dto.BuilderMetric;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.codenvy.ide.extension.builder.client.build.BuildProjectPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action used to show time when build task finished.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuildFinishedAction extends InfoAction {
    private final BuildProjectPresenter buildProjectPresenter;

    @Inject
    public BuildFinishedAction(BuildProjectPresenter buildProjectPresenter, BuilderResources resources) {
        super("Build Finished At", false, resources);
        this.buildProjectPresenter = buildProjectPresenter;
    }

    @Override
    public void update(ActionEvent e) {
        final Presentation presentation = e.getPresentation();
        final BuilderMetric metric = buildProjectPresenter.getLastBuildEndTime();
        if (metric != null) {
            presentation.putClientProperty(Properties.DATA_PROPERTY, metric.getValue());
            presentation.putClientProperty(Properties.HINT_PROPERTY, metric.getDescription());
        } else {
            presentation.putClientProperty(Properties.DATA_PROPERTY, "--:--:--");
        }
    }
}

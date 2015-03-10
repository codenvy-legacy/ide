/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.builder.client.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.api.build.BuildContext;
import org.eclipse.che.ide.extension.builder.client.BuilderLocalizationConstant;
import org.eclipse.che.ide.util.Config;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Action to open target folder of the last build
 *
 * @author Vitaliy Guliy
 */
@Singleton
public class BrowseTargetFolderAction extends ProjectAction {

    private final String               baseUrl;
    private       BuildContext         buildContext;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public BrowseTargetFolderAction(@Named("restContext") String baseUrl, BuilderLocalizationConstant localizationConstant,
                                    BuildContext buildContext, AnalyticsEventLogger eventLogger) {
        super(localizationConstant.browseTargetFolderActionTitle(), localizationConstant.browseTargetFolderActionDescription(), null);
        this.baseUrl = baseUrl;
        this.buildContext = buildContext;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        if (buildContext.getBuildTaskDescriptor() != null) {
            String url = Window.Location.getProtocol() + "//"
                         + Window.Location.getHost() + baseUrl + "/builder/"
                         + Config.getCurrentWorkspace().getId()
                         + "/browse/"
                         + buildContext.getBuildTaskDescriptor().getTaskId()
                         + "?path=target";
            Window.open(url, "", "");
        }
    }

    @Override
    protected void updateProjectAction(ActionEvent e) {
        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(appContext.getCurrentProject().getBuilder() != null
                                       && !buildContext.isBuilding()
                                       && buildContext.getBuildTaskDescriptor() != null);
    }

}

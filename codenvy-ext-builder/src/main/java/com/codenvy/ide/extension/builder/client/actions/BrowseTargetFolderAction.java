/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.builder.client.actions;

import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.build.BuildContext;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.util.Config;
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

    @Inject
    public BrowseTargetFolderAction(@Named("restContext") String baseUrl,
                            BuilderLocalizationConstant localizationConstant, BuildContext buildContext) {
        super(localizationConstant.browseTargetFolderActionTitle(), localizationConstant.browseTargetFolderActionDescription(), null);
        this.baseUrl = baseUrl;
        this.buildContext = buildContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
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

/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.ext.extruntime.client.actions;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeLocalizationConstant;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeResources;
import com.codenvy.ide.ext.extruntime.client.ExtensionsController;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.extruntime.client.ExtRuntimeExtension.CODENVY_EXTENSION_PROJECT_TYPE;

/**
 * Action to launch Codenvy application with custom extension.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: LaunchAction.java Jul 3, 2013 1:58:47 PM azatsarynnyy $
 */
@Singleton
public class LaunchAction extends Action {

    private final ResourceProvider     resourceProvider;
    private       ExtensionsController controller;

    @Inject
    public LaunchAction(ExtensionsController controller, ExtRuntimeResources resources,
                        ResourceProvider resourceProvider, ExtRuntimeLocalizationConstant localizationConstants) {
        super(localizationConstants.launchExtensionActionText(),
              localizationConstants.launchExtensionActionDescription(), resources.launchApp());
        this.controller = controller;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.buildAndLaunch();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        if (activeProject != null) {
            e.getPresentation()
             .setVisible(activeProject.getDescription().getNatures().contains(CODENVY_EXTENSION_PROJECT_TYPE));
            e.getPresentation().setEnabled(!controller.isAnyAppLaunched());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

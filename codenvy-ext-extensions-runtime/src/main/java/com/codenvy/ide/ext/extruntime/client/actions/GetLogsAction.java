/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.extruntime.client.actions;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeLocalizationConstant;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeResources;
import com.codenvy.ide.ext.extruntime.client.LaunchExtensionController;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.extruntime.client.ExtRuntimeExtension.CODENVY_EXTENSION_PROJECT_TYPE;

/**
 * Action to get logs of launched extension.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GetLogsAction.java Jul 3, 2013 1:58:47 PM azatsarynnyy $
 */
@Singleton
public class GetLogsAction extends Action {

    private LaunchExtensionController controller;
    private final ResourceProvider    resourceProvider;

    @Inject
    public GetLogsAction(LaunchExtensionController controller,
                         ExtRuntimeResources resources,
                         ResourceProvider resourceProvider,
                         ExtRuntimeLocalizationConstant localizationConstants) {
        super(localizationConstants.getExtensionLogsActionText(), localizationConstants.getExtensionLogsDescription(),
              resources.getAppLogs());
        this.controller = controller;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.getLogs();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        if (activeProject != null) {
            e.getPresentation().setVisible(activeProject.getDescription().getNatures().contains(CODENVY_EXTENSION_PROJECT_TYPE));
            e.getPresentation().setEnabled(controller.isAnyAppLaunched());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

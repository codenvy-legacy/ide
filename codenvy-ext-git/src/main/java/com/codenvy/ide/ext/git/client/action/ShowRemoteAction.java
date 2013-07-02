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
package com.codenvy.ide.ext.git.client.action;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.git.client.GitClientResources;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.remote.RemotePresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.git.client.GitExtension.GIT_REPOSITORY_PROP;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class ShowRemoteAction extends Action {
    private RemotePresenter  presenter;
    private ResourceProvider resourceProvider;

    @Inject
    public ShowRemoteAction(RemotePresenter presenter, ResourceProvider resourceProvider, GitClientResources resources,
                            GitLocalizationConstant constant) {
        super(constant.remotesControlTitle(), constant.remotesControlPrompt(), resources.remotes());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();

        e.getPresentation().setVisible(activeProject != null);

        if (activeProject != null) {
            boolean isGitRepository = activeProject.getProperty(GIT_REPOSITORY_PROP) != null;
            e.getPresentation().setEnabled(isGitRepository);
        }
    }
}
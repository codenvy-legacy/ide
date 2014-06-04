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
package com.codenvy.ide.ext.github.client.welcome;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.user.gwt.client.UserServiceClient;
import com.codenvy.api.user.shared.dto.User;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.github.client.GitHubLocalizationConstant;
import com.codenvy.ide.ext.github.client.GitHubResources;
import com.codenvy.ide.ext.github.client.load.ImportPresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The action what provides some actions when import project from github item is clicked.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ImportProjectFromGitHubAction extends Action {
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final ImportPresenter        importPresenter;
    private final UserServiceClient      service;
    private final AnalyticsEventLogger   eventLogger;

    @Inject
    public ImportProjectFromGitHubAction(GitHubLocalizationConstant constant,
                                         GitHubResources resources,
                                         ImportPresenter importPresenter,
                                         UserServiceClient service,
                                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                         AnalyticsEventLogger eventLogger) {
        super(constant.importFromGithubTitle(), constant.welcomeImportText(), null, resources.importFromGithub());
        this.importPresenter = importPresenter;
        this.service = service;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Import project from GitHub");
        service.getCurrentUser(new AsyncRequestCallback<User>(dtoUnmarshallerFactory.newUnmarshaller(User.class)) {
            @Override
            protected void onSuccess(User result) {
                importPresenter.showDialog(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(ImportProjectFromGitHubAction.class, "Can't get user", exception);
            }
        });
    }
}
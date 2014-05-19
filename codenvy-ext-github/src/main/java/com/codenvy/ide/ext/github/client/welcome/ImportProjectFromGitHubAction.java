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
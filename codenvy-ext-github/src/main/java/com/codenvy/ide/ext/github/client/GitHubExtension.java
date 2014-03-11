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
package com.codenvy.ide.ext.github.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.parts.WelcomePart;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.github.client.welcome.ImportProjectAction;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Extension adds GitHub support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "GitHub", version = "3.0.0")
public class GitHubExtension {

    public static final String GITHUB_HOST        = "github.com";
    
    @Inject
    public GitHubExtension(GitHubResources resources,
                           ActionManager actionManager,
                           GitHubLocalizationConstant constant,
                           ImportProjectAction importProjectAction,
                           SshKeyService sshKeyService,
                           GitHubSshKeyProvider gitHubSshKeyProvider) {

        sshKeyService.registerSshKeyProvider(GITHUB_HOST, gitHubSshKeyProvider);
        actionManager.registerAction("importProjectAction", importProjectAction);
        DefaultActionGroup file = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_FILE);
        file.add(importProjectAction);
    }
}
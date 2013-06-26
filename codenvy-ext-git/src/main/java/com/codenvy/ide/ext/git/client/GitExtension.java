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
package com.codenvy.ide.ext.git.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.ext.git.client.action.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_MENU;

/**
 * Extension add Git support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Git Support.", version = "3.0.0")
public class GitExtension {
    public static final String GIT_REPOSITORY_PROP        = "isGitRepository";
    public static final String GIT_GROUP_MAIN_MENU        = "Git";
    public static final String REPOSITORY_GROUP_MAIN_MENU = "GitRepository";
    public static final String COMMAND_GROUP_MAIN_MENU    = "GitCommand";
    public static final String HISTORY_GROUP_MAIN_MENU    = "GitHistory";

    @Inject
    public GitExtension(GitClientResources resources, ActionManager actionManager, CloneRepositoryAction cloneAction,
                        InitRepositoryAction initAction, DeleteRepositoryAction deleteAction, AddToIndexAction addToIndexAction,
                        ResetToCommitAction resetToCommitAction, RemoveFromIndexAction removeFromIndexAction, CommitAction commitAction) {
        resources.gitCSS().ensureInjected();

        DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);

        DefaultActionGroup git = new DefaultActionGroup(GIT_GROUP_MAIN_MENU, true, actionManager);
        actionManager.registerAction(GIT_GROUP_MAIN_MENU, git);
        mainMenu.add(git);

        DefaultActionGroup commandGroup = new DefaultActionGroup(COMMAND_GROUP_MAIN_MENU, false, actionManager);
        actionManager.registerAction(COMMAND_GROUP_MAIN_MENU, commandGroup);
        git.add(commandGroup);
        git.addSeparator();

        DefaultActionGroup historyGroup = new DefaultActionGroup(HISTORY_GROUP_MAIN_MENU, false, actionManager);
        actionManager.registerAction(HISTORY_GROUP_MAIN_MENU, historyGroup);
        git.add(historyGroup);
        git.addSeparator();

        DefaultActionGroup repositoryGroup = new DefaultActionGroup(REPOSITORY_GROUP_MAIN_MENU, false, actionManager);
        actionManager.registerAction(REPOSITORY_GROUP_MAIN_MENU, repositoryGroup);
        git.add(repositoryGroup);

        actionManager.registerAction("GitCloneRepository", cloneAction);
        repositoryGroup.add(cloneAction);
        actionManager.registerAction("GitInitRepository", initAction);
        repositoryGroup.add(initAction);
        actionManager.registerAction("GitDeleteRepository", deleteAction);
        repositoryGroup.add(deleteAction);

        actionManager.registerAction("GitAddToIndex", addToIndexAction);
        commandGroup.add(addToIndexAction);
        actionManager.registerAction("GitResetToCommit", resetToCommitAction);
        commandGroup.add(resetToCommitAction);
        actionManager.registerAction("GitRemoveFromIndexCommit", removeFromIndexAction);
        commandGroup.add(removeFromIndexAction);
        actionManager.registerAction("GitCommit", commitAction);
        commandGroup.add(commitAction);
    }
}
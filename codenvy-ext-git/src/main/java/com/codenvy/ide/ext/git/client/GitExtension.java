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
package com.codenvy.ide.ext.git.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.ext.git.client.action.AddToIndexAction;
import com.codenvy.ide.ext.git.client.action.CommitAction;
import com.codenvy.ide.ext.git.client.action.DeleteRepositoryAction;
import com.codenvy.ide.ext.git.client.action.FetchAction;
import com.codenvy.ide.ext.git.client.action.HistoryAction;
import com.codenvy.ide.ext.git.client.action.InitRepositoryAction;
import com.codenvy.ide.ext.git.client.action.PullAction;
import com.codenvy.ide.ext.git.client.action.PushAction;
import com.codenvy.ide.ext.git.client.action.RemoveFromIndexAction;
import com.codenvy.ide.ext.git.client.action.ResetFilesAction;
import com.codenvy.ide.ext.git.client.action.ResetToCommitAction;
import com.codenvy.ide.ext.git.client.action.ShowBranchesAction;
import com.codenvy.ide.ext.git.client.action.ShowGitUrlAction;
import com.codenvy.ide.ext.git.client.action.ShowMergeAction;
import com.codenvy.ide.ext.git.client.action.ShowRemoteAction;
import com.codenvy.ide.ext.git.client.action.ShowStatusAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.Anchor.BEFORE;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_WINDOW;

/**
 * Extension add Git support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Git", version = "3.0.0")
public class GitExtension {
    public static final String GIT_REPOSITORY_PROP        = "isGitRepository";
    public static final String GIT_GROUP_MAIN_MENU        = "Git";
    public static final String REPOSITORY_GROUP_MAIN_MENU = "GitRepositoryGroup";
    public static final String COMMAND_GROUP_MAIN_MENU    = "GitCommandGroup";
    public static final String HISTORY_GROUP_MAIN_MENU    = "GitHistoryGroup";

    @Inject
    public GitExtension(GitResources resources, ActionManager actionManager,
                        InitRepositoryAction initAction, DeleteRepositoryAction deleteAction, AddToIndexAction addToIndexAction,
                        ResetToCommitAction resetToCommitAction, RemoveFromIndexAction removeFromIndexAction, CommitAction commitAction,
                        ShowBranchesAction showBranchesAction, ShowMergeAction showMergeAction, ResetFilesAction resetFilesAction,
                        ShowStatusAction showStatusAction, ShowGitUrlAction showGitUrlAction, ShowRemoteAction showRemoteAction,
                        PushAction pushAction, FetchAction fetchAction, PullAction pullAction, GitLocalizationConstant constant,
                        HistoryAction historyAction) {
        resources.gitCSS().ensureInjected();

        DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);

        DefaultActionGroup git = new DefaultActionGroup(GIT_GROUP_MAIN_MENU, true, actionManager);
        actionManager.registerAction(GIT_GROUP_MAIN_MENU, git);
        Constraints beforeWindow = new Constraints(BEFORE, GROUP_WINDOW);
        mainMenu.add(git, beforeWindow);

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
        actionManager.registerAction("GitBranches", showBranchesAction);
        commandGroup.add(showBranchesAction);
        actionManager.registerAction("GitMerge", showMergeAction);
        commandGroup.add(showMergeAction);
        DefaultActionGroup remoteGroup = new DefaultActionGroup(constant.remotesControlTitle(), true, actionManager);
        remoteGroup.getTemplatePresentation().setSVGIcon(resources.remote());
        actionManager.registerAction("GitRemoteGroup", remoteGroup);
        commandGroup.add(remoteGroup);
        actionManager.registerAction("GitResetFiles", resetFilesAction);
        commandGroup.add(resetFilesAction);

        actionManager.registerAction("GitHistory", historyAction);
        historyGroup.add(historyAction);
        actionManager.registerAction("GitStatus", showStatusAction);
        historyGroup.add(showStatusAction);
        actionManager.registerAction("GitUrl", showGitUrlAction);
        historyGroup.add(showGitUrlAction);

        actionManager.registerAction("GitPush", pushAction);
        remoteGroup.add(pushAction);
        actionManager.registerAction("GitFetch", fetchAction);
        remoteGroup.add(fetchAction);
        actionManager.registerAction("GitPull", pullAction);
        remoteGroup.add(pullAction);
        actionManager.registerAction("GitRemote", showRemoteAction);
        remoteGroup.add(showRemoteAction);

       
    }
}
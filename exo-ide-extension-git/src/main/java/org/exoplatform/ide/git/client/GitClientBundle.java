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
package org.exoplatform.ide.git.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 2:39:07 PM anya $
 */
public interface GitClientBundle extends ClientBundle {
    GitClientBundle INSTANCE = GWT.<GitClientBundle> create(GitClientBundle.class);

    @Source("org/exoplatform/ide/git/images/push/arrow.png")
    ImageResource arrow();

    @Source("org/exoplatform/ide/git/images/buttons/ok.png")
    ImageResource ok();

    @Source("org/exoplatform/ide/git/images/buttons/ok_Disabled.png")
    ImageResource okDisabled();

    @Source("org/exoplatform/ide/git/images/buttons/add.png")
    ImageResource add();

    @Source("org/exoplatform/ide/git/images/buttons/add_Disabled.png")
    ImageResource addDisabled();

    @Source("org/exoplatform/ide/git/images/buttons/cancel.png")
    ImageResource cancel();

    @Source("org/exoplatform/ide/git/images/buttons/cancel_Disabled.png")
    ImageResource cancelDisabled();

    @Source("org/exoplatform/ide/git/images/buttons/remove.png")
    ImageResource remove();

    @Source("org/exoplatform/ide/git/images/buttons/remove_Disabled.png")
    ImageResource removeDisabled();

    @Source("org/exoplatform/ide/git/images/buttons/rename.png")
    ImageResource rename();

    @Source("org/exoplatform/ide/git/images/buttons/rename_Disabled.png")
    ImageResource renameDisabled();

    @Source("org/exoplatform/ide/git/images/controls/remove.png")
    ImageResource removeFiles();

    @Source("org/exoplatform/ide/git/images/controls/remove_Disabled.png")
    ImageResource removeFilesDisabled();

    @Source("org/exoplatform/ide/git/images/controls/reset.png")
    ImageResource reset();

    @Source("org/exoplatform/ide/git/images/controls/reset_Disabled.png")
    ImageResource resetDisabled();

    @Source("org/exoplatform/ide/git/images/controls/cloneRepo.png")
    ImageResource cloneRepo();

    @Source("org/exoplatform/ide/git/images/controls/cloneRepo_Disabled.png")
    ImageResource cloneRepoDisabled();

    @Source("org/exoplatform/ide/git/images/controls/initRepo.png")
    ImageResource initRepo();

    @Source("org/exoplatform/ide/git/images/controls/initRepo_Disabled.png")
    ImageResource initRepoDisabled();

    @Source("org/exoplatform/ide/git/images/controls/deleteRepo.png")
    ImageResource deleteRepo();

    @Source("org/exoplatform/ide/git/images/controls/merge.png")
    ImageResource merge();

    @Source("org/exoplatform/ide/git/images/controls/merge_Disabled.png")
    ImageResource mergeDisabled();

    @Source("org/exoplatform/ide/git/images/controls/deleteRepo_Disabled.png")
    ImageResource deleteRepoDisabled();

    @Source("org/exoplatform/ide/git/images/controls/add.png")
    ImageResource addToIndex();

    @Source("org/exoplatform/ide/git/images/controls/add_Disabled.png")
    ImageResource addToIndexDisabled();

    @Source("org/exoplatform/ide/git/images/controls/branches.png")
    ImageResource branches();

    @Source("org/exoplatform/ide/git/images/controls/branches_Disabled.png")
    ImageResource branchesDisabled();

    @Source("org/exoplatform/ide/git/images/controls/remotes.png")
    ImageResource remotes();

    @Source("org/exoplatform/ide/git/images/controls/remotes_Disabled.png")
    ImageResource remotesDisabled();

    @Source("org/exoplatform/ide/git/images/controls/commit.png")
    ImageResource commit();

    @Source("org/exoplatform/ide/git/images/controls/commit_Disabled.png")
    ImageResource commitDisabled();

    @Source("org/exoplatform/ide/git/images/controls/push.png")
    ImageResource push();

    @Source("org/exoplatform/ide/git/images/controls/push_Disabled.png")
    ImageResource pushDisabled();

    @Source("org/exoplatform/ide/git/images/controls/pull.png")
    ImageResource pull();

    @Source("org/exoplatform/ide/git/images/controls/pull_Disabled.png")
    ImageResource pullDisabled();

    @Source("org/exoplatform/ide/git/images/history/arrows.png")
    ImageResource arrows();

    @Source("org/exoplatform/ide/git/images/history/history.png")
    ImageResource history();

    @Source("org/exoplatform/ide/git/images/history/history_Disabled.png")
    ImageResource historyDisabled();

    @Source("org/exoplatform/ide/git/images/history/project_level.png")
    ImageResource projectLevel();

    @Source("org/exoplatform/ide/git/images/history/project_level_Disabled.png")
    ImageResource projectLevelDisabled();

    @Source("org/exoplatform/ide/git/images/history/resource_level.png")
    ImageResource resourceLevel();

    @Source("org/exoplatform/ide/git/images/history/resource_level_Disabled.png")
    ImageResource resourceLevelDisabled();

    @Source("org/exoplatform/ide/git/images/history/diff_index.png")
    ImageResource diffIndex();

    @Source("org/exoplatform/ide/git/images/history/diff_index_Disabled.png")
    ImageResource diffIndexDisabled();

    @Source("org/exoplatform/ide/git/images/history/diff_working_dir.png")
    ImageResource diffWorkTree();

    @Source("org/exoplatform/ide/git/images/history/diff_working_dir_Disabled.png")
    ImageResource diffWorTreeDisabled();

    @Source("org/exoplatform/ide/git/images/history/diff_prev_version.png")
    ImageResource diffPrevVersion();

    @Source("org/exoplatform/ide/git/images/history/diff_prev_version_Disabled.png")
    ImageResource diffPrevVersionDisabled();

    @Source("org/exoplatform/ide/git/images/history/refresh.png")
    ImageResource refresh();

    @Source("org/exoplatform/ide/git/images/history/refresh_Disabled.png")
    ImageResource refreshDisabled();

    @Source("org/exoplatform/ide/git/images/controls/fetch.png")
    ImageResource fetch();

    @Source("org/exoplatform/ide/git/images/controls/fetch_Disabled.png")
    ImageResource fetchDisabled();

    @Source("org/exoplatform/ide/git/images/controls/status.png")
    ImageResource status();

    @Source("org/exoplatform/ide/git/images/controls/status_Disabled.png")
    ImageResource statusDisabled();

    @Source("org/exoplatform/ide/git/images/controls/checkout.png")
    ImageResource checkout();

    @Source("org/exoplatform/ide/git/images/controls/checkout_Disabled.png")
    ImageResource checkoutDisabled();

    @Source("org/exoplatform/ide/git/images/statuses/added.png")
    ImageResource itemAdded();

    @Source("org/exoplatform/ide/git/images/statuses/changed.png")
    ImageResource itemChanged();

    @Source("org/exoplatform/ide/git/images/statuses/conflicting.png")
    ImageResource itemConflicting();

    @Source("org/exoplatform/ide/git/images/statuses/ignored_not_in_index.png")
    ImageResource itemIgnoredNotInIndex();

    @Source("org/exoplatform/ide/git/images/statuses/in_repo.png")
    ImageResource itemInRepo();

    @Source("org/exoplatform/ide/git/images/statuses/missing.png")
    ImageResource itemMissing();

    @Source("org/exoplatform/ide/git/images/statuses/modified.png")
    ImageResource itemModified();

    @Source("org/exoplatform/ide/git/images/statuses/removed.png")
    ImageResource itemRemoved();

    @Source("org/exoplatform/ide/git/images/statuses/root.png")
    ImageResource itemRoot();

    @Source("org/exoplatform/ide/git/images/statuses/untracked.png")
    ImageResource itemUntracked();

    @Source("org/exoplatform/ide/git/images/branch/current.png")
    ImageResource currentBranch();

    @Source("org/exoplatform/ide/git/images/controls/remote_Disabled.png")
    ImageResource remoteDisabled();

    @Source("org/exoplatform/ide/git/images/controls/remote.png")
    ImageResource remote();

    @Source("org/exoplatform/ide/git/images/controls/revert_Disabled.png")
    ImageResource revertDisabled();

    @Source("org/exoplatform/ide/git/images/controls/revert.png")
    ImageResource revert();

    @Source("org/exoplatform/ide/git/images/branch/branch.png")
    ImageResource branch();

    @Source("org/exoplatform/ide/git/images/branch/local_branches.png")
    ImageResource localBanches();

    @Source("org/exoplatform/ide/git/images/branch/remote_branches.png")
    ImageResource remoteBranches();

    @Source("org/exoplatform/ide/git/images/controls/repository.png")
    ImageResource projectReadOnlyGitUrl();

    @Source("org/exoplatform/ide/git/images/controls/repository_Disabled.png")
    ImageResource projectReadOnlyGitUrlDisabled();
}

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
package com.codenvy.ide.ext.git.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitClientServiceImpl;
import com.codenvy.ide.ext.git.client.add.AddToIndexView;
import com.codenvy.ide.ext.git.client.add.AddToIndexViewImpl;
import com.codenvy.ide.ext.git.client.branch.BranchView;
import com.codenvy.ide.ext.git.client.branch.BranchViewImpl;
import com.codenvy.ide.ext.git.client.clone.CloneRepositoryView;
import com.codenvy.ide.ext.git.client.clone.CloneRepositoryViewImpl;
import com.codenvy.ide.ext.git.client.commit.CommitView;
import com.codenvy.ide.ext.git.client.commit.CommitViewImpl;
import com.codenvy.ide.ext.git.client.fetch.FetchView;
import com.codenvy.ide.ext.git.client.fetch.FetchViewImpl;
import com.codenvy.ide.ext.git.client.history.HistoryView;
import com.codenvy.ide.ext.git.client.history.HistoryViewImpl;
import com.codenvy.ide.ext.git.client.init.InitRepositoryView;
import com.codenvy.ide.ext.git.client.init.InitRepositoryViewImpl;
import com.codenvy.ide.ext.git.client.merge.MergeView;
import com.codenvy.ide.ext.git.client.merge.MergeViewImpl;
import com.codenvy.ide.ext.git.client.pull.PullView;
import com.codenvy.ide.ext.git.client.pull.PullViewImpl;
import com.codenvy.ide.ext.git.client.push.PushToRemoteView;
import com.codenvy.ide.ext.git.client.push.PushToRemoteViewImpl;
import com.codenvy.ide.ext.git.client.remote.RemoteView;
import com.codenvy.ide.ext.git.client.remote.RemoteViewImpl;
import com.codenvy.ide.ext.git.client.remote.add.AddRemoteRepositoryView;
import com.codenvy.ide.ext.git.client.remote.add.AddRemoteRepositoryViewImpl;
import com.codenvy.ide.ext.git.client.remove.RemoveFromIndexView;
import com.codenvy.ide.ext.git.client.remove.RemoveFromIndexViewImpl;
import com.codenvy.ide.ext.git.client.reset.commit.ResetToCommitView;
import com.codenvy.ide.ext.git.client.reset.commit.ResetToCommitViewImpl;
import com.codenvy.ide.ext.git.client.reset.files.ResetFilesView;
import com.codenvy.ide.ext.git.client.reset.files.ResetFilesViewImpl;
import com.codenvy.ide.ext.git.client.url.ShowProjectGitReadOnlyUrlView;
import com.codenvy.ide.ext.git.client.url.ShowProjectGitReadOnlyUrlViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class GitGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(GitClientService.class).to(GitClientServiceImpl.class).in(Singleton.class);

        bind(CloneRepositoryView.class).to(CloneRepositoryViewImpl.class).in(Singleton.class);
        bind(InitRepositoryView.class).to(InitRepositoryViewImpl.class).in(Singleton.class);
        bind(AddToIndexView.class).to(AddToIndexViewImpl.class).in(Singleton.class);
        bind(ResetToCommitView.class).to(ResetToCommitViewImpl.class).in(Singleton.class);
        bind(RemoveFromIndexView.class).to(RemoveFromIndexViewImpl.class).in(Singleton.class);
        bind(CommitView.class).to(CommitViewImpl.class).in(Singleton.class);
        bind(BranchView.class).to(BranchViewImpl.class).in(Singleton.class);
        bind(MergeView.class).to(MergeViewImpl.class).in(Singleton.class);
        bind(ResetFilesView.class).to(ResetFilesViewImpl.class).in(Singleton.class);
        bind(ShowProjectGitReadOnlyUrlView.class).to(ShowProjectGitReadOnlyUrlViewImpl.class).in(Singleton.class);
        bind(RemoteView.class).to(RemoteViewImpl.class).in(Singleton.class);
        bind(AddRemoteRepositoryView.class).to(AddRemoteRepositoryViewImpl.class).in(Singleton.class);
        bind(PushToRemoteView.class).to(PushToRemoteViewImpl.class).in(Singleton.class);
        bind(FetchView.class).to(FetchViewImpl.class).in(Singleton.class);
        bind(PullView.class).to(PullViewImpl.class).in(Singleton.class);
        bind(HistoryView.class).to(HistoryViewImpl.class).in(Singleton.class);
    }
}
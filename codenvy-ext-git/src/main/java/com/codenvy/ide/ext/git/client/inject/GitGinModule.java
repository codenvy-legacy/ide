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
package com.codenvy.ide.ext.git.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitServiceClientImpl;
import com.codenvy.ide.ext.git.client.add.AddToIndexView;
import com.codenvy.ide.ext.git.client.add.AddToIndexViewImpl;
import com.codenvy.ide.ext.git.client.branch.BranchView;
import com.codenvy.ide.ext.git.client.branch.BranchViewImpl;
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
        bind(GitServiceClient.class).to(GitServiceClientImpl.class).in(Singleton.class);

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
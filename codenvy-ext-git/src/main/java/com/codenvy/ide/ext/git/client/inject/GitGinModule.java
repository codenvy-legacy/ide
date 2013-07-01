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
import com.codenvy.ide.ext.git.client.init.InitRepositoryView;
import com.codenvy.ide.ext.git.client.init.InitRepositoryViewImpl;
import com.codenvy.ide.ext.git.client.merge.MergeView;
import com.codenvy.ide.ext.git.client.merge.MergeViewImpl;
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
    }
}
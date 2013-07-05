/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 2:39:07 PM anya $
 */
public interface GitResources extends ClientBundle {
    public interface GitCSS extends CssResource {
        String textFont();
    }

    @Source({"Git.css", "com/codenvy/ide/api/ui/style.css"})
    GitCSS gitCSS();

    @Source("push/arrow.png")
    ImageResource arrow();

    @Source("buttons/ok.png")
    ImageResource ok();

    @Source("buttons/add.png")
    ImageResource add();

    @Source("buttons/cancel.png")
    ImageResource cancel();

    @Source("buttons/remove.png")
    ImageResource remove();

    @Source("buttons/rename.png")
    ImageResource rename();

    @Source("controls/remove.png")
    ImageResource removeFiles();

    @Source("controls/reset.png")
    ImageResource reset();

    @Source("controls/cloneRepo.png")
    ImageResource cloneRepo();

    @Source("controls/initRepo.png")
    ImageResource initRepo();

    @Source("controls/deleteRepo.png")
    ImageResource deleteRepo();

    @Source("controls/merge.png")
    ImageResource merge();

    @Source("controls/add.png")
    ImageResource addToIndex();

    @Source("controls/branches.png")
    ImageResource branches();

    @Source("controls/remotes.png")
    ImageResource remotes();

    @Source("controls/commit.png")
    ImageResource commit();

    @Source("controls/push.png")
    ImageResource push();

    @Source("controls/pull.png")
    ImageResource pull();

    @Source("history/arrows.png")
    ImageResource arrows();

    @Source("history/history.png")
    ImageResource history();

    @Source("history/project_level.png")
    ImageResource projectLevel();

    @Source("history/resource_level.png")
    ImageResource resourceLevel();

    @Source("history/diff_index.png")
    ImageResource diffIndex();

    @Source("history/diff_working_dir.png")
    ImageResource diffWorkTree();

    @Source("history/diff_prev_version.png")
    ImageResource diffPrevVersion();

    @Source("history/refresh.png")
    ImageResource refresh();

    @Source("controls/fetch.png")
    ImageResource fetch();

    @Source("controls/status.png")
    ImageResource status();

    @Source("controls/checkout.png")
    ImageResource checkout();

    @Source("statuses/added.png")
    ImageResource itemAdded();

    @Source("statuses/changed.png")
    ImageResource itemChanged();

    @Source("statuses/conflicting.png")
    ImageResource itemConflicting();

    @Source("statuses/ignored_not_in_index.png")
    ImageResource itemIgnoredNotInIndex();

    @Source("statuses/in_repo.png")
    ImageResource itemInRepo();

    @Source("statuses/missing.png")
    ImageResource itemMissing();

    @Source("statuses/modified.png")
    ImageResource itemModified();

    @Source("statuses/removed.png")
    ImageResource itemRemoved();

    @Source("statuses/root.png")
    ImageResource itemRoot();

    @Source("statuses/untracked.png")
    ImageResource itemUntracked();

    @Source("branch/current.png")
    ImageResource currentBranch();

    @Source("controls/remote.png")
    ImageResource remote();

    @Source("controls/revert.png")
    ImageResource revert();

    @Source("controls/repository.png")
    ImageResource projectReadOnlyGitUrl();
}
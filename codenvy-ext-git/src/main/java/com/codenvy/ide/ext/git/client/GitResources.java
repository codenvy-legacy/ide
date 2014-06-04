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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 2:39:07 PM anya $
 */
public interface GitResources extends ClientBundle {
    public interface GitCSS extends CssResource {
        String textFont();

        String simpleListContainer();
    }

    @Source({"git.css", "com/codenvy/ide/api/ui/style.css"})
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

    @Source("buttons/next.png")
    ImageResource next();

    @Source("controls/remove.svg")
    SVGResource removeFiles();

    @Source("controls/reset.svg")
    SVGResource reset();

    @Source("controls/init.svg")
    SVGResource initRepo();

    @Source("controls/delete-repo.svg")
    SVGResource deleteRepo();

    @Source("controls/merge.svg")
    SVGResource merge();

    @Source("controls/add.svg")
    SVGResource addToIndex();

    @Source("controls/branches.svg")
    SVGResource branches();

    @Source("controls/remotes.svg")
    SVGResource remotes();

    @Source("controls/commit.svg")
    SVGResource commit();

    @Source("controls/push.svg")
    SVGResource push();

    @Source("controls/pull.svg")
    SVGResource pull();

    @Source("history/arrows.png")
    ImageResource arrows();

    @Source("history/history.png")
    ImageResource history();
    
    @Source("history/show-history.svg")
    SVGResource showHistory();

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

    @Source("controls/fetch.svg")
    SVGResource fetch();

    @Source("controls/status.svg")
    SVGResource status();

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

    @Source("controls/remote.svg")
    SVGResource remote();

    @Source("controls/revert.svg")
    SVGResource revert();

    @Source("controls/git-url.svg")
    SVGResource projectReadOnlyGitUrl();

    @Source("welcome/project_open.png")
    ImageResource project();
}
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
package org.exoplatform.ide.git.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 22, 2011 2:39:07 PM anya $
 *
 */
public interface GitClientBundle extends ClientBundle
{
   GitClientBundle INSTANCE = GWT.<GitClientBundle>create(GitClientBundle.class);
   
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
   
   @Source("org/exoplatform/ide/git/images/controls/remove.png")
   ImageResource removeFiles();
   
   @Source("org/exoplatform/ide/git/images/controls/remove_Disabled.png")
   ImageResource removeFilesDisabled();
   
   @Source("org/exoplatform/ide/git/images/controls/reset.png")
   ImageResource reset();
   
   @Source("org/exoplatform/ide/git/images/controls/reset_Disabled.png")
   ImageResource resetDisabled();
   
   @Source("org/exoplatform/ide/git/images/buttons/cancel.png")
   ImageResource cancel();
   
   @Source("org/exoplatform/ide/git/images/buttons/cancel_Disabled.png")
   ImageResource cancelDisabled();
   
   @Source("org/exoplatform/ide/git/images/buttons/remove.png")
   ImageResource remove();
   
   @Source("org/exoplatform/ide/git/images/buttons/remove_Disabled.png")
   ImageResource removeDisabled();
   
   @Source("org/exoplatform/ide/git/images/controls/cloneRepo.png")
   ImageResource cloneRepo();
   
   @Source("org/exoplatform/ide/git/images/controls/cloneRepo_Disabled.png")
   ImageResource cloneRepoDisabled();
   
   @Source("org/exoplatform/ide/git/images/controls/initRepo.png")
   ImageResource initRepo();
   
   @Source("org/exoplatform/ide/git/images/controls/initRepo_Disabled.png")
   ImageResource initRepoDisabled();
   
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
   
   @Source("org/exoplatform/ide/git/images/controls/fetch.png")
   ImageResource fetch();
   
   @Source("org/exoplatform/ide/git/images/controls/fetch_Disabled.png")
   ImageResource fetchDisabled();
   
   @Source("org/exoplatform/ide/git/images/controls/status.png")
   ImageResource status();
   
   @Source("org/exoplatform/ide/git/images/controls/status_Disabled.png")
   ImageResource statusDisabled();
   
   @Source("org/exoplatform/ide/git/images/items/item_in_repository.png")
   ImageResource itemInRepoditory();
   
   @Source("org/exoplatform/ide/git/images/items/item_in_repository_question.png")
   ImageResource itemNew();
   
   @Source("org/exoplatform/ide/git/images/items/item_in_repository_star.png")
   ImageResource itemChanged();
   
   @Source("org/exoplatform/ide/git/images/items/item_not_commited.png")
   ImageResource itemNotCommited();
   
   @Source("org/exoplatform/ide/git/images/items/repository_root.png")
   ImageResource repositoryRoot();
   
   @Source("org/exoplatform/ide/git/images/branch/current.png")
   ImageResource currentBranch();
}

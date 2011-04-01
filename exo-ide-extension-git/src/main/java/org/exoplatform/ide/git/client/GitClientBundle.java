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
   public static final GitClientBundle INSTANCE = GWT.create(GitClientBundle.class);
   
   
   @Source("org/exoplatform/ide/git/images/buttons/ok.png")
   ImageResource ok();
   
   @Source("org/exoplatform/ide/git/images/buttons/ok_Disabled.png")
   ImageResource okDisabled();
   
   @Source("org/exoplatform/ide/git/images/buttons/cancel.png")
   ImageResource cancel();
   
   @Source("org/exoplatform/ide/git/images/buttons/cancel_Disabled.png")
   ImageResource cancelDisabled();
   
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
   
   @Source("org/exoplatform/ide/git/images/controls/commit.png")
   ImageResource commit();
   
   @Source("org/exoplatform/ide/git/images/controls/commit_Disabled.png")
   ImageResource commitDisabled();
   
   @Source("org/exoplatform/ide/git/images/controls/status.png")
   ImageResource status();
   
   @Source("org/exoplatform/ide/git/images/controls/status_Disabled.png")
   ImageResource statusDisabled();
}

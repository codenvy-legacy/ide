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

/**
 * Messages shown to Git user.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 31, 2011 11:33:45 AM anya $
 *
 */
public interface Messages
{
   public static final String NOT_GIT_REPOSITORY = "Not a git repository (or any of the parent directories).";

   public static final String SELECTED_ITEMS_FAIL = "Please, select one item(s), you want to add, in browser tree.";

   public static final String ADD_SUCCESS = "Successfully added to index.";

   public static final String ADD_FAILED = "Failed on adding changes to index.";
   
   public static final String BRANCHES_LIST_FAILED = "Failed on getting branches list.";
   
   public static final String BRANCH_CREATE_FAILED = "Create new branch failed.";
   
   public static final String BRANCH_CHECKOUT_FAILED = "Branch checkout failed.";
   
   public static final String BRANCH_DELETE_FAILED = "Branch delete failed.";

   public static final String CLONE_SUCCESS = "Repository was successfully cloned.";

   public static final String CLONE_FAILED = "Clone repository failed.";

   public static final String COMMIT_SUCCESS = "Successfully commited.";

   public static final String COMMIT_FAILED = "Commit failed.";

   public static final String INIT_SUCCESS = "Repository was successfully initialized.";

   public static final String INIT_FAILED = "Init repository failed.";
   
   public static final String PUSH_SUCCESS = "Successfully pushed to remote repository.";
   
   public static final String PUSH_FAIL = "Push to remote repository failed.";
   
   public static final String REMOTE_LIST_FAILED = "No remote repositories are found.";
   
   public static final String REPOSITORY_ALREADY_EXISTS = "Git repository already exists in this folder or parent one.";
   
   public static final String STATUS_FAILED = "Get work tree status failed.";
}

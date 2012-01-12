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
package org.exoplatform.ide.git.core;

import com.thoughtworks.selenium.Selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * Git extension test module.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 23, 2011 10:39:29 AM anya $
 *
 */
public class GIT
{
   public interface Messages
   {
      String INIT_SUCCESS = "Repository was successfully initialized.";

      String CLONE_SUCCESS = "Repository was successfully cloned.";

      String CLONE_REPO_EXISTS = "[ERROR] Repository already exists:";

      String GIT_REPO_EXISTS = "Git repository already exists in this folder or parent one.";

      String DELETE_SUCCESS = "Git repository was successfully deleted.";

      String NOT_GIT_REPO = "Not a git repository (or any of the parent directories).";

      String ADD_SUCCESS = "[INFO] Successfully added to index.";

      String RESET_COMMIT_SUCCESS = "[INFO] Successfully reseted.";

      String NOTHING_TO_COMMIT = "Nothing to commit.";

      String RESET_FILES_SUCCESS = "[INFO] Successfully reseted files.";

      String COMMIT_SUCCESS = "[INFO] Commited with revision";

      String DELETE_BRANCH_QUESTION = "Are you sure you want to delete branch %s?";

      String DELETE_REMOTE_QUESTION = "Are you sure you want to delete remote repository %s?";

      String NO_REMOTE_REPOSITORIES = "No remote repositories are found.";

      String PUSH_SUCCESS = "[INFO] Successfully pushed to remote repository %s";

      String PULL_SUCCESS = "[INFO] Successfully pulled from remote repository %s";
      
      String REPOSITORY_EXISTS = "[ERROR] Repository already exists:";
   }

   public interface DialogTitles
   {
      String DELETE_DIALOG = "Delete Git repository";

      String ERROR = "Error";
   }

   private Selenium selenium;

   private static GIT instance;

   public static GIT getInstance()
   {
      return instance;
   }

   public GIT(Selenium selenium, WebDriver driver)
   {
      this.selenium = selenium;
      instance = this;

      INIT_REPOSITORY = PageFactory.initElements(driver, InitRepository.class);
      CLONE_REPOSITORY = PageFactory.initElements(driver, CloneRepository.class);
      ADD = PageFactory.initElements(driver, Add.class);
      BRANCHES = PageFactory.initElements(driver, Branches.class);
      COMMIT = PageFactory.initElements(driver, Commit.class);
      MERGE = PageFactory.initElements(driver, Merge.class);
      SHOW_HISTORY = PageFactory.initElements(driver, ShowHistory.class);
      RESET_FILES = PageFactory.initElements(driver, ResetFiles.class);
      REMOVE_FILES = PageFactory.initElements(driver, RemoveFiles.class);
      RESET_TO_COMMIT = PageFactory.initElements(driver, ResetToCommit.class);
      REMOTES = PageFactory.initElements(driver, Remotes.class);
      PUSH = PageFactory.initElements(driver, Push.class);
      PULL = PageFactory.initElements(driver, Pull.class);
   }

   public Selenium getSelenium()
   {
      return selenium;
   }

   public InitRepository INIT_REPOSITORY;

   public CloneRepository CLONE_REPOSITORY;

   public Add ADD;

   public Branches BRANCHES;

   public Commit COMMIT;

   public Merge MERGE;

   public Status STATUS = new Status();

   public ShowHistory SHOW_HISTORY;

   public ResetFiles RESET_FILES;

   public RemoveFiles REMOVE_FILES;

   public ResetToCommit RESET_TO_COMMIT;

   public Remotes REMOTES;

   public Push PUSH;

   public Pull PULL;
}

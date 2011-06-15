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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.fetch.FetchPresenter;
import org.exoplatform.ide.git.client.pull.PullPresenter;
import org.exoplatform.ide.git.client.push.PushToRemotePresenter;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.Remote;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract presenter for displays, which has to show and work with branches 
 * ({@link FetchPresenter}, {@link PullPresenter}, {@link PushToRemotePresenter}) for not to duplicate same methods.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 21, 2011 10:55:28 AM anya $
 *
 */
public abstract class HasBranchesPresenter extends GitPresenter
{
   /**
    * The list of remote branches.
    */
   protected List<Branch> remoteBranches;

   /**
    * @param eventBus
    */
   public HasBranchesPresenter(HandlerManager eventBus)
   {
      super(eventBus);
   }

   /**
    * Get the list of remote repositories for local one.
    * If remote repositories are found, then method {@link #onRemotesReceived(List)} is called.
    */
   public void getRemotes()
   {
      GitClientService.getInstance().remoteList(workDir, null, true, new AsyncRequestCallback<List<Remote>>()
      {
         @Override
         protected void onSuccess(List<Remote> result)
         {
            if (result.size() == 0)
            {
               Dialogs.getInstance().showError(GitExtension.MESSAGES.remoteListFailed());
               return;
            }

            onRemotesReceived(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.remoteListFailed();
            Dialogs.getInstance().showError(errorMessage);
         }
      });
   }

   public abstract void onRemotesReceived(List<Remote> remotes);

   /**
    * Get the list of branches.
    * 
    * @param workDir Git repository work tree location
    * @param remote get remote branches if <code>true</code>
    */
   public void getBranches(String workDir, final boolean remote)
   {
      GitClientService.getInstance().branchList(workDir, remote, new AsyncRequestCallback<List<Branch>>()
      {

         @Override
         protected void onSuccess(List<Branch> result)
         {
            if (remote)
            {
               remoteBranches = result;
               setRemoteBranches(remoteBranches);
               return;
            }

            setLocalBranches(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.branchesListFailed();
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * Set remote branches to display the values.
    * 
    * @param branches remote branches
    */
   protected abstract void setRemoteBranches(List<Branch> branches);

   /**
    * Set local branches to display the values.
    * 
    * @param branches local branches
    */
   protected abstract void setLocalBranches(List<Branch> branches);

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      getRemotes();
   }

   /**
    * Set values of remote branches: filter remote branches due to 
    * selected remote repository.
    * 
    * @param remoteName
    */
   protected String[] getRemoteBranchesToDisplay(String remoteName)
   {
      List<String> branchesToDisplay = new ArrayList<String>();
      if (remoteBranches == null || remoteBranches.size() <= 0 || remoteName == null)
      {
         branchesToDisplay.add("master");
         return branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
      }
      String compareString = "refs/remotes/" + remoteName + "/";
      for (Branch branch : remoteBranches)
      {
         if (branch.getName().startsWith(compareString))
         {
            branchesToDisplay.add(branch.getName().replaceFirst(compareString, "refs/heads/"));
         }
      }
      return branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
   }

   /**
    * Set values of remote branches (show only simple name): filter remote branches due to 
    * selected remote repository.
    * 
    * @param remoteName
    */
   protected String[] getRemoteBranchesNamesToDisplay(String remoteName)
   {
      List<String> branchesToDisplay = new ArrayList<String>();
      if (remoteBranches == null || remoteBranches.size() <= 0 || remoteName == null)
      {
         branchesToDisplay.add("master");
         return branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
      }
      String compareString = "refs/remotes/" + remoteName + "/";
      for (Branch branch : remoteBranches)
      {
         if (branch.getName().startsWith(compareString))
         {
            branchesToDisplay.add(branch.getName().replaceFirst(compareString, ""));
         }
      }
      return branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
   }
}

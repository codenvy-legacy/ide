/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.openshift.client.deploy;

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.remote.HasBranchesPresenter;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import com.google.gwt.http.client.RequestException;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PullApplicationSourcesHandler extends HasBranchesPresenter
{

   private PullCompleteCallback callback;

   private ProjectModel project;

   private List<Remote> remotes;

   private List<Branch> remoteBranches;

   private List<Branch> localBranches;

   public void pullApplicationSources(VirtualFileSystemInfo vfsInfo, ProjectModel project, PullCompleteCallback callback)
   {
      this.project = project;
      this.callback = callback;
      vfs = vfsInfo;

      getRemotes(project.getId());
   }

   @Override
   public void onRemotesReceived(List<Remote> remotes)
   {
      this.remotes = remotes;
      getBranches(project.getId(), false);
   }

   @Override
   protected void setLocalBranches(List<Branch> branches)
   {
      localBranches = branches;
      getBranches(project.getId(), true);
   }

   @Override
   protected void setRemoteBranches(List<Branch> branches)
   {
      remoteBranches = branches;
      pullSources();
   }

   public void pullSources()
   {
      String remoteName = remotes.get(0).getName();
      final String remoteUrl = remotes.get(0).getUrl();

      String localBranch =
         localBranches != null && !localBranches.isEmpty() ? localBranches.get(0).getDisplayName() : "master";

      String remoteBranch =
         remoteBranches != null && !remoteBranches.isEmpty() ? remoteBranches.get(0).getDisplayName() : localBranch;
      // Form the refspec. User points only the branch names:

      //      System.out.println("remote name [" + remoteName + "]");
      //      System.out.println("local branch [" + localBranch + "]");
      //      System.out.println("remote branch [" + remoteBranch + "]");

      String refs =
         (localBranch == null || localBranch.length() == 0) ? remoteBranch : "refs/heads/" + remoteBranch + ":"
            + "refs/remotes/" + remoteName + "/" + remoteBranch;

      //      System.out.println("refs [" + refs + "]");

      try
      {
         GitClientService.getInstance().pull(vfs.getId(), project, refs, remoteName, new AsyncRequestCallback<String>()
         {
            @Override
            protected void onSuccess(String result)
            {
               IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.pullSuccess(remoteUrl), Type.INFO));
               IDE.fireEvent(new RefreshBrowserEvent());

               if (callback != null)
               {
                  callback.onPullComplete(true);
                  callback = null;
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               handleError(exception, remoteUrl);
            }
         });
      }
      catch (RequestException e)
      {
         handleError(e, remoteUrl);
      }
   }

   private void handleError(Throwable e, String remoteGitUrl)
   {
      e.printStackTrace();

      if (callback != null)
      {
         callback.onPullComplete(false);
         callback = null;
      }
   }

}

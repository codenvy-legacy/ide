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
package org.exoplatform.ide.git.client.push;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.Remote;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter of view for pushing changes to remote repository.
 * The view is pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 4, 2011 9:53:07 AM anya $
 *
 */
public class PushToRemotePresenter implements ItemsSelectedHandler, PushToRemoteHandler
{
   public interface Display extends IsView
   {
      /**
       * Get the push button click handler.
       * 
       * @return {@link HasClickHandlers} push button
       */
      HasClickHandlers getPushButton();

      /**
       * Get the cancel button click handler.
       * 
       * @return {@link HasClickHandlers} cancel button
       */
      HasClickHandlers getCancelButton();

      /**
       * Get remote repository field value.
       * 
       * @return {@link HasValue} field
       */
      HasValue<String> getRemoteValue();

      /**
       * Get remote branches field value.
       * 
       * @return {@link HasValue} field
       */
      HasValue<String> getRemoteBranchesValue();

      /**
       * Get local branches field value.
       * 
       * @return {@link HasValue} field
       */
      HasValue<String> getLocalBranchesValue();

      /**
       * Set values of remote repositories. 
       * 
       * @param values values to set
       */
      void setRemoteValues(String[] values);

      /**
       * Set values of remote repository branches. 
       * 
       * @param values values to set
       */
      void setRemoteBranches(String[] values);

      /**
       * Set values of local repository branches. 
       * 
       * @param values values to set
       */
      void setLocalBranches(String[] values);

      /**
       * Change the enable state of the push button.
       * 
       * @param enable enable state
       */
      void enablePushButton(boolean enable);
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * Selected items in browser tree.
    */
   private List<Item> selectedItems;

   /**
    * Working directory of the Git repository.
    */
   private String workDir;

   public List<Branch> remoteBranches;

   /**
    * @param eventBus events handler
    */
   public PushToRemotePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(PushToRemoteEvent.TYPE, this);
   }

   /**
    * Bind pointed display with presenter.
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getPushButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doPush();
         }
      });

      display.getRemoteValue().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            setRemoteBranches(event.getValue());
         }
      });

      display.getRemoteBranchesValue().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean empty = (event.getValue() == null || event.getValue().length() <= 0);
            display.enablePushButton(!empty);
         }
      });
   }

   /**
    * Set values of remote branches: filter remote branches due to 
    * selected remote repository.
    * 
    * @param remoteName
    */
   private void setRemoteBranches(String remoteName)
   {
      if (remoteBranches == null || remoteBranches.size() <= 0 || remoteName == null)
      {
         return;
      }
      List<String> branchesToDisplay = new ArrayList<String>();
      String compareString = "refs/remotes/" + remoteName + "/";
      for (Branch branch : remoteBranches)
      {
         if (branch.getName().startsWith(compareString))
         {
            branchesToDisplay.add(branch.getName().replaceFirst(compareString, "refs/heads/"));
         }
      }
      String[] branches = branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
      display.setRemoteBranches(branches);

      if (branches.length > 0)
      {
         display.getRemoteBranchesValue().setValue(branches[0]);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemoteHandler#onPushToRemote(org.exoplatform.ide.git.client.push.PushToRemoteEvent)
    */
   @Override
   public void onPushToRemote(PushToRemoteEvent event)
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(Messages.SELECTED_ITEMS_FAIL);
         return;
      }

      //First get the working directory of the repository if exists:
      GitClientService.getInstance().getWorkDir(selectedItems.get(0).getHref(),
         new AsyncRequestCallback<WorkDirResponse>()
         {
            @Override
            protected void onSuccess(WorkDirResponse result)
            {
               workDir = result.getWorkDir();
               workDir = (workDir.endsWith("/.git")) ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;
               getRemotes(workDir);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError(Messages.NOT_GIT_REPOSITORY);
            }
         });
   }

   /**
    * Get the list of remote repositories for local one.
    * If remote repositories are found, then get the list of 
    * branches (remote and local).
    * 
    * @param workDir
    */
   public void getRemotes(final String workDir)
   {
      GitClientService.getInstance().remoteList(workDir, null, true, new AsyncRequestCallback<List<Remote>>()
      {
         @Override
         protected void onSuccess(List<Remote> result)
         {
            if (result.size() == 0)
            {
               Dialogs.getInstance().showError(Messages.REMOTE_LIST_FAILED);
               return;
            }

            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            bindDisplay(d);

            String[] remotes = new String[result.size()];
            for (int i = 0; i < result.size(); i++)
            {
               remotes[i] = (result.get(i).getName() != null) ? result.get(i).getName() : result.get(i).getUrl();
            }

            display.setRemoteValues(remotes);

            getBranches(workDir, false);
            getBranches(workDir, true);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : Messages.REMOTE_LIST_FAILED;
            Dialogs.getInstance().showError(errorMessage);
         }
      });
   }

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
            if (result.size() <= 0)
               return;
            if (remote)
            {
               remoteBranches = result;
               setRemoteBranches(display.getRemoteValue().getValue());
               return;
            }

            String[] branches = new String[result.size()];
            for (int i = 0; i < result.size(); i++)
            {
               branches[i] = result.get(i).getName();
            }
            display.setLocalBranches(branches);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : Messages.BRANCHES_LIST_FAILED;
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * Push changes to remote repository.
    */
   public void doPush()
   {
      if (workDir == null)
         return;

      String remote = display.getRemoteValue().getValue();
      String localBranch = display.getLocalBranchesValue().getValue();
      String remoteBranch = display.getRemoteBranchesValue().getValue();

      GitClientService.getInstance().push(workDir, new String[]{localBranch + ":" + remoteBranch}, remote, false,
         new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               eventBus.fireEvent(new OutputEvent(Messages.PUSH_SUCCESS, Type.INFO));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : Messages.PUSH_FAIL;
               eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
            }
         });
      IDE.getInstance().closeView(display.asView().getId());
   }
}

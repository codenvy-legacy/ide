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
package org.exoplatform.ide.git.client.fetch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.remote.HasBranchesPresenter;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Presenter of the view for fetching changes from remote repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 20, 2011 1:33:17 PM anya $
 *
 */
public class FetchPresenter extends HasBranchesPresenter implements FetchHandler
{
   interface Display extends IsView
   {
      /**
       * Get fetch button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getFetchButton();

      /**
       * Get cancel button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Get remote repository field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getRemoteName();

      /**
       * Get remote branches field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getRemoteBranches();

      /**
       * Get local branches field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getLocalBranches();

      /**
       * Get remove deleted refs field.
       * 
       * @return {@link HasValue}
       */
      HasValue<Boolean> getRemoveDeletedRefs();

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
       * Change the enable state of the fecth button.
       * 
       * @param enable enable state
       */
      void enableFetchButton(boolean enable);

      /**
       * Set values of remote repositories. 
       * 
       * @param values values to set
       */
      void setRemoteValues(LinkedHashMap<String, String> values);

      /** Get remote repository display value.
       * 
       * @return String
       */
      String getRemoteDisplayValue();
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * @param eventBus events handler
    */
   public FetchPresenter(HandlerManager eventBus)
   {
      super(eventBus);
      eventBus.addHandler(FetchEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    * 
    * @param d
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

      display.getFetchButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doFetch();
         }
      });

      display.getRemoteName().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.setRemoteBranches(getRemoteBranchesNamesToDisplay(display.getRemoteDisplayValue()));
         }
      });

      display.getRemoteBranches().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean empty = (event.getValue() == null || event.getValue().length() <= 0);
            display.enableFetchButton(!empty);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchHandler#onFetch(org.exoplatform.ide.git.client.fetch.FetchEvent)
    */
   @Override
   public void onFetch(FetchEvent event)
   {
      if (makeSelectionCheck())
      {
         String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
         getRemotes(projectId);
      }
   }

   /**
    * Perform fetch from remote repository.
    */
   public void doFetch()
   {
      final String remoteUrl = display.getRemoteName().getValue();
      String remoteName = display.getRemoteDisplayValue();
      String localBranch = display.getLocalBranches().getValue();
      String remoteBranch = display.getRemoteBranches().getValue();
      boolean removeDeletedRefs = display.getRemoveDeletedRefs().getValue();
      String refs =
         (localBranch == null || localBranch.length() == 0) ? remoteBranch : "refs/heads/" + remoteBranch + ":"
            + "refs/remotes/" + remoteName + "/" + remoteBranch;
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      GitClientService.getInstance().fetch(vfs.getId(), projectId, remoteName, new String[]{refs}, removeDeletedRefs,
         new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               eventBus.fireEvent(new OutputEvent(GitExtension.MESSAGES.fetchSuccess(remoteUrl), Type.INFO));
               IDE.getInstance().closeView(display.asView().getId());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               String errorMessage =
                  (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.fetchFail(remoteUrl);
               eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
            }
         });
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#onRemotesReceived(java.util.List)
    */
   @Override
   public void onRemotesReceived(List<Remote> remotes)
   {
      Display d = GWT.create(Display.class);
      IDE.getInstance().openView(d.asView());
      bindDisplay(d);
      display.enableFetchButton(false);

      LinkedHashMap<String, String> remoteValues = new LinkedHashMap<String, String>();
      for (Remote remote : remotes)
      {
         remoteValues.put(remote.getUrl(), remote.getName());
      }

      display.setRemoteValues(remoteValues);
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      getBranches(projectId, false);
      getBranches(projectId, true);
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#setRemoteBranches(java.util.List)
    */
   @Override
   protected void setRemoteBranches(List<Branch> result)
   {
      display.setRemoteBranches(getRemoteBranchesNamesToDisplay(display.getRemoteDisplayValue()));
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#setLocalBranches(java.lang.String[])
    */
   @Override
   protected void setLocalBranches(List<Branch> branches)
   {
      String[] values = new String[branches.size()];
      for (int i = 0; i < branches.size(); i++)
      {
         values[i] = branches.get(i).getDisplayName();
      }
      display.setLocalBranches(values);
   }
}

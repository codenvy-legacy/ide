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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

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
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Presenter of view for pushing changes to remote repository.
 * The view is pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 4, 2011 9:53:07 AM anya $
 *
 */
public class PushToRemotePresenter extends HasBranchesPresenter implements PushToRemoteHandler
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
      void setRemoteValues(LinkedHashMap<String, String> values);

      String getRemoteDisplayValue();

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
    *
    */
   public PushToRemotePresenter()
   {
      IDE.addHandler(PushToRemoteEvent.TYPE, this);
   }

   /**
    * Bind pointed display with presenter.
    * 
    * @param d display
    */
   public void bindDisplay()
   {
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
            display.setRemoteBranches(getRemoteBranchesToDisplay(display.getRemoteDisplayValue()));
         }
      });

      display.getRemoteBranchesValue().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean empty = (event.getValue() == null || event.getValue().isEmpty());
            empty =
               empty || display.getLocalBranchesValue().getValue() == null
                  || display.getLocalBranchesValue().getValue().isEmpty();

            display.enablePushButton(!empty);
         }
      });

      display.getLocalBranchesValue().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean empty = (event.getValue() == null || event.getValue().isEmpty());
            empty =
               empty || display.getRemoteBranchesValue().getValue() == null
                  || display.getRemoteBranchesValue().getValue().isEmpty();
            display.enablePushButton(!empty);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemoteHandler#onPushToRemote(org.exoplatform.ide.git.client.push.PushToRemoteEvent)
    */
   @Override
   public void onPushToRemote(PushToRemoteEvent event)
   {
      if (makeSelectionCheck())
      {
         String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
         getRemotes(projectId);
      }
   }

   /**
    * Push changes to remote repository.
    */
   public void doPush()
   {
      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();

      final String remote = display.getRemoteValue().getValue();
      String localBranch = display.getLocalBranchesValue().getValue();
      String remoteBranch = display.getRemoteBranchesValue().getValue();
      IDE.getInstance().closeView(display.asView().getId());
      try
      {
         GitClientService.getInstance().push(vfs.getId(), project, new String[]{localBranch + ":" + remoteBranch},
            remote, false, new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.pushSuccess(remote), Type.INFO));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         handleError(e);
      }
   }

   private void handleError(Throwable t)
   {
      String errorMessage = (t.getMessage() != null) ? t.getMessage() : GitExtension.MESSAGES.pushFail();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#onRemotesReceived(java.util.List)
    */
   @Override
   public void onRemotesReceived(List<Remote> remotes)
   {
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      display = GWT.create(Display.class);
      bindDisplay();
      IDE.getInstance().openView(display.asView());

      LinkedHashMap<String, String> remoteValues = new LinkedHashMap<String, String>();
      for (Remote remote : remotes)
      {
         remoteValues.put(remote.getUrl(), remote.getName());
      }
      display.setRemoteValues(remoteValues);

      getBranches(projectId, false);
      getBranches(projectId, true);
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#setRemoteBranches(java.util.List)
    */
   @Override
   protected void setRemoteBranches(List<Branch> result)
   {
      display.setRemoteBranches(getRemoteBranchesToDisplay(display.getRemoteDisplayValue()));
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#setLocalBranches(java.lang.String[])
    */
   @Override
   protected void setLocalBranches(List<Branch> branches)
   {
      if (branches == null || branches.isEmpty())
      {
         display.setLocalBranches(new String[]{"master"});
         return;
      }

      String[] values = new String[branches.size()];
      for (int i = 0; i < branches.size(); i++)
      {
         values[i] = branches.get(i).getName();
      }
      display.setLocalBranches(values);
   }

}

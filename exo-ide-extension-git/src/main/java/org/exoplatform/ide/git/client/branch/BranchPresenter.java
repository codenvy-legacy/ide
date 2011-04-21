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
package org.exoplatform.ide.git.client.branch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.StringValueReceivedHandler;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
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

import java.util.List;

/**
 * Presenter of view for displaying and work with branches.
 * The view must be pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 8, 2011 12:02:49 PM anya $
 *
 */
public class BranchPresenter implements ShowBranchesHandler, ItemsSelectedHandler
{
   interface Display extends IsView
   {
      /**
       * Click handler for create branch button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCreateBranchButton();

      /**
       * Click handler for checkout branch button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCheckoutBranchButton();

      /**
       * Click handler for delete branch button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getDeleteBranchButton();

      /**
       * Click handler for cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

      /**
       * Returns the grid, responsible for displaying branches.
       * 
       * @return {@link ListGridItem}
       */
      ListGridItem<Branch> getBranchesGrid();

      /**
       * Get selected branch in grid.
       * 
       * @return {@link Branch} selected branch
       */
      Branch getSelectedBranch();

      /**
       * Change the enable state of the delete button.
       * 
       * @param enabled is enabled
       */
      void enableDeleteButton(boolean enabled);

      /**
       * Change the enable state of the checkout button.
       * 
       * @param enabled is enabled
       */
      void enableCheckoutButton(boolean enabled);
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * Working directory of Git repository.
    */
   private String workDir;

   /**
    * Selected items in browser tree.
    */
   private List<Item> selectedItems;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus
    */
   public BranchPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ShowBranchesEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getBranchesGrid().addSelectionHandler(new SelectionHandler<Branch>()
      {

         @Override
         public void onSelection(SelectionEvent<Branch> event)
         {
            boolean enabled = (event.getSelectedItem() != null && !event.getSelectedItem().isActive());
            display.enableDeleteButton(enabled);
            display.enableCheckoutButton(enabled);
         }
      });

      display.getCheckoutBranchButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doCheckoutBranch();
         }
      });

      display.getCreateBranchButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            askNewBranchName();
         }
      });

      display.getDeleteBranchButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            askDeleteBranch();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

   }

   /**
    * Get the location of the Git working directory, starting 
    * from pointed href.
    * 
    * @param href
    */
   private void getWorkDir(String href)
   {
      GitClientService.getInstance().getWorkDir(href, new AsyncRequestCallback<WorkDirResponse>()
      {

         @Override
         protected void onSuccess(WorkDirResponse result)
         {
            workDir = result.getWorkDir();
            workDir = workDir.endsWith("/.git") ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;

            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            bindDisplay(d);

            display.enableCheckoutButton(false);
            display.enableDeleteButton(false);
            getBranches(workDir);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            Dialogs.getInstance().showInfo(Messages.NOT_GIT_REPOSITORY);
         }
      });
   }

   /**
    * Get the list of branches.
    * 
    * @param workDir Git repository work tree location
    * @param remote get remote branches if <code>true</code>
    */
   public void getBranches(String workDir)
   {
      GitClientService.getInstance().branchList(workDir, false, new AsyncRequestCallback<List<Branch>>()
      {

         @Override
         protected void onSuccess(List<Branch> result)
         {
            display.getBranchesGrid().setValue(result);
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
    * @see org.exoplatform.ide.git.client.branch.ShowBranchesHandler#onShowBranches(org.exoplatform.ide.git.client.branch.ShowBranchesEvent)
    */
   @Override
   public void onShowBranches(ShowBranchesEvent event)
   {
      if (selectedItems != null && selectedItems.size() > 0)
      {
         getWorkDir(selectedItems.get(0).getHref());
      }
   }

   private void askNewBranchName()
   {
      Dialogs.getInstance().askForValue("Create new branch", "Type branch name:", "", new StringValueReceivedHandler()
      {

         @Override
         public void stringValueReceived(String value)
         {
            if (value != null)
            {
               doCreateBranch(value);
            }
         }
      });
   }

   /**
    * Create branch with pointed name.
    * 
    * @param name new branch's name
    */
   private void doCreateBranch(String name)
   {
      if (workDir == null)
         return;

      GitClientService.getInstance().branchCreate(workDir, name, null, new AsyncRequestCallback<Branch>()
      {

         @Override
         protected void onSuccess(Branch result)
         {
            getBranches(workDir);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : Messages.BRANCH_CREATE_FAILED;
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * Checkout the branch.
    */
   private void doCheckoutBranch()
   {
      String name = display.getSelectedBranch().getDisplayName();
      if (name == null || workDir == null)
      {
         return;
      }

      GitClientService.getInstance().branchCheckout(workDir, name, null, false, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            getBranches(workDir);
            eventBus.fireEvent(new RefreshBrowserEvent());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : Messages.BRANCH_CHECKOUT_FAILED;
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });

   }

   /**
    * Show ask dialog for deleting branch, if user confirms - delete branch.
    */
   private void askDeleteBranch()
   {
      final String name = display.getSelectedBranch().getName();
      if (workDir == null || name == null)
         return;

      Dialogs.getInstance().ask("Delete branch", "Are you sure you want to delete branch <b>" + name + "</b>?",
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDeleteBranch(name);
               }
            }
         });
   }

   /**
    * Delete branch with pointed name.
    * 
    * @param name name of branch to delete
    */
   private void doDeleteBranch(String name)
   {
      GitClientService.getInstance().branchDelete(workDir, name, true, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            getBranches(workDir);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : Messages.BRANCH_DELETE_FAILED;
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

}

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

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

/**
 * Presenter of view for displaying and work with branches.
 * The view must be pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 8, 2011 12:02:49 PM anya $
 *
 */
public class BranchPresenter extends GitPresenter implements ShowBranchesHandler
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
       * Click handler for close button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCloseButton();

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
    *
    */
   public BranchPresenter()
   {
      IDE.addHandler(ShowBranchesEvent.TYPE, this);
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

      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

   }

   /**
    * Get the list of branches.
    * 
    * @param workDir Git repository work tree location
    * @param remote get remote branches if <code>true</code>
    */
   public void getBranches(String projectId)
   {
      GitClientService.getInstance().branchList(vfs.getId(), projectId, false, new AsyncRequestCallback<List<Branch>>()
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
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.branchesListFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.ShowBranchesHandler#onShowBranches(org.exoplatform.ide.git.client.branch.ShowBranchesEvent)
    */
   @Override
   public void onShowBranches(ShowBranchesEvent event)
   {
      if (makeSelectionCheck())
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);

         display.enableCheckoutButton(false);
         display.enableDeleteButton(false);
         String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
         getBranches(projectId);
      }
   }

   private void askNewBranchName()
   {
      Dialogs.getInstance().askForValue(GitExtension.MESSAGES.branchCreateNew(), GitExtension.MESSAGES.branchTypeNew(), "", new StringValueReceivedHandler()
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
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      
      GitClientService.getInstance().branchCreate(vfs.getId(), projectId, name, null, new AsyncRequestCallback<Branch>()
      {

         @Override
         protected void onSuccess(Branch result)
         {
            getBranches(projectId);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.branchCreateFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * Checkout the branch.
    */
   private void doCheckoutBranch()
   {
      String name = display.getSelectedBranch().getDisplayName();
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      if (name == null)
      {
         return;
      }

      GitClientService.getInstance().branchCheckout(vfs.getId(), projectId, name, null, false, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            getBranches(projectId);
            IDE.fireEvent(new RefreshBrowserEvent());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.branchCheckoutFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });

   }

   /**
    * Show ask dialog for deleting branch, if user confirms - delete branch.
    */
   private void askDeleteBranch()
   {
      final String name = display.getSelectedBranch().getName();

      Dialogs.getInstance().ask(GitExtension.MESSAGES.branchDelete(), GitExtension.MESSAGES.branchDeleteAsk(name),
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
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      GitClientService.getInstance().branchDelete(vfs.getId(), projectId, name, true, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            getBranches(projectId);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.branchDeleteFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }
}

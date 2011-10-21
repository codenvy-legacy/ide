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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Branch;

/**
 * View for displaying branches and work with it.
 * Must be pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 8, 2011 9:50:54 AM anya $
 *
 */
public class BranchView extends ViewImpl implements BranchPresenter.Display
{
   private static final int HEIGHT = 300;

   private static final int WIDTH = 470;

   public static final String ID = "ideBranchView";

   /*Elements IDs*/

   private static final String CREATE_BUTTON_ID = "ideBranchViewCreateButton";

   private static final String CHECKOUT_BUTTON_ID = "ideBranchViewCheckoutButton";

   private static final String DELETE_BUTTON_ID = "ideBranchViewDeleteButton";

   private static final String CLOSE_BUTTON_ID = "ideBranchViewCloseButton";

   /**
    * Create branch button.
    */
   @UiField
   ImageButton createButton;

   /**
    * Checkout branch button.
    */
   @UiField
   ImageButton checkoutButton;

   /**
    * Delete branch button.
    */
   @UiField
   ImageButton deleteButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton closeButton;

   @UiField
   BranchGrid branchGrid;

   interface BranchViewUiBinder extends UiBinder<Widget, BranchView>
   {
   }

   private static BranchViewUiBinder uiBinder = GWT.create(BranchViewUiBinder.class);

   public BranchView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.branchTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      checkoutButton.setButtonId(CHECKOUT_BUTTON_ID);
      createButton.setButtonId(CREATE_BUTTON_ID);
      deleteButton.setButtonId(DELETE_BUTTON_ID);
      closeButton.setButtonId(CLOSE_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getCreateBranchButton()
    */
   @Override
   public HasClickHandlers getCreateBranchButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getCheckoutBranchButton()
    */
   @Override
   public HasClickHandlers getCheckoutBranchButton()
   {
      return checkoutButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getDeleteBranchButton()
    */
   @Override
   public HasClickHandlers getDeleteBranchButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getBranchesGrid()
    */
   @Override
   public ListGridItem<Branch> getBranchesGrid()
   {
      return branchGrid;
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getSelectedBranch()
    */
   @Override
   public Branch getSelectedBranch()
   {
      return branchGrid.getSelectedBranch();
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#enableDeleteButton(boolean)
    */
   @Override
   public void enableDeleteButton(boolean enabled)
   {
      deleteButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#enableCheckoutButton(boolean)
    */
   @Override
   public void enableCheckoutButton(boolean enabled)
   {
      checkoutButton.setEnabled(enabled);
   }

}

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

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;
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

   public static final String TYPE = "modal";

   public static final String TITLE = "Branches";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   /*Elements IDs*/

   private static final String CREATE_BUTTON_ID = "ideBranchViewCreateButton";
   
   private static final String CHECKOUT_BUTTON_ID = "ideBranchViewCheckoutButton";

   private static final String DELETE_BUTTON_ID = "ideBranchViewDeleteButton";

   private static final String CLOSE_BUTTON_ID = "ideBranchViewCloseButton";
   
   /* Element's titles*/
   private static final String CREATE_BUTTON_TITLE = "Create";
   
   private static final String CHECKOUT_BUTTON_TITLE = "Checkout";

   private static final String DELETE_BUTTON_TITLE = "Delete";

   private static final String CLOSE_BUTTON_TITLE = "Close";
   
   /**
    * Create branch button.
    */
   private IButton createButton;
   
   /**
    * Checkout branch button.
    */
   private IButton checkoutButton;
   
   /**
    * Delete branch button.
    */
   private IButton deleteButton;
   
   /**
    * Cancel button.
    */
   private IButton closeButton;
   
   private BranchGrid branchGrid;

   public BranchView()
   {
      super(ID, TYPE, TITLE, null, WIDTH, HEIGHT);
      
      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(10);
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      
      Border border = new Border();
      border.setWidth("100%");
      branchGrid = new BranchGrid();
      branchGrid.setWidth("100%");
      branchGrid.setHeight(180);
      border.add(branchGrid);
      mainLayout.add(border);
      
      addButtonsLayout(mainLayout);
      
      add(mainLayout);
   }
   
   /**
    * Add buttons to the pointed panel.
    * 
    * @param panel
    */
   private void addButtonsLayout(VerticalPanel panel)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT+ 10 + "px");
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      buttonsLayout.setSpacing(5);

      createButton = createButton(CREATE_BUTTON_ID, CREATE_BUTTON_TITLE, GitClientBundle.INSTANCE.add(), GitClientBundle.INSTANCE.addDisabled());
      checkoutButton = createButton(CHECKOUT_BUTTON_ID, CHECKOUT_BUTTON_TITLE, GitClientBundle.INSTANCE.ok(), GitClientBundle.INSTANCE.okDisabled());
      deleteButton = createButton(DELETE_BUTTON_ID, DELETE_BUTTON_TITLE, GitClientBundle.INSTANCE.remove(), GitClientBundle.INSTANCE.removeDisabled());
      closeButton = createButton(CLOSE_BUTTON_ID, CLOSE_BUTTON_TITLE, GitClientBundle.INSTANCE.cancel(), GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(checkoutButton);
      buttonsLayout.add(createButton);
      buttonsLayout.add(deleteButton);
      buttonsLayout.add(closeButton);
      
      buttonsLayout.setCellWidth(closeButton, "100%");
      buttonsLayout.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
      panel.add(buttonsLayout);
   }

   /**
    * Creates button.
    * 
    * @param id button's id
    * @param title button's title
    * @param icon button's normal icon
    * @param disabledIcon button's icon in disabled state
    * @return {@link IButton}
    */
   private IButton createButton(String id, String title, ImageResource icon, ImageResource disabledIcon)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setIcon(icon.getURL(), disabledIcon.getURL());
      button.setHeight(BUTTON_HEIGHT);
      button.setWidth(BUTTON_WIDTH);
      return button;
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

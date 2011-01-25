/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.module.groovy.classpath.ui;

import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.layout.HLayout;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.module.groovy.Images;

import java.util.List;

/**
 * Form for choosing sources for class path.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class ChooseSourcePathForm extends DialogWindow implements ChooseSourcePathPresenter.Display
{
   public static final int WIDTH = 450;

   public static final int HEIGHT = 300;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   public static final String ID = "ideChooseSourcePathForm";

   //IDs for Selenium tests:

   private final String ID_CANCEL_BUTTON = "ideChooseSourcePathFormCancelButton";

   private final String ID_OK_BUTTON = "ideChooseSourcePathFormOkButton";

   private final String ID_TREE_GRID = "ideChooseSourcePathFormTreeGrid";

   private final String TITLE = "Choose source path";

   /**
    * Cancel button.
    */
   private IButton cancelButton;

   /**
    * Confirm button.
    */
   private IButton okButton;

   /**
    * Tree for displaying items.
    */
   private ItemTreeGrid<Item> treeGrid;

   /**
    * @param eventBus handler manager
    */
   public ChooseSourcePathForm(HandlerManager eventBus)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setTitle(TITLE);

      VLayout mainLayout = new VLayout();
      mainLayout.setWidth100();
      mainLayout.setHeight100();
      mainLayout.setMembersMargin(15);
      mainLayout.setPadding(15);
      treeGrid = new ItemTreeGrid<Item>(ID_TREE_GRID);
      treeGrid.setShowHeader(false);

      mainLayout.addMember(treeGrid);
      mainLayout.addMember(createButtonsLayout());

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

      addItem(mainLayout);
      show();
   }

   /**
    * Create layout with buttons.
    * 
    * @return {@link HLayout} layout with buttons
    */
   private HLayout createButtonsLayout()
   {
      HLayout hLayout = new HLayout();
      hLayout.setHeight(BUTTON_HEIGHT);
      hLayout.setAutoWidth();
      hLayout.setLayoutAlign(Alignment.CENTER);
      hLayout.setMembersMargin(10);
      hLayout.setLayoutBottomMargin(5);

      okButton = createButton(ID_OK_BUTTON, "Ok", Images.Buttons.OK);
      cancelButton = createButton(ID_CANCEL_BUTTON, "Cancel", Images.Buttons.NO);

      hLayout.addMember(okButton);
      hLayout.addMember(cancelButton);

      return hLayout;
   }

   /**
    * Create button.
    * 
    * @param id button's id
    * @param title button's title
    * @param icon button's icon
    * @return {@link IButton} created button
    */
   private IButton createButton(String id, String title, String icon)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setIcon(icon);
      button.setWidth(BUTTON_WIDTH);
      button.setHeight(BUTTON_HEIGHT);
      return button;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#getOkButton()
    */
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#closeView()
    */
   public void closeView()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#getItemsTree()
    */
   public TreeGridItem<Item> getItemsTree()
   {
      return treeGrid;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#getSelectedItems()
    */
   public List<Item> getSelectedItems()
   {
      return treeGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#enableOkButtonState(boolean)
    */
   public void enableOkButtonState(boolean isEnabled)
   {
      if (isEnabled)
      {
         okButton.enable();
      }
      else
      {
         okButton.disable();
      }
   }

}

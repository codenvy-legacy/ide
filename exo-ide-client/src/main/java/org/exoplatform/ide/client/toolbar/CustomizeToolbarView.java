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
package org.exoplatform.ide.client.toolbar;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CustomizeToolbarView extends ViewImpl implements
   org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display
{

   /**
    * 
    */
   private static final String ID = "ide.core.customize-toolbar";

   /**
    * 
    */
   private static final String TITLE = IDE.PREFERENCES_CONSTANT.customizeToolbarTitle();

   /**
    * 
    */
   private static final int WIDTH = 700;

   /**
    * 
    */
   private static final int HEIGHT = 360;

   /**
    * 
    */
   private static CustomizeToolbarViewUiBinder uiBinder = GWT.create(CustomizeToolbarViewUiBinder.class);

   interface CustomizeToolbarViewUiBinder extends UiBinder<Widget, CustomizeToolbarView>
   {
   }

   /**
    * 
    */
   @UiField
   CommandsListGrid availableCommandsListGrid;

   /**
    * 
    */
   @UiField
   ToolbarItemListGrid toolbarItemsListGrid;

   /**
    * 
    */
   @UiField
   ImageButton addCommandButton, addDelimiterButton, deleteButton, moveUpButton, moveDownButton, okButton,
            cancelButton, defaultsButton;

   /**
    * 
    */
   public CustomizeToolbarView()
   {
      super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.customizeToolbar()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getCommandsListGrid()
    */
   @Override
   public ListGridItem<CommandItemEx> getCommandsListGrid()
   {
      return availableCommandsListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getToolbarItemsListGrid()
    */
   @Override
   public ListGridItem<ToolbarItem> getToolbarItemsListGrid()
   {
      return toolbarItemsListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#selectToolbarItem(org.exoplatform.ide.client.toolbar.ToolbarItem)
    */
   @Override
   public void selectToolbarItem(ToolbarItem item)
   {
      toolbarItemsListGrid.selectItem(item);
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getAddCommandButton()
    */
   @Override
   public HasClickHandlers getAddCommandButton()
   {
      return addCommandButton;
   }

   @Override
   public HasClickHandlers getAddDelimiterButton()
   {
      return addDelimiterButton;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getMoveUpButton()
    */
   @Override
   public HasClickHandlers getMoveUpButton()
   {
      return moveUpButton;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getMoveDownButton()
    */
   @Override
   public HasClickHandlers getMoveDownButton()
   {
      return moveDownButton;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#getDefaultsButton()
    */
   @Override
   public HasClickHandlers getDefaultsButton()
   {
      return defaultsButton;
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#setAddCommandButtonEnabled(boolean)
    */
   @Override
   public void setAddCommandButtonEnabled(boolean enabled)
   {
      addCommandButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#setAddDelimiterButtonEnabled(boolean)
    */
   @Override
   public void setAddDelimiterButtonEnabled(boolean enabled)
   {
      addDelimiterButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#setDeleteButtonEnabled(boolean)
    */
   @Override
   public void setDeleteButtonEnabled(boolean enabled)
   {
      deleteButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#setMoveUpButtonEnabled(boolean)
    */
   @Override
   public void setMoveUpButtonEnabled(boolean enabled)
   {
      moveUpButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter.Display#setMoveDownButtonEnabled(boolean)
    */
   @Override
   public void setMoveDownButtonEnabled(boolean enabled)
   {
      moveDownButton.setEnabled(enabled);
   }

}

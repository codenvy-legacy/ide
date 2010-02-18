/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.toolbar.customize;

import org.exoplatform.gwtframework.ui.component.ListGridItem;
import org.exoplatform.gwtframework.ui.smartgwt.component.IButton;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CustomizeToolbarForm extends DialogWindow implements CustomizeToolbarPresenter.Display
{

   private static final int WIDTH = 700;

   private static final int HEIGHT = 360;

   private static final String TITLE = "Customize Toolbar";

   private static final String BUTTON_OK = "OK";

   private static final String BUTTON_CANCEL = "Cancel";

   private static final String BUTTON_RESTOREDEFAULTS = "Defaults";

   private static final String BUTTON_ADD = "Add";

   private static final String BUTTON_DELIMITER = "Delimiter";

   private static final String BUTTON_DELETE = "Delete";

   private static final String BUTTON_MOVEUP = "Move Up";

   private static final String BUTTON_MOVEDOWN = "Move Down";

   private static final int BUTTONS_WIDTH = 120;

   private static final int CONTROL_BUTTONS_WIDTH = 100;

   private CustomizeToolbarPresenter presenter;

   private VLayout vLayout;

   private HLayout hLayout;

   private IButton addCommandButton;

   private IButton addDelimiterButton;

   private IButton deleteCommandButton;

   private IButton moveUpButton;

   private IButton moveDownButton;

   private CommandItemExListGrid commandItemListGrid;

   private ToolbarItemListGrid toolbarItemListGrid;

   private IButton okButton;

   private IButton cancelButton;

   private IButton defaultsButton;

   public CustomizeToolbarForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, WIDTH, HEIGHT);
      setShowMaximizeButton(true);
      setTitle(TITLE);
      setCanDragResize(true);

      vLayout = new VLayout();
      vLayout.setMargin(5);
      addItem(vLayout);

      hLayout = new HLayout();
      vLayout.addMember(hLayout);
      hLayout.setMargin(5);
      hLayout.setWidth100();
      //hLayout.setBackgroundColor("#FFEEAA");

      createCommandsListGrid();
      createActionsForm();
      createToolbarItemsListGrid();
      createButtonsForm();

      show();

      presenter = new CustomizeToolbarPresenter(eventBus, context);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

   }

   private void createCommandsListGrid()
   {
      commandItemListGrid = new CommandItemExListGrid();
      hLayout.addMember(commandItemListGrid);
   }

   private void createActionsForm()
   {
      VLayout buttonsLayout = new VLayout();
      buttonsLayout.setWidth(159);
      buttonsLayout.setHeight100();
      //buttonsLayout.setBackgroundColor("#FFAAEE");
      hLayout.addMember(buttonsLayout);

      buttonsLayout.setAlign(Alignment.CENTER);

      buttonsLayout.setMargin(17);
      buttonsLayout.setMembersMargin(15);

      addCommandButton = new IButton(BUTTON_ADD);
      addCommandButton.setWidth(BUTTONS_WIDTH);
      addCommandButton.setHeight(22);
      addCommandButton.setIcon(Images.Buttons.ADD);

      addDelimiterButton = new IButton(BUTTON_DELIMITER);
      addDelimiterButton.setWidth(BUTTONS_WIDTH);
      addDelimiterButton.setHeight(22);
      addDelimiterButton.setIcon(Images.Buttons.ADD);

      deleteCommandButton = new IButton(BUTTON_DELETE);
      deleteCommandButton.setWidth(BUTTONS_WIDTH);
      deleteCommandButton.setHeight(22);
      deleteCommandButton.setIcon(Images.Buttons.DELETE);

      moveUpButton = new IButton(BUTTON_MOVEUP);
      moveUpButton.setWidth(BUTTONS_WIDTH);
      moveUpButton.setHeight(22);
      moveUpButton.setIcon(Images.Toolbar.UP);

      moveDownButton = new IButton(BUTTON_MOVEDOWN);
      moveDownButton.setWidth(BUTTONS_WIDTH);
      moveDownButton.setHeight(22);
      moveDownButton.setIcon(Images.Toolbar.DOWN);

      buttonsLayout.addMember(addCommandButton);
      buttonsLayout.addMember(addDelimiterButton);
      buttonsLayout.addMember(deleteCommandButton);

      Layout l = new Layout();
      l.setHeight(20);
      buttonsLayout.addMember(l);

      buttonsLayout.addMember(moveUpButton);
      buttonsLayout.addMember(moveDownButton);
   }

   private void createToolbarItemsListGrid()
   {
      toolbarItemListGrid = new ToolbarItemListGrid();
      hLayout.addMember(toolbarItemListGrid);
   }

   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(10);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      okButton = new IButton(BUTTON_OK);
      okButton.setWidth(CONTROL_BUTTONS_WIDTH);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton(BUTTON_CANCEL);
      cancelButton.setWidth(CONTROL_BUTTONS_WIDTH);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL);

      defaultsButton = new IButton(BUTTON_RESTOREDEFAULTS);
      defaultsButton.setWidth(CONTROL_BUTTONS_WIDTH);
      defaultsButton.setHeight(22);
      defaultsButton.setIcon(Images.Toolbar.DEFAULTS);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(3);
      tbi.setButtons(okButton, delimiter1, cancelButton, delimiter2, defaultsButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();

      vLayout.addMember(buttonsForm);
   }

   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   public void closeForm()
   {
      destroy();
   }

   public HasClickHandlers getAddCommandButton()
   {
      return addCommandButton;
   }

   public HasClickHandlers getAddDelimiterButton()
   {
      return addDelimiterButton;
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getApplyButton()
   {
      return okButton;
   }

   public HasClickHandlers getDeleteCommandButton()
   {
      return deleteCommandButton;
   }

   public HasClickHandlers getMoveDownButton()
   {
      return moveDownButton;
   }

   public HasClickHandlers getMoveUpButton()
   {
      return moveUpButton;
   }
   
   public ListGridItem<CommandItemEx> getCommandItemListGrid()
   {
      return commandItemListGrid;
   }

   public ListGridItem<org.exoplatform.ideall.client.toolbar.customize.ToolbarItem> getToolbarItemsListGrid()
   {
      return toolbarItemListGrid;
   }

   public void disableAddCommandButton()
   {
      addCommandButton.disable();
   }

   public void disableAddDelimiterButton()
   {
      addDelimiterButton.disable();
   }

   public void disableDeleteCommandButton()
   {
      deleteCommandButton.disable();
   }

   public void disableMoveDownButton()
   {
      moveDownButton.disable();
   }

   public void disableMoveUpButton()
   {
      moveUpButton.disable();
   }

   public void enableAddCommandButton()
   {
      addCommandButton.enable();
   }

   public void enableAddDelimiterButton()
   {
      addDelimiterButton.enable();
   }

   public void enableDeleteCommandButton()
   {
      deleteCommandButton.enable();
   }

   public void enableMoveDownButton()
   {
      moveDownButton.enable();
   }

   public void enableMoveUpButton()
   {
      moveUpButton.enable();
   }

   public HasClickHandlers getDefaultsButton()
   {
      return defaultsButton;
   }

   public void toolbarItemsListGridSelectItem(org.exoplatform.ideall.client.toolbar.customize.ToolbarItem item)
   {
      toolbarItemListGrid.selectItem(item);
   }

}

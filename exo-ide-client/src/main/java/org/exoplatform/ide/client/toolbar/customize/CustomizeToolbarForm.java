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
package org.exoplatform.ide.client.toolbar.customize;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.google.gwt.user.client.ui.HorizontalPanel;

import com.google.gwt.user.client.ui.VerticalPanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

import java.util.List;

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

   private static final String ID = "ideCustomizeToolbarForm";

   private static final String ID_ADD_BUTTON = "ideCustomizeToolbarFormAddButton";

   private static final String ID_OK_BUTTON = "ideCustomizeToolbarFormOkButton";

   private static final String ID_CANCEL_BUTTON = "ideCustomizeToolbarFormCancelButton";

   private static final String ID_RESTOREDEFAULTS_BUTTON = "ideCustomizeToolbarFormRestoreDefaultsButton";

   private static final String ID_DELIMITER_BUTTON = "ideCustomizeToolbarFormDelimeterButton";

   private static final String ID_DELETE_BUTTON = "ideCustomizeToolbarFormDeleteButton";

   private static final String ID_MOVEUP_BUTTON = "ideCustomizeToolbarFormMoveUpButton";

   private static final String ID_MOVEDOWN_BUTTON = "ideCustomizeToolbarFormMoveDownButton";

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

   private VerticalPanel vLayout;

   private HorizontalPanel hLayout;

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

   public CustomizeToolbarForm(HandlerManager eventBus, ApplicationSettings applicationSettings, List<Control> controls)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setCanMaximize(true);
      setTitle(TITLE);
      //TODO setCanDragResize(true);

      vLayout = new VerticalPanel();
      vLayout.setWidth("100%");
      vLayout.setHeight("100%");
      vLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      setWidget(vLayout);

      hLayout = new HorizontalPanel();
      vLayout.add(hLayout);
      
      hLayout.setWidth("100%");
      hLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      createCommandsListGrid();
      createActionsForm();
      createToolbarItemsListGrid();
      createButtonsForm();

      show();

      presenter = new CustomizeToolbarPresenter(eventBus, applicationSettings, controls);
      presenter.bindDisplay(this);

   }

   private void createCommandsListGrid()
   {
      commandItemListGrid = new CommandItemExListGrid();
      commandItemListGrid.setHeight(240);
      hLayout.add(commandItemListGrid);
   }

   private void createActionsForm()
   {
      VerticalPanel buttonsLayout = new VerticalPanel();
      buttonsLayout.setWidth(159 + "px");
      buttonsLayout.setHeight("100%");
      hLayout.add(buttonsLayout);

      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      buttonsLayout.setSpacing(15);

      addCommandButton = new IButton(BUTTON_ADD);
      addCommandButton.setID(ID_ADD_BUTTON);
      addCommandButton.setWidth(BUTTONS_WIDTH);
      addCommandButton.setHeight(22);
      addCommandButton.setIcon(Images.Buttons.ADD);

      addDelimiterButton = new IButton(BUTTON_DELIMITER);
      addDelimiterButton.setID(ID_DELIMITER_BUTTON);
      addDelimiterButton.setWidth(BUTTONS_WIDTH);
      addDelimiterButton.setHeight(22);
      addDelimiterButton.setIcon(Images.Buttons.ADD);

      deleteCommandButton = new IButton(BUTTON_DELETE);
      deleteCommandButton.setID(ID_DELETE_BUTTON);
      deleteCommandButton.setWidth(BUTTONS_WIDTH);
      deleteCommandButton.setHeight(22);
      deleteCommandButton.setIcon(Images.Buttons.DELETE);

      moveUpButton = new IButton(BUTTON_MOVEUP);
      moveUpButton.setID(ID_MOVEUP_BUTTON);
      moveUpButton.setWidth(BUTTONS_WIDTH);
      moveUpButton.setHeight(22);
      moveUpButton.setIcon(Images.Buttons.UP);

      moveDownButton = new IButton(BUTTON_MOVEDOWN);
      moveDownButton.setID(ID_MOVEDOWN_BUTTON);
      moveDownButton.setWidth(BUTTONS_WIDTH);
      moveDownButton.setHeight(22);
      moveDownButton.setIcon(Images.Buttons.DOWN);

      buttonsLayout.add(addCommandButton);
      buttonsLayout.add(addDelimiterButton);
      buttonsLayout.add(deleteCommandButton);

      HorizontalPanel l = new HorizontalPanel();
      l.setHeight(20+"px");
      buttonsLayout.add(l);

      buttonsLayout.add(moveUpButton);
      buttonsLayout.add(moveDownButton);
   }

   private void createToolbarItemsListGrid()
   {
      toolbarItemListGrid = new ToolbarItemListGrid();
      toolbarItemListGrid.setHeight(240);
      hLayout.add(toolbarItemListGrid);
   }

   private void createButtonsForm()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(22 + "px");
      buttonsLayout.setSpacing(5);

      okButton = new IButton(BUTTON_OK);
      okButton.setID(ID_OK_BUTTON);
      okButton.setWidth(CONTROL_BUTTONS_WIDTH);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton(BUTTON_CANCEL);
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(CONTROL_BUTTONS_WIDTH);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL);

      defaultsButton = new IButton(BUTTON_RESTOREDEFAULTS);
      defaultsButton.setID(ID_RESTOREDEFAULTS_BUTTON);
      defaultsButton.setWidth(CONTROL_BUTTONS_WIDTH);
      defaultsButton.setHeight(22);
      defaultsButton.setIcon(Images.Buttons.DEFAULTS);

      buttonsLayout.add(okButton);
      buttonsLayout.add(cancelButton);
      buttonsLayout.add(defaultsButton);
      vLayout.add(buttonsLayout);
   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
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

   public ListGridItem<org.exoplatform.ide.client.toolbar.customize.ToolbarItem> getToolbarItemsListGrid()
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

   public void toolbarItemsListGridSelectItem(org.exoplatform.ide.client.toolbar.customize.ToolbarItem item)
   {
      toolbarItemListGrid.selectItem(item);
   }

}

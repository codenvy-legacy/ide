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
package org.exoplatform.ide.client.toolbar;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.ui.IDEDialogWindow;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CustomizeToolbarForm extends IDEDialogWindow implements CustomizeToolbarPresenter.Display
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

   private static final String TITLE = IDE.PREFERENCES_CONSTANT.customizeToolbarTitle();

   private static final String BUTTON_OK = IDE.IDE_LOCALIZATION_CONSTANT.okButton();

   private static final String BUTTON_CANCEL = IDE.IDE_LOCALIZATION_CONSTANT.cancelButton();

   private static final String BUTTON_RESTOREDEFAULTS = IDE.PREFERENCES_CONSTANT.customizeToolbarDefaultButton();

   private static final String BUTTON_ADD = IDE.IDE_LOCALIZATION_CONSTANT.addButton();

   private static final String BUTTON_DELIMITER = IDE.PREFERENCES_CONSTANT.customizeToolbarDelimiterButton();

   private static final String BUTTON_DELETE = IDE.IDE_LOCALIZATION_CONSTANT.deleteButton();

   private static final String BUTTON_MOVEUP = IDE.PREFERENCES_CONSTANT.customizeToolbarMoveUpButton();

   private static final String BUTTON_MOVEDOWN = IDE.PREFERENCES_CONSTANT.customizeToolbarMoveDownButton();

   private static final String BUTTONS_WIDTH = "120px";
   
   private static final String BUTTONS_HEIGHT = "22px";
   
   private static final String CONTROL_BUTTONS_WIDTH = "100px";

   private CustomizeToolbarPresenter presenter;

   private VerticalPanel vLayout;

   private HorizontalPanel hLayout;

   private ImageButton addCommandButton;

   private ImageButton addDelimiterButton;

   private ImageButton deleteCommandButton;

   private ImageButton moveUpButton;

   private ImageButton moveDownButton;

   private CommandItemExListGrid commandItemListGrid;

   private ToolbarItemListGrid toolbarItemListGrid;

   private ImageButton okButton;

   private ImageButton cancelButton;

   private ImageButton defaultsButton;

   public CustomizeToolbarForm(HandlerManager eventBus, ApplicationSettings applicationSettings, List<Control> controls)
   {
      super(WIDTH, HEIGHT, ID);
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

      addCommandButton = new ImageButton(BUTTON_ADD);
      addCommandButton.setButtonId(ID_ADD_BUTTON);
      addCommandButton.setWidth(BUTTONS_WIDTH);
      addCommandButton.setHeight(BUTTONS_HEIGHT);
      addCommandButton.setImage(new Image(Images.Buttons.ADD));

      addDelimiterButton = new ImageButton(BUTTON_DELIMITER);
      addDelimiterButton.setButtonId(ID_DELIMITER_BUTTON);
      addDelimiterButton.setWidth(BUTTONS_WIDTH);
      addDelimiterButton.setHeight(BUTTONS_HEIGHT);
      addDelimiterButton.setImage(new Image(Images.Buttons.ADD));

      deleteCommandButton = new ImageButton(BUTTON_DELETE);
      deleteCommandButton.setButtonId(ID_DELETE_BUTTON);
      deleteCommandButton.setWidth(BUTTONS_WIDTH);
      deleteCommandButton.setHeight(BUTTONS_HEIGHT);
      deleteCommandButton.setImage(new Image(Images.Buttons.DELETE));

      moveUpButton = new ImageButton(BUTTON_MOVEUP);
      moveUpButton.setButtonId(ID_MOVEUP_BUTTON);
      moveUpButton.setWidth(BUTTONS_WIDTH);
      moveUpButton.setHeight(BUTTONS_HEIGHT);
      moveUpButton.setImage(new Image(Images.Buttons.UP));

      moveDownButton = new ImageButton(BUTTON_MOVEDOWN);
      moveDownButton.setButtonId(ID_MOVEDOWN_BUTTON);
      moveDownButton.setWidth(BUTTONS_WIDTH);
      moveDownButton.setHeight(BUTTONS_HEIGHT);
      moveDownButton.setImage(new Image(Images.Buttons.DOWN));

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

      okButton = new ImageButton(BUTTON_OK);
      okButton.setButtonId(ID_OK_BUTTON);
      okButton.setWidth(CONTROL_BUTTONS_WIDTH);
      okButton.setHeight(BUTTONS_HEIGHT);
      okButton.setImage(new Image(Images.Buttons.YES));

      cancelButton = new ImageButton(BUTTON_CANCEL);
      cancelButton.setButtonId(ID_CANCEL_BUTTON);
      cancelButton.setWidth(CONTROL_BUTTONS_WIDTH);
      cancelButton.setHeight(BUTTONS_HEIGHT);
      cancelButton.setImage(new Image(Images.Buttons.CANCEL));

      defaultsButton = new ImageButton(BUTTON_RESTOREDEFAULTS);
      defaultsButton.setButtonId(ID_RESTOREDEFAULTS_BUTTON);
      defaultsButton.setWidth(CONTROL_BUTTONS_WIDTH);
      defaultsButton.setHeight(BUTTONS_HEIGHT);
      defaultsButton.setImage(new Image(Images.Buttons.DEFAULTS));

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

   public ListGridItem<org.exoplatform.ide.client.toolbar.ToolbarItem> getToolbarItemsListGrid()
   {
      return toolbarItemListGrid;
   }

   public void disableAddCommandButton()
   {
      addCommandButton.setEnabled(false);
   }

   public void disableAddDelimiterButton()
   {
      addDelimiterButton.setEnabled(false);
   }

   public void disableDeleteCommandButton()
   {
      deleteCommandButton.setEnabled(false);
   }

   public void disableMoveDownButton()
   {
      moveDownButton.setEnabled(false);
   }

   public void disableMoveUpButton()
   {
      moveUpButton.setEnabled(false);
   }

   public void enableAddCommandButton()
   {
      addCommandButton.setEnabled(true);
   }

   public void enableAddDelimiterButton()
   {
      addDelimiterButton.setEnabled(true);
   }

   public void enableDeleteCommandButton()
   {
      deleteCommandButton.setEnabled(true);
   }

   public void enableMoveDownButton()
   {
      moveDownButton.setEnabled(true);
   }

   public void enableMoveUpButton()
   {
      moveUpButton.setEnabled(true);
   }

   public HasClickHandlers getDefaultsButton()
   {
      return defaultsButton;
   }

   public void toolbarItemsListGridSelectItem(org.exoplatform.ide.client.toolbar.ToolbarItem item)
   {
      toolbarItemListGrid.selectItem(item);
   }

}

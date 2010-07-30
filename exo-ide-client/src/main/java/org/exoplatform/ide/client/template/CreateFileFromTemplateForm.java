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
package org.exoplatform.ideall.client.template;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.framework.ui.DialogWindow;
import org.exoplatform.ideall.client.model.template.Template;
import org.exoplatform.ideall.client.module.vfs.api.Item;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
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

public class CreateFileFromTemplateForm extends DialogWindow implements CreateFileFromTemplatePresenter.Display
{

   public static final int WIDTH = 550;

   public static final int HEIGHT = 350;
   
   private static final String ID = "ideCreateFileFromTemplateForm";
   
   private static final String ID_CREATE_BUTTON = "ideCreateFileFromTemplateFormCreateButton";
   
   private static final String ID_CANCEL_BUTTON = "ideCreateFileFromTemplateFormCancelButton";
   
   private static final String ID_DELETE_BUTTON = "ideCreateFileFromTemplateFormDeleteButton";
   
   private static final String ID_DYNAMIC_FORM = "ideCreateFileFromTemplateFormDynamicForm";
   
   private static final String FILE_NAME_FIELD = "ideCreateFileFromTemplateFormFileNameField";
   
   private CreateFileFromTemplatePresenter presenter;

   private VLayout windowLayout;

   private IButton createButton;

   private IButton cancelButton;

   private IButton deleteButton;

   private TemplateListGrid templateListGrid;

   private TextField fileNameField;

   public CreateFileFromTemplateForm(HandlerManager eventBus, List<Item> selectedItems, List<Template> templateList)
   {
      super(eventBus, WIDTH, HEIGHT, ID);

      this.eventBus = eventBus;

      setTitle("Create file");
      setCanDragResize(true);
      setShowMaximizeButton(true);

      windowLayout = new VLayout();
      windowLayout.setMargin(10);
      addItem(windowLayout);

      createTypeLayout();

      Layout l = new Layout();
      l.setHeight(10);
      windowLayout.addMember(l);

      windowLayout.addMember(getActionsForm());

      show();

      presenter = new CreateFileFromTemplatePresenter(eventBus, selectedItems, templateList);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
   }

   private void createTypeLayout()
   {
      templateListGrid = new TemplateListGrid();
      templateListGrid.setCanFocus(false);
      windowLayout.addMember(templateListGrid);
   }

   private HLayout getActionsForm()
   {
      HLayout actionsLayout = new HLayout();
      actionsLayout.setHeight(35);
      actionsLayout.setWidth100();

      DynamicForm form = new DynamicForm();
      form.setID(ID_DYNAMIC_FORM);
      fileNameField = new TextField("Name","File Name");
      fileNameField.setName(FILE_NAME_FIELD);
      fileNameField.setWidth(200);
      fileNameField.setWrapTitle(false);
      form.setColWidths("*", "195");
      form.setItems(fileNameField);
      actionsLayout.addMember(form);

      Layout l = new Layout();
      l.setWidth100();
      actionsLayout.addMember(l);

      actionsLayout.addMember(getButtonsForm());
      return actionsLayout;
   }

   private DynamicForm getButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      createButton = new IButton("Create");
      createButton.setID(ID_CREATE_BUTTON);
      createButton.setWidth(75);
      createButton.setHeight(22);
      createButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(75);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      deleteButton = new IButton("Delete");
      deleteButton.setID(ID_DELETE_BUTTON);
      deleteButton.setWidth(75);
      deleteButton.setHeight(22);
      deleteButton.setIcon(Images.Buttons.DELETE);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(2);

      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(2);

      tbi.setButtons(deleteButton, delimiter1, createButton, delimiter2, cancelButton);
      buttonsForm.setFields(tbi);
      buttonsForm.setAutoWidth();
      return buttonsForm;
   }

   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   public ListGridItem<Template> getTemplateListGrid()
   {
      return templateListGrid;
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   public void closeForm()
   {
      destroy();
   }

   public HasValue<String> getFileNameField()
   {
      return fileNameField;
   }

   public void disableCreateButton()
   {
      createButton.disable();
   }

   public void enableCreateButton()
   {
      createButton.enable();
   }

   /**
    * @see org.exoplatform.ideall.client.template.CreateFileFromTemplatePresenter.Display#getDeleteButton()
    */
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ideall.client.template.CreateFileFromTemplatePresenter.Display#setEnabledDeleteButton(boolean)
    */
   public void setDeleteButtonDisabled(boolean value)
   {
      deleteButton.setDisabled(value);
   }

   /**
    * @see org.exoplatform.ideall.client.template.CreateFileFromTemplatePresenter.Display#selectLastTemplate()
    */
   public void selectLastTemplate()
   {
      templateListGrid.selectRecord(templateListGrid.getRecords().length - 1);
   }

}

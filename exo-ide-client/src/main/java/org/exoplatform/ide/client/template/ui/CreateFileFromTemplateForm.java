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
package org.exoplatform.ide.client.template.ui;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.IDEDialogWindow;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.navigation.template.CreateFileFromTemplateView;
import org.exoplatform.ide.client.project.CreateProjectTemplateForm;
import org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * Used to add file template to project template in {@link CreateProjectTemplateForm}
 * 
 * TODO: Remove this form and refactore {@link CreateFileFromTemplateView}
 * to reuse code.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFileFromTemplateForm extends IDEDialogWindow 
implements CreateFromTemplateDisplay<FileTemplate>
{
   public static final int WIDTH = 550;

   public static final int HEIGHT = 300;
   
   private static final String ID = "ideCreateFileFromTemplateForm";
   
   private static final String ID_CREATE_BUTTON = "ideCreateFileFromTemplateFormCreateButton";
   
   private static final String ID_CANCEL_BUTTON = "ideCreateFileFromTemplateFormCancelButton";
   
   private static final String ID_DELETE_BUTTON = "ideCreateFileFromTemplateFormDeleteButton";
   
   private static final String FILE_NAME_FIELD = "ideCreateFileFromTemplateFormFileNameField";
   
   private final String BUTTON_W = "75px";
      
   private final String BUTTON_H = "22px";
   
   protected VerticalPanel windowLayout;

   private ImageButton createButton;

   private ImageButton cancelButton;

   private ImageButton deleteButton;

   protected TemplateListGrid<FileTemplate> templateListGrid;

   private TextField nameField;
   
   private CreateFileFromTemplatePresenter presenter;

   public CreateFileFromTemplateForm(HandlerManager eventBus, List<Template> templateList, 
      CreateFileFromTemplatePresenter presenter)
   {
      super(WIDTH, HEIGHT, ID);
      initForm(eventBus);
      this.presenter = presenter;
   }
   
   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }
   
   /**
    * Create main form and initialize it.
    * 
    * @param eventBus - the eventbus
    */
   private void initForm(HandlerManager eventBus)
   {
      setTitle(getFormTitle());
      //TODO setCanDragResize(true);
      setCanMaximize(true);

      windowLayout = new VerticalPanel();
      windowLayout.setWidth("100%");
      windowLayout.setHeight("100%");
      windowLayout.setSpacing(10);
      setWidget(windowLayout);

      createTypeLayout();

      windowLayout.add(getActionsForm());

      show();
   }
   
   /**
    * Create form with name field and action buttons.
    * 
    * @return {@link HorizontalPanel}
    */
   private HorizontalPanel getActionsForm()
   {
      HorizontalPanel actionsLayout = new HorizontalPanel();
      actionsLayout.setHeight("35px");
      actionsLayout.setWidth("100%");
      actionsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      nameField = new TextField("Name", getNameFieldLabel());
      nameField.setName(FILE_NAME_FIELD);
      nameField.setWidth(150);
      
      actionsLayout.add(nameField);

      actionsLayout.add(getButtonsForm());
      return actionsLayout;
   }

   protected void createTypeLayout()
   {
      templateListGrid = new TemplateListGrid<FileTemplate>();
      templateListGrid.setWidth("100%");
      templateListGrid.setHeight(HEIGHT - 30 +"px");
      // templateListGrid.setCanFocus(false);  // to fix bug IDE-258 "Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility."
      windowLayout.add(templateListGrid);
   }

   public String getCreateButtonTitle()
   {
      return IDE.TEMPLATE_CONSTANT.createFileFromTemplateCreateButton();
   }

   public String getFormTitle()
   {
      return IDE.TEMPLATE_CONSTANT.createFileFromTemplateFormTitle();
   }

   protected String getNameFieldLabel()
   {
      return IDE.TEMPLATE_CONSTANT.createFileFromTemplateNameField();
   }
   
   /**
    * Create the horizontal panel with action buttons:
    * create, delete and cancel.
    * @return {@link HorizontalPanel}
    */
   private HorizontalPanel getButtonsForm()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setWidth("100%");
      buttonsLayout.setHeight("22px");
      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      buttonsLayout.setSpacing(5);

      createButton = new ImageButton(getCreateButtonTitle());
      createButton.setButtonId(ID_CREATE_BUTTON);
      createButton.setWidth(BUTTON_W);
      createButton.setHeight(BUTTON_H);
      createButton.setImage(new Image(Images.Buttons.YES));

      cancelButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.cancelButton());
      cancelButton.setButtonId(ID_CANCEL_BUTTON);
      cancelButton.setWidth(BUTTON_W);
      cancelButton.setHeight(BUTTON_H);
      cancelButton.setImage(new Image(Images.Buttons.NO));

      deleteButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.deleteButton());
      deleteButton.setButtonId(ID_DELETE_BUTTON);
      deleteButton.setWidth(BUTTON_W);
      deleteButton.setHeight(BUTTON_H);
      deleteButton.setImage(new Image(Images.Buttons.REMOVE));

     buttonsLayout.add(deleteButton);
     buttonsLayout.add(createButton);
     buttonsLayout.add(cancelButton);

      return buttonsLayout;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getCreateButton()
    */
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#closeForm()
    */
   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#disableCreateButton()
    */
   public void disableCreateButton()
   {
      createButton.setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#enableCreateButton()
    */
   public void enableCreateButton()
   {
      createButton.setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter.Display#getDeleteButton()
    */
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter.Display#selectLastTemplate()
    */
   public void selectLastTemplate()
   {
      templateListGrid.selectLastItem();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getTemplateListGrid()
    */
   public ListGridItem<FileTemplate> getTemplateListGrid()
   {
      return templateListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getNameField()
    */
   public HasValue<String> getNameField()
   {
      return nameField;
   }
   
   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getTemplatesSelected()
    */
   public List<FileTemplate> getTemplatesSelected()
   {
      return templateListGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#enableDeleteButton()
    */
   public void enableDeleteButton()
   {
      deleteButton.setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#disableDeleteButton()
    */
   public void disableDeleteButton()
   {
      deleteButton.setEnabled(false);
   }
   

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#enableNameField()
    */
   public void enableNameField()
   {
      nameField.enable();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#disableNameField()
    */
   public void disableNameField()
   {
      nameField.disable();
   }

}

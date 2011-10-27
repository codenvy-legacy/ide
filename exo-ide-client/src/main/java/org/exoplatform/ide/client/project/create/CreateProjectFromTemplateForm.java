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
package org.exoplatform.ide.client.project.create;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.template.ui.TemplateListGrid;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateProjectFromTemplateForm extends ViewImpl implements org.exoplatform.ide.client.project.create.CreateProjectFromTemplatePresenter.Display
{

   public static final int DEFAULT_HEIGHT = 300;

   public static final int DEFAULT_WIDTH = 550;
   
   private final String BUTTON_W = "75px";
   
   private final String BUTTON_H = "22px";

   private static final String FILE_NAME_FIELD = "ideCreateFileFromTemplateFormFileNameField";

   private static final String ID_CANCEL_BUTTON = "ideCreateFileFromTemplateFormCancelButton";

   private static final String ID_CREATE_BUTTON = "ideCreateFileFromTemplateFormCreateButton";

   private static final String ID_DELETE_BUTTON = "ideCreateFileFromTemplateFormDeleteButton";
   
   private static final String TITLE = IDE.TEMPLATE_CONSTANT.createProjectFromTemplateTitle();
   
   private static final String NAME_FIELD = IDE.TEMPLATE_CONSTANT.createProjectFromTemplateName();

   private ImageButton cancelButton;

   private ImageButton createButton;

   private ImageButton deleteButton;

   private TextField nameField;

   protected ProjectTemplateListGrid templateListGrid;

   protected VerticalPanel layout;

   public CreateProjectFromTemplateForm()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.createFromTemplate()), DEFAULT_WIDTH,
         DEFAULT_HEIGHT,false);

      layout = new VerticalPanel();
      layout.setWidth("100%");
      layout.setHeight("100%");
      layout.setSpacing(10);
      add(layout);

      templateListGrid = new ProjectTemplateListGrid();
      templateListGrid.setWidth("100%");
      templateListGrid.setHeight(200);
      layout.add(templateListGrid);

      HorizontalPanel actionsLayout = new HorizontalPanel();
      actionsLayout.setHeight("35px");
      actionsLayout.setWidth("100%");
      actionsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      nameField = new TextField("Name", NAME_FIELD);
      nameField.setName(FILE_NAME_FIELD);
      nameField.setWidth(150);
      actionsLayout.add(nameField);

      actionsLayout.add(getButtonsForm());
      layout.add(actionsLayout);
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

      createButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.createButton());
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

   public void setCreateButtonEnabled(boolean enabled)
   {
      createButton.setEnabled(enabled);
   }

   public void setDeleteButtonEnabled(boolean enabled)
   {
      deleteButton.setEnabled(enabled);
   }

   public void setNameFieldEnabled(boolean enabled)
   {
      nameField.setEnabled(enabled);
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
    * @see org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter.Display#getDeleteButton()
    */
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getNameField()
    */
   public HasValue<String> getNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getTemplateListGrid()
    */
   public ListGridItem<ProjectTemplate> getTemplateListGrid()
   {
      return templateListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#getTemplatesSelected()
    */
   public List<ProjectTemplate> getSelectedTemplates()
   {
      return templateListGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter.Display#selectLastTemplate()
    */
   public void selectLastTemplate()
   {
      templateListGrid.selectLastItem();
   }

}

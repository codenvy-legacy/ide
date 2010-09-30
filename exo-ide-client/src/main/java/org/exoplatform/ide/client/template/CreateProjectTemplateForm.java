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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.model.template.Template;

import java.util.List;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateProjectTemplateForm extends DialogWindow implements CreateProjectTemplatePresenter.Display
{
   public static final int WIDTH = 550;

   public static final int HEIGHT = 350;
   
   public static final int TEXT_FIELD_WIDTH = 450;
   
   private static final String ID = "ideCreateProjectTemplateForm";
   
   private static final String ID_CREATE_BUTTON = "ideCreateProjectTemplateFormCreateButton";
   
   private static final String ID_CANCEL_BUTTON = "ideCreateProjectTemplateFormCancelButton";
   
   private static final String TEMPLATE_NAME_FIELD = "ideCreateProjectTemplateFormNameField";
   
   private static final String DESCRIPTION_FIELD = "ideCreateProjectTemplateFormDescriptionField";
   
   private VLayout windowLayout;

   private IButton createButton;

   private IButton cancelButton;

   private TemplateListGrid fileTemplateListGrid;

   private TextField templateNameField;
   
   private TextAreaItem templateDescriptionField;
   
   private CreateProjectTemplatePresenter presenter;
   
   public CreateProjectTemplateForm(HandlerManager eventBus, List<Template> templateList)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      
      
      setTitle("Create project template");
      setCanDragResize(true);
      setShowMaximizeButton(true);

      windowLayout = new VLayout();
      windowLayout.setMargin(15);
      addItem(windowLayout);

      createFieldsForm();

      Layout l = new Layout();
      l.setHeight(3);
      windowLayout.addMember(l);
      
      createFileTemplateListLayout();
      
      Layout l2 = new Layout();
      l2.setHeight(10);
      windowLayout.addMember(l2);
      
      createButtonsForm();

      show();

      presenter = new CreateProjectTemplatePresenter(eventBus, templateList);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
      
   }
   
   private void createFieldsForm()
   {
      VLayout fieldsLayout = new VLayout();
      fieldsLayout.setHeight(35);
      fieldsLayout.setWidth100();

      DynamicForm form = new DynamicForm();
      templateNameField = new TextField("TemplateName","Name");
      templateNameField.setName(TEMPLATE_NAME_FIELD);
      templateNameField.setWidth(TEXT_FIELD_WIDTH);
      templateNameField.setWrapTitle(false);
      
      templateDescriptionField = new TextAreaItem("Description");
      templateDescriptionField.setName(DESCRIPTION_FIELD);
      templateDescriptionField.setTitle("Description");
      templateDescriptionField.setTitleOrientation(TitleOrientation.LEFT);
      templateDescriptionField.setColSpan(2);
      templateDescriptionField.setHeight(40);
      templateDescriptionField.setWidth(TEXT_FIELD_WIDTH);

      form.setColWidths("*", "" + TEXT_FIELD_WIDTH);
      form.setItems(templateNameField, templateDescriptionField);
      fieldsLayout.addMember(form);
      
      windowLayout.addMember(fieldsLayout);
   }
   
   private void createFileTemplateListLayout()
   {
      Label label = new Label();  
      label.setHeight(10);  
      label.setWidth100();
      label.setMargin(5);
      label.setContents("Select files, that will be included to project template");
      label.setAlign(Alignment.CENTER);
      
      windowLayout.addMember(label);  
      
      fileTemplateListGrid = new TemplateListGrid();
      windowLayout.addMember(fileTemplateListGrid);
   }

   private void createButtonsForm()
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

      ToolbarItem tbi = new ToolbarItem();

      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(2);

      tbi.setButtons(createButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);
      buttonsForm.setAutoWidth();
      
      windowLayout.addMember(buttonsForm);
   }
   
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getTemplateListGrid()
    */
   public ListGridItem<Template> getTemplateListGrid()
   {
      return fileTemplateListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getNameField()
    */
   public HasValue<String> getNameField()
   {
      return templateNameField;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getDescriptionField()
    */
   public HasValue<String> getDescriptionField()
   {
      return templateDescriptionField;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getCreateButton()
    */
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#closeForm()
    */
   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#enableCreateButton()
    */
   public void enableCreateButton()
   {
      createButton.setDisabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableCreateButton()
    */
   public void disableCreateButton()
   {
      createButton.setDisabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getFileTemplatesSelected()
    */
   public List<Template> getFileTemplatesSelected()
   {
      return fileTemplateListGrid.getSelectedItems();
   }

}

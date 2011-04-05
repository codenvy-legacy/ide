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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Abstract class, that creates base form for templates.
 * 
 * Create empty list grid, name field and action buttons: create, delete, cancel.
 * 
 * Initializing of lisg grid must implement concrete subclasses.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: AbstractCreateFromTemplateForm.java Mar 31, 2011 11:14:45 AM vereshchaka $
 *
 * @param <T> - type of template (file or project)
 */
public abstract class AbstractCreateFromTemplateForm<T extends Template> extends DialogWindow 
implements CreateFromTemplateDisplay<T>
{

   public static final int WIDTH = 550;

   public static final int HEIGHT = 300;
   
   private static final String ID = "ideCreateFileFromTemplateForm";
   
   private static final String ID_CREATE_BUTTON = "ideCreateFileFromTemplateFormCreateButton";
   
   private static final String ID_CANCEL_BUTTON = "ideCreateFileFromTemplateFormCancelButton";
   
   private static final String ID_DELETE_BUTTON = "ideCreateFileFromTemplateFormDeleteButton";
   
   private static final String FILE_NAME_FIELD = "ideCreateFileFromTemplateFormFileNameField";
   
   protected VerticalPanel windowLayout;

   private IButton createButton;

   private IButton cancelButton;

   private IButton deleteButton;

   protected TemplateListGrid<T> templateListGrid;

   private TextField nameField;
   
   private AbstractCreateFromTemplatePresenter<T> presenter;
   
   private HandlerManager eventBus;
   
   public AbstractCreateFromTemplateForm(HandlerManager eventBus, AbstractCreateFromTemplatePresenter<T> presenter)
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
      this.eventBus = eventBus;

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
    * Create list grid for templates.
    */
   abstract void createTypeLayout();

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
   
   /**
    * Get the title of button, that create new instance
    * @return {@link String}
    */
   abstract String getCreateButtonTitle();
   
   /**
    * Get the name of field for typing new instance name.
    * @return {@link String}
    */
   abstract String getNameFieldLabel();
   
   /**
    * Get the title of form (window).
    * @return {@link String}
    */
   abstract String getFormTitle();
   
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

      createButton = new IButton(getCreateButtonTitle());
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
      createButton.disable();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#enableCreateButton()
    */
   public void enableCreateButton()
   {
      createButton.enable();
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
   public ListGridItem<T> getTemplateListGrid()
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
   public List<T> getTemplatesSelected()
   {
      return templateListGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#enableDeleteButton()
    */
   public void enableDeleteButton()
   {
      deleteButton.setDisabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateFromTemplateDisplay#disableDeleteButton()
    */
   public void disableDeleteButton()
   {
      deleteButton.setDisabled(true);
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

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
package org.exoplatform.ide.client.navigation.template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.model.template.FileTemplate;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateFileFromTemplateView extends ViewImpl implements CreateFileFromTemplatePresenter.Display
{
   
   private static final String ID = "ideCreateFileFromTemplateForm";
   
   private static final int WIDTH = 550;

   private static final int HEIGHT = 300;
   
   private static final String TITLE = IDE.TEMPLATE_CONSTANT.createFileFromTemplateFormTitle();

   private static CreateFileFromTemplateViewUiBinder uiBinder = GWT.create(CreateFileFromTemplateViewUiBinder.class);

   interface CreateFileFromTemplateViewUiBinder extends UiBinder<Widget, CreateFileFromTemplateView>
   {
   }
   
   @UiField
   FileTemplateListGrid fileTemplateListGrid;
   
   @UiField
   TextInput fileNameField;
   
   @UiField
   ImageButton createButton;
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   ImageButton deleteButton;

   public CreateFileFromTemplateView()
   {
      super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.createFromTemplate()), WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public ListGridItem<FileTemplate> getTemplateListGrid()
   {
      return fileTemplateListGrid;
   }

   @Override
   public FileTemplate getSelectedTemplate()
   {
      return fileTemplateListGrid.getSelectedItems().size() == 0 ? null : fileTemplateListGrid.getSelectedItems().get(0);
   }
   
   public void selectTemplate(FileTemplate template) {
      fileTemplateListGrid.selectItem(template);
   }

   @Override
   public HasValue<String> getFileNameField()
   {
      return fileNameField;
   }

   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   @Override
   public void setCreateButtonEnabled(boolean enabled)
   {
      createButton.setEnabled(enabled);
   }

   @Override
   public void setDeleteButtonEnabled(boolean enabled)
   {
      deleteButton.setEnabled(enabled);
   }

   @Override
   public void setFileNameFieldEnabled(boolean enabled)
   {
      fileNameField.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.navigation.template.CreateFileFromTemplatePresenter.Display#setSubmitButtonTitle(java.lang.String)
    */
   @Override
   public void setSubmitButtonTitle(String title)
   {
      createButton.setTitle(title);
   }

}

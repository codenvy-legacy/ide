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
package org.exoplatform.ide.extension.ruby.client.create.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.ruby.client.RubyExtension;
import org.exoplatform.ide.extension.ruby.client.create.CreateRubyProjectPresenter;

/**
 * View for creation new java project.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateJavaProjectView.java Jun 22, 2011 10:00:17 AM vereshchaka $
 *
 */
public class CreateRubyProjectView extends ViewImpl implements CreateRubyProjectPresenter.Display
{
   private static final String ID = "ideCreateJavaProjectView";

   private static final int WIDTH = 400;

   private static final int HEIGHT = 160;

   private static final String CREATE_BUTTON_ID = "ideCreateJavaProjectViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateJavaProjectViewCancelButton";

   private static final String NAME_FIELD_ID = "ideCreateJavaProjectViewNameField";
   
   private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

   interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateRubyProjectView>
   {
   }

   /**
    * Project name field.
    */
   @UiField
   TextField nameField;
   
   /**
    * Create project button.
    */
   @UiField
   ImageButton createButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;
   
   public CreateRubyProjectView()
   {
      super(ID, ViewType.MODAL, RubyExtension.RUBY_CONSTANT.createRubyProjectViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      nameField.setName(NAME_FIELD_ID);
      nameField.setHeight(22);
      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.CreateRubyProjectPresenter.client.create.CreateJavaProjectPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.extension.CreateRubyProjectPresenter.client.create.CreateJavaProjectPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.CreateRubyProjectPresenter.client.create.CreateJavaProjectPresenter.Display#getProjectNameField()
    */
   @Override
   public HasValue<String> getProjectNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.CreateRubyProjectPresenter.client.create.CreateJavaProjectPresenter.Display#focusInProjectNameField()
    */
   @Override
   public void focusInProjectNameField()
   {
      nameField.focusInItem();
   }

   /**
    * @see org.exoplatform.ide.extension.CreateRubyProjectPresenter.client.create.CreateJavaProjectPresenter.Display#disableCreateButton()
    */
   @Override
   public void disableCreateButton()
   {
      createButton.setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.extension.CreateRubyProjectPresenter.client.create.CreateJavaProjectPresenter.Display#enableCreateButton()
    */
   @Override
   public void enableCreateButton()
   {
      createButton.setEnabled(true);
   }

}

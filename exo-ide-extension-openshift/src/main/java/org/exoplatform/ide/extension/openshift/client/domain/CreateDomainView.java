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
package org.exoplatform.ide.extension.openshift.client.domain;

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
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

/**
 * View for creating domain on OpenShift.
 * View must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 2, 2011 2:26:41 PM anya $
 *
 */
public class CreateDomainView extends ViewImpl implements CreateDomainPresenter.Display
{
   private static final String ID = "ideCreateDomainView";

   private static final int WIDTH = 410;

   private static final int HEIGHT = 160;

   private static final String CREATE_BUTTON_ID = "ideCreateDomainViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateDomainViewCancelButton";

   private static final String NAME_FIELD_ID = "ideCreateDomainViewNameField";

   /**
    * Domain's name field.
    */
   @UiField
   TextField nameField;

   /**
    * Create button.
    */
   @UiField
   ImageButton createButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   private static CreateDomainViewUiBinder uiBinder = GWT.create(CreateDomainViewUiBinder.class);

   interface CreateDomainViewUiBinder extends UiBinder<Widget, CreateDomainView>
   {
   }

   public CreateDomainView()
   {
      super(ID, ViewType.MODAL, OpenShiftExtension.LOCALIZATION_CONSTANT.createDomainViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      nameField.setName(NAME_FIELD_ID);
      nameField.setHeight(22);
      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.domain.CreateDomainPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.domain.CreateDomainPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.domain.CreateDomainPresenter.Display#getDomainNameField()
    */
   @Override
   public HasValue<String> getDomainNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.domain.CreateDomainPresenter.Display#enableCreateButton(boolean)
    */
   @Override
   public void enableCreateButton(boolean enable)
   {
      createButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.domain.CreateDomainPresenter.Display#focusInDomainNameField()
    */
   @Override
   public void focusInDomainNameField()
   {
      nameField.focusInItem();
   }

}

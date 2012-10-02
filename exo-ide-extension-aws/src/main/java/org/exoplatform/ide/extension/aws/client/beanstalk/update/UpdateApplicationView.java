/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.beanstalk.update;

import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 11:13:32 AM anya $
 * 
 */
public class UpdateApplicationView extends ViewImpl implements UpdateApplicationPresenter.Display
{
   private static final String ID = "ideUpdateApplicationView";

   private static final int WIDTH = 420;

   private static final int HEIGHT = 200;

   private static final String DESCRIPTION_FIELD_ID = "ideUpdateApplicationViewDescriptionField";

   private static final String UPDATE_BUTTON_ID = "ideUpdateApplicationViewUpdateButton";

   private static final String CANCEL_BUTTON_ID = "ideUpdateApplicationViewCancelButton";

   private static UpdateApplicationViewUiBinder uiBinder = GWT.create(UpdateApplicationViewUiBinder.class);

   @UiField
   TextAreaInput descriptionField;

   @UiField
   ImageButton updateButton;

   @UiField
   ImageButton cancelButton;

   interface UpdateApplicationViewUiBinder extends UiBinder<Widget, UpdateApplicationView>
   {
   }

   public UpdateApplicationView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.updateDescriptionViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      descriptionField.setName(DESCRIPTION_FIELD_ID);
      updateButton.setButtonId(UPDATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.update.UpdateApplicationPresenter.Display#getUpdateButton()
    */
   @Override
   public HasClickHandlers getUpdateButton()
   {
      return updateButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.update.UpdateApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.update.UpdateApplicationPresenter.Display#getDescriptionField()
    */
   @Override
   public HasValue<String> getDescriptionField()
   {
      return descriptionField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.update.UpdateApplicationPresenter.Display#enableUpdateButton(boolean)
    */
   @Override
   public void enableUpdateButton(boolean enabled)
   {
      updateButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.update.UpdateApplicationPresenter.Display#focusInDescriptionField()
    */
   @Override
   public void focusInDescriptionField()
   {
      descriptionField.setFocus(true);
      descriptionField.selectAll();
   }
}

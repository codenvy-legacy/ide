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
package org.exoplatform.ide.extension.aws.client.beanstalk.environment.create;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 3:59:12 PM anya $
 * 
 */
public class CreateEnvironmentView extends ViewImpl implements CreateEnvironmentPresenter.Display
{
   private static final String ID = "ideCreateEnvironmentView";

   private static final int WIDTH = 555;

   private static final int HEIGHT = 195;

   private static final String ENV_NAME_FIELD_ID = "ideCreateEnvironmentViewEnvNameField";

   private static final String ENV_DESCRIPTION_FIELD_ID = "ideCreateEnvironmentViewEnvDescriptionField";

   private static final String SOLUTION_STACK_FIELD_ID = "ideCreateEnvironmentViewSolutionStackField";

   private static final String CREATE_BUTTON_ID = "ideCreateEnvironmentViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateEnvironmentViewCancelButton";

   private static CreateEnvironmentViewUiBinder uiBinder = GWT.create(CreateEnvironmentViewUiBinder.class);

   interface CreateEnvironmentViewUiBinder extends UiBinder<Widget, CreateEnvironmentView>
   {
   }

   @UiField
   TextInput envNameField;

   @UiField
   TextInput envDescriptionField;

   @UiField
   SelectItem solutionStackField;

   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   public CreateEnvironmentView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.createEnvironmentViewTitle(), null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));

      envNameField.setName(ENV_NAME_FIELD_ID);
      envDescriptionField.setName(ENV_DESCRIPTION_FIELD_ID);
      solutionStackField.setName(SOLUTION_STACK_FIELD_ID);
      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.create.CreateEnvironmentPresenter.Display#getEnvNameField()
    */
   @Override
   public TextFieldItem getEnvNameField()
   {
      return envNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.create.CreateEnvironmentPresenter.Display#getEnvDescriptionField()
    */
   @Override
   public TextFieldItem getEnvDescriptionField()
   {
      return envDescriptionField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.create.CreateEnvironmentPresenter.Display#getSolutionStackField()
    */
   @Override
   public HasValue<String> getSolutionStackField()
   {
      return solutionStackField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.create.CreateEnvironmentPresenter.Display#setSolutionStackValues(java.lang.String[])
    */
   @Override
   public void setSolutionStackValues(String[] values)
   {
      solutionStackField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.create.CreateEnvironmentPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.create.CreateEnvironmentPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.create.CreateEnvironmentPresenter.Display#enableCreateButton(boolean)
    */
   @Override
   public void enableCreateButton(boolean enabled)
   {
      createButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.create.CreateEnvironmentPresenter.Display#focusInEnvNameField()
    */
   @Override
   public void focusInEnvNameField()
   {
      envNameField.setFocus(true);
   }
}

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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch;

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
public class LaunchEnvironmentView extends ViewImpl implements LaunchEnvironmentPresenter.Display
{
   private static final String ID = "ideLaunchEnvironmentView";

   private static final int WIDTH = 555;

   private static final int HEIGHT = 195;

   private static final String ENV_NAME_FIELD_ID = "ideLaunchEnvironmentViewEnvNameField";

   private static final String ENV_DESCRIPTION_FIELD_ID = "ideLaunchEnvironmentViewEnvDescriptionField";

   private static final String SOLUTION_STACK_FIELD_ID = "ideLaunchEnvironmentViewSolutionStackField";

   private static final String LAUNCH_BUTTON_ID = "ideLaunchEnvironmentViewLaunchButton";

   private static final String CANCEL_BUTTON_ID = "ideLaunchEnvironmentViewCancelButton";

   private static CreateEnvironmentViewUiBinder uiBinder = GWT.create(CreateEnvironmentViewUiBinder.class);

   interface CreateEnvironmentViewUiBinder extends UiBinder<Widget, LaunchEnvironmentView>
   {
   }

   @UiField
   TextInput envNameField;

   @UiField
   TextInput envDescriptionField;

   @UiField
   SelectItem solutionStackField;

   @UiField
   ImageButton launchButton;

   @UiField
   ImageButton cancelButton;

   public LaunchEnvironmentView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentViewTitle(), null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));

      envNameField.setName(ENV_NAME_FIELD_ID);
      envDescriptionField.setName(ENV_DESCRIPTION_FIELD_ID);
      solutionStackField.setName(SOLUTION_STACK_FIELD_ID);
      launchButton.setButtonId(LAUNCH_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getEnvNameField()
    */
   @Override
   public TextFieldItem getEnvNameField()
   {
      return envNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getEnvDescriptionField()
    */
   @Override
   public TextFieldItem getEnvDescriptionField()
   {
      return envDescriptionField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getSolutionStackField()
    */
   @Override
   public HasValue<String> getSolutionStackField()
   {
      return solutionStackField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#setSolutionStackValues(java.lang.String[])
    */
   @Override
   public void setSolutionStackValues(String[] values)
   {
      solutionStackField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getLaunchButton()
    */
   @Override
   public HasClickHandlers getLaunchButton()
   {
      return launchButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#enableLaunchButton(boolean)
    */
   @Override
   public void enableLaunchButton(boolean enabled)
   {
      launchButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#focusInEnvNameField()
    */
   @Override
   public void focusInEnvNameField()
   {
      envNameField.setFocus(true);
   }
}

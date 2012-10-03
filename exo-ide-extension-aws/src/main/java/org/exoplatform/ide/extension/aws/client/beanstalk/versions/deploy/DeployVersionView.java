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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

import java.util.LinkedHashMap;

/**
 * View for deploy application's version.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: DeployVersionView.java Sep 27, 2012 6:05:14 PM azatsarynnyy $
 *
 */
public class DeployVersionView extends ViewImpl implements DeployVersionPresenter.Display
{
   private static final String ID = "ideDeployVersionView";

   private static final int WIDTH = 580;

   private static final int HEIGHT = 230;

   private static final String DEPLOY_MODE_RADIO_BUTTON_ID = "ideDeployVersionViewDeployMode";

   private static final String ENVIRONMENTS_FIELD_ID = "ideDeployVersionViewEnvironmentsField";

   private static final String DEPLOY_BUTTON_ID = "ideDeployVersionViewDeployButton";

   private static final String CANCEL_BUTTON_ID = "ideDeployVersionViewCancelButton";

   private static CreateVersionViewUiBinder uiBinder = GWT.create(CreateVersionViewUiBinder.class);

   interface CreateVersionViewUiBinder extends UiBinder<Widget, DeployVersionView>
   {
   }

   @UiField
   RadioButton deployToNewEnvironment;

   @UiField
   RadioButton deployToExistingEnvironment;

   @UiField
   SelectItem environmentsField;

   @UiField
   ImageButton deployButton;

   @UiField
   ImageButton cancelButton;

   public DeployVersionView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.deployVersionViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      deployToNewEnvironment.setName(DEPLOY_MODE_RADIO_BUTTON_ID);
      deployToExistingEnvironment.setName(DEPLOY_MODE_RADIO_BUTTON_ID);
      environmentsField.setName(ENVIRONMENTS_FIELD_ID);
      environmentsField.setEnabled(false);
      deployButton.setButtonId(DEPLOY_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionPresenter.Display#getNewEnvironmentMode()
    */
   @Override
   public HasValue<Boolean> getNewEnvironmentMode()
   {
      return deployToNewEnvironment;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionPresenter.Display#getExistingEnvironmentMode()
    */
   @Override
   public HasValue<Boolean> getExistingEnvironmentMode()
   {
      return deployToExistingEnvironment;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionPresenter.Display#getEnvironmentsField()
    */
   @Override
   public HasValue<String> getEnvironmentsField()
   {
      return environmentsField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionPresenter.Display#setEnvironmentsValues(java.util.LinkedHashMap)
    */
   @Override
   public void setEnvironmentsValues(LinkedHashMap<String, String> values)
   {
      environmentsField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionPresenter.Display#enableEnvironmentsField(boolean)
    */
   @Override
   public void enableEnvironmentsField(boolean value)
   {
      environmentsField.setEnabled(value);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getDeployButton()
    */
   @Override
   public HasClickHandlers getDeployButton()
   {
      return deployButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionPresenter.Display#enableDeployButton(boolean)
    */
   @Override
   public void enableDeployButton(boolean value)
   {
      deployButton.setEnabled(value);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

}

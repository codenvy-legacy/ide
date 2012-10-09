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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * Edit environment configuration view.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: ConfigurationView.java Oct 5, 2012 6:16:25 PM azatsarynnyy $
 *
 */
public class EditConfigurationView extends ViewImpl implements EditConfigurationPresenter.Display
{
   public static final String ID = "ideEditEnvironmentConfigurationView";

   private static final int HEIGHT = 350;

   private static final int WIDTH = 950;

   private static final String SERVER_TAB_ID = "ideEditEnvironmentConfigurationViewServerTab";

   private static final String LOAD_BALANCER_TAB_ID = "ideEditEnvironmentConfigurationViewLoadBalancerTab";

   private static final String CONTAINER_TAB_ID = "ideEditEnvironmentConfigurationViewContainerTab";

   private static EditConfigurationViewUiBinder uiBinder = GWT.create(EditConfigurationViewUiBinder.class);

   @UiField
   TabPanel configurationTabPanel;

   /**
    * OK button.
    */
   //@UiField
   ImageButton okButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   private ServerTabPain serverTabPain;

   private LoadBalancerTabPain loadBalancerTabPain;

   private ContainerTabPain containerTabPain;

   interface EditConfigurationViewUiBinder extends UiBinder<Widget, EditConfigurationView>
   {
   }

   public EditConfigurationView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.environmentConfigurationTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      serverTabPain = new ServerTabPain();
      configurationTabPanel.addTab(SERVER_TAB_ID, null, AWSExtension.LOCALIZATION_CONSTANT.serverTab(), serverTabPain,
         false);

      loadBalancerTabPain = new LoadBalancerTabPain();
      configurationTabPanel.addTab(LOAD_BALANCER_TAB_ID, null, AWSExtension.LOCALIZATION_CONSTANT.loadBalancerTab(),
         loadBalancerTabPain, false);

      containerTabPain = new ContainerTabPain();
      configurationTabPanel.addTab(CONTAINER_TAB_ID, null, AWSExtension.LOCALIZATION_CONSTANT.containerTab(),
         containerTabPain, false);
   }

   /**
    * @see org.exoplatform.ide.extension.ConfigurationPresenter.client.info.ApplicationInfoPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getEC2InstanceTypeField()
    */
   @Override
   public HasValue<String> getEC2InstanceTypeField()
   {
      return serverTabPain.getEC2InstanceTypeField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getEC2SecurityGroupsField()
    */
   @Override
   public TextFieldItem getEC2SecurityGroupsField()
   {
      return serverTabPain.getEC2SecurityGroupsField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getKeyNameField()
    */
   @Override
   public TextFieldItem getKeyNameField()
   {
      return serverTabPain.getKeyNameField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getMonitoringIntervalField()
    */
   @Override
   public TextFieldItem getMonitoringIntervalField()
   {
      return serverTabPain.getMonitoringIntervalField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getImageIdField()
    */
   @Override
   public TextFieldItem getImageIdField()
   {
      return serverTabPain.getImageIdField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getAppHealthCheckCheckUrlField()
    */
   @Override
   public TextFieldItem getAppHealthCheckCheckUrlField()
   {
      return loadBalancerTabPain.getAppHealthCheckUrlField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getHealthCheckIntervalField()
    */
   @Override
   public TextFieldItem getHealthCheckIntervalField()
   {
      return loadBalancerTabPain.getHealthCheckIntervalField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getHealthCheckTimeoutField()
    */
   @Override
   public TextFieldItem getHealthCheckTimeoutField()
   {
      return loadBalancerTabPain.getHealthCheckTimeoutField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getHealthyThresholdField()
    */
   @Override
   public TextFieldItem getHealthyThresholdField()
   {
      return loadBalancerTabPain.getHealthyThresholdField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getUnhealthyThresholdField()
    */
   @Override
   public TextFieldItem getUnhealthyThresholdField()
   {
      return loadBalancerTabPain.getUnhealthyThresholdField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getInitialJVMHeapSizeField()
    */
   @Override
   public TextFieldItem getInitialJVMHeapSizeField()
   {
      return containerTabPain.getInitialJVMHeapSizeField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getMaximumJVMHeapSizeField()
    */
   @Override
   public TextFieldItem getMaximumJVMHeapSizeField()
   {
      return containerTabPain.getMaximumJVMHeapSizeField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getMaxPermSizeField()
    */
   @Override
   public TextFieldItem getMaxPermSizeField()
   {
      return containerTabPain.getMaxPermSizeField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter.Display#getJVMOptionsField()
    */
   @Override
   public TextFieldItem getJVMOptionsField()
   {
      return containerTabPain.getJVMOptionsField();
   }

}

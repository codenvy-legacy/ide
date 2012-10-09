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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: LoadBalancerTabPain.java Oct 8, 2012 5:38:06 PM azatsarynnyy $
 *
 */
public class LoadBalancerTabPain extends Composite
{

   private static LoadBalancerTabPainUiBinder uiBinder = GWT.create(LoadBalancerTabPainUiBinder.class);

   interface LoadBalancerTabPainUiBinder extends UiBinder<Widget, LoadBalancerTabPain>
   {
   }

   private static final String APP_HEALTH_CHECK_URL_FIELD_ID = "ideLoadBalancerTabPainAppHealthCheckUrlField";

   private static final String HEALTH_CHECK_INTERVAL_FIELD_ID = "ideLoadBalancerTabPainHealthCheckIntervalField";

   private static final String HEALTH_CHECK_TIMEOUT_FIELD_ID = "ideLoadBalancerTabPainHealthCheckTimeoutField";

   private static final String HEALTHY_THRESHOLD_FIELD_ID = "ideLoadBalancerTabPainHealthyThresholdField";

   private static final String UNHEALTHY_THRESHOLD_FIELD_ID = "ideLoadBalancerTabPainUnhealthyThresholdField";

   @UiField
   TextInput appHealthCheckUrlField;

   @UiField
   TextInput healthCheckIntervalField;

   @UiField
   TextInput healthCheckTimeoutField;

   @UiField
   TextInput healthyThresholdField;

   @UiField
   TextInput unhealthyThresholdField;

   public LoadBalancerTabPain()
   {
      initWidget(uiBinder.createAndBindUi(this));

      appHealthCheckUrlField.setName(APP_HEALTH_CHECK_URL_FIELD_ID);
      healthCheckIntervalField.setName(HEALTH_CHECK_INTERVAL_FIELD_ID);
      healthCheckTimeoutField.setName(HEALTH_CHECK_TIMEOUT_FIELD_ID);
      healthyThresholdField.setName(HEALTHY_THRESHOLD_FIELD_ID);
      unhealthyThresholdField.setName(UNHEALTHY_THRESHOLD_FIELD_ID);
   }

   /**
    * @return the appHealthCheckUrlField
    */
   public TextInput getAppHealthCheckUrlField()
   {
      return appHealthCheckUrlField;
   }

   /**
    * @return the healthCheckIntervalField
    */
   public TextInput getHealthCheckIntervalField()
   {
      return healthCheckIntervalField;
   }

   /**
    * @return the healthCheckTimeoutField
    */
   public TextInput getHealthCheckTimeoutField()
   {
      return healthCheckTimeoutField;
   }

   /**
    * @return the healthyThresholdField
    */
   public TextInput getHealthyThresholdField()
   {
      return healthyThresholdField;
   }

   /**
    * @return the unhealthyThresholdField
    */
   public TextInput getUnhealthyThresholdField()
   {
      return unhealthyThresholdField;
   }
}

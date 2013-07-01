/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface EditConfigurationView extends View<EditConfigurationView.ActionDelegate> {
    interface ActionDelegate {
        void onOkButtonClicked();

        void onCancelButtonClicked();
    }

    //Server tab
    String getEC2InstanceType();

    void setEC2InstanceTypeValues(JsonArray<String> values, String selectedValue);

    void setEC2SecurityGroups(String group);

    void setKeyName(String keyName);

    String getMonitoringInterval();

    void setMonitoringIntervalValues(JsonArray<String> values, String selectedValue);

    void setImageId(String amiId);

    //Load Balancer tab
    void setAppHealthCheckCheckUrl(String value);

    void setHealthCheckInterval(String value);

    void setHealthCheckTimeout(String value);

    void setHealthyThreshold(String value);

    void setUnhealthyThreshold(String value);

    //Container tab
    void setInitialJVMHeapSizeField(String value);

    void setMaximumJVMHeapSizeField(String value);

    void setMaxPermSizeField(String value);

    void setJVMOptionsField(String value);

    boolean isShown();

    void showDialog();

    void close();
}

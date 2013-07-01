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

import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ServerTabPain extends Composite {
    interface ServerTabPainUiBinder extends UiBinder<Widget, ServerTabPain> {}

    private static ServerTabPainUiBinder uiBinder = GWT.create(ServerTabPainUiBinder.class);

    @UiField
    ListBox ec2InstanceTypeField;

    @UiField
    TextBox ec2SecurityGroupsField;

    @UiField
    TextBox keyNameField;

    @UiField
    ListBox monitoringIntervalField;

    @UiField
    TextBox imageIdField;

    @Inject
    protected ServerTabPain() {
        Widget widget = uiBinder.createAndBindUi(this);

        initWidget(widget);
    }

    /** @return the ec2InstanceTypeField */
    public String getEC2InstanceType() {
        return ec2InstanceTypeField.getItemText(ec2InstanceTypeField.getSelectedIndex());
    }

    /**
     * Set new value map and select the <code>selected</code> value.
     *
     * @param values
     *         the list of values
     * @param selected
     *         the selected value
     */
    public void setEC2InstanceTypeValues(JsonArray<String> values, String selected) {
        int indexToSelect = 0;

        for (int i = 0; i < values.size(); i++) {
            ec2InstanceTypeField.addItem(values.get(i));
            if (selected.equals(values.get(i))) {
                indexToSelect = i;
            }
        }
        ec2InstanceTypeField.setSelectedIndex(indexToSelect);
    }

    public void setEC2SecurityGroups(String group) {
        ec2SecurityGroupsField.setText(group);
    }

    /** @return the ec2KeyNameField */
    public void setKeyName(String keyName) {
        keyNameField.setText(keyName);
    }

    /** @return the monitoringIntervalField */
    public String getMonitoringInterval() {
        return monitoringIntervalField.getItemText(monitoringIntervalField.getSelectedIndex());
    }

    /**
     * Set new value map and select the <code>selected</code> value.
     *
     * @param values
     *         the list of values
     * @param selected
     *         the selected value
     */
    public void setMonitoringIntervalValues(JsonArray<String> values, String selected) {
        int indexToSelect = 0;

        for (int i = 0; i < values.size(); i++) {
            monitoringIntervalField.addItem(values.get(i));
            if (selected.equals(values.get(i))) {
                indexToSelect = i;
            }
        }
        monitoringIntervalField.setSelectedIndex(indexToSelect);
    }

    /** @return the imageIdField */
    public void setImageId(String amiId) {
        imageIdField.setText(amiId);
    }
}

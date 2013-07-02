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

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
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
public class ServerTabPainViewImpl extends Composite implements ServerTabPainView {
    interface ServerTabPainViewImplUiBinder extends UiBinder<Widget, ServerTabPainViewImpl> {}

    private static ServerTabPainViewImplUiBinder uiBinder = GWT.create(ServerTabPainViewImplUiBinder.class);

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

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    @Inject
    protected ServerTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initWidget(widget);
    }

    @Override
    public void setEc2InstanceTypes(JsonArray<String> instanceTypes) {
        ec2InstanceTypeField.clear();

        for (int i = 0; i < instanceTypes.size(); i++) {
            ec2InstanceTypeField.addItem(instanceTypes.get(i));
        }
    }

    @Override
    public String getEc2InstanceType() {
        return ec2InstanceTypeField.getItemText(ec2InstanceTypeField.getSelectedIndex());
    }

    @Override
    public void setEc2SecurityGroup(String securityGroup) {
        ec2SecurityGroupsField.setText(securityGroup);
    }

    @Override
    public String getEc2SecurityGroup() {
        return ec2SecurityGroupsField.getText();
    }

    @Override
    public void setKeyPair(String keyPair) {
        keyNameField.setText(keyPair);
    }

    @Override
    public String getKeyPair() {
        return keyNameField.getText();
    }

    @Override
    public void setMonitoringInterval(JsonArray<String> interval) {
        monitoringIntervalField.clear();

        for (int i = 0; i < interval.size(); i++) {
            monitoringIntervalField.addItem(interval.get(i));
        }
    }

    public String getMonitoringInterval() {
        return monitoringIntervalField.getItemText(monitoringIntervalField.getSelectedIndex());
    }

    @Override
    public void setAmiId(String amiId) {
        imageIdField.setText(amiId);
    }

    @Override
    public String getAmiId() {
        return imageIdField.getText();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}

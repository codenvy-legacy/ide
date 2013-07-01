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
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EditConfigurationViewImpl extends DialogBox implements EditConfigurationView {
    interface EditConfigurationViewImplUiBinder extends UiBinder<Widget, EditConfigurationViewImpl> {
    }

    private static EditConfigurationViewImplUiBinder uiBinder = GWT.create(EditConfigurationViewImplUiBinder.class);

    @UiField
    TabPanel configurationTabPanel;

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    private ServerTabPain serverTabPain;

    private LoadBalancerTabPain loadBalancerTabPain;

    private ContainerTabPain containerTabPain;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private boolean isShown;

    private ActionDelegate delegate;

    @Inject
    protected EditConfigurationViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.environmentConfigurationTitle());
        this.setWidget(widget);

        serverTabPain = new ServerTabPain();
        configurationTabPanel.add(serverTabPain, constant.serverTab());

        loadBalancerTabPain = new LoadBalancerTabPain();
        configurationTabPanel.add(loadBalancerTabPain, constant.loadBalancerTab());

        containerTabPain = new ContainerTabPain();
        configurationTabPanel.add(containerTabPain, constant.containerTab());
    }

    //Server tab
    @Override
    public String getEC2InstanceType() {
        return serverTabPain.getEC2InstanceType();
    }

    @Override
    public void setEC2InstanceTypeValues(JsonArray<String> values, String selectedValue) {
        serverTabPain.setEC2InstanceTypeValues(values, selectedValue);
    }

    @Override
    public void setEC2SecurityGroups(String group) {
        serverTabPain.setEC2SecurityGroups(group);
    }

    @Override
    public void setKeyName(String keyName) {
        serverTabPain.setKeyName(keyName);
    }

    @Override
    public String getMonitoringInterval() {
        return serverTabPain.getMonitoringInterval();
    }

    @Override
    public void setMonitoringIntervalValues(JsonArray<String> values, String selectedValue) {
        serverTabPain.setMonitoringIntervalValues(values, selectedValue);
    }

    @Override
    public void setImageId(String amiId) {
        serverTabPain.setImageId(amiId);
    }

    //Load balancer tab
    @Override
    public void setAppHealthCheckCheckUrl(String value) {
        loadBalancerTabPain.setAppHealthCheckUrl(value);
    }

    @Override
    public void setHealthCheckInterval(String value) {
        loadBalancerTabPain.setHealthCheckInterval(value);
    }

    @Override
    public void setHealthCheckTimeout(String value) {
        loadBalancerTabPain.setHealthCheckTimeout(value);
    }

    @Override
    public void setHealthyThreshold(String value) {
        loadBalancerTabPain.setHealthyThreshold(value);
    }

    @Override
    public void setUnhealthyThreshold(String value) {
        loadBalancerTabPain.setUnhealthyThreshold(value);
    }

    //Container tab
    @Override
    public void setInitialJVMHeapSizeField(String value) {
        containerTabPain.setInitialJVMHeapSizeField(value);
    }

    @Override
    public void setMaximumJVMHeapSizeField(String value) {
        containerTabPain.setMaximumJVMHeapSizeField(value);
    }

    @Override
    public void setMaxPermSizeField(String value) {
        containerTabPain.setMaxPermSizeField(value);
    }

    @Override
    public void setJVMOptionsField(String value) {
        containerTabPain.setJVMOptionsField(value);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("okButton")
    public void onOkButtonClicked(ClickEvent event) {
        delegate.onOkButtonClicked();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonClicked();
    }
}

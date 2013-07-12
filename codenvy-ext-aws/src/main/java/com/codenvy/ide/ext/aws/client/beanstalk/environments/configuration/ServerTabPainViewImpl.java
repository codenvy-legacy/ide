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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link ServerTabPainView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ServerTabPainViewImpl extends Composite implements ServerTabPainView {
    interface ServerTabPainViewImplUiBinder extends UiBinder<Widget, ServerTabPainViewImpl> {
    }

    private static ServerTabPainViewImplUiBinder uiBinder = GWT.create(ServerTabPainViewImplUiBinder.class);

    @UiField
    ModifiableListBox ec2InstanceTypeField;

    @UiField
    ModifiableTextBox ec2SecurityGroupsField;

    @UiField
    ModifiableTextBox keyNameField;

    @UiField
    ModifiableListBox monitoringIntervalField;

    @UiField
    ModifiableTextBox imageIdField;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected ServerTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setEc2InstanceTypes(JsonArray<String> instanceTypes, String valueToSelect) {
        ec2InstanceTypeField.clear();
        int indexToSelect = 0;

        for (int i = 0; i < instanceTypes.size(); i++) {
            ec2InstanceTypeField.addItem(instanceTypes.get(i));
            if (instanceTypes.get(i).equals(valueToSelect)) {
                indexToSelect = i;
            }
        }

        ec2InstanceTypeField.setSelectedIndex(indexToSelect);
    }

    /** {@inheritDoc} */
    @Override
    public String getEc2InstanceType() {
        return ec2InstanceTypeField.getItemText(ec2InstanceTypeField.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setEc2SecurityGroup(String securityGroup) {
        ec2SecurityGroupsField.setText(securityGroup);
    }

    /** {@inheritDoc} */
    @Override
    public String getEc2SecurityGroup() {
        return ec2SecurityGroupsField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setKeyPair(String keyPair) {
        keyNameField.setText(keyPair);
    }

    /** {@inheritDoc} */
    @Override
    public String getKeyPair() {
        return keyNameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setMonitoringInterval(JsonArray<String> interval, String valueForSelect) {
        monitoringIntervalField.clear();
        int indexToSelect = 0;

        for (int i = 0; i < interval.size(); i++) {
            monitoringIntervalField.addItem(interval.get(i));
            if (interval.get(i).equals(valueForSelect)) {
                indexToSelect = i;
            }
        }

        monitoringIntervalField.setSelectedIndex(indexToSelect);
    }

    /** {@inheritDoc} */
    public String getMonitoringInterval() {
        return monitoringIntervalField.getItemText(monitoringIntervalField.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setAmiId(String amiId) {
        imageIdField.setText(amiId);
    }

    /** {@inheritDoc} */
    @Override
    public String getAmiId() {
        return imageIdField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void resetModifiedFields() {
        ec2InstanceTypeField.setModified(false);
        ec2SecurityGroupsField.setModified(false);
        keyNameField.setModified(false);
        monitoringIntervalField.setModified(false);
        imageIdField.setModified(false);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEc2InstanceTypeModified() {
        return ec2InstanceTypeField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEc2SecurityGroupModified() {
        return ec2SecurityGroupsField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHeyPairModified() {
        return keyNameField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMonitoringIntervalModified() {
        return monitoringIntervalField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAmiIdModified() {
        return imageIdField.isModified();
    }

    @UiHandler("ec2InstanceTypeField")
    public void onEc2InstanceTypeFieldChanged(ChangeEvent event) {
        ec2InstanceTypeField.setModified(true);
    }

    @UiHandler("ec2SecurityGroupsField")
    public void onEc2SecurityGroupsFieldChanged(KeyUpEvent event) {
        ec2SecurityGroupsField.setModified(true);
    }

    @UiHandler("keyNameField")
    public void onKeyNameFieldChanged(KeyUpEvent event) {
        keyNameField.setModified(true);
    }

    @UiHandler("monitoringIntervalField")
    public void onMonitoringIntervalFieldChanged(ChangeEvent event) {
        monitoringIntervalField.setModified(true);
    }

    @UiHandler("imageIdField")
    public void onImageIdFieldChanged(KeyUpEvent event) {
        imageIdField.setModified(true);
    }
}

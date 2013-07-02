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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ContainerTabPainViewImpl extends Composite implements ContainerTabPainView {
    interface ContainerTabPainViewImplUiBinder extends UiBinder<Widget, ContainerTabPainViewImpl> {}

    private static ContainerTabPainViewImplUiBinder uiBinder = GWT.create(ContainerTabPainViewImplUiBinder.class);

    @UiField
    ModifiableTextBox initialJVMHeapSizeField;

    @UiField
    ModifiableTextBox maximumJVMHeapSizeField;

    @UiField
    ModifiableTextBox maxPermSizeField;

    @UiField
    ModifiableTextBox jvmOptionsField;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    @Inject
    protected ContainerTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.initWidget(widget);
    }

    @Override
    public void setInitialHeapSize(String heapSize) {
        initialJVMHeapSizeField.setText(heapSize);
    }

    @Override
    public String getInitialHeapSize() {
        return initialJVMHeapSizeField.getText();
    }

    @Override
    public void setMaxHeapSize(String maxHeapSize) {
        maximumJVMHeapSizeField.setText(maxHeapSize);
    }

    @Override
    public String getMaxHeapSize() {
        return maximumJVMHeapSizeField.getText();
    }

    @Override
    public void setMaxPermGenSize(String maxPermGenSize) {
        maxPermSizeField.setText(maxPermGenSize);
    }

    @Override
    public String getMaxPermGenSize() {
        return maxPermSizeField.getText();
    }

    @Override
    public void setJVMCommandLineOpt(String jvmCommandLineOpt) {
        jvmOptionsField.setText(jvmCommandLineOpt);
    }

    @Override
    public String getJVMCommandLineOpt() {
        return jvmOptionsField.getText();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void resetModifiedFields() {
        initialJVMHeapSizeField.setModified(false);
        maximumJVMHeapSizeField.setModified(false);
        maxPermSizeField.setModified(false);
        jvmOptionsField.setModified(false);
    }

    @Override
    public boolean isInitialHeapSizeModified() {
        return initialJVMHeapSizeField.isModified();
    }

    @Override
    public boolean isMaxHeapSizeModified() {
        return maximumJVMHeapSizeField.isModified();
    }

    @Override
    public boolean isMaxPermGenSizeModified() {
        return maxPermSizeField.isModified();
    }

    @Override
    public boolean isJVMCommandLineOptModified() {
        return jvmOptionsField.isModified();
    }

    @UiHandler("initialJVMHeapSizeField")
    public void onInitialJVMHeapSizeFieldChanged(KeyUpEvent event) {
        initialJVMHeapSizeField.setModified(true);
    }

    @UiHandler("maximumJVMHeapSizeField")
    public void onMaximumJVMHeapSizeFieldChanged(KeyUpEvent event) {
        maximumJVMHeapSizeField.setModified(true);
    }

    @UiHandler("maxPermSizeField")
    public void onMaxPermSizeFieldChanged(KeyUpEvent event) {
        maxPermSizeField.setModified(true);
    }

    @UiHandler("jvmOptionsField")
    public void onJvmOptionsFieldChanged(KeyUpEvent event) {
        jvmOptionsField.setModified(true);
    }


}

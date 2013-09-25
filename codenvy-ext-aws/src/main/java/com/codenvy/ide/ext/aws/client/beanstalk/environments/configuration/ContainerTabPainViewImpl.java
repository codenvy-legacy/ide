/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
 * The implementation of {@link ContainerTabPainView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ContainerTabPainViewImpl extends Composite implements ContainerTabPainView {
    interface ContainerTabPainViewImplUiBinder extends UiBinder<Widget, ContainerTabPainViewImpl> {
    }

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

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected ContainerTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.initWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setInitialHeapSize(String heapSize) {
        initialJVMHeapSizeField.setText(heapSize);
    }

    /** {@inheritDoc} */
    @Override
    public String getInitialHeapSize() {
        return initialJVMHeapSizeField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxHeapSize(String maxHeapSize) {
        maximumJVMHeapSizeField.setText(maxHeapSize);
    }

    /** {@inheritDoc} */
    @Override
    public String getMaxHeapSize() {
        return maximumJVMHeapSizeField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxPermGenSize(String maxPermGenSize) {
        maxPermSizeField.setText(maxPermGenSize);
    }

    /** {@inheritDoc} */
    @Override
    public String getMaxPermGenSize() {
        return maxPermSizeField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setJVMCommandLineOpt(String jvmCommandLineOpt) {
        jvmOptionsField.setText(jvmCommandLineOpt);
    }

    /** {@inheritDoc} */
    @Override
    public String getJVMCommandLineOpt() {
        return jvmOptionsField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void resetModifiedFields() {
        initialJVMHeapSizeField.setModified(false);
        maximumJVMHeapSizeField.setModified(false);
        maxPermSizeField.setModified(false);
        jvmOptionsField.setModified(false);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInitialHeapSizeModified() {
        return initialJVMHeapSizeField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMaxHeapSizeModified() {
        return maximumJVMHeapSizeField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMaxPermGenSizeModified() {
        return maxPermSizeField.isModified();
    }

    /** {@inheritDoc} */
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

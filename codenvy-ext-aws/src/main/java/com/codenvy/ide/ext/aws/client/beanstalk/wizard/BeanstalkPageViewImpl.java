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
package com.codenvy.ide.ext.aws.client.beanstalk.wizard;

import com.codenvy.ide.ext.aws.client.AWSResource;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link BeanstalkPageView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class BeanstalkPageViewImpl extends Composite implements BeanstalkPageView {
    interface BeanstalkPageViewImplUiBinder extends UiBinder<Widget, BeanstalkPageViewImpl> {
    }

    private static BeanstalkPageViewImplUiBinder uiBinder = GWT.create(BeanstalkPageViewImplUiBinder.class);

    @UiField
    TextBox appNameField;

    @UiField
    TextBox envNameField;

    @UiField
    ListBox solutionStack;

    @UiField(provided = true)
    final AWSResource resource;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resource
     */
    @Inject
    public BeanstalkPageViewImpl(AWSResource resource) {
        this.resource = resource;

        initWidget(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationName(String applicationName) {
        this.appNameField.setText(applicationName);
    }

    /** {@inheritDoc} */
    @Override
    public String getApplicationName() {
        return appNameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setEnvironmentName(String environmentName) {
        this.envNameField.setText(environmentName);
    }

    /** {@inheritDoc} */
    @Override
    public String getEnvironmentName() {
        return envNameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setSolutionStack(JsonArray<String> stack) {
        for (int i = 0; i < stack.size(); i++) {
            this.solutionStack.addItem(stack.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getSolutionStack() {
        return solutionStack.getItemText(solutionStack.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("appNameField")
    public void onApplicationNameChanged(KeyUpEvent event) {
        delegate.onApplicationNameChange();
    }

    @UiHandler("envNameField")
    public void onEnvironmentNameChanged(KeyUpEvent event) {
        delegate.onEnvironmentNameChange();
    }
}

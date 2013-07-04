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

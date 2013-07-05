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
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link EditConfigurationView}.
 *
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

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private boolean isShown;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected EditConfigurationViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.environmentConfigurationTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget addServerTabPain(String tabText) {
        SimplePanel panel = new SimplePanel();
        configurationTabPanel.add(panel, tabText);
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget addLoadBalancerTabPain(String tabText) {
        SimplePanel panel = new SimplePanel();
        configurationTabPanel.add(panel, tabText);
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget addContainerTabPain(String tabText) {
        SimplePanel panel = new SimplePanel();
        configurationTabPanel.add(panel, tabText);
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    public void focusInFirstTab() {
        configurationTabPanel.selectTab(0);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("okButton")
    public void onOkButtonClicked(ClickEvent event) {
        delegate.onApplyButtonCLicked();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonClicked();
    }
}

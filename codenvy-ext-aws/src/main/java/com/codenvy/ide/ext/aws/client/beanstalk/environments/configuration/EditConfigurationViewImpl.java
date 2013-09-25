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

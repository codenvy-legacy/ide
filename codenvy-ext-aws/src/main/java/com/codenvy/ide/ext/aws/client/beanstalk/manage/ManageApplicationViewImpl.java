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
package com.codenvy.ide.ext.aws.client.beanstalk.manage;

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
 * The implementation of {@link ManageApplicationView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ManageApplicationViewImpl extends DialogBox implements ManageApplicationView {
    interface ManageApplicationViewImplUiBinder extends UiBinder<Widget, ManageApplicationViewImpl> {
    }

    private static ManageApplicationViewImplUiBinder uiBinder = GWT.create(ManageApplicationViewImplUiBinder.class);

    @UiField
    TabPanel applicationTabPanel;

    @UiField
    Button closeButton;

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
    protected ManageApplicationViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.manageApplicationViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget addMainTabPain(String tabText) {
        SimplePanel panel = new SimplePanel();
        applicationTabPanel.add(panel, tabText);
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget addVersionTabPain(String tabText) {
        SimplePanel panel = new SimplePanel();
        applicationTabPanel.add(panel, tabText);
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget addEnvironmentTabPain(String tabText) {
        SimplePanel panel = new SimplePanel();
        applicationTabPanel.add(panel, tabText);
        return panel;
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

    /** {@inheritDoc} */
    @Override
    public void focusInFirstTab() {
        applicationTabPanel.selectTab(0);
    }

    @UiHandler("closeButton")
    public void onCloseButtonClicked(ClickEvent event) {
        delegate.onCloseButtonClicked();
    }
}

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
 * The implementation of {@link MainTabPainView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class MainTabPainViewImpl extends Composite implements MainTabPainView {
    interface MainTabPainViewImplUiBinder extends UiBinder<Widget, MainTabPainViewImpl> {
    }

    private static MainTabPainViewImplUiBinder uiBinder = GWT.create(MainTabPainViewImplUiBinder.class);

    @UiField
    TextBox nameField;

    @UiField
    TextBox descriptionField;

    @UiField
    Button editDescriptionButton;

    @UiField
    Button deleteApplicationButton;

    @UiField
    Button createVersionButton;

    @UiField
    Button launchEnvironmentButton;

    @UiField
    Label creationDateField;

    @UiField
    Label updatedDateField;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected MainTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationName(String applicationName) {
        nameField.setText(applicationName);
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(String description) {
        descriptionField.setText(description);
    }

    /** {@inheritDoc} */
    @Override
    public void setCreationDate(String date) {
        creationDateField.setText(date);
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdateDate(String date) {
        updatedDateField.setText(date);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("editDescriptionButton")
    public void onEditDescriptionButtonClicked(ClickEvent event) {
        delegate.onEditDescriptionButtonClicked();
    }

    @UiHandler("deleteApplicationButton")
    public void onDeleteApplicationButtonClicked(ClickEvent event) {
        delegate.onDeleteApplicationButtonClicked();
    }

    @UiHandler("createVersionButton")
    public void onCreateVersionButtonClicked(ClickEvent event) {
        delegate.onCreateNewVersionButtonClicked();
    }

    @UiHandler("launchEnvironmentButton")
    public void onLaunchEnvironmentButtonClicked(ClickEvent event) {
        delegate.onLaunchNewEnvironmentButtonClicked();
    }
}

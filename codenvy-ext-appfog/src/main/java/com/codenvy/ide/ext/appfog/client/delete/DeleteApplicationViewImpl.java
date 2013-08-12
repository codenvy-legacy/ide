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
package com.codenvy.ide.ext.appfog.client.delete;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link DeleteApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DeleteApplicationViewImpl extends DialogBox implements DeleteApplicationView {
    interface DeleteApplicationViewImplUiBinder extends UiBinder<Widget, DeleteApplicationViewImpl> {
    }

    private static DeleteApplicationViewImplUiBinder ourUiBinder = GWT.create(DeleteApplicationViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    com.codenvy.ide.ui.Button btnDelete;
    @UiField
    CheckBox                  deleteServicesField;
    @UiField
    Label                     askLabel;
    @UiField(provided = true)
    final   AppfogResources            res;
    @UiField(provided = true)
    final   AppfogLocalizationConstant locale;
    private ActionDelegate             delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected DeleteApplicationViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("Delete application from AppFog");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDeleteServices() {
        return deleteServicesField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setDeleteServices(boolean isDeleted) {
        deleteServicesField.setValue(isDeleted);
    }

    /** {@inheritDoc} */
    @Override
    public void setAskMessage(String message) {
        askLabel.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    @UiHandler("btnDelete")
    void onBtnDeleteClick(ClickEvent event) {
        delegate.onDeleteClicked();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }
}
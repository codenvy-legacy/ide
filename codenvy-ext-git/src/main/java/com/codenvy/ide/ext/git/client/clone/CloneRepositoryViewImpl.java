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
package com.codenvy.ide.ext.git.client.clone;

import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link CloneRepositoryView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloneRepositoryViewImpl extends DialogBox implements CloneRepositoryView {
    interface CloneRepositoryViewImplUiBinder extends UiBinder<Widget, CloneRepositoryViewImpl> {
    }

    private static CloneRepositoryViewImplUiBinder ourUiBinder = GWT.create(CloneRepositoryViewImplUiBinder.class);

    @UiField
    TextBox                   remoteUri;
    @UiField
    TextBox                   projectName;
    @UiField
    TextBox                   remoteName;
    @UiField
    com.codenvy.ide.ui.Button btnClone;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField(provided = true)
    final   GitResources            res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private ActionDelegate          delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    public CloneRepositoryViewImpl(GitResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.cloneTitle());
        this.setWidget(widget);

        this.remoteUri.getElement().setPropertyString("placeholder", this.locale.cloneRemoteUriFieldExample());
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getProjectName() {
        return projectName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectName(@NotNull String projectName) {
        this.projectName.setText(projectName);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getRemoteUri() {
        return remoteUri.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoteUri(@NotNull String remoteUri) {
        this.remoteUri.setText(remoteUri);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getRemoteName() {
        return remoteName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoteName(@NotNull String remoteName) {
        this.remoteName.setText(remoteName);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableCloneButton(boolean enable) {
        btnClone.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInRemoteUrlField() {
        remoteUri.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    @UiHandler("btnClone")
    public void onCloneClicked(ClickEvent event) {
        delegate.onCloneClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("remoteUri")
    public void onRemoteUriChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}
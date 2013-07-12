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
package com.codenvy.ide.ext.git.client.clone;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
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
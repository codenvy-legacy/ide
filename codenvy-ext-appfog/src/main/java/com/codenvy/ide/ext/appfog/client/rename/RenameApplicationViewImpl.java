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
package com.codenvy.ide.ext.appfog.client.rename;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
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
 * The implementation of {@link RenameApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class RenameApplicationViewImpl extends DialogBox implements RenameApplicationView {
    interface RenameApplicationViewImplUiBinder extends UiBinder<Widget, RenameApplicationViewImpl> {
    }

    private static RenameApplicationViewImplUiBinder ourUiBinder = GWT.create(RenameApplicationViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnRename;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    TextBox                   nameField;
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
    protected RenameApplicationViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.renameApplicationViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void selectValueInRenameField() {
        nameField.selectAll();
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableRenameButton(boolean isEnabled) {
        btnRename.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return nameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        nameField.setText(name);
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

    @UiHandler("btnRename")
    void onBtnRenameClick(ClickEvent event) {
        delegate.onRenameClicked();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("nameField")
    void onNameFieldKeyUp(KeyUpEvent event) {
        delegate.onNameChanged();
    }
}
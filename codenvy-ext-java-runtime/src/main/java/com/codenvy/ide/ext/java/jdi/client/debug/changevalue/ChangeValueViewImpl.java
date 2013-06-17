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
package com.codenvy.ide.ext.java.jdi.client.debug.changevalue;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link ChangeValueView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ChangeValueViewImpl extends DialogBox implements ChangeValueView {
    interface ChangeValueViewImplUiBinder extends UiBinder<Widget, ChangeValueViewImpl> {
    }

    private static ChangeValueViewImplUiBinder ourUiBinder = GWT.create(ChangeValueViewImplUiBinder.class);

    @UiField
    Button                          btnChange;
    @UiField
    Button                          btnCancel;
    @UiField
    TextArea                        value;
    @UiField
    Label                           changeValueLabel;
    @UiField(provided = true)
    JavaRuntimeLocalizationConstant locale;
    @UiField(provided = true)
    JavaRuntimeResources            res;
    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected ChangeValueViewImpl(JavaRuntimeResources resources, JavaRuntimeLocalizationConstant locale) {
        this.locale = locale;
        this.res = resources;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(this.locale.changeValueViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getValue() {
        return value.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(@NotNull String value) {
        this.value.setText(value);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableChangeButton(boolean isEnable) {
        btnChange.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInValueField() {
        value.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void selectAllText() {
        value.selectAll();
    }

    /** {@inheritDoc} */
    @Override
    public void setValueTitle(@NotNull String title) {
        changeValueLabel.setText(title);
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

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnChange")
    public void onChangeButtonClicked(ClickEvent event) {
        delegate.onChangeClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("value")
    public void onValueChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}
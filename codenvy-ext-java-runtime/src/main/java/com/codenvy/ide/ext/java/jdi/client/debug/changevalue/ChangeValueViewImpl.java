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
package com.codenvy.ide.ext.java.jdi.client.debug.changevalue;

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

import javax.validation.constraints.NotNull;

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
        changeValueLabel.getElement().setInnerHTML(title);
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
        delegate.onVariableValueChanged();
    }
}
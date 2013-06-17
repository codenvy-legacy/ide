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
package com.codenvy.ide.ext.java.jdi.client.debug.expression;

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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link EvaluateExpressionView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class EvaluateExpressionViewImpl extends DialogBox implements EvaluateExpressionView {
    interface EvaluateExpressionViewImplUiBinder extends UiBinder<Widget, EvaluateExpressionViewImpl> {
    }

    private static EvaluateExpressionViewImplUiBinder ourUiBinder = GWT.create(EvaluateExpressionViewImplUiBinder.class);

    @UiField
    Button                          btnEvaluate;
    @UiField
    Button                          btnCancel;
    @UiField
    TextArea                        expression;
    @UiField
    TextArea                        result;
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
    protected EvaluateExpressionViewImpl(JavaRuntimeResources resources, JavaRuntimeLocalizationConstant locale) {
        this.locale = locale;
        this.res = resources;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(this.locale.evaluateExpressionViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getExpression() {
        return expression.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setExpression(@NotNull String expression) {
        this.expression.setText(expression);
    }

    /** {@inheritDoc} */
    @Override
    public void setResult(@NotNull String value) {
        this.result.setText(value);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableEvaluateButton(boolean enabled) {
        btnEvaluate.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInExpressionField() {
        expression.setFocus(true);
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

    @UiHandler("btnEvaluate")
    public void onEvaluateClicked(ClickEvent event) {
        delegate.onEvaluateClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("expression")
    public void handleKeyUp(KeyUpEvent event) {
        delegate.onValueExpressionChanged();
    }
}
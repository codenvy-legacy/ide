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
package com.codenvy.ide.ext.git.client.add;

import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
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

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link AddToIndexView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class AddToIndexViewImpl extends DialogBox implements AddToIndexView {
    interface AddToIndexViewImplUiBinder extends UiBinder<Widget, AddToIndexViewImpl> {
    }

    private static AddToIndexViewImplUiBinder ourUiBinder = GWT.create(AddToIndexViewImplUiBinder.class);

    @UiField
    Label                     message;
    @UiField
    CheckBox                  update;
    @UiField
    com.codenvy.ide.ui.Button btnAdd;
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
    protected AddToIndexViewImpl(GitResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.addToIndexTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setMessage(@NotNull String message) {
        this.message.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUpdated() {
        return update.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdated(boolean isUpdated) {
        update.setValue(isUpdated);
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

    @UiHandler("btnAdd")
    public void onAddClicked(ClickEvent event) {
        delegate.onAddClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }
}
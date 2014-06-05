/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.client.init;

import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link InitRepositoryView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class InitRepositoryViewImpl extends Window implements InitRepositoryView {
    interface InitRepositoryViewImplUiBinder extends UiBinder<Widget, InitRepositoryViewImpl> {
    }

    private static InitRepositoryViewImplUiBinder ourUiBinder = GWT.create(InitRepositoryViewImplUiBinder.class);

    @UiField
    TextBox  workdir;
    Button   btnOk;
    Button   btnCancel;
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
    protected InitRepositoryViewImpl(GitResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.createTitle());
        this.setWidget(widget);
        
        
        btnCancel = createButton(locale.buttonCancel(), "git-initializeRepository-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnOk = createButton(locale.buttonOk(), "git-initializeRepository-ok", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onOkClicked();
            }
        });
        getFooter().add(btnOk);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getWorkDir() {
        return workdir.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setWorkDir(@NotNull String workDir) {
        this.workdir.setText(workDir);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableOkButton(boolean enable) {
        btnOk.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    @UiHandler("workdir")
    public void onValueChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}
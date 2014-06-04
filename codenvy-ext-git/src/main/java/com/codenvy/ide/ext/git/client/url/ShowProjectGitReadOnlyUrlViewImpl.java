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
package com.codenvy.ide.ext.git.client.url;

import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link ShowProjectGitReadOnlyUrlView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowProjectGitReadOnlyUrlViewImpl extends Window implements ShowProjectGitReadOnlyUrlView {
    interface ShowProjectGitReadOnlyUrlViewImplUiBinder extends UiBinder<Widget, ShowProjectGitReadOnlyUrlViewImpl> {
    }

    private static ShowProjectGitReadOnlyUrlViewImplUiBinder ourUiBinder = GWT.create(ShowProjectGitReadOnlyUrlViewImplUiBinder.class);

    @UiField
    TextBox url;
    Button  btnClose;
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
    protected ShowProjectGitReadOnlyUrlViewImpl(GitResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;
        this.ensureDebugId("projectReadOnlyGitUrl-window");

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.projectReadOnlyGitUrlTitle());
        this.setWidget(widget);
        
        btnClose = createButton(locale.buttonClose(), "", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        btnClose.ensureDebugId("projectReadOnlyGitUrl-btnClose");
        getFooter().add(btnClose);
    }

    /** {@inheritDoc} */
    @Override
    public void setUrl(@NotNull String url) {
        this.url.setText(url);
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

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}
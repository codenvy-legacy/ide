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
package com.codenvy.ide.ext.git.client.url;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitClientResources;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link ShowProjectGitReadOnlyUrlView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowProjectGitReadOnlyUrlViewImpl extends DialogBox implements ShowProjectGitReadOnlyUrlView {
    interface ShowProjectGitReadOnlyUrlViewImplUiBinder extends UiBinder<Widget, ShowProjectGitReadOnlyUrlViewImpl> {
    }

    private static ShowProjectGitReadOnlyUrlViewImplUiBinder ourUiBinder = GWT.create(ShowProjectGitReadOnlyUrlViewImplUiBinder.class);

    @UiField
    TextBox                   url;
    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField(provided = true)
    final   GitClientResources      res;
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
    protected ShowProjectGitReadOnlyUrlViewImpl(GitClientResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.projectReadOnlyGitUrlTitle());
        this.setWidget(widget);
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
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnClose")
    public void onCloseClicked(ClickEvent event) {
        delegate.onCloseClicked();
    }
}
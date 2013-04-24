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
package com.codenvy.ide.java.client.template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public class CreateJavaProjectPageViewImpl extends Composite implements CreateJavaProjectPageView {

    interface CreateJavaProjectPageViewImplUiBinder
            extends UiBinder<Widget, CreateJavaProjectPageViewImpl> {
    }

    private static CreateJavaProjectPageViewImplUiBinder ourUiBinder = GWT.create(CreateJavaProjectPageViewImplUiBinder.class);

    private ActionDelegate delegate;

    @UiField
    TextBox sourceFolder;

    @Inject
    protected CreateJavaProjectPageViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getSourceFolder() {
        return sourceFolder.getText();
    }

    @UiHandler("sourceFolder")
    public void handleKeyUp(KeyUpEvent event) {
        delegate.checkSourceFolederInput();
    }
}
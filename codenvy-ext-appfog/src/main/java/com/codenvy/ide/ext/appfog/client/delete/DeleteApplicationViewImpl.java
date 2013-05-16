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
package com.codenvy.ide.ext.appfog.client.delete;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link DeleteApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DeleteApplicationViewImpl extends DialogBox implements DeleteApplicationView {
    interface DeleteApplicationViewImplUiBinder extends UiBinder<Widget, DeleteApplicationViewImpl> {
    }

    private static DeleteApplicationViewImplUiBinder ourUiBinder = GWT.create(DeleteApplicationViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    com.codenvy.ide.ui.Button btnDelete;
    @UiField
    CheckBox                  deleteServicesField;
    @UiField
    Label                     askLabel;
    @UiField(provided = true)
    final AppfogResources            res;
    @UiField(provided = true)
    final AppfogLocalizationConstant locale;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected DeleteApplicationViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("Delete application from AppFog");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDeleteServices() {
        return deleteServicesField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setDeleteServices(boolean isDeleted) {
        deleteServicesField.setValue(isDeleted);
    }

    /** {@inheritDoc} */
    @Override
    public void setAskMessage(String message) {
        askLabel.setText(message);
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
}
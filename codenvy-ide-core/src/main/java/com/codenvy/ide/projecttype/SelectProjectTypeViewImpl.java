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
package com.codenvy.ide.projecttype;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/** @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a> */
@Singleton
public class SelectProjectTypeViewImpl extends DialogBox implements SelectProjectTypeView {

    @UiField
    ListBox projectTypeField;
    @UiField
    Label   selectProjectLabel;
    @UiField
    Button  btnOk;
    @UiField
    Button  btnCancel;
    private ActionDelegate delegate;
    private Array<ProjectTypeDescriptor> projectTypes = Collections.createArray();

    @Inject
    protected SelectProjectTypeViewImpl(CoreLocalizationConstant localizationConstant, SelectProjectTypeViewImplUiBinder uiBinder) {
        this.setText(localizationConstant.setProjectTypeTitle());
        Widget widget = uiBinder.createAndBindUi(this);
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setTypes(Array<ProjectTypeDescriptor> types) {
        projectTypes.addAll(types);
        for (ProjectTypeDescriptor type : projectTypes.asIterable()) {
            projectTypeField.addItem(type.getProjectTypeName());
        }
    }

    @Override
    public void clearTypes() {
        projectTypes.clear();
        projectTypeField.clear();
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

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnOk")
    void onBtnOpenClick(ClickEvent event) {
        delegate.onOkClicked();
    }

    /** {@inheritDoc} */
    @Override
    public ProjectTypeDescriptor getSelectedProjectType() {
        final int index = projectTypeField.getSelectedIndex();
        return projectTypes.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public void setLabel(@NotNull String label) {
        selectProjectLabel.getElement().setInnerHTML(label);
    }

    interface SelectProjectTypeViewImplUiBinder extends UiBinder<Widget, SelectProjectTypeViewImpl> {
    }
}

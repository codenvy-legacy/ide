/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.projecttype;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.collections.Array;
import com.google.gwt.core.client.GWT;
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

/**
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 * @version $Id:
 */
@Singleton
public class SelectProjectTypeViewImpl extends DialogBox implements SelectProjectTypeView {

    interface SelectProjectTypeViewImplUiBinder extends UiBinder<Widget, SelectProjectTypeViewImpl> {
    }

    @UiField
    ListBox projectTypeField;
    @UiField
    Label   selectProjectLabel;

    @UiField
    Button btnOk;
    @UiField
    Button btnCancel;

    private ActionDelegate delegate;

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
    public void setTypes(Array<String> types) {
        for (String type : types.asIterable()) {
            projectTypeField.addItem(type);
        }
    }

    @Override
    public void clearTypes() {
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
    public String getProjectType() {
        int index = projectTypeField.getSelectedIndex();
        return index != -1 ? projectTypeField.getItemText(index) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setLabel(@NotNull String label) {
        selectProjectLabel.getElement().setInnerHTML(label);
    }
}

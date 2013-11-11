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
package com.codenvy.ide.tutorial.wizard.pages.page1;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The implementation of {@link Page1View}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class Page1ViewImpl extends Composite implements Page1View {
    interface Page1ViewImplUiBinder extends UiBinder<Widget, Page1ViewImpl> {
    }

    private static Page1ViewImplUiBinder ourUiBinder = GWT.create(Page1ViewImplUiBinder.class);

    @UiField
    RadioButton page2;
    @UiField
    RadioButton page3;
    @UiField
    CheckBox    page4;
    private ActionDelegate delegate;

    @Inject
    public Page1ViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPage2Next() {
        return page2.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setPage2Next(boolean page2Next) {
        if (page2Next) {
            page2.setValue(true);
        } else {
            page3.setValue(true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPage4Show() {
        return page4.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setPage4Show(boolean skip) {
        page4.setValue(skip);
    }

    @UiHandler("page2")
    public void onPage2Clicked(ClickEvent event) {
        delegate.onPage2Chosen();
    }

    @UiHandler("page3")
    public void onPage3Clicked(ClickEvent event) {
        delegate.onPage3Chosen();
    }

    @UiHandler("page4")
    public void onPage4Clicked(ClickEvent event) {
        delegate.onPage4Clicked();
    }
}
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
package com.codenvy.ide.tutorial.wizard.pages.page1;

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

    @UiField
    RadioButton page2;
    @UiField
    RadioButton page3;
    @UiField
    CheckBox    page4;
    private ActionDelegate delegate;

    @Inject
    public Page1ViewImpl(Page1ViewImplUiBinder ourUiBinder) {
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
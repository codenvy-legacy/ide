/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Singleton;

/**
 * WizardViewImpl is the view of wizard.
 * The view shows wizard pages to the end user. It has an area at the top containing
 * the wizard page title and notice, at the middle of page is the current wizard page,
 * Back and Next buttons, at the bottom of page is Cancel and Finish buttons.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class WizardViewImpl extends DialogBox implements WizardView {
    interface ViewImplUiBinder extends UiBinder<Widget, WizardViewImpl> {
    }

    private static ViewImplUiBinder uiBinder = GWT.create(ViewImplUiBinder.class);

    private final int ANIMATION_TIME = 400;
    private final int NO_TIME        = 0;

    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    com.codenvy.ide.ui.Button btnFinish;
    @UiField
    com.codenvy.ide.ui.Button btnBack;
    @UiField
    com.codenvy.ide.ui.Button btnNext;
    @UiField
    SimplePanel               imagePanel;
    @UiField
    Label                     caption;
    @UiField
    Label                     notice;
    @UiField
    DeckLayoutPanel           contentPanel;
    @UiField(provided = true)
    final   WizardResource res;
    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param title
     * @param resource
     */
    protected WizardViewImpl(@NotNull String title, @NotNull WizardResource resource) {
        this.res = resource;

        Widget widget = uiBinder.createAndBindUi(this);
        this.setText(title);
        this.setWidget(widget);
    }


    /** {@inheritDoc} */
    public void setNextButtonVisible(boolean isVisible) {
        btnNext.setVisible(isVisible);
    }

    /** {@inheritDoc} */
    public void setNextButtonEnabled(boolean isEnabled) {
        btnNext.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    public void setBackButtonVisible(boolean isVisible) {
        btnBack.setVisible(isVisible);
    }

    /** {@inheritDoc} */
    public void setFinishButtonEnabled(boolean isEnabled) {
        btnFinish.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    public void setCaption(@NotNull String caption) {
        this.caption.setText(caption);
    }

    /** {@inheritDoc} */
    public void setNotice(@Nullable String notice) {
        this.notice.setText(notice);
    }

    /** {@inheritDoc} */
    public void setImage(@Nullable Image image) {
        imagePanel.setWidget(image);
    }

    /** {@inheritDoc} */
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    public void showWizard() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    public void setChangePageAnimationEnabled(boolean isEnabled) {
        if (isEnabled) {
            contentPanel.setAnimationDuration(ANIMATION_TIME);
        } else {
            contentPanel.setAnimationDuration(NO_TIME);
        }
    }

    /** {@inheritDoc} */
    public AcceptsOneWidget getContentPanel() {
        return contentPanel;
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnFinish")
    void onBtnFinishClick(ClickEvent event) {
        delegate.onFinishClicked();
    }

    @UiHandler("btnNext")
    void onBtnNextClick(ClickEvent event) {
        delegate.onNextClicked();
    }

    @UiHandler("btnBack")
    void onBtnBackClick(ClickEvent event) {
        delegate.onBackClicked();
    }
}
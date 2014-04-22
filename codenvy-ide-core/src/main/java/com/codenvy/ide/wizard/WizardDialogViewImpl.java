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
package com.codenvy.ide.wizard;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * WizardDialogViewImpl is the view of wizard.
 * The view shows wizard pages to the end user. It has an area at the top containing
 * the wizard page title and notice, at the middle of page is the current wizard page,
 * Back and Next buttons, at the bottom of page is Cancel and Finish buttons.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class WizardDialogViewImpl extends Window implements WizardDialogView {
    interface ViewImplUiBinder extends UiBinder<Widget, WizardDialogViewImpl> {
    }

    private static ViewImplUiBinder uiBinder = GWT.create(ViewImplUiBinder.class);

    private final int ANIMATION_TIME = 400;
    private final int NO_TIME        = 0;

    Button          btnCancel;
    Button          btnFinish;
    Button          btnBack;
    Button          btnNext;
    @UiField
    SimplePanel     imagePanel;
    @UiField
    HTML            caption;
    @UiField
    HTML            notice;
    @UiField
    DeckLayoutPanel contentPanel;
    @UiField(provided = true)
    final   com.codenvy.ide.Resources      res;
    final CoreLocalizationConstant locale;
    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resource
     */
    @Inject
    protected WizardDialogViewImpl(com.codenvy.ide.Resources resource, CoreLocalizationConstant locale) {
        this.res = resource;
        this.locale = locale;
        
        Widget widget = uiBinder.createAndBindUi(this);
        this.setWidget(widget);
        createButtons();
    }
    
    private void createButtons(){
        btnCancel = createButton(locale.cancel(), "file-newOther-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnFinish = createButton(locale.finish(), "file-newOther-finish", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onFinishClicked();
            }
        });
        getFooter().add(btnFinish);

        btnNext = createButton(locale.next(), "file-newOther-next", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onNextClicked();
            }
        });
        getFooter().add(btnNext);

        btnBack = createButton(locale.back(), "file-newOther-back", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onBackClicked();
            }
        });
        getFooter().add(btnBack);
    }

    /** {@inheritDoc} */
    @Override
    public void setNextButtonVisible(boolean isVisible) {
        btnNext.setVisible(isVisible);
    }

    /** {@inheritDoc} */
    @Override
    public void setNextButtonEnabled(boolean isEnabled) {
        btnNext.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setBackButtonVisible(boolean isVisible) {
        btnBack.setVisible(isVisible);
    }

    /** {@inheritDoc} */
    @Override
    public void setFinishButtonEnabled(boolean isEnabled) {
        btnFinish.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setCaption(@NotNull String caption) {
        this.caption.setHTML(caption);
    }

    /** {@inheritDoc} */
    @Override
    public void setNotice(@Nullable String notice) {
        this.notice.setHTML(notice);
    }

    /** {@inheritDoc} */
    @Override
    public void setImage(@Nullable ImageResource image) {
        imagePanel.setWidget(image == null ? null : new Image(image));
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
    public void setEnabledAnimation(boolean isEnabled) {
        contentPanel.setAnimationDuration(isEnabled ? ANIMATION_TIME : NO_TIME);
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContentPanel() {
        return contentPanel;
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}
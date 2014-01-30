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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.wizard.newproject.step.Step;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
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
public class ProjectWizardViewImpl extends Window implements ProjectWizardView {
    interface ViewImplUiBinder extends UiBinder<Widget, ProjectWizardViewImpl> {
    }

    private static ViewImplUiBinder uiBinder = GWT.create(ViewImplUiBinder.class);

    private final        int            ANIMATION_TIME  = 400;
    private final        int            NO_TIME         = 0;
    private static final WizardResource WIZARD_RESOURCE = GWT.create(WizardResource.class);
    static{
        WIZARD_RESOURCE.css().ensureInjected();
    }

    @UiField
    Button          btnCancel;
    @UiField
    Button          btnFinish;
    @UiField
    Button          btnBack;
    @UiField
    Button          btnNext;
    //    @UiField
//    SimplePanel     imagePanel;
//    @UiField
//    HTML            caption;
//    @UiField
//    HTML            notice;
    @UiField
    DeckLayoutPanel contentPanel;
    @UiField(provided = true)
    final com.codenvy.ide.Resources res;
    @UiField
    FlowPanel stepsPanel;
    @UiField
    HTMLPanel arrow;

    @UiField(provided = true)
    Css style;
    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resource
     */
    @Inject
    protected ProjectWizardViewImpl(com.codenvy.ide.Resources resource) {
        this.res = resource;
        this.style = WIZARD_RESOURCE.css();
        Widget widget = uiBinder.createAndBindUi(this);
        this.setWidget(widget);
    }

    @Override
    public void setStepTitles(Array<String> stepsTitles) {
        stepsPanel.clear();
        int i = 1;
        for (String s : stepsTitles.asIterable()) {
            if (s != null && !s.isEmpty())
                stepsPanel.add(new Step(s, i++));
        }
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

    @Override
    protected void onClose() {
        delegate.onCancelClicked();
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
//        this.caption.setHTML(caption);
    }

    /** {@inheritDoc} */
    @Override
    public void setNotice(@Nullable String notice) {
//        this.notice.setHTML(notice);
    }

    /** {@inheritDoc} */
    @Override
    public void setImage(@Nullable ImageResource image) {
//        imagePanel.setWidget(image == null ? null : new Image(image));
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        arrow.getElement().getStyle().setTop(WIZARD_RESOURCE.css().arrowTop(), Style.Unit.PX);
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledAnimation(boolean isEnabled) {
//        contentPanel.setAnimationDuration(isEnabled ? ANIMATION_TIME : NO_TIME);
    }

    /** {@inheritDoc} */
    @Override
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
        arrow.getElement().getStyle().setTop(arrow.getElement().getOffsetTop() + 42, Style.Unit.PX);
    }

    @UiHandler("btnBack")
    void onBtnBackClick(ClickEvent event) {
        delegate.onBackClicked();
        arrow.getElement().getStyle().setTop(arrow.getElement().getOffsetTop() - 42, Style.Unit.PX);
    }

    public interface Css extends CssResource{

        String alignBtn();

        String stepsPanel();

        String buttonPanel();

        String buttons();

        String centralPage();

        String arrow_box();

        String space();

        int arrowTop();
    }

    public interface WizardResource extends ClientBundle{

        @Source({"Wizard.css", "com/codenvy/ide/api/ui/style.css"})
        Css css();
    }
}
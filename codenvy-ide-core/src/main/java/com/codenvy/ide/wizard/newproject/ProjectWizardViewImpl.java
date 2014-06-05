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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.wizard.newproject.step.Step;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

    Button          btnCancel;
    Button          btnFinish;
    Button          btnBack;
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
    private CoreLocalizationConstant locale;

    /**
     * Create view.
     *
     * @param resource
     */
    @Inject
    protected ProjectWizardViewImpl(com.codenvy.ide.Resources resource, CoreLocalizationConstant locale) {
        this.res = resource;
        this.locale = locale;
        this.style = WIZARD_RESOURCE.css();
        Widget widget = uiBinder.createAndBindUi(this);
        this.setWidget(widget);
        createButtons();
    }
    
    private void createButtons(){
        btnCancel = createButton(locale.cancel(), "file-newProject-cancel", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);
        
        btnFinish = createButton(locale.finish(), "file-newProject-finish", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                delegate.onFinishClicked();
            }
        });
        getFooter().add(btnFinish);
        
        btnNext = createButton(locale.next(), "file-newProject-next", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                delegate.onNextClicked();
            }
        });
        getFooter().add(btnNext);
        
        btnBack = createButton(locale.back(), "file-newProject-back", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                delegate.onBackClicked();
            }
        });
        getFooter().add(btnBack);
    }

    @Override
    public void setStepTitles(Array<String> stepsTitles) {
        stepsPanel.clear();
        int i = 1;
        for (String s : stepsTitles.asIterable()) {
            stepsPanel.add(new Step(s, i++));
        }
    }

    @Override
    public void setStepArrowPosition(int position) {
        arrow.getElement().getStyle().setTop(arrow.getElement().getOffsetTop() + 42 * position, Style.Unit.PX);
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
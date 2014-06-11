/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.wizard.project;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class ProjectWizardViewImpl extends Window implements ProjectWizardView {
    private static ProjectWizardViewImplUiBinder ourUiBinder = GWT.create(ProjectWizardViewImplUiBinder.class);
    @UiField
    TextBox     projectName;
    @UiField
    SimplePanel wizardPanel;
    @UiField
    FlowPanel   nextStep;
    @UiField
    FlowPanel   previousStep;
    @UiField
    Style       style;
    @UiField
    TextBox     projectDescription;
    @UiField
    RadioButton projectPrivate;
    @UiField
    RadioButton projectPublic;
    @UiField
    Button      saveButton;
    private ActionDelegate delegate;
    private Map<Presenter, Widget> pageCache = new HashMap<>();

    @Inject
    public ProjectWizardViewImpl(com.codenvy.ide.Resources resources) {
        super(false);
        setTitle("Project Configuration");
        setWidget(ourUiBinder.createAndBindUi(this));
        SVGImage svgImage = new SVGImage(resources.wizardArrow());
        svgImage.setClassNameBaseVal(style.svgStyle());
        nextStep.add(svgImage);

        SVGImage svgImageLeft = new SVGImage(resources.wizardArrow());
        svgImageLeft.setClassNameBaseVal(style.svgRotated());
        previousStep.add(svgImageLeft);
        nextStep.sinkEvents(Event.ONCLICK);
        previousStep.sinkEvents(Event.ONCLICK);
        nextStep.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!nextStep.getStyleName().contains(style.disabled()))
                    delegate.onNextClicked();
            }
        }, ClickEvent.getType());
        previousStep.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!previousStep.getStyleName().contains(style.disabled()))
                    delegate.onBackClicked();
            }
        }, ClickEvent.getType());
    }

    @UiHandler("projectName")
    void onProjectNameChanged(KeyUpEvent event) {
        delegate.projectNameChanged(projectName.getText());
    }

    @UiHandler("projectDescription")
    void onProjectDescriptionChanged(KeyUpEvent event) {
        delegate.projectDescriptionChanged(projectDescription.getValue());
    }

    @UiHandler({"projectPublic", "projectPrivate"})
    void visibilityHandler(ValueChangeEvent<Boolean> event) {
        delegate.projectVisibilityChanged(projectPublic.getValue());
    }

    @UiHandler("saveButton")
    void saveClick(ClickEvent event) {
        delegate.onSaveClicked();
    }

    @Override
    public void showPage(Presenter presenter) {
        wizardPanel.clear();
        if (pageCache.containsKey(presenter)) {
            wizardPanel.add(pageCache.get(presenter));
        } else {
            presenter.go(wizardPanel);
            pageCache.put(presenter, wizardPanel.getWidget());
        }
    }

    @Override
    public void showDialog() {
        show();
    }

    @Override
    public void setEnabledAnimation(boolean enabled) {

    }

    @Override
    public void close() {
        hide();
        pageCache.clear();
    }

    @Override
    public void setNextButtonEnabled(boolean enabled) {
        if (enabled) {
            nextStep.removeStyleName(style.disabled());
        } else {
            nextStep.addStyleName(style.disabled());
        }
    }

    @Override
    public void setFinishButtonEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
    }

    @Override
    public void setBackButtonEnabled(boolean enabled) {
        if (enabled) {
            previousStep.removeStyleName(style.disabled());
        } else {
            previousStep.addStyleName(style.disabled());
        }
    }

    @Override
    public void reset() {
        projectName.setText("");
        projectDescription.setText("");
        projectPublic.setValue(true, true);
        changeEnabledState(true);
    }

    @Override
    public void enableInput() {
        changeEnabledState(true);
    }

    @Override
    public void disableInput() {
        changeEnabledState(false);
    }

    @Override
    public void setName(String name) {
        projectName.setValue(name, true);
    }

    @Override
    public void setVisibility(boolean visible) {
        projectPublic.setValue(visible, false);
    }

    @Override
    public void removeNameError() {
        projectName.removeStyleName(style.inputError());
    }

    @Override
    public void showNameError() {
        projectName.addStyleName(style.inputError());
    }

    void changeEnabledState(boolean enabled) {
        projectName.setEnabled(enabled);
        projectDescription.setEnabled(enabled);
        projectPublic.setEnabled(enabled);
        projectPrivate.setEnabled(enabled);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    protected void onClose() {
        pageCache.clear();
        delegate.onCancelClicked();
    }

    interface Style extends CssResource {
        String svgStyle();

        String namePanel();

        String projectNamePosition();

        String project();

        String svgRotated();

        String buttonLikePanelRight();

        String buttonLikePanelLeft();

        String bottomPanel();

        String privacy();

        String tab();

        String namePanelRight();

        String rootPanel();

        String topPanel();

        String centerPanel();

        String blueButton();

        String disabled();

        String inputError();
    }

    interface ProjectWizardViewImplUiBinder
            extends UiBinder<FlowPanel, ProjectWizardViewImpl> {
    }
}
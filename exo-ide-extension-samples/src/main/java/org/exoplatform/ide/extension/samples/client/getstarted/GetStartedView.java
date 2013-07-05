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
package org.exoplatform.ide.extension.samples.client.getstarted;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class GetStartedView extends ViewImpl implements GetStartedPresenter.Display {
    private static final String       ID              = "codenvyGetStartedView";

    private static final String       PROJECT_NAME_ID = "codenvyGetStartedWizardProjectName";

    private static final String       TITLE           = "Get started";

    private static final int          HEIGHT          = 300;

    private static final int          WIDTH           = 630;

    private static GetStartedUiBinder uiBinder        = GWT.create(GetStartedUiBinder.class);

    interface GetStartedUiBinder extends UiBinder<Widget, GetStartedView> {
    }

    @UiField
    Anchor                                skipAnchor;

    @UiField
    Label                                 currentStepNumber;

    @UiField
    Button                                prevButton;

    @UiField
    Button                                nextButton;

    @UiField
    TextInput                             projectName;

    @UiField
    HTMLPanel                             chooseNamePanel;

    @UiField
    HTMLPanel                             chooseTechnologyPanel;

    @UiField
    HTMLPanel                             choosePaaSPanel;

    @UiField
    Grid                                  projectTypesGrid;

    @UiField
    Grid                                  paasGrid;

    @UiField
    Label                                 errorLabel;

    private List<ProjectTypeToggleButton> projectTypeToggleButtonList = new ArrayList<ProjectTypeToggleButton>();

    private List<PaaSToggleButton>        paaSToggleButtonList        = new ArrayList<PaaSToggleButton>();

    public GetStartedView() {
        super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));
        projectName.getElement().setId(PROJECT_NAME_ID);
        chooseNamePanel.setVisible(false);
        chooseTechnologyPanel.setVisible(false);
        choosePaaSPanel.setVisible(false);
        errorLabel.setVisible(false);
        setCloseOnEscape(true);
    }

    @Override
    public void showChooseNameStep() {
        chooseNamePanel.setVisible(true);
        chooseTechnologyPanel.setVisible(false);
        choosePaaSPanel.setVisible(false);

        // hide previous button and set to next button title "Get Started"
        prevButton.setVisible(false);
        nextButton.setText("Get Started");
    }

    @Override
    public void showChooseTechnologyStep() {
        chooseNamePanel.setVisible(false);
        chooseTechnologyPanel.setVisible(true);
        choosePaaSPanel.setVisible(false);

        prevButton.setVisible(true);
        nextButton.setText("Next");
    }

    @Override
    public void showChoosePaaSStep() {
        chooseNamePanel.setVisible(false);
        chooseTechnologyPanel.setVisible(false);
        choosePaaSPanel.setVisible(true);

        prevButton.setVisible(true);
        nextButton.setText("Finish");
    }

    @Override
    public HasClickHandlers getPrevButton() {
        return prevButton;
    }

    @Override
    public HasClickHandlers getNextButton() {
        return nextButton;
    }

    @Override
    public void setProjectTypes(List<ProjectType> projectTypes) {
        int columnCount = 9;
        int rowCount = (int)Math.ceil((double)projectTypes.size() / columnCount);

        projectTypesGrid.clear();
        projectTypesGrid.setSize("100%", "100%");
        projectTypesGrid.resize(rowCount, columnCount);
        projectTypesGrid.setVisible(true);
        projectTypeToggleButtonList.clear();

        Iterator typeIterator = projectTypes.iterator();

        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            for (int colNum = 0; colNum < columnCount; colNum++) {
                DockPanel dock = new DockPanel();
                dock.setSpacing(1);
                dock.setWidth("58px");
                dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);

                ProjectType projectType = (ProjectType)typeIterator.next();
                Image image = new Image(resolveProjectTypeImage(projectType));
                image.getElement().getStyle().setHeight(36, Style.Unit.PX);
                image.getElement().getStyle().setWidth(36, Style.Unit.PX);

                ProjectTypeToggleButton projectTypeToggleButton = new ProjectTypeToggleButton();
                projectTypeToggleButton.getUpFace().setImage(image);
                projectTypeToggleButton.setSize("36px", "36px");
                projectTypeToggleButton.getElement().getStyle().setPropertyPx("borderRadius", 5);
                projectTypeToggleButton.getElement().getStyle().setPropertyPx("outline", 0);
                projectTypeToggleButton.getElement().setId("CREATE-PROJECT-" + projectType);
                projectTypeToggleButton.setProjectType(projectType);
                projectTypeToggleButtonList.add(projectTypeToggleButton);

                HTML labelForToggleButton;
                switch (projectType) {
                    case JSP:
                        labelForToggleButton = getNewButtonLabel("Java Web Application (WAR)");
                        break;
                    case JAR:
                        labelForToggleButton = getNewButtonLabel("Java Library (JAR)");
                        break;
                    case SPRING:
                        labelForToggleButton = getNewButtonLabel("Java Spring");
                        break;
                    case RUBY_ON_RAILS:
                        labelForToggleButton = getNewButtonLabel("Ruby on Rails");
                        break;
                    case NODE_JS:
                        labelForToggleButton = getNewButtonLabel("Node.js");
                        break;
                    case MultiModule:
                        labelForToggleButton = getNewButtonLabel("Maven Multi-Module");
                        break;
                    default:
                        labelForToggleButton = getNewButtonLabel(projectType.value());
                }

                dock.add(projectTypeToggleButton, DockPanel.NORTH);
                dock.add(labelForToggleButton, DockPanel.SOUTH);

                projectTypesGrid.setWidget(rowNum, colNum, dock);
            }
        }
    }

    @Override
    public void setPaaSTypes(List<PaaS> paaSTypes) {
        int columnCount = 7;
        int rowCount = (int)Math.ceil((double)paaSTypes.size() / columnCount);

        paasGrid.clear();
        paasGrid.setSize("100%", "100%");
        paasGrid.resize(rowCount, columnCount);
        paasGrid.setVisible(true);
        paaSToggleButtonList.clear();

        int buttonNum = 0;
        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            for (int colNum = 0; colNum < columnCount; colNum++) {
                DockPanel dock = new DockPanel();
                dock.setSpacing(1);
                dock.setWidth("58px");
                dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);

                PaaS paaS = paaSTypes.get(buttonNum++);

                Image paasImg = new Image(resolvePaaSImage(paaS, false));
                paasImg.setSize("36px", "36px");

                Image paasImgDisabled = new Image(resolvePaaSImage(paaS, true));
                paasImgDisabled.setSize("36px", "36px");


                PaaSToggleButton paaSToggleButton = new PaaSToggleButton();
                paaSToggleButton.getUpFace().setImage(paasImg);
                paaSToggleButton.getUpDisabledFace().setImage(paasImgDisabled);
                paaSToggleButton.setSize("36px", "36px");
                paaSToggleButton.getElement().getStyle().setPropertyPx("borderRadius", 5);
                paaSToggleButton.getElement().getStyle().setPropertyPx("outline", 0);
                paaSToggleButton.getElement().setId("CREATE-PROJECT-" + paaS.getId());
                paaSToggleButton.setPaaS(paaS);
                paaSToggleButtonList.add(paaSToggleButton);

                dock.add(paaSToggleButton, DockPanel.NORTH);
                dock.add(getNewButtonLabel(paaS.getTitle()), DockPanel.SOUTH);

                paasGrid.setWidget(rowNum, colNum, dock);
            }
        }
    }

    @Override
    public HasClickHandlers getSkipButton() {
        return skipAnchor;
    }

    @Override
    public void setCurrentStepPagination(String step) {
        currentStepNumber.setText(step);
    }

    @Override
    public List<ProjectTypeToggleButton> getProjectTypeButtonsList() {
        return projectTypeToggleButtonList;
    }

    @Override
    public List<PaaSToggleButton> getPaaSToggleButtonsList() {
        return paaSToggleButtonList;
    }

    @Override
    public void setActiveProjectTypeButton(ProjectTypeToggleButton button) {
        for (ProjectTypeToggleButton toggleButton : projectTypeToggleButtonList) {
            if (toggleButton != button) {
                toggleButton.setValue(false);
            }
        }
    }

    @Override
    public void setActivePaaSButton(PaaSToggleButton button) {
        for (PaaSToggleButton toggleButton : paaSToggleButtonList) {
            if (toggleButton != button) {
                toggleButton.setValue(false);
            }
        }
    }

    @Override
    public HasValue<String> getProjectName() {
        return projectName;
    }

    @Override
    public void setNextButtonEnable(boolean isEnable) {
        nextButton.setEnabled(isEnable);
    }

    @Override
    public void setProjectNameFocus() {
        projectName.setFocus(true);
    }

    @Override
    public void setErrorVisible(boolean visible) {
        if (visible) {
            nextButton.setEnabled(false);
            errorLabel.setVisible(true);
        } else {
            nextButton.setEnabled(true);
            errorLabel.setVisible(false);
        }
    }

    // -------------------------------------------

    private HTML getNewButtonLabel(String label) {
        HTML titleLabel = new HTML();
        if (label != null) {
            titleLabel.setHTML(label);
        }
        titleLabel.setSize("auto", "40px");
        titleLabel.getElement().getStyle().setProperty("fontFamily", "Verdana, Bitstream Vera Sans, sans-serif");
        titleLabel.getElement().getStyle().setFontSize(10, Style.Unit.PX);
        titleLabel.getElement().getStyle().setColor("#545454");
        return titleLabel;
    }

    private ImageResource resolveProjectTypeImage(ProjectType projectType) {
        switch (projectType) {
            case JAR:
                return SamplesClientBundle.INSTANCE.jarTechnology();
            case JAVASCRIPT:
                return SamplesClientBundle.INSTANCE.jsTechnology();
            case JSP:
                return SamplesClientBundle.INSTANCE.jspTechnology();
            case MultiModule:
                return SamplesClientBundle.INSTANCE.multiModuleTechnology();
            case PHP:
                return SamplesClientBundle.INSTANCE.phpTechnology();
            case PYTHON:
                return SamplesClientBundle.INSTANCE.pythonTechnology();
            case RUBY_ON_RAILS:
                return SamplesClientBundle.INSTANCE.rorTechnology();
            case SPRING:
                return SamplesClientBundle.INSTANCE.springTechnology();
            case NODE_JS:
                return SamplesClientBundle.INSTANCE.nodejsTechnology();
            default:
                return null;
        }
    }

    private ImageResource resolvePaaSImage(PaaS paaS, boolean disable) {
        if (paaS.getId().equals("CloudBees")) {
            return !disable ? SamplesClientBundle.INSTANCE.cloudBeesPaaS() : SamplesClientBundle.INSTANCE.cloudBeesPaaSDisabled();
        } else if (paaS.getId().equals("CloudFoundry")) {
            return !disable ? SamplesClientBundle.INSTANCE.cloudfoundryPaaS() : SamplesClientBundle.INSTANCE.cloudfoundryPaaSDisabled();
        } else if (paaS.getId().equals("AppFog")) {
            return !disable ? SamplesClientBundle.INSTANCE.appfogPaaS() : SamplesClientBundle.INSTANCE.appfogPaaSDisabled();
        } else if (paaS.getId().equals("Heroku")) {
            return !disable ? SamplesClientBundle.INSTANCE.herokuPaaS() : SamplesClientBundle.INSTANCE.herokuPaaSDisabled();
        } else if (paaS.getId().equals("OpenShift")) {
            return !disable ? SamplesClientBundle.INSTANCE.openShiftPaaS() : SamplesClientBundle.INSTANCE.openShiftPaaSDisabled();
        } else if (paaS.getId().equals("GAE")) {
            return !disable ? SamplesClientBundle.INSTANCE.gaePaaS() : SamplesClientBundle.INSTANCE.gaePaaSDisabled();
        } else if (paaS.getId().equals("AWS")) {
            return !disable ? SamplesClientBundle.INSTANCE.beansTalkPaaS() : SamplesClientBundle.INSTANCE.beansTalkPaaSDisabled();
        } else if (paaS.getId().equals("Tier3WF")) {
            return !disable ? SamplesClientBundle.INSTANCE.tier3WebFabricPaaS() : SamplesClientBundle.INSTANCE.tier3WebFabricPaaSDisabled();
        } else {
            return !disable ? SamplesClientBundle.INSTANCE.nonePaaS() : SamplesClientBundle.INSTANCE.nonePaaS();
        }
    }
}

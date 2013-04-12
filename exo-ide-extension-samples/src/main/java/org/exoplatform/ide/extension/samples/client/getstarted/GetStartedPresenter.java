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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.client.framework.template.marshal.ProjectTemplateListUnmarshaller;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.*;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class GetStartedPresenter implements DeployResultHandler, GetStartedHandler, ViewClosedHandler, VfsChangedHandler {

    interface Display extends IsView {
        void showChooseNameStep();

        void showChooseTechnologyStep();

        void showChoosePaaSStep();

        HasClickHandlers getPrevButton();

        HasClickHandlers getNextButton();

        HasClickHandlers getSkipButton();

        void setProjectTypes(Set<ProjectType> projectTypes);

        void setPaaSTypes(List<PaaS> paaSTypes);

        void setCurrentStepPagination(String step);

        List<ProjectTypeToggleButton> getProjectTypeButtonsList();

        List<PaaSToggleButton> getPaaSToggleButtonsList();

        void setActiveProjectTypeButton(ProjectTypeToggleButton button);

        void setActivePaaSButton(PaaSToggleButton button);

        HasValue<String> getProjectName();

        void setNextButtonEnable(boolean isEnable);

        void setProjectNameFocus();

        void setErrorVisible(boolean visible);
    }

    /** representing display */
    private Display display;

    /** Representing current step */
    private WizardStep currentStep;

    private ProjectType currentProjectType;

    private ProjectTemplate currentProjectTemplate;

    /** current selected PaaS */
    private PaaS currentPaaS;

    /** available project templates */
    private List<ProjectTemplate> availableProjectTemplates = new ArrayList<ProjectTemplate>();

    /** available project types */
    private Set<ProjectType> availableProjectTypes = new TreeSet<ProjectType>();

    /** non selected PaaS */
    private final PaaS noneTarget = new NoneTarget();

    private VirtualFileSystemInfo vfsInfo;

    final Loader loader = new GWTLoader();

    public GetStartedPresenter() {
        IDE.addHandler(GetStartedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    //------------------------------------------------
    public void bindDisplay() {
        display.getNextButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switch (currentStep) {
                    case NAME:
                        showChooseTechnologyStep();
                        break;

                    case TECHNOLOGY:
                        showChoosePaaSStep();
                        break;

                    case PAAS:
                        createAndDeploy();
                        break;
                }
            }
        });

        display.getPrevButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switch (currentStep) {
                    case NAME:
                        break;

                    case TECHNOLOGY:
                        showChooseNameStep();
                        break;

                    case PAAS:
                        showChooseTechnologyStep();
                        break;
                }
            }
        });

        display.getSkipButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getProjectName().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (!event.getValue().matches("[a-zA-Z0-9]{1,25}")) {
                    display.setErrorVisible(true);
                } else {
                    display.setErrorVisible(false);
                }
            }
        });
    }

    @Override
    public void onDeployFinished(boolean success) {
        loader.hide();
        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());
        }
        IDE.fireEvent(new RefreshBrowserEvent());
    }

    @Override
    public void onProjectCreated(ProjectModel project) {
        loader.hide();
        IDE.fireEvent(new RefreshBrowserEvent());
        IDE.fireEvent(new OpenProjectEvent(project));

        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    @Override
    public void onGetStarted(GetStartedEvent event) {
        if (this.display == null) {
            Display disp = GWT.create(Display.class);
            IDE.getInstance().openView(disp.asView());
            this.display = disp;
            bindDisplay();
            currentStep = WizardStep.NAME;
            showChooseNameStep();
        }
    }

    private void showChooseNameStep() {
        display.showChooseNameStep();
        display.setCurrentStepPagination("1/3");

        currentStep = WizardStep.NAME;
        display.setNextButtonEnable(true);
        display.setProjectNameFocus();
    }

    private void showChooseTechnologyStep() {
        display.showChooseTechnologyStep();
        display.setCurrentStepPagination("2/3");

        currentStep = WizardStep.TECHNOLOGY;
        display.setNextButtonEnable(false);
        setProjectTypes();
    }

    private void showChoosePaaSStep() {
        display.showChoosePaaSStep();
        display.setCurrentStepPagination("3/3");

        currentStep = WizardStep.PAAS;

        List<PaaS> availablePaaSes = new ArrayList<PaaS>(IDE.getInstance().getPaaSes());
        availablePaaSes.add(noneTarget);

        display.setPaaSTypes(availablePaaSes);

        setPaaSButtonsHandlers();

        display.setNextButtonEnable(false);
    }

    private void createAndDeploy() {
        loader.setMessage("Loading...");
        loader.show();
        if (currentPaaS instanceof NoneTarget) {
            createProjectFromTemplate(currentProjectTemplate, this);
        } else {
            currentPaaS.getPaaSActions().deployFirstTime(display.getProjectName().getValue(), currentProjectTemplate, this);
        }
    }

    private void setProjectTypes() {
        try {
            TemplateService.getInstance().getProjectTemplateList(
                    new AsyncRequestCallback<List<ProjectTemplate>>(new ProjectTemplateListUnmarshaller(
                            new ArrayList<ProjectTemplate>())) {
                        @Override
                        protected void onSuccess(List<ProjectTemplate> result) {
                            for (ProjectTemplate template : result) {
                                availableProjectTypes.add(ProjectType.fromValue(template.getType()));

                                availableProjectTemplates.add(template);
                            }
                            display.setProjectTypes(availableProjectTypes);

                            setProjectTypesButtonsHandlers();
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            Dialogs.getInstance().showError("Something wrong.");
                        }
                    });
        } catch (RequestException e) {
            Dialogs.getInstance().showError("Something wrong while taking request.");
        }
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfsInfo = event.getVfsInfo();
    }

    //------------------------------------------------

    /** Need to set for project types buttons handlers that will allow to use one of programming technology */
    private void setProjectTypesButtonsHandlers() {
        for (final ProjectTypeToggleButton toggleButton : display.getProjectTypeButtonsList()) {
            toggleButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        display.setActiveProjectTypeButton(toggleButton);

                        currentProjectType = toggleButton.getProjectType();
                        display.setNextButtonEnable(true);
                    } else {
                        toggleButton.setDown(true);
                    }
                }
            });
        }
    }

    /** Need to set for paas buttons handlers that will allow to use one of paas */
    private void setPaaSButtonsHandlers() {
        for (final PaaSToggleButton toggleButton : display.getPaaSToggleButtonsList()) {
            if (!(toggleButton.getPaaS() instanceof NoneTarget) &&
                !toggleButton.getPaaS().getSupportedProjectTypes().contains(currentProjectType)) {
                toggleButton.setEnabled(false);
                continue;
            }
            toggleButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        display.setActivePaaSButton(toggleButton);
                        currentPaaS = toggleButton.getPaaS();

                        currentProjectTemplate = selectProjectTemplate(toggleButton.getPaaS());
                        display.setNextButtonEnable(true);
                    } else {
                        toggleButton.setDown(true);
                    }
                }
            });
        }
    }

    private void createProjectFromTemplate(ProjectTemplate projectTemplate, final DeployResultHandler deployResultHandler) {
        if (vfsInfo == null || vfsInfo.getRoot() == null) {
            Dialogs.getInstance().showError("Vfs error");  //TODO
            return;
        }

        try {
            String parentId = vfsInfo.getRoot().getId();

            String projectName = display.getProjectName().getValue();
            IDELoader.getInstance().setMessage("Creating project..."); //TODO
            IDELoader.getInstance().show();
            TemplateService.getInstance().createProjectFromTemplate(vfsInfo.getId(), parentId, projectName,
                                                                    projectTemplate.getName(),
                                                                    new AsyncRequestCallback<ProjectModel>(
                                                                            new ProjectUnmarshaller(new ProjectModel())) {
                                                                        @Override
                                                                        protected void onSuccess(final ProjectModel result) {
                                                                            IDELoader.getInstance().hide();
                                                                            IDE.getInstance().closeView(display.asView().getId());
                                                                            IDE.fireEvent(new ProjectCreatedEvent(result));
                                                                            deployResultHandler.onProjectCreated(result);
                                                                        }

                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            IDELoader.getInstance().hide();
                                                                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                        }
                                                                    });
        } catch (RequestException e) {
            IDELoader.getInstance().hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private ProjectTemplate selectProjectTemplate(PaaS paaS) {
        String startWithName;

        /**
         * OpenShift has specific procedure for creating project, for first we should create application on openshift after that we may
         *
         */
        if ("OpenShift".equals(paaS.getId()) || "GAE".equals(paaS.getId()) || "Heroku".equals(paaS.getId())) {
            startWithName = paaS.getId() + "_";
        } else {
            startWithName = "Simple_";
        }

        for (ProjectTemplate template : availableProjectTemplates) {
            if (template.getName().startsWith(startWithName) && template.getType().equals(currentProjectType.value())) {
                return template;
            }
        }

        return null;
    }

    //TODO fill image
    private class NoneTarget extends PaaS {
        public NoneTarget() {
            super("none", "None", new Image(SamplesClientBundle.INSTANCE.gitHub()),
                  new Image(SamplesClientBundle.INSTANCE.gitHubDisabled()), new ArrayList<ProjectType>());
        }
    }
}

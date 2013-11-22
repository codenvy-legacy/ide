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
package org.exoplatform.ide.extension.samples.client.getstarted;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.regexp.shared.RegExp;
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
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        void setProjectTypes(List<ProjectType> projectTypes);

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
    private Display                              display;

    /** Representing current step */
    private WizardStep                           currentStep;

    private ProjectType                          currentProjectType;

    private ProjectTemplate                      currentProjectTemplate;

    /** current selected PaaS */
    private PaaS                                 currentPaaS;

    /** Name of the property for using JRebel. */
    private static final String                  JREBEL                    = "jrebel";

    /** available project templates */
    private List<ProjectTemplate>                availableProjectTemplates = new ArrayList<ProjectTemplate>();

    /** available project types */
    private List<ProjectType>                    availableProjectTypes     = new ArrayList<ProjectType>();

    /** non selected PaaS */
    private final PaaS                           noneTarget                = new NoneTarget();

    /** Comparator for ordering project types. */
    private static final Comparator<ProjectType> PROJECT_TYPES_COMPARATOR  = new ProjectTypesComparator();

    private static final Comparator<PaaS>        PAAS_COMPARATOR           = new PaaSComparator();

    private VirtualFileSystemInfo                vfsInfo;

    final Loader                                 loader                    = new GWTLoader();

    /**
     * Creates the new instance of this {@link GetStartedPresenter}
     */
    public GetStartedPresenter() {
        IDE.addHandler(GetStartedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /**
     * Binds display.
     */
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
    }

    /**
     * @see org.exoplatform.ide.client.framework.paas.DeployResultHandler#onDeployFinished(boolean)
     */
    @Override
    public void onDeployFinished(boolean success) {
        loader.hide();
        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());
        }
        IDE.fireEvent(new RefreshBrowserEvent());
    }

    /**
     * @see org.exoplatform.ide.client.framework.paas.DeployResultHandler#onProjectCreated(org.exoplatform.ide.vfs.client.model.ProjectModel)
     */
    @Override
    public void onProjectCreated(ProjectModel project) {
        loader.hide();
        IDE.fireEvent(new RefreshBrowserEvent());
        IDE.fireEvent(new OpenProjectEvent(project));

        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    /**
     * @see org.exoplatform.ide.extension.samples.client.getstarted.GetStartedHandler#onGetStarted(org.exoplatform.ide.extension.samples.client.getstarted.GetStartedEvent)
     */
    @Override
    public void onGetStarted(GetStartedEvent event) {
        if (this.display == null) {
            if (isRoUser()) {
                return;
            } else {
                Display disp = GWT.create(Display.class);
                IDE.getInstance().openView(disp.asView());
                this.display = disp;
                bindDisplay();
                currentStep = WizardStep.NAME;
                showChooseNameStep();
            }
        }
    }

    /**
     * Determines write permissions on current workspace.
     * 
     * @return <b>true</b> if user can write, <b>false</b> otherwise
     */
    public boolean isRoUser() {
        return !IDE.user.getRoles().contains("developer") && !IDE.user.getRoles().contains("admin");
    }

    /**
     * Displays first step.
     */
    private void showChooseNameStep() {
        display.showChooseNameStep();
        display.setCurrentStepPagination("1/3");

        currentStep = WizardStep.NAME;
        display.setNextButtonEnable(true);
        display.setProjectNameFocus();
    }

    /**
     * Displays next step.
     */
    private void showChooseTechnologyStep() {
        if (!isNameValid()) {
            if (display.getProjectName().getValue().startsWith("_")) {
                Dialogs.getInstance()
                       .showInfo(SamplesExtension.LOCALIZATION_CONSTANT.noIncorrectProjectNameTitle(),
                                 SamplesExtension.LOCALIZATION_CONSTANT.projectNameStartWith_Message());
            } else {
                Dialogs.getInstance()
                       .showInfo(SamplesExtension.LOCALIZATION_CONSTANT.noIncorrectProjectNameTitle(),
                                 SamplesExtension.LOCALIZATION_CONSTANT.noIncorrectProjectNameMessage());
            }
            return;
        }
        display.showChooseTechnologyStep();
        display.setCurrentStepPagination("2/3");

        currentStep = WizardStep.TECHNOLOGY;
        display.setNextButtonEnable(false);
        
        loadTemplates();
    }

    /**
     * Displays step to select PAAS.
     */
    private void showChoosePaaSStep() {
        display.showChoosePaaSStep();
        display.setCurrentStepPagination("3/3");

        currentStep = WizardStep.PAAS;

        List<PaaS> availablePaaSes = new ArrayList<PaaS>(IDE.getInstance().getPaaSes());
        Collections.sort(availablePaaSes, PAAS_COMPARATOR);
        availablePaaSes.add(noneTarget);

        display.setPaaSTypes(availablePaaSes);

        setPaaSButtonsHandlers();

        display.setNextButtonEnable(false);
    }

    /**
     * Validates the project name.
     * 
     * @return <b>true</b> if project name is correct, <b>false</b> otherwise
     */
    private boolean isNameValid() {
        RegExp regExp = RegExp.compile("(^[-.a-zA-Z0-9])([-._a-zA-Z0-9])*$");
        return regExp.test(display.getProjectName().getValue());
    }

    /**
     * Creates and deploys the project.
     */
    private void createAndDeploy() {
        if (currentPaaS instanceof NoneTarget) {
            createProjectFromTemplate(currentProjectTemplate, this);
        } else {
            currentPaaS.getPaaSActions().deployFirstTime(display.getProjectName().getValue(), currentProjectTemplate, this);
        }
    }

    /**
     * Loads list of templates from the server.
     */
    private void loadTemplates() {
        try {
            TemplateService.getInstance().getProjectTemplateList(
                new AsyncRequestCallback<List<ProjectTemplate>>(new ProjectTemplateListUnmarshaller(new ArrayList<ProjectTemplate>())) {
                   @Override
                   protected void onSuccess(List<ProjectTemplate> result) {
                       availableProjectTemplates = result;
                       showTemplates();
                   }
                   @Override
                   protected void onFailure(Throwable exception) {
                       Dialogs.getInstance().showError("Project list can not be loaded.");
                   }
               });
        } catch (RequestException e) {
            Dialogs.getInstance().showError("Project list can not be loaded.");
        }
    }
    
    /**
     * Shows templates.
     */
    private void showTemplates() {
        availableProjectTypes = getProjectTypesFromTemplates(availableProjectTemplates);
        Collections.sort(availableProjectTypes, PROJECT_TYPES_COMPARATOR);
        display.setProjectTypes(availableProjectTypes);
        setProjectTypesButtonsHandlers();        
    }

    /**
     * Prepares project type list to be displayed.
     * 
     * @param projectTemplates available project templates
     * @return {@link List}
     */
    private List<ProjectType> getProjectTypesFromTemplates(List<ProjectTemplate> projectTemplates) {
        List<ProjectType> projectTypes = new ArrayList<ProjectType>();
        for (ProjectTemplate projectTemplate : projectTemplates) {
            ProjectType projectType = ProjectType.fromValue(projectTemplate.getType());
            if (!projectTypes.contains(projectType)) {
                projectTypes.add(projectType);
            }
        }
        return projectTypes;
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfsInfo = event.getVfsInfo();
    }

    /**
     * Adds handlers to project types buttons.
     */
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

    /**
     * Adds handlers to PAAS buttons.
     */
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

    /**
     * Creates project from template.
     * 
     * @param projectTemplate project template
     * @param deployResultHandler handler to perform operations after creation complete.
     */
    private void createProjectFromTemplate(ProjectTemplate projectTemplate, final DeployResultHandler deployResultHandler) {
        if (vfsInfo == null || vfsInfo.getRoot() == null) {
            Dialogs.getInstance().showError("Vfs error"); // TODO
            return;
        }

        try {
            String parentId = vfsInfo.getRoot().getId();

            String projectName = display.getProjectName().getValue();
            IDELoader.getInstance().setMessage("Creating project..."); // TODO
            IDELoader.getInstance().show();
            TemplateService.getInstance()
                           .createProjectFromTemplate(vfsInfo.getId(), parentId, projectName,
                                                      projectTemplate.getName(),
                                                      new AsyncRequestCallback<ProjectModel>(
                                                                                             new ProjectUnmarshaller(new ProjectModel())) {
                                                          @Override
                                                          protected void onSuccess(final ProjectModel result) {
                                                              if ((currentProjectType == ProjectType.JSP ||
                                                              currentProjectType == ProjectType.SPRING)) {
//                                                                  writeUseJRebelProperty(result);
                                                              }
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

    /**
     * Selects active project template after mouse clicking. 
     * 
     * @param paaS PAAS
     * @return selected project template
     */
    private ProjectTemplate selectProjectTemplate(PaaS paaS) {
        String startWithName;

        /**
         * OpenShift has specific procedure for creating project, for first we should create application on openshift after that we may
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

    /**
     * Dummy implementation of PASS. It allows the user to create the project without selecting deployment. 
     */
    private class NoneTarget extends PaaS {
        public NoneTarget() {
            super("none", "None", new Image(SamplesClientBundle.INSTANCE.gitHub()),
                  new Image(SamplesClientBundle.INSTANCE.gitHubDisabled()), new ArrayList<ProjectType>());
        }
    }
    
}

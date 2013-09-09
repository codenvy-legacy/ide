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
package com.codenvy.ide.extension.android.client.deploy;

import com.codenvy.ide.extension.android.client.event.RunApplicationEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.paas.InitializeDeployViewHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class DeployApplicationPresenter implements HasPaaSActions, VfsChangedHandler, ProjectOpenedHandler {

    public interface Display {
        Composite getView();
    }

    private Display display;

    private VirtualFileSystemInfo vfs;

    private DeployResultHandler deployResultHandler;

    private String projectName;

    private final DeployApplicationPresenter instance;

    public DeployApplicationPresenter() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        instance = this;
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    @Override
    public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        createProject(projectName, projectTemplate);
    }

    @Override
    public void deploy(ProjectModel project, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        IDE.addHandler(ProjectOpenedEvent.TYPE, instance);
        this.deployResultHandler.onProjectCreated(project);
    }

    @Override
    public Composite getDeployView(String projectName, ProjectType projectType, InitializeDeployViewHandler initializeDeployViewHandler) {
        if (display == null) {
            display = GWT.create(Display.class);
        }
        this.projectName = projectName;
        return display.getView();
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void deployFirstTime(String projectName, ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        if (display == null) {
            display = GWT.create(Display.class);
        }
        createProject(projectName, projectTemplate);
    }

    private void createProject(String name, ProjectTemplate projectTemplate) {
        final Loader loader = new GWTLoader();
        loader.setMessage("Creating project...");
        loader.show();
        try {
            TemplateService.getInstance().createProjectFromTemplate(
                    vfs.getId(),
                    vfs.getRoot().getId(),
                    name,
                    projectTemplate.getName(),
                    new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel())) {

                        @Override
                        protected void onSuccess(ProjectModel result) {
                            loader.hide();
                            IDE.addHandler(ProjectOpenedEvent.TYPE, instance);
                            deployResultHandler.onProjectCreated(result);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            loader.hide();
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            loader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                IDE.fireEvent(new RunApplicationEvent());
                IDE.removeHandler(ProjectOpenedEvent.TYPE, instance);
            }
        });
    }
}

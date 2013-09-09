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
package org.exoplatform.ide.extension.cloudbees.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.cloudbees.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.cloudbees.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoEvent;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.update.UpdateApplicationEvent;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for managing project, deployed on CloudBeess.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 5, 2011 9:42:32 AM anya $
 */
public class CloudBeesProjectPresenter extends GitPresenter implements ManageCloudBeesProjectHandler,
            ViewClosedHandler, ApplicationDeletedHandler {
    
    interface Display extends IsView {
        HasClickHandlers getCloseButton();

        HasClickHandlers getUpdateButton();

        HasClickHandlers getDeleteButton();

        HasValue<String> getApplicationName();

        void setApplicationURL(String URL);

        HasValue<String> getApplicationStatus();

        HasValue<String> getApplicationInstances();

        HasClickHandlers getInfoButton();
    }

    /** Presenter's display. */
    private Display display;

    public CloudBeesProjectPresenter() {
        IDE.getInstance().addControl(new CloudBeesControl());
        IDE.addHandler(ManageCloudBeesProjectEvent.TYPE, this);
        IDE.addHandler(ApplicationDeletedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind presenter with display. */
    public void bindDisplay() {
        display.getDeleteButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new DeleteApplicationEvent());
            }
        });

        display.getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UpdateApplicationEvent());
            }
        });

        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getInfoButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new ApplicationInfoEvent());
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.ManageCloudBeesProjectHandler#onManageCloudBeesProject(org
     * .exoplatform.ide.extension.cloudbees.client.project.ManageCloudBeesProjectEvent) */
    @Override
    public void onManageCloudBeesProject(ManageCloudBeesProjectEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }
        getApplicationInfo(getSelectedProject());
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /**
     * Get application's properties.
     *
     * @param project
     *         project deployed to CloudBees
     */
    private void getApplicationInfo(final ProjectModel project) {
        try {
            AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();
            CloudBeesClientService.getInstance().getApplicationInfo(
                    null,
                    vfs.getId(),
                    project.getId(),
                    new CloudBeesAsyncRequestCallback<ApplicationInfo>(
                            new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                   new LoggedInHandler() {
                                       @Override
                                       public void onLoggedIn() {
                                           getApplicationInfo(project);
                                       }
                                   }, null) {
                        @Override
                        protected void onSuccess(ApplicationInfo appInfo) {
                            showAppInfo(appInfo);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Show application's properties.
     *
     * @param map
     */
    private void showAppInfo(ApplicationInfo appInfo) {
        display.getApplicationName().setValue(appInfo.getTitle());
        display.getApplicationStatus().setValue(appInfo.getStatus());
        display.getApplicationInstances().setValue(appInfo.getClusterSize());
        display.setApplicationURL(appInfo.getUrl());
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide
     * .extension.cloudbees.client.delete.ApplicationDeletedEvent) */
    @Override
    public void onApplicationDeleted(ApplicationDeletedEvent event) {
        ProjectModel project = getSelectedProject();
        if (event.getApplicationId() != null && project != null
            && event.getApplicationId().equals((String)project.getPropertyValue("cloudbees-application"))) {
            if (display != null) {
                IDE.getInstance().closeView(display.asView().getId());
            }
            IDE.fireEvent(new RefreshBrowserEvent(project));
        }
    }
    
}

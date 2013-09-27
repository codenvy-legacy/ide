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
package org.exoplatform.ide.extension.openshift.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.openshift.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.openshift.client.info.ShowApplicationInfoEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.client.preview.PreviewApplicationEvent;
import org.exoplatform.ide.extension.openshift.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.openshift.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.openshift.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for managing project, deployed on OpenShift.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 8, 2011 9:39:29 AM anya $
 */
public class OpenShiftProjectPresenter extends GitPresenter implements ManageOpenShiftProjectHandler, 
        ViewClosedHandler, LoggedInHandler, ApplicationDeletedHandler, ApplicationInfoChangedHandler {

    interface Display extends IsView {

        HasClickHandlers getCloseButton();

        HasClickHandlers getPreviewButton();

        HasClickHandlers getDeleteButton();

        HasClickHandlers getInfoButton();

        HasClickHandlers getStartButton();

        HasClickHandlers getStopButton();

        HasClickHandlers getRestartButton();

        HasValue<String> getApplicationName();

        void setControlsActivity(boolean active);

        void setApplicationURL(String URL);

        HasValue<String> getApplicationType();
    }

    private Display display;

    public OpenShiftProjectPresenter() {
        IDE.getInstance().addControl(new OpenShiftControl());

        IDE.addHandler(ManageOpenShiftProjectEvent.TYPE, this);
        IDE.addHandler(ApplicationDeletedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ApplicationInfoChangedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getDeleteButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new DeleteApplicationEvent());
            }
        });

        display.getPreviewButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new PreviewApplicationEvent());
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
                IDE.eventBus().fireEvent(new ShowApplicationInfoEvent());
            }
        });

        display.getStartButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new StartApplicationEvent(display.getApplicationName().getValue()));
            }
        });

        display.getStopButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new StopApplicationEvent(display.getApplicationName().getValue()));
            }
        });

        display.getRestartButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new RestartApplicationEvent(display.getApplicationName().getValue()));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.openshift.client.project.ManageOpenShiftProjectHandler#onManageOpenShiftProject(org.exoplatform
     * .ide.extension.openshift.client.project.ManageOpenShiftProjectEvent) */
    @Override
    public void onManageOpenShiftProject(ManageOpenShiftProjectEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }
        getApplicationInfo();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Get application's information. */
    public void getApplicationInfo() {
        try {
            final ProjectModel project = getSelectedProject();

            AutoBean<AppInfo> appInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.appInfo();
            AutoBeanUnmarshaller<AppInfo> unmarshaller = new AutoBeanUnmarshaller<AppInfo>(appInfo);
            OpenShiftClientService.getInstance().getApplicationInfo(null, vfs.getId(), project.getId(),
                                                                    new AsyncRequestCallback<AppInfo>(unmarshaller) {

                                                                        @Override
                                                                        protected void onSuccess(AppInfo result) {
                                                                            display.getApplicationName().setValue(result.getName());
                                                                            display.setApplicationURL(result.getPublicUrl());
                                                                            display.getApplicationType().setValue(result.getType());
                                                                            setControlsButtonState(project.getName());
                                                                        }

                                                                        /**
                                                                         * @see org.exoplatform.gwtframework.commons.rest
                                                                         * .AsyncRequestCallback#onFailure(java.lang.Throwable)
                                                                         */
                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            if (exception instanceof ServerException) {
                                                                                ServerException serverException =
                                                                                        (ServerException)exception;
                                                                                if (HTTPStatus.OK == serverException.getHTTPStatus()
                                                                                    && "Authentication-required".equals(serverException
                                                                                                                           .getHeader(












                                                                                                                                        HTTPHeader.JAXRS_BODY_PROVIDED))) {
                                                                                    addLoggedInHandler();
                                                                                    IDE.fireEvent(new LoginEvent());
                                                                                    return;
                                                                                }
                                                                            }
                                                                            IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception,
                                                                                                                            OpenShiftExtension
                                                                                                                                    .LOCALIZATION_CONSTANT
                                                                                                                                    .getApplicationInfoFail()));
                                                                        }

                                                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                 .getApplicationInfoFail()));
        }
    }

    /** Register {@link LoggedInHandler} handler. */
    protected void addLoggedInHandler() {
        IDE.addHandler(LoggedInEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift
     * .client.login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            getApplicationInfo();
        }
    }

    /** @see org.exoplatform.ide.extension.openshift.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide
     * .extension.openshift.client.delete.ApplicationDeletedEvent) */
    @Override
    public void onApplicationDeleted(ApplicationDeletedEvent event) {
        //      if (display != null && vfs.getId().equals(event.getVfsId()) && openedProject != null
        //         && openedProject.getId().equals(event.getProjectId()))
        //      {
        //         IDE.getInstance().closeView(display.asView().getId());
        //      }

        ProjectModel project = getSelectedProject();
        if (display != null && vfs.getId().equals(event.getVfsId()) && project != null
            && project.getId().equals(event.getProjectId())) {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    @Override
    public void onApplicationInfoChanged(ApplicationInfoChangedEvent event) {
        setControlsButtonState(event.getAppName());
    }

    private void setControlsButtonState(String appName) {
        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());
            OpenShiftClientService.getInstance().getApplicationHealth(appName, new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                @Override
                protected void onSuccess(StringBuilder result) {
                    if (result.toString().equals("STARTED")) {
                        display.setControlsActivity(true);
                    } else {
                        display.setControlsActivity(false);
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}

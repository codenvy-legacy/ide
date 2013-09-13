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
package org.exoplatform.ide.extension.appfog.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.appfog.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.appfog.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.info.ApplicationInfoEvent;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.extension.appfog.client.services.ManageServicesEvent;
import org.exoplatform.ide.extension.appfog.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.update.UpdateApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.update.UpdateInstancesEvent;
import org.exoplatform.ide.extension.appfog.client.update.UpdateMemoryEvent;
import org.exoplatform.ide.extension.appfog.client.url.UnmapUrlEvent;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for managing project, deployed on Appfog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogProjectPresenter extends GitPresenter implements
        ManageAppfogProjectHandler, ViewClosedHandler, ApplicationDeletedHandler, ApplicationInfoChangedHandler
{
    interface Display extends IsView {
        HasClickHandlers getCloseButton();

        HasClickHandlers getUpdateButton();

        HasClickHandlers getLogsButton();

        HasClickHandlers getServicesButton();

        HasClickHandlers getDeleteButton();

        HasClickHandlers getInfoButton();

        HasValue<String> getApplicationName();

        void setApplicationURL(String url);

        HasValue<String> getApplicationModel();

        HasValue<String> getApplicationInfra();

        HasValue<String> getApplicationStack();

        HasValue<String> getApplicationInstances();

        HasValue<String> getApplicationMemory();

        HasValue<String> getApplicationStatus();

        HasClickHandlers getStartButton();

        HasClickHandlers getStopButton();

        HasClickHandlers getRestartButton();

        HasClickHandlers getEditMemoryButton();

        HasClickHandlers getEditURLButton();

        HasClickHandlers getEditInstancesButton();

        HasClickHandlers getShowUrisAnchor();

        void setStartButtonEnabled(boolean enabled);

        void setStopButtonEnabled(boolean enabled);

        void setRestartButtonEnabled(boolean enabled);

        void setUrisPopupVisible(boolean visible);
    }

    /** Presenter's display. */
    private Display display;

    private AppfogApplication application;

    private List<String> appUris;

    public AppfogProjectPresenter() {
        IDE.getInstance().addControl(new AppfogControl());

        IDE.addHandler(ManageAppfogProjectEvent.TYPE, this);
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

        display.getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UpdateApplicationEvent());
            }
        });

        display.getLogsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getLogs();
            }
        });

        display.getServicesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new ManageServicesEvent(application));
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

        display.getStartButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new StartApplicationEvent());
            }
        });

        display.getStopButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new StopApplicationEvent());
            }
        });

        display.getRestartButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new RestartApplicationEvent());
            }
        });

        display.getEditInstancesButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UpdateInstancesEvent());
            }
        });

        display.getEditMemoryButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UpdateMemoryEvent());
            }
        });

        display.getEditURLButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UnmapUrlEvent());
            }
        });

        display.getShowUrisAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final PopupPanel simplePopup = new PopupPanel();
                simplePopup.ensureDebugId("cfUrisPopup");
                simplePopup.setWidth("auto");
                simplePopup.getElement().getStyle().setPadding(5.0, Style.Unit.PX);

                StringBuilder uris = new StringBuilder();

                //need to fill uris list from second uri, cause first uri is filled in project info window
                for (int i = 1; i < appUris.size(); i++) {
                    uris.append("<div><a href=\"http://");
                    uris.append(appUris.get(i));
                    uris.append("\" target=\"_blank\">http://");
                    uris.append(appUris.get(i));
                    uris.append("</a></div>\n");
                }

                simplePopup.setWidget(new HTML("<div>" + uris.toString() + "</div>"));

                int left = event.getClientX() + 10;
                int top = event.getClientY() + 10;
                simplePopup.setPopupPosition(left, top);
                simplePopup.show();
                simplePopup.setAutoHideEnabled(true);
            }
        });

    }

    protected void getLogs() {
        try {
            ProjectModel project = getSelectedProject();

            AppfogClientService.getInstance().getLogs(vfs.getId(), project.getId(),
                                                      new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {

                                                          @Override
                                                          protected void onSuccess(StringBuilder result) {
                                                              IDE.fireEvent(
                                                                      new OutputEvent("<pre>" + result.toString() + "</pre>", Type.OUTPUT));
                                                          }

                                                          @Override
                                                          protected void onFailure(Throwable exception) {
                                                              IDE.fireEvent(new ExceptionThrownEvent(exception.getMessage()));
                                                          }
                                                      });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
            e.printStackTrace();
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.project.ManageCloudFoundryProjectHandler#onManageCloudFoundryProject(org
     * .exoplatform.ide.extension.cloudfoundry.client.project.ManageCloudFoundryProjectEvent) */
    @Override
    public void onManageAppfogProject(ManageAppfogProjectEvent event) {
        getApplicationInfo(getSelectedProject());
    }

    /**
     * Get application properties.
     *
     * @param project
     */
    protected void getApplicationInfo(final ProjectModel project) {
        try {
            AutoBean<AppfogApplication> appfogApplication =
                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            AppfogClientService.getInstance().getApplicationInfo(vfs.getId(), project.getId(), null, null,
                                                                 new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
                                                                                                                   new LoggedInHandler() {
                                                                                                                       @Override
                                                                                                                       public void
                                                                                                                       onLoggedIn() {


























                                                                                                                           getApplicationInfo(
                                                                                                                                   project);
                                                                                                                       }
                                                                                                                   }, null) {
                                                                     @Override
                                                                     protected void onSuccess(AppfogApplication result) {
                                                                         if (display == null) {
                                                                             display = GWT.create(Display.class);
                                                                             bindDisplay();
                                                                             IDE.getInstance().openView(display.asView());
                                                                         }
                                                                         application = result;
                                                                         displayApplicationProperties(result);
                                                                     }
                                                                 });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedEvent) */
    @Override
    public void onApplicationDeleted(ApplicationDeletedEvent event) {
        if (event.getApplicationName() != null && getSelectedProject() != null
            && event.getApplicationName().equals((String)getSelectedProject().getPropertyValue("appfog-application"))) {
            if (display != null) {
                IDE.getInstance().closeView(display.asView().getId());
            }
            IDE.fireEvent(new RefreshBrowserEvent(getSelectedProject()));
        }
    }

    protected void displayApplicationProperties(AppfogApplication application) {
        display.getApplicationName().setValue(application.getName());
        display.getApplicationInstances().setValue(String.valueOf(application.getInstances()));
        display.getApplicationMemory().setValue(String.valueOf(application.getResources().getMemory()) + "MB");
        display.getApplicationModel().setValue(String.valueOf(application.getStaging().getModel()));
        display.getApplicationStack().setValue(String.valueOf(application.getStaging().getStack()));
        display.getApplicationStatus().setValue(String.valueOf(application.getState()));
        display.getApplicationInfra().setValue(String.valueOf(application.getInfra().getProvider()));

        if (application.getUris() != null && application.getUris().size() > 0) {
            appUris = application.getUris();
            display.setApplicationURL(appUris.get(0));

            if (appUris.size() == 1) {
                display.setUrisPopupVisible(false);
            } else {
                display.setUrisPopupVisible(true);
            }
        } else {
            //Set empty field if we specialy unmap all urls and closed url controller window, if whe don't do this, in
            //info window will be appear old url, that is not good
            display.setApplicationURL(null);
            display.setUrisPopupVisible(false);
        }
        boolean isStarted = ("STARTED".equals(application.getState()));
        display.setStartButtonEnabled(!isStarted);
        display.setStopButtonEnabled(isStarted);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedHandler#onApplicationInfoChanged(org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent) */
    @Override
    public void onApplicationInfoChanged(ApplicationInfoChangedEvent event) {
//      if (display != null && event.getProjectId() != null && vfs.getId().equals(event.getVfsId())
//         && openedProject != null && openedProject.getId().equals(event.getProjectId()))
//      {
//         getApplicationInfo(openedProject);
//      }
        if (display != null && event.getProjectId() != null && vfs.getId().equals(event.getVfsId())
            && getSelectedProject() != null && getSelectedProject().getId().equals(event.getProjectId())) {
            getApplicationInfo(getSelectedProject());
        }
    }

}

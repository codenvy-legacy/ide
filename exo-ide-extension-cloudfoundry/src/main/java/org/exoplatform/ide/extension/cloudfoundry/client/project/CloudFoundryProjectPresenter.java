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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

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
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateInstancesEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateMemoryEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlEvent;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for managing project, deployed on CloudFoundry.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 2, 2011 5:54:50 PM anya $
 */
public class CloudFoundryProjectPresenter extends GitPresenter implements
                                                              ManageCloudFoundryProjectHandler, ViewClosedHandler,
                                                              ApplicationDeletedHandler, ApplicationInfoChangedHandler
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
    private Display                 display;

    private CloudFoundryApplication application;

    private List<String>            appUris;

    private PAAS_PROVIDER           paasProvider = null;

    public CloudFoundryProjectPresenter() {
        IDE.addHandler(ManageCloudFoundryProjectEvent.TYPE, this);
        IDE.addHandler(ApplicationDeletedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ApplicationInfoChangedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getDeleteButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new DeleteApplicationEvent(paasProvider));
            }
        });

        display.getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UpdateApplicationEvent(paasProvider));
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
                IDE.fireEvent(new ManageServicesEvent(application, paasProvider));
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
                IDE.eventBus().fireEvent(new ApplicationInfoEvent(paasProvider));
            }
        });

        display.getStartButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new StartApplicationEvent(paasProvider));
            }
        });

        display.getStopButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new StopApplicationEvent(paasProvider));
            }
        });

        display.getRestartButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new RestartApplicationEvent(paasProvider));
            }
        });

        display.getEditInstancesButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UpdateInstancesEvent(paasProvider));
            }
        });

        display.getEditMemoryButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UpdateMemoryEvent(paasProvider));
            }
        });

        display.getEditURLButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.eventBus().fireEvent(new UnmapUrlEvent(paasProvider));
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

                // need to fill uris list from second uri, cause first uri is filled in project info window
                for (int i = 1; i < appUris.size(); i++)
                {
                    uris.append("<div><a href=\"");
                    uris.append("http://" + appUris.get(i));
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
        ProjectModel project = getSelectedProject();
        try {
            CloudFoundryClientService.getInstance()
                                     .getLogs(vfs.getId(), project.getId(), new AsyncRequestCallback<StringBuilder>(
                                                                                      new StringUnmarshaller(new StringBuilder())) {

                                                  @Override
                                                  protected void onSuccess(StringBuilder result) {
                                                      IDE.fireEvent(new OutputEvent("<pre>" + result.toString() + "</pre>",
                                                                                    Type.OUTPUT));
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

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /**
     * @see org.exoplatform.ide.extension.cloudfoundry.client.project.ManageCloudFoundryProjectHandler#onManageCloudFoundryProject(org
     *      .exoplatform.ide.extension.cloudfoundry.client.project.ManageCloudFoundryProjectEvent)
     */
    @Override
    public void onManageCloudFoundryProject(ManageCloudFoundryProjectEvent event) {
        paasProvider = event.getPaasProvider();
        getApplicationInfo(getSelectedProject());
    }

    /**
     * Get application properties.
     * 
     * @param project
     */
    protected void getApplicationInfo(final ProjectModel project) {
        try {
            AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                                                                        CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

            AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                                                                         new AutoBeanUnmarshaller<CloudFoundryApplication>(
                                                                                                                           cloudFoundryApplication);

            CloudFoundryClientService.getInstance()
                                     .getApplicationInfo(vfs.getId(),
                                                         project.getId(),
                                                         null,
                                                         null,
                                                         new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                                                       unmarshaller,
                                                                                                                       new LoggedInHandler() {
                                                                                                                           @Override
                                                                                                                           public void onLoggedIn(String server) {
                                                                                                                               getApplicationInfo(project);
                                                                                                                           }
                                                                                                                       }, null,
                                                                                                                       paasProvider) {
                                                             @Override
                                                             protected void onSuccess(CloudFoundryApplication result) {
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

    /**
     * @see org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide
     *      .extension.cloudfoundry.client.delete.ApplicationDeletedEvent)
     */
    @Override
    public void onApplicationDeleted(ApplicationDeletedEvent event) {
        ProjectModel project = getSelectedProject();
        final String applicationName = event.getApplicationName();
        final String cloudFoundryAppName = (String)project.getPropertyValue("cloudfoundry-application");
        final String webFabricAppName = (String)project.getPropertyValue("tier3webfabric-application");
        if (applicationName != null && project != null
            && (applicationName.equals(cloudFoundryAppName) || applicationName.equals(webFabricAppName))) {
            if (display != null) {
                IDE.getInstance().closeView(display.asView().getId());
            }
            IDE.fireEvent(new RefreshBrowserEvent(project));
        }
    }

    protected void displayApplicationProperties(CloudFoundryApplication application) {
        display.getApplicationName().setValue(application.getName());
        display.getApplicationInstances().setValue(String.valueOf(application.getInstances()));
        display.getApplicationMemory().setValue(String.valueOf(application.getResources().getMemory()) + "MB");
        display.getApplicationModel().setValue(String.valueOf(application.getStaging().getModel()));
        display.getApplicationStack().setValue(String.valueOf(application.getStaging().getStack()));
        display.getApplicationStatus().setValue(String.valueOf(application.getState()));

        if (application.getUris() != null && application.getUris().size() > 0) {
            appUris = application.getUris();
            display.setApplicationURL(appUris.get(0));

            if (appUris.size() == 1) {
                display.setUrisPopupVisible(false);
            } else {
                display.setUrisPopupVisible(true);
            }
        } else {
            // Set empty field if we specialy unmap all urls and closed url controller window, if whe don't do this, in
            // info window will be appear old url, that is not good
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
        ProjectModel project = getSelectedProject();
        if (display != null && event.getProjectId() != null && vfs.getId().equals(event.getVfsId())
            && project != null && project.getId().equals(event.getProjectId())) {
            getApplicationInfo(project);
        }
    }

}

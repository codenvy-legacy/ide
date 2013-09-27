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
package org.exoplatform.ide.extension.cloudbees.client.initialize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesRESTfulRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.marshaller.DomainsUnmarshaller;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationPresenter.java Jun 23, 2011 12:49:09 PM vereshchaka $
 */
public class InitializeApplicationPresenter extends GitPresenter implements ViewClosedHandler,
                                                                            InitializeApplicationHandler, ApplicationBuiltHandler {

    interface Display extends IsView {
        HasClickHandlers getCreateButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getDomainField();

        HasValue<String> getNameField();

        HasValue<String> getApplicationIdField();

        void enableCreateButton(boolean enable);

        void focusInApplicationNameField();

        void setDomainValues(String[] domains);
    }

    private String[] domains;

    private Display display;

    private String warUrl;

    private String applicationId;

    public InitializeApplicationPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(InitializeApplicationEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getCreateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                buildApplication();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                closeView();
            }
        });

        display.getDomainField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                setApplicationId();
            }
        });

        display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue() == null || event.getValue().isEmpty()) {
                    display.enableCreateButton(false);
                } else {
                    display.enableCreateButton(true);
                }
                setApplicationId();
            }
        });

        display.setDomainValues(domains);
        display.enableCreateButton(false);
        setApplicationId();
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.initialize.InitializeApplicationHandler#onInitializeApplication(org
     * .exoplatform.ide.extension.cloudbees.client.initialize.InitializeApplicationEvent) */
    @Override
    public void onInitializeApplication(InitializeApplicationEvent event) {
        if (makeSelectionCheck()) {
            getDomains();
        }
    }

    private void getDomains() {
        try {
            CloudBeesClientService.getInstance().getDomains(
                    new CloudBeesAsyncRequestCallback<List<String>>(new DomainsUnmarshaller(new ArrayList<String>()),
                                                                    new LoggedInHandler() {
                                                                        @Override
                                                                        public void onLoggedIn() {
                                                                            getDomains();
                                                                        }
                                                                    }, null) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            showView(result);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void buildApplication() {
        applicationId = display.getApplicationIdField().getValue();

        IDE.addHandler(ApplicationBuiltEvent.TYPE, this);
//      IDE.fireEvent(new BuildApplicationEvent(((ItemContext)selectedItems.get(0)).getProject()));
        IDE.fireEvent(new BuildApplicationEvent(getSelectedProject()));
        closeView();
    }

    private LoggedInHandler deployWarLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            doDeployApplication();
        }
    };

    /**
     * Deploy application to Cloud Bees by sending request over WebSocket or HTTP.
     *
     * @param project
     *         {@link ProjectModel} to deploy
     */
    private void doDeployApplication() {
//      final ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        final ProjectModel project = getSelectedProject();
        AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();

        try {
            CloudBeesClientService.getInstance().initializeApplicationWS(
                    applicationId,
                    vfs.getId(),
                    project.getId(),
                    warUrl,
                    null,
                    new CloudBeesRESTfulRequestCallback<ApplicationInfo>(new AutoBeanUnmarshallerWS<ApplicationInfo>(autoBean),
                                                                         deployWarLoggedInHandler, null) {
                        @Override
                        protected void onSuccess(ApplicationInfo appInfo) {
                            onDeploySuccess(appInfo);
                            IDE.fireEvent(new RefreshBrowserEvent(project));
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT
                                                                            .deployApplicationFailureMessage(), Type.INFO));
                            super.onFailure(exception);
                        }
                    });
        } catch (WebSocketException e) {
            doDeployApplicationREST(project);
        }
    }

    /**
     * Deploy application to Cloud Bees by sending request over HTTP.
     *
     * @param project
     *         {@link ProjectModel} to deploy
     */
    private void doDeployApplicationREST(final ProjectModel project) {
        AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();
        try {
            CloudBeesClientService.getInstance().initializeApplication(
                    applicationId,
                    vfs.getId(),
                    project.getId(),
                    warUrl,
                    null,
                    new CloudBeesAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                                                       deployWarLoggedInHandler, null) {
                        @Override
                        protected void onSuccess(ApplicationInfo appInfo) {
                            onDeploySuccess(appInfo);
                            IDE.fireEvent(new RefreshBrowserEvent(project));
                        }

                        /**
                         * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback#onFailure(java.lang.Throwable)
                         */
                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT
                                                                            .deployApplicationFailureMessage(), Type.INFO));
                            super.onFailure(exception);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void onDeploySuccess(ApplicationInfo appInfo) {
        StringBuilder output =
                new StringBuilder(CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationSuccess()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationInfo()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridId()).append(" : ")
              .append(appInfo.getId()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridTitle()).append(" : ")
              .append(appInfo.getTitle()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridServerPool()).append(" : ")
              .append(appInfo.getServerPool()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridStatus()).append(" : ")
              .append(appInfo.getStatus()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridContainer()).append(" : ")
              .append(appInfo.getContainer()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridIdleTimeout()).append(" : ")
              .append(appInfo.getIdleTimeout()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridMaxMemory()).append(" : ")
              .append(appInfo.getMaxMemory()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridSecurityMode()).append(" : ")
              .append(appInfo.getSecurityMode()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridClusterSize()).append(" : ")
              .append(appInfo.getClusterSize()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridUrl()).append(" : ")
              .append("<a href='").append(appInfo.getUrl()).append("' target='_blank'>").append(appInfo.getUrl())
              .append("</a>").append("<br>");

        IDE.fireEvent(new OutputEvent(output.toString(), Type.INFO));
    }

    /** Set the application id, which has the next form: "domain/aplicationName". */
    private void setApplicationId() {
        final String domain = display.getDomainField().getValue();
        String name = display.getNameField().getValue();
        if (name == null)
            name = "";
        display.getApplicationIdField().setValue(domain + "/" + name);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void showView(List<String> domains) {
        if (display == null) {
            display = GWT.create(Display.class);
            this.domains = new String[domains.size()];
            this.domains = domains.toArray(this.domains);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
            display.focusInApplicationNameField();
        }
    }

    private void closeView() {
        IDE.getInstance().closeView(display.asView().getId());
    }

    /** @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension
     * .jenkins.client.event.ApplicationBuiltEvent) */
    @Override
    public void onApplicationBuilt(ApplicationBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getJobStatus().getArtifactUrl() != null) {
            warUrl = event.getJobStatus().getArtifactUrl();
            doDeployApplication();
        }
    }

}

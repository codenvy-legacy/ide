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
package org.exoplatform.ide.extension.openshift.client.create;

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
import org.exoplatform.ide.extension.openshift.client.*;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginCanceledEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.openshift.client.marshaller.ApplicationTypesUnmarshaller;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for creating new OpenShift application.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 7, 2011 5:50:34 PM anya $
 */
public class CreateApplicationPresenter extends GitPresenter implements CreateApplicationHandler, ViewClosedHandler {

    public interface Display extends IsView {
        /**
         * Get create button's click handler.
         *
         * @return {@link HasClickHandlers} click handler @Override
         */
        HasClickHandlers getCreateButton();

        /**
         * Get cancel button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get application name field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getApplicationNameField();

        /**
         * Get application's directory location field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getWorkDirLocationField();

        /**
         * Get application's type field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getTypeField();

        /**
         * Change the enable state of the create button.
         *
         * @param enable
         */
        void enableCreateButton(boolean enable);

        /** Give focus to application name field. */
        void focusInApplicationNameField();

        void setApplicationTypeValues(String[] values);
    }

    private Display display;

    /**
     *
     */
    public CreateApplicationPresenter() {
        IDE.addHandler(CreateApplicationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getCreateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doCreateApplication();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getApplicationNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                boolean isNotEmpty = (event.getValue() != null && event.getValue().trim().length() > 0);
                display.enableCreateButton(isNotEmpty);
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.extension.openshift.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide
     * .extension.openshift.client.create.CreateApplicationEvent) */
    @Override
    public void onCreateApplication(final CreateApplicationEvent createApplicationEvent) {
        if (makeSelectionCheck()) {
//         final ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();
            final ProjectModel projectModel = getSelectedProject();

            try {

                OpenShiftClientService.getInstance().getApplicationTypes(
                        new OpenShiftAsyncRequestCallback<List<String>>(
                                new ApplicationTypesUnmarshaller(new ArrayList<String>()), new LoggedInHandler() {
                            @Override
                            public void onLoggedIn(LoggedInEvent event) {
                                onCreateApplication(createApplicationEvent);
                            }
                        },

                                new LoginCanceledHandler() {
                                    @Override
                                    public void onLoginCanceled(LoginCanceledEvent event) {

                                    }
                                }
                        ) {
                            @Override
                            protected void onSuccess(List<String> result) {
                                if (display == null) {
                                    display = GWT.create(Display.class);
                                    bindDisplay();
                                    IDE.getInstance().openView(display.asView());
                                    display.setApplicationTypeValues(result.toArray(new String[result.size()]));
                                    display.focusInApplicationNameField();
                                    display.getWorkDirLocationField().setValue(projectModel.getPath());
                                    display.enableCreateButton(false);
                                }
                            }
                        });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        }
    }

    /** Perform creation of application on OpenShift by sending request over WebSocket or HTTP. */
    protected void doCreateApplication() {
        String applicationName = display.getApplicationNameField().getValue();
        String type = display.getTypeField().getValue();

        ProjectModel project = getSelectedProject();
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = project.getId();

        AutoBean<AppInfo> appInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.appInfo();
        AutoBeanUnmarshallerWS<AppInfo> unmarshaller = new AutoBeanUnmarshallerWS<AppInfo>(appInfo);
        String errorMessage = OpenShiftExtension.LOCALIZATION_CONSTANT.createApplicationFail(applicationName);

        try {
            OpenShiftClientService.getInstance().createApplicationWS(applicationName, vfs.getId(), projectId, type, false,
                                                                     new OpenShiftRESTfulRequestCallback<AppInfo>(unmarshaller,
                                                                                                                  new LoggedInHandler() {
                                                                                                                      @Override
                                                                                                                      public void
                                                                                                                      onLoggedIn(























                                                                                                                              LoggedInEvent event) {
                                                                                                                          doCreateApplication();
                                                                                                                      }
                                                                                                                  },
                                                                                                                  new LoginCanceledHandler() {

                                                                                                                      @Override
                                                                                                                      public void onLoginCanceled(
                                                                                                                              LoginCanceledEvent event) {
                                                                                                                          IDE.getInstance()
                                                                                                                             .closeView(
                                                                                                                                     display.asView()
                                                                                                                                            .getId());
                                                                                                                      }
                                                                                                                  }, errorMessage
                                                                     ) {
                                                                         @Override
                                                                         protected void onSuccess(AppInfo result) {
                                                                             onCreatedSuccess(result);
                                                                         }
                                                                     });
            IDE.getInstance().closeView(display.asView().getId());
        } catch (WebSocketException e) {
            doCreateApplicationREST(applicationName, projectId, type);
        }
    }

    /**
     * Perform creation of application on OpenShift by sending request over HTTP.
     *
     * @param applicationName
     *         application's name
     * @param projectId
     *         identifier of the project to deploy
     * @param type
     *         type of the application
     */
    protected void doCreateApplicationREST(String applicationName, String projectId, String type) {
        AutoBean<AppInfo> appInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.appInfo();
        AutoBeanUnmarshaller<AppInfo> unmarshaller = new AutoBeanUnmarshaller<AppInfo>(appInfo);
        String errorMessage = OpenShiftExtension.LOCALIZATION_CONSTANT.createApplicationFail(applicationName);

        try {
            OpenShiftClientService.getInstance().createApplication(applicationName, vfs.getId(), projectId, type, false,
                                                                   new OpenShiftAsyncRequestCallback<AppInfo>(unmarshaller,
                                                                                                              new LoggedInHandler() {
                                                                                                                  @Override
                                                                                                                  public void onLoggedIn(
                                                                                                                          LoggedInEvent event) {
                                                                                                                      doCreateApplication();
                                                                                                                  }
                                                                                                              },
                                                                                                              new LoginCanceledHandler() {

                                                                                                                  @Override
                                                                                                                  public void onLoginCanceled(
                                                                                                                          LoginCanceledEvent event) {
                                                                                                                      IDE.getInstance()
                                                                                                                         .closeView(
                                                                                                                                 display.asView()
                                                                                                                                        .getId());
                                                                                                                  }
                                                                                                              }, errorMessage
                                                                   ) {
                                                                       @Override
                                                                       protected void onSuccess(AppInfo result) {
                                                                           onCreatedSuccess(result);
                                                                       }
                                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                 .createApplicationFail(applicationName)));
        }
        IDE.getInstance().closeView(display.asView().getId());
    }

    private void onCreatedSuccess(AppInfo app) {
        IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(app), Type.INFO));
//      IDE.fireEvent(new RefreshBrowserEvent(((ItemContext)selectedItems.get(0)).getProject()));
        IDE.fireEvent(new RefreshBrowserEvent(getSelectedProject()));
    }

    /**
     * Forms the message to be shown, when application is created.
     *
     * @param appInfo
     *         application information
     * @return {@link String} message
     */
    protected String formApplicationCreatedMessage(AppInfo appInfo) {
        String applicationStr = "<br> [";
        applicationStr += "<b>Name</b>" + " : " + appInfo.getName() + "<br>";
        applicationStr += "<b>Git URL</b>" + " : " + appInfo.getGitUrl() + "<br>";
        applicationStr +=
                "<b>Public URL</b>" + " : <a href=\"" + appInfo.getPublicUrl() + "\" target=\"_blank\">"
                + appInfo.getPublicUrl() + "</a><br>";
        applicationStr += "<b>Type</b>" + " : " + appInfo.getType() + "<br>";
        applicationStr += "] ";

        return OpenShiftExtension.LOCALIZATION_CONSTANT.createApplicationSuccess(applicationStr);
    }

}

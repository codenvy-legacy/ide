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
package org.exoplatform.ide.extension.appfog.client.start;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for start and stop application commands.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class StartApplicationPresenter extends GitPresenter implements StartApplicationHandler, StopApplicationHandler,
                                                                       RestartApplicationHandler {

    /** Name of the server. */
    private String serverName;
    
    /** The name of application. */
    private String appName;
    
    public StartApplicationPresenter() {
        IDE.addHandler(StartApplicationEvent.TYPE, this);
        IDE.addHandler(StopApplicationEvent.TYPE, this);
        IDE.addHandler(RestartApplicationEvent.TYPE, this);
    }

    public void bindDisplay(List<Framework> frameworks) {
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationHandler#onStopApplication(org.exoplatform.ide
     * .extension.cloudfoundry.client.start.StopApplicationEvent) */
    @Override
    public void onStopApplication(StopApplicationEvent event) {
        serverName = event.getServer();
        if (event.getApplicationName() == null) {
            checkIsStopped();
        } else {
            appName = event.getApplicationName();
            stopApplication(appName);
        }
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler startLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            startApplication(null);
        }
    };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler stopLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            stopApplication(null);
        }
    };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStartedLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            checkIsStarted();
        }
    };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStoppedLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            checkIsStopped();
        }
    };

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationHandler#onStartApplication(org.exoplatform.ide
     * .extension.cloudfoundry.client.start.StartApplicationEvent) */
    @Override
    public void onStartApplication(StartApplicationEvent event) {
        serverName = event.getServer();
        if (event.getApplicationName() == null && makeSelectionCheck()) {
            checkIsStarted();
        } else {
            appName = event.getApplicationName();
            startApplication(appName);
        }
    }

    private void checkIsStarted() {
//      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel project = getSelectedProject();

        try {
            AutoBean<AppfogApplication> appfogApplication =
                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();
            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);
            AppfogClientService.getInstance().getApplicationInfo(
                    vfs.getId(),
                    project.getId(),
                    null,
                    null,
                    new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, checkIsStartedLoggedInHandler,
                                                                      null) {
                        @Override
                        protected void onSuccess(AppfogApplication result) {
                            if ("STARTED".equals(result.getState()) && result.getInstances() == result.getRunningInstances()) {
                                String msg =
                                        AppfogExtension.LOCALIZATION_CONSTANT.applicationAlreadyStarted(result.getName());
                                IDE.fireEvent(new OutputEvent(msg));
                            } else {
                                startApplication(null);
                            }
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void checkIsStopped() {
//      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel project = getSelectedProject();

        try {
            AutoBean<AppfogApplication> appfogApplication =
                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            AppfogClientService.getInstance().getApplicationInfo(
                    vfs.getId(),
                    project.getId(),
                    null,
                    null,
                    new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, checkIsStoppedLoggedInHandler,
                                                                      null) {

                        @Override
                        protected void onSuccess(AppfogApplication result) {
                            if ("STOPPED".equals(result.getState())) {
                                String msg =
                                        AppfogExtension.LOCALIZATION_CONSTANT.applicationAlreadyStopped(result.getName());
                                IDE.fireEvent(new OutputEvent(msg));
                            } else {
                                stopApplication(null);
                            }
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void startApplication(String name) {
//      final ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();
        final String projectId;

        if (selectedItem != null && getSelectedProject() != null && getSelectedProject().getPropertyValue("appfog-application") != null
            && appName.equals((String)getSelectedProject().getPropertyValue("appfog-application"))) {
            projectId = getSelectedProject().getId();
        } else {
            projectId = null;
        }

        try {
            AutoBean<AppfogApplication> appfogApplication =
                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            AppfogClientService.getInstance().startApplication(vfs.getId(), projectId, appName, serverName,
                                                               new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
                                                                                                                 startLoggedInHandler,
                                                                                                                 null) {
                                                                   @Override
                                                                   protected void onSuccess(AppfogApplication result) {
                                                                       if ("STARTED".equals(result.getState()) &&
                                                                           result.getInstances() == result.getRunningInstances()) {
                                                                           String msg =
                                                                                   AppfogExtension.LOCALIZATION_CONSTANT
                                                                                                  .applicationCreatedSuccessfully(
                                                                                                          result.getName());
                                                                           if (result.getUris().isEmpty()) {
                                                                               msg += "<br>" + AppfogExtension.LOCALIZATION_CONSTANT


















































                                                                                                              .applicationStartedWithNoUrls();
                                                                           } else {
                                                                               msg +=
                                                                                       "<br>"
                                                                                       + AppfogExtension.LOCALIZATION_CONSTANT
                                                                                                        .applicationStartedOnUrls(
                                                                                                                result.getName(),
                                                                                                                getAppUrisAsString(result));
                                                                           }
                                                                           IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
                                                                           IDE.fireEvent(
                                                                                   new ApplicationInfoChangedEvent(vfs.getId(), projectId));
                                                                       } else {
                                                                           String msg =
                                                                                   AppfogExtension.LOCALIZATION_CONSTANT
                                                                                                  .applicationWasNotStarted(
                                                                                                          result.getName());
                                                                           IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.ERROR));
                                                                       }

                                                                   }
                                                               });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private String getAppUrisAsString(AppfogApplication application) {
        String appUris = "";
        for (String uri : application.getUris()) {
            if (!uri.startsWith("http")) {
                uri = "http://" + uri;
            }
            appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
        }
        if (!appUris.isEmpty()) {
            // crop unnecessary symbols
            appUris = appUris.substring(2);
        }
        return appUris;
    }

    private void stopApplication(final String name) {
//      final ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();
        final String projectId;

        if (selectedItem != null && getSelectedProject() != null && getSelectedProject().getPropertyValue("appfog-application") != null
            && appName.equals((String)getSelectedProject().getPropertyValue("appfog-application"))) {
            projectId = getSelectedProject().getId();
        } else {
            projectId = null;
        }        

        try {
            AppfogClientService.getInstance().stopApplication(null, null, appName, serverName,
                                                              new AppfogAsyncRequestCallback<String>(null, stopLoggedInHandler, null) {
                                                                  @Override
                                                                  protected void onSuccess(String result) {
                                                                      try {
                                                                          AutoBean<AppfogApplication> appfogApplication =
                                                                                  AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

                                                                          AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                                                                                  new AutoBeanUnmarshaller<AppfogApplication>(
                                                                                          appfogApplication);

                                                                          AppfogClientService.getInstance()
                                                                                             .getApplicationInfo(vfs.getId(), projectId,
                                                                                                                 name, null,
                                                                                                                 new AppfogAsyncRequestCallback<AppfogApplication>(
                                                                                                                         unmarshaller, null,
                                                                                                                         null) {
                                                                                                                     @Override
                                                                                                                     protected void onSuccess(
                                                                                                                             AppfogApplication result) {
                                                                                                                         final String msg =
                                                                                                                                 AppfogExtension
                                                                                                                                         .LOCALIZATION_CONSTANT
                                                                                                                                         .applicationStopped(
                                                                                                                                                 result.getName());
                                                                                                                         IDE.fireEvent(
                                                                                                                                 new OutputEvent(
                                                                                                                                         msg));
                                                                                                                         IDE.fireEvent(
                                                                                                                                 new ApplicationInfoChangedEvent(
                                                                                                                                         vfs.getId(),
                                                                                                                                         projectId));
                                                                                                                     }
                                                                                                                 });
                                                                      } catch (RequestException e) {
                                                                          IDE.fireEvent(new ExceptionThrownEvent(e));
                                                                      }
                                                                  }
                                                              });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationHandler#onRestartApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent) */
    @Override
    public void onRestartApplication(RestartApplicationEvent event) {
        serverName = event.getServer();
        appName = event.getApplicationName();
        restartApplication(appName);
    }

    private LoggedInHandler restartLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            restartApplication(null);
        }
    };

    private void restartApplication(String name) {
//      final ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();
        final String projectId;

        if (selectedItem != null && getSelectedProject() != null && getSelectedProject().getPropertyValue("appfog-application") != null
            && appName.equals((String)getSelectedProject().getPropertyValue("appfog-application"))) {
            projectId = getSelectedProject().getId();
        } else {
            projectId = null;
        }

        try {
            AutoBean<AppfogApplication> appfogApplication =
                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            AppfogClientService.getInstance().restartApplication(null, null, appName, serverName,
                                                                 new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
                                                                                                                   restartLoggedInHandler,
                                                                                                                   null) {
                                                                     @Override
                                                                     protected void onSuccess(AppfogApplication result) {
                                                                         if (result.getInstances() == result.getRunningInstances()) {
                                                                             final String appUris = getAppUrisAsString(result);
                                                                             String msg;
                                                                             if (appUris.isEmpty()) {
                                                                                 msg = AppfogExtension.LOCALIZATION_CONSTANT
                                                                                                      .applicationRestarted(
                                                                                                              result.getName());
                                                                             } else {
                                                                                 msg =
                                                                                         AppfogExtension.LOCALIZATION_CONSTANT
                                                                                                        .applicationRestartedUris(
                                                                                                                result.getName(),
                                                                                                                appUris);
                                                                             }
                                                                             IDE.fireEvent(new OutputEvent(msg, Type.INFO));
                                                                             IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(),
                                                                                                                           projectId));
                                                                         } else {
                                                                             String msg =
                                                                                     AppfogExtension.LOCALIZATION_CONSTANT
                                                                                                    .applicationWasNotStarted(
                                                                                                            result.getName());
                                                                             IDE.fireEvent(new OutputEvent(msg, Type.ERROR));
                                                                         }
                                                                     }
                                                                 });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

}

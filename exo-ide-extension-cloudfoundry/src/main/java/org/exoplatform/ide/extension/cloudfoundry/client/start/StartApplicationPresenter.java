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
package org.exoplatform.ide.extension.cloudfoundry.client.start;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for start and stop application commands.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: StartApplicationPresenter.java Jul 12, 2011 3:58:22 PM vereshchaka $
 */
public class StartApplicationPresenter extends GitPresenter implements StartApplicationHandler, StopApplicationHandler,
                                                           RestartApplicationHandler {

    private PAAS_PROVIDER paasProvider;

    public StartApplicationPresenter() {
        IDE.addHandler(StartApplicationEvent.TYPE, this);
        IDE.addHandler(StopApplicationEvent.TYPE, this);
        IDE.addHandler(RestartApplicationEvent.TYPE, this);
    }

    public void bindDisplay(List<Framework> frameworks) {
    }

    /**
     * @see org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationHandler#onStopApplication(org.exoplatform.ide
     *      .extension.cloudfoundry.client.start.StopApplicationEvent)
     */
    @Override
    public void onStopApplication(StopApplicationEvent event) {
        paasProvider = event.getPaasProvider();
        if (event.getApplicationName() == null) {
            checkIsStopped();
        } else {
            stopApplication(event.getApplicationName(), event.getServer());
        }
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler startLoggedInHandler          = new LoggedInHandler() {
                                                              @Override
                                                              public void onLoggedIn(String server) {
                                                                  startApplication(null, null);
                                                              }
                                                          };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler stopLoggedInHandler           = new LoggedInHandler() {
                                                              @Override
                                                              public void onLoggedIn(String server) {
                                                                  stopApplication(null, null);
                                                              }
                                                          };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStartedLoggedInHandler = new LoggedInHandler() {
                                                              @Override
                                                              public void onLoggedIn(String server) {
                                                                  checkIsStarted();
                                                              }
                                                          };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStoppedLoggedInHandler = new LoggedInHandler() {
                                                              @Override
                                                              public void onLoggedIn(String server) {
                                                                  checkIsStopped();
                                                              }
                                                          };

    /**
     * @see org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationHandler#onStartApplication(org.exoplatform.ide
     *      .extension.cloudfoundry.client.start.StartApplicationEvent)
     */
    @Override
    public void onStartApplication(StartApplicationEvent event) {
        paasProvider = event.getPaasProvider();
        if (event.getApplicationName() == null && makeSelectionCheck()) {
            checkIsStarted();
        } else {
            startApplication(event.getApplicationName(), event.getServer());
        }
    }

    private void checkIsStarted() {
        // ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel project = getSelectedProject();

        try {
            AutoBean<CloudFoundryApplication> CloudFoundryApplication =
                                                                        CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();
            AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                                                                         new AutoBeanUnmarshaller<CloudFoundryApplication>(
                                                                                                                           CloudFoundryApplication);
            CloudFoundryClientService.getInstance()
                                     .getApplicationInfo(
                                                         vfs.getId(),
                                                         project.getId(),
                                                         null,
                                                         null,
                                                         new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                                                       unmarshaller,
                                                                                                                       checkIsStartedLoggedInHandler,
                                                                                                                       null, paasProvider) {
                                                             @Override
                                                             protected void onSuccess(CloudFoundryApplication result) {
                                                                 if ("STARTED".equals(result.getState())
                                                                     && result.getInstances() == result.getRunningInstances()) {
                                                                     String msg =
                                                                                  CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationAlreadyStarted(result.getName());
                                                                     IDE.fireEvent(new OutputEvent(msg));
                                                                 } else {
                                                                     startApplication(null, null);
                                                                 }
                                                             }
                                                         });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void checkIsStopped() {
        // ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel project = getSelectedProject();

        try {
            AutoBean<CloudFoundryApplication> CloudFoundryApplication =
                                                                        CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

            AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                                                                         new AutoBeanUnmarshaller<CloudFoundryApplication>(
                                                                                                                           CloudFoundryApplication);

            CloudFoundryClientService.getInstance()
                                     .getApplicationInfo(
                                                         vfs.getId(),
                                                         project.getId(),
                                                         null,
                                                         null,
                                                         new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                                                       unmarshaller,
                                                                                                                       checkIsStoppedLoggedInHandler,
                                                                                                                       null, paasProvider) {

                                                             @Override
                                                             protected void onSuccess(CloudFoundryApplication result) {
                                                                 if ("STOPPED".equals(result.getState())) {
                                                                     String msg =
                                                                                  CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationAlreadyStopped(result.getName());
                                                                     IDE.fireEvent(new OutputEvent(msg));
                                                                 } else {
                                                                     stopApplication(null, null);
                                                                 }
                                                             }
                                                         });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void startApplication(String name, String server) {
        // final ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        final ProjectModel project = getSelectedProject();

        try {
            AutoBean<CloudFoundryApplication> cloudFoundryApplication = CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();
            AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                                                                         new AutoBeanUnmarshaller<CloudFoundryApplication>(
                                                                                                                           cloudFoundryApplication);
            CloudFoundryClientService.getInstance()
                                     .startApplication(vfs.getId(),
                                                       project.getId(),
                                                       (name != null) ? name : project.getName(),
                                                       server,
                                                       paasProvider, new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                                                     startLoggedInHandler,
                                                                                                                     null, paasProvider) {
                                                           @Override
                                                           protected void onSuccess(CloudFoundryApplication result) {
                                                               if ("STARTED".equals(result.getState()) &&
                                                                   result.getInstances() == result.getRunningInstances()) {
                                                                   String msg =
                                                                                CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationCreatedSuccessfully(result.getName());
                                                                   if (result.getUris().isEmpty()) {
                                                                       msg +=
                                                                              "<br>"
                                                                                  +
                                                                                  CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationStartedWithNoUrls();
                                                                   } else {
                                                                       msg +=
                                                                              "<br>"
                                                                                  + CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(result.getName(),
                                                                                                                                                         getAppUrisAsString(
                                                                                                                                                         result));
                                                                   }
                                                                   IDE.fireEvent(
                                                                      new OutputEvent(msg, OutputMessage.Type.INFO));
                                                                   IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(),
                                                                                                                 project.getId()));
                                                               } else {
                                                                   String msg =
                                                                                CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationWasNotStarted(result.getName());
                                                                   IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.ERROR));
                                                               }
                                                           }

                                                           @Override
                                                           protected void onFailure(Throwable exception) {
                                                               String msg =
                                                                            CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationWasNotStarted(project.getName());
                                                               Dialogs.getInstance().showError(msg);
                                                           }
                                                       });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private String getAppUrisAsString(CloudFoundryApplication application) {
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

    private void stopApplication(final String name, String server) {
        // final String projectId =
        // ((selectedItems != null && !selectedItems.isEmpty() && ((ItemContext)selectedItems.get(0)).getProject() != null)
        // ? ((ItemContext)selectedItems.get(0)).getProject().getId() : null);

        final String projectId = getSelectedProject() != null ? getSelectedProject().getId() : null;

        try {
            CloudFoundryClientService.getInstance().stopApplication(vfs.getId(), projectId, name, server, paasProvider, new CloudFoundryAsyncRequestCallback<String>(null, stopLoggedInHandler,
                                                                                                                 null, paasProvider) {
                @Override
                protected void onSuccess(String result) {
                    try {
                        AutoBean<CloudFoundryApplication> CloudFoundryApplication =
                                                                                    CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

                        AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                                                                                     new AutoBeanUnmarshaller<CloudFoundryApplication>(
                                                                                                                                       CloudFoundryApplication);

                        CloudFoundryClientService.getInstance()
                                                 .getApplicationInfo(vfs.getId(),
                                                                     projectId,
                                                                     name,
                                                                     null,
                                                                     new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                                                                   unmarshaller,
                                                                                                                                   null,
                                                                                                                                   null, paasProvider) {
                                                                         @Override
                                                                         protected void onSuccess(CloudFoundryApplication result) {
                                                                             final String
                                                                             msg =
                                                                                   CloudFoundryExtension.LOCALIZATION_CONSTANT
                                                                                                                              .applicationStopped(result.getName());
                                                                             IDE.fireEvent(new OutputEvent(
                                                                                                           msg));
                                                                             IDE.fireEvent(new ApplicationInfoChangedEvent(
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
        this.paasProvider = event.getPaasProvider();
        restartApplication(event.getApplicationName(), event.getServer());
    }

    private LoggedInHandler restartLoggedInHandler = new LoggedInHandler() {
                                                       @Override
                                                       public void onLoggedIn(String server) {
                                                           restartApplication(null, null);
                                                       }
                                                   };

    private void restartApplication(String name, String server) {
        // final String projectId =
        // ((selectedItems != null && !selectedItems.isEmpty() && ((ItemContext)selectedItems.get(0)).getProject() != null)
        // ? ((ItemContext)selectedItems.get(0)).getProject().getId() : null);

        final String projectId = getSelectedProject() != null ? getSelectedProject().getId() : null;

        try {
            AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                                                                        CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

            AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                                                                         new AutoBeanUnmarshaller<CloudFoundryApplication>(
                                                                                                                           cloudFoundryApplication);

            CloudFoundryClientService.getInstance()
                                     .restartApplication(vfs.getId(),
                                                         projectId,
                                                         name,
                                                         server,
                                                         paasProvider, new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                                                       unmarshaller,
                                                                                                                       restartLoggedInHandler,
                                                                                                                       null, paasProvider) {
                                                             @Override
                                                             protected void onSuccess(CloudFoundryApplication result) {
                                                                 if (result.getInstances() == result.getRunningInstances()) {
                                                                     final String appUris = getAppUrisAsString(result);
                                                                     String msg = "";
                                                                     if (appUris.isEmpty()) {
                                                                         msg =
                                                                               CloudFoundryExtension.LOCALIZATION_CONSTANT
                                                                                                                          .applicationRestarted(
                                                                                                                          result.getName());
                                                                     } else {
                                                                         msg =
                                                                               CloudFoundryExtension.LOCALIZATION_CONSTANT
                                                                                                                          .applicationRestartedUris(
                                                                                                                                                    result.getName(),
                                                                                                                                                    appUris);
                                                                     }
                                                                     IDE.fireEvent(new OutputEvent(msg, Type.INFO));
                                                                     IDE.fireEvent(
                                                                        new ApplicationInfoChangedEvent(vfs.getId(),
                                                                                                        projectId));
                                                                 } else {
                                                                     String msg =
                                                                                  CloudFoundryExtension.LOCALIZATION_CONSTANT
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

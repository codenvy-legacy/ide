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
package org.exoplatform.ide.extension.appfog.client.update;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.extension.appfog.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter updating memory and number of instances of application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class UpdatePropertiesPresenter extends GitPresenter implements UpdateMemoryHandler, UpdateInstancesHandler {

    private int memory;

    private String instances;

    public UpdatePropertiesPresenter() {
        IDE.addHandler(UpdateMemoryEvent.TYPE, this);
        IDE.addHandler(UpdateInstancesEvent.TYPE, this);
    }

    public void bindDisplay(List<Framework> frameworks) {
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateMemoryHandler#onUpdateMemory(org.exoplatform.ide.extension
     * .cloudfoundry.client.update.UpdateMemoryEvent) */
    @Override
    public void onUpdateMemory(UpdateMemoryEvent event) {
        if (makeSelectionCheck()) {
            getOldMemoryValue();
        }
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler getOldMemoryValueLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getOldMemoryValue();
        }
    };

    private void getOldMemoryValue() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AutoBean<AppfogApplication> appfogApplication =
                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            AppfogClientService.getInstance().getApplicationInfo(
                    vfs.getId(),
                    projectId,
                    null,
                    null,
                    new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
                                                                      getOldMemoryValueLoggedInHandler, null) {
                        @Override
                        protected void onSuccess(AppfogApplication result) {
                            askForNewMemoryValue(result.getResources().getMemory());
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void askForNewMemoryValue(int oldMemoryValue) {
        Dialogs.getInstance().askForValue(AppfogExtension.LOCALIZATION_CONSTANT.updateMemoryDialogTitle(),
                                          AppfogExtension.LOCALIZATION_CONSTANT.updateMemoryDialogMessage(), String.valueOf(oldMemoryValue),
                                          new StringValueReceivedHandler() {
                                              @Override
                                              public void stringValueReceived(String value) {
                                                  if (value != null) {
                                                      try {
                                                          memory = Integer.parseInt(value);
                                                          updateMemory(memory);
                                                      } catch (NumberFormatException e) {
                                                          String msg =
                                                                  AppfogExtension.LOCALIZATION_CONSTANT.updateMemoryInvalidNumberMessage();
                                                          IDE.fireEvent(new ExceptionThrownEvent(msg));
                                                      }
                                                  }
                                              }
                                          });
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler updateMemoryLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            updateMemory(memory);
        }
    };

    private void updateMemory(final int memory) {
//      ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel projectModel = getSelectedProject();

        final String server = projectModel.getProperty("appfog-target").getValue().get(0);
        final String appName = projectModel.getProperty("appfog-application").getValue().get(0);
        final String projectId = projectModel.getId();

        try {
            AppfogClientService.getInstance().updateMemory(null, null, appName, server, memory,
                                                           new AppfogAsyncRequestCallback<String>(null, updateMemoryLoggedInHandler, null) {
                                                               @Override
                                                               protected void onSuccess(String result) {
                                                                   String msg = AppfogExtension.LOCALIZATION_CONSTANT
                                                                                               .updateMemorySuccess(String.valueOf(memory));
                                                                   IDE.fireEvent(new OutputEvent(msg));
                                                                   IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
                                                               }
                                                           });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateInstancesHandler#onUpdateInstances(org.exoplatform.ide
     * .extension.cloudfoundry.client.update.UpdateInstancesEvent) */
    @Override
    public void onUpdateInstances(UpdateInstancesEvent event) {
        getOldInstancesValue();
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler getOldInstancesValueLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getOldInstancesValue();
        }
    };

    private void getOldInstancesValue() {
//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            AutoBean<AppfogApplication> appfogApplication =
                    AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            AppfogClientService.getInstance().getApplicationInfo(
                    vfs.getId(),
                    projectId,
                    null,
                    null,
                    new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
                                                                      getOldInstancesValueLoggedInHandler, null) {
                        @Override
                        protected void onSuccess(AppfogApplication result) {
                            askForInstancesNumber(result.getInstances());
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void askForInstancesNumber(int oldInstancesValue) {
        Dialogs.getInstance().askForValue(AppfogExtension.LOCALIZATION_CONSTANT.updateInstancesDialogTitle(),
                                          AppfogExtension.LOCALIZATION_CONSTANT.updateInstancesDialogMessage(),
                                          String.valueOf(oldInstancesValue),
                                          new StringValueReceivedHandler() {
                                              @Override
                                              public void stringValueReceived(String value) {
                                                  if (value != null) {

                                                      instances = value;
                                                      try {
                                                          // check, is instances contains only numbers
                                                          Integer.parseInt(instances);
                                                          updateInstances(instances);
                                                      } catch (NumberFormatException e) {
                                                          String msg = AppfogExtension.LOCALIZATION_CONSTANT
                                                                                      .updateInstancesInvalidValueMessage();
                                                          IDE.fireEvent(new ExceptionThrownEvent(msg));
                                                      }
                                                  }
                                              }
                                          });
    }

    private LoggedInHandler updateInstancesLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            updateInstances(instances);
        }
    };

    /**
     * @param instancesExpression
     *         how should we change number of instances. Expected are:
     *         <ul>
     *         <li>&lt;num&gt; - set number of instances to &lt;num&gt;</li>
     *         <li>&lt;+num&gt; - increase by &lt;num&gt; of instances</li>
     *         <li>&lt;-num&gt; - decrease by &lt;num&gt; of instances</li>
     *         </ul>
     */
    private void updateInstances(final String instancesExpression) {
//      ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel projectModel = getSelectedProject();

        final String server = projectModel.getProperty("appfog-target").getValue().get(0);
        final String appName = projectModel.getProperty("appfog-application").getValue().get(0);

//      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        final String projectId = projectModel.getId();

        String encodedExp = URL.encodePathSegment(instancesExpression);

        try {
            AppfogClientService.getInstance().updateInstances(vfs.getId(), projectId, appName, server, encodedExp,
                                                              new AppfogAsyncRequestCallback<StringBuilder>(
                                                                      new StringUnmarshaller(new StringBuilder()),
                                                                      updateInstancesLoggedInHandler, null) {
                                                                  @Override
                                                                  protected void onSuccess(StringBuilder result) {
                                                                      try {
                                                                          AutoBean<AppfogApplication> appfogApplication =
                                                                                  AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

                                                                          AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                                                                                  new AutoBeanUnmarshaller<AppfogApplication>(
                                                                                          appfogApplication);

                                                                          AppfogClientService.getInstance()
                                                                                             .getApplicationInfo(vfs.getId(), projectId,
                                                                                                                 null,
                                                                                                                 AppfogExtension
                                                                                                                         .DEFAULT_SERVER,
                                                                                                                 new





























                                                                                                                         AppfogAsyncRequestCallback<AppfogApplication>(
                                                                                                                         unmarshaller, null,
                                                                                                                         null) {
                                                                                                                     @Override
                                                                                                                     protected void onSuccess(
                                                                                                                             AppfogApplication result) {
                                                                                                                         String msg =
                                                                                                                                 AppfogExtension
                                                                                                                                         .LOCALIZATION_CONSTANT
                                                                                                                                         .updateInstancesSuccess(
                                                                                                                                                 String
                                                                                                                                                         .valueOf(
                                                                                                                                                                 result.getInstances()));
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

}

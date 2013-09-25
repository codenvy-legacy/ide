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
package com.codenvy.ide.ext.appfog.client.update;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppFogExtension;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter updating memory and number of instances of application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class UpdatePropertiesPresenter {
    private int                        memory;
    private String                     instances;
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    private AppfogLocalizationConstant constant;
    private AsyncCallback<String>      updatePropertiesCallback;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private NotificationManager        notificationManager;

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected UpdatePropertiesPresenter(EventBus eventBus, ResourceProvider resourceProvider, AppfogLocalizationConstant constant,
                                        LoginPresenter loginPresenter, AppfogClientService service,
                                        NotificationManager notificationManager) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /**
     * Shows dialog for updating application's memory.
     *
     * @param callback
     */
    public void showUpdateMemoryDialog(AsyncCallback<String> callback) {
        this.updatePropertiesCallback = callback;

        getOldMemoryValue();
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler getOldMemoryValueLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getOldMemoryValue();
        }
    };

    /** Gets old memory value. */
    private void getOldMemoryValue() {
        String projectId = resourceProvider.getActiveProject().getId();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, getOldMemoryValueLoggedInHandler,
                                                                                         null, eventBus, constant,
                                                                                         loginPresenter, notificationManager) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               askForNewMemoryValue(result.getResources().getMemory());
                                           }
                                       });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Shows dialog for changing memory.
     *
     * @param oldMemoryValue
     */
    private void askForNewMemoryValue(int oldMemoryValue) {
        String value = Window.prompt(constant.updateMemoryInvalidNumberMessage(), String.valueOf(oldMemoryValue));
        if (value != null) {
            try {
                // check, is instances contains only numbers
                memory = Integer.parseInt(value);
                updateMemory(memory);
            } catch (NumberFormatException e) {
                String msg = constant.updateMemoryInvalidNumberMessage();
                eventBus.fireEvent(new ExceptionThrownEvent(msg));
                Window.alert(msg);
            }
        }
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler updateMemoryLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            updateMemory(memory);
        }
    };

    /**
     * Updates memory.
     *
     * @param memory
     */
    private void updateMemory(final int memory) {
        Project project = resourceProvider.getActiveProject();

        final String server = project.getProperty("appfog-target").getValue().get(0);
        final String appName = project.getProperty("appfog-application").getValue().get(0);
        final String projectId = project.getId();

        try {
            service.updateMemory(null, null, appName, server, memory,
                                 new AppfogAsyncRequestCallback<String>(null, updateMemoryLoggedInHandler, null, eventBus, constant,
                                                                        loginPresenter, notificationManager) {
                                     @Override
                                     protected void onSuccess(String result) {
                                         String msg = constant.updateMemorySuccess(String.valueOf(memory));
                                         Notification notification = new Notification(msg, INFO);
                                         notificationManager.showNotification(notification);
                                         updatePropertiesCallback.onSuccess(projectId);
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Shows dialog for updating application's instances.
     *
     * @param callback
     */
    public void showUpdateInstancesDialog(AsyncCallback<String> callback) {
        this.updatePropertiesCallback = callback;

        getOldInstancesValue();
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler getOldInstancesValueLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getOldInstancesValue();
        }
    };

    /** Gets old instances value. */
    private void getOldInstancesValue() {
        String projectId = resourceProvider.getActiveProject().getId();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, getOldInstancesValueLoggedInHandler,
                                                                                         null, eventBus, constant, loginPresenter,
                                                                                         notificationManager) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               askForInstancesNumber(result.getInstances());
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Shows dialog for changing instances.
     *
     * @param oldInstancesValue
     */
    private void askForInstancesNumber(int oldInstancesValue) {
        String value = Window.prompt(constant.updateInstancesDialogMessage(), String.valueOf(oldInstancesValue));
        if (value != null) {
            instances = value;
            try {
                // check, is instances contains only numbers
                Integer.parseInt(instances);
                updateInstances(instances);
            } catch (NumberFormatException e) {
                String msg = constant.updateInstancesInvalidValueMessage();
                eventBus.fireEvent(new ExceptionThrownEvent(msg));
                Window.alert(msg);
            }
        }
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
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
        Project project = resourceProvider.getActiveProject();

        final String server = project.getProperty("appfog-target").getValue().get(0);
        final String appName = project.getProperty("appfog-application").getValue().get(0);
        final String projectId = project.getId();

        String encodedExp = URL.encodePathSegment(instancesExpression);

        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller();
            service.updateInstances(resourceProvider.getVfsId(), projectId, appName, server, encodedExp,
                                    new AppfogAsyncRequestCallback<String>(unmarshaller, updateInstancesLoggedInHandler, null,
                                                                           eventBus, constant, loginPresenter, notificationManager) {
                                        @Override
                                        protected void onSuccess(String result) {
                                            AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

                                            try {
                                                service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null,
                                                                           AppFogExtension.DEFAULT_SERVER,
                                                                           new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
                                                                                                                             null,
                                                                                                                             null, eventBus,
                                                                                                                             constant,
                                                                                                                             loginPresenter,
                                                                                                                             notificationManager) {
                                                                               @Override
                                                                               protected void onSuccess(AppfogApplication result) {
                                                                                   String msg = constant.updateInstancesSuccess(
                                                                                           String.valueOf(result.getInstances()));
                                                                                   Notification notification = new Notification(msg, INFO);
                                                                                   notificationManager.showNotification(notification);
                                                                                   updatePropertiesCallback.onSuccess(projectId);
                                                                               }
                                                                           });
                                            } catch (RequestException e) {
                                                eventBus.fireEvent(new ExceptionThrownEvent(e));
                                                Notification notification = new Notification(e.getMessage(), ERROR);
                                                notificationManager.showNotification(notification);
                                            }
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }
}
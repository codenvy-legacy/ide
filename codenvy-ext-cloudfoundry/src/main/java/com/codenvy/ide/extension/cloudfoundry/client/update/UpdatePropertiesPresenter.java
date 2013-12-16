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
package com.codenvy.ide.extension.cloudfoundry.client.update;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter updating memory and number of instances of application.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: MapUnmapUrlPresenter.java Jul 18, 2011 9:22:02 AM vereshchaka $
 */
@Singleton
public class UpdatePropertiesPresenter {
    private int                                 memory;
    private String                              instances;
    private EventBus                            eventBus;
    private ResourceProvider                    resourceProvider;
    private ConsolePart                         console;
    private CloudFoundryLocalizationConstant    constant;
    private AsyncCallback<String>               updatePropertiesCallback;
    private LoginPresenter                      loginPresenter;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected UpdatePropertiesPresenter(EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                        CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter,
                                        CloudFoundryClientService service) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /**
     * Shows dialog for updating application's memory.
     *
     * @param paasProvider
     * @param callback
     */
    public void showUpdateMemoryDialog(CloudFoundryExtension.PAAS_PROVIDER paasProvider, AsyncCallback<String> callback) {
        this.paasProvider = paasProvider;
        this.updatePropertiesCallback = callback;

        getOldMemoryValue();
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler getOldMemoryValueLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getOldMemoryValue();
        }
    };

    /** Gets old memory value. */
    private void getOldMemoryValue() {
        String projectId = resourceProvider.getActiveProject().getId();
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller();

        try {
            service.getApplicationInfo(resourceProvider.getVfsInfo().getId(), projectId, null, null,
                                       new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                                     getOldMemoryValueLoggedInHandler, null,
                                                                                                     eventBus, console, constant,
                                                                                                     loginPresenter, paasProvider) {
                                           @Override
                                           protected void onSuccess(CloudFoundryApplication result) {
                                               askForNewMemoryValue(result.getResources().getMemory());
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
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
        final String projectId = resourceProvider.getActiveProject().getId();

        try {
            service.updateMemory(resourceProvider.getVfsInfo().getId(), projectId, null, null, memory,
                                 new CloudFoundryAsyncRequestCallback<String>(null, updateMemoryLoggedInHandler, null, eventBus, console,
                                                                              constant, loginPresenter, paasProvider) {
                                     @Override
                                     protected void onSuccess(String result) {
                                         String msg = constant.updateMemorySuccess(String.valueOf(memory));
                                         console.print(msg);
                                         updatePropertiesCallback.onSuccess(projectId);
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Shows dialog for updating application's instances.
     *
     * @param paasProvider
     * @param callback
     */
    public void showUpdateInstancesDialog(CloudFoundryExtension.PAAS_PROVIDER paasProvider, AsyncCallback<String> callback) {
        this.paasProvider = paasProvider;
        this.updatePropertiesCallback = callback;

        getOldInstancesValue();
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler getOldInstancesValueLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getOldInstancesValue();
        }
    };

    /** Gets old instances value. */
    private void getOldInstancesValue() {
        String projectId = resourceProvider.getActiveProject().getId();
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller();

        try {
            service.getApplicationInfo(resourceProvider.getVfsInfo().getId(), projectId, null, null,
                                       new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                                     getOldInstancesValueLoggedInHandler,
                                                                                                     null, eventBus, console, constant,
                                                                                                     loginPresenter, paasProvider) {
                                           @Override
                                           protected void onSuccess(CloudFoundryApplication result) {
                                               askForInstancesNumber(result.getInstances());
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
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
        final String projectId = resourceProvider.getActiveProject().getId();
        String encodedExp = URL.encodePathSegment(instancesExpression);

        try {
            service.updateInstances(resourceProvider.getVfsInfo().getId(), projectId, null, null, encodedExp,
                                    new CloudFoundryAsyncRequestCallback<String>(null, updateInstancesLoggedInHandler, null, eventBus,
                                                                                 console, constant, loginPresenter, paasProvider) {
                                        @Override
                                        protected void onSuccess(String result) {
                                            CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller();
                                            try {
                                                service.getApplicationInfo(resourceProvider.getVfsInfo().getId(), projectId, null, null,
                                                                           new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                   unmarshaller, null, null, eventBus, console, constant,
                                                                                   loginPresenter, paasProvider) {
                                                                               @Override
                                                                               protected void onSuccess(CloudFoundryApplication result) {
                                                                                   String msg = constant.updateInstancesSuccess(
                                                                                           String.valueOf(result.getInstances()));
                                                                                   console.print(msg);
                                                                                   updatePropertiesCallback.onSuccess(projectId);
                                                                               }
                                                                           });
                                            } catch (RequestException e) {
                                                eventBus.fireEvent(new ExceptionThrownEvent(e));
                                                console.print(e.getMessage());
                                            }
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}
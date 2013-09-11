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
package org.exoplatform.ide.extension.openshift.client.start;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftAsyncRequestCallback;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.project.ApplicationInfoChangedEvent;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class StartApplicationPresenter
        implements StartApplicationHandler, StopApplicationHandler, RestartApplicationHandler {
    private String appName;

    public StartApplicationPresenter() {
        IDE.addHandler(StartApplicationEvent.TYPE, this);
        IDE.addHandler(StopApplicationEvent.TYPE, this);
        IDE.addHandler(RestartApplicationEvent.TYPE, this);
    }

    /** If user is not logged in to OpenShift, this handler will be called, after user logged in. */
    private LoggedInHandler startLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn(LoggedInEvent event) {
            if (!event.isFailed()) {
                startApplication(appName);
            }
        }
    };

    /** If user is not logged in to OpenShift, this handler will be called, after user logged in. */
    private LoggedInHandler stopLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn(LoggedInEvent event) {
            if (!event.isFailed()) {
                stopApplication(appName);
            }
        }
    };

    /** If user is not logged in to OpenShift, this handler will be called, after user logged in. */
    private LoggedInHandler restartLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn(LoggedInEvent event) {
            if (!event.isFailed()) {
                restartApplication(appName);
            }
        }
    };

    @Override
    public void onRestartApplication(RestartApplicationEvent event) {
        appName = event.getApplicationName();
        restartApplication(appName);
    }

    @Override
    public void onStartApplication(StartApplicationEvent event) {
        appName = event.getApplicationName();
        startApplication(appName);
    }

    @Override
    public void onStopApplication(StopApplicationEvent event) {
        appName = event.getApplicationName();
        stopApplication(appName);
    }

    private void startApplication(final String appName) {
        try {
            OpenShiftClientService.getInstance().startApplication(appName,
                                                                  new OpenShiftAsyncRequestCallback<Void>(null, startLoggedInHandler,
                                                                                                          null) {
                                                                      @Override
                                                                      protected void onSuccess(Void result) {
                                                                          IDE.fireEvent(
                                                                                  new OutputEvent("Application " + appName + " started"));
                                                                          IDE.fireEvent(new ApplicationInfoChangedEvent(appName));
                                                                      }

                                                                      @Override
                                                                      protected void onFailure(Throwable exception) {
                                                                      }
                                                                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void stopApplication(final String appName) {
        try {
            OpenShiftClientService.getInstance().stopApplication(appName,
                                                                 new OpenShiftAsyncRequestCallback<Void>(null, stopLoggedInHandler, null) {
                                                                     @Override
                                                                     protected void onSuccess(Void result) {
                                                                         IDE.fireEvent(
                                                                                 new OutputEvent("Application " + appName + " stopped"));
                                                                         IDE.fireEvent(new ApplicationInfoChangedEvent(appName));
                                                                     }

                                                                     @Override
                                                                     protected void onFailure(Throwable exception) {
                                                                     }
                                                                 });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void restartApplication(final String appName) {
        try {
            OpenShiftClientService.getInstance().restartApplication(appName,
                                                                    new OpenShiftAsyncRequestCallback<Void>(null, restartLoggedInHandler,
                                                                                                            null) {
                                                                        @Override
                                                                        protected void onSuccess(Void result) {
                                                                            IDE.fireEvent(new OutputEvent(
                                                                                    "Application " + appName + " restarted"));
                                                                            IDE.fireEvent(new ApplicationInfoChangedEvent(appName));
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

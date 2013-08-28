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
package org.exoplatform.ide.extension.googleappengine.client.backends;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineWsRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.model.State;

/**
 * Handler for operations with backends.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class BackendsHandler extends GoogleAppEnginePresenter implements UpdateBackendsHandler,
                                                                         UpdateBackendStateHandler, DeleteBackendHandler,
                                                                         RollbackBackendsHandler, ConfigureBackendHandler {

    /** Current backend's name. */
    private String backendName;

    /** Current backend's state. */
    private State backendState;

    public BackendsHandler() {
        IDE.addHandler(UpdateBackendsEvent.TYPE, this);
        IDE.addHandler(UpdateBackendStateEvent.TYPE, this);
        IDE.addHandler(DeleteBackendEvent.TYPE, this);
        IDE.addHandler(ConfigureBackendEvent.TYPE, this);
        IDE.addHandler(RollbackBackendsEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendsHandler#onUpdateBackend(org.exoplatform.ide
     * .extension.googleappengine.client.backends.UpdateBackendsEvent) */
    @Override
    public void onUpdateBackend(UpdateBackendsEvent event) {
        if (isAppEngineProject()) {
            if (event.isAll()) {
                updateAllBackends();
            } else {
                backendName = event.getBackendName();
                updateBackend();
            }
        } else {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
        }
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendStateHandler#onUpdateBackendState(org.exoplatform
     * .ide.extension.googleappengine.client.backends.UpdateBackendStateEvent) */
    @Override
    public void onUpdateBackendState(UpdateBackendStateEvent event) {
        if (isAppEngineProject()) {
            backendName = event.getBackendName();
            backendState = event.getState();
            updateState();
        } else {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
        }
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.backends.ConfigureBackendHandler#onConfigureBackend(org.exoplatform.ide
     * .extension.googleappengine.client.backends.ConfigureBackendEvent) */
    @Override
    public void onConfigureBackend(ConfigureBackendEvent event) {
        if (isAppEngineProject()) {
            backendName = event.getBackendName();
            configureBackend();
        } else {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
        }
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.backends.RollbackBackendsHandler#onRollbackBackend(org.exoplatform.ide
     * .extension.googleappengine.client.backends.RollbackBackendsEvent) */
    @Override
    public void onRollbackBackend(RollbackBackendsEvent event) {
        if (isAppEngineProject()) {
            if (event.isAll()) {
                rollbackAllBackends();
            } else {
                backendName = event.getBackendName();
                rollbackBackend();
            }
        } else {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
        }
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.backends.DeleteBackendHandler#onDeleteBackend(org.exoplatform.ide
     * .extension.googleappengine.client.backends.DeleteBackendEvent) */
    @Override
    public void onDeleteBackend(DeleteBackendEvent event) {
        if (isAppEngineProject()) {
            backendName = event.getBackendName();
            deleteBackend();
        } else {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
        }
    }

    /** Perform updating backend's state. */
    private void updateState() {
        try {
            GoogleAppEngineClientService.getInstance().setBackendState(currentVfs.getId(), currentProject.getId(),
                                                                       backendName, backendState.name(),
                                                                       new GoogleAppEngineAsyncRequestCallback<Object>() {

                                                                           @Override
                                                                           protected void onSuccess(Object result) {
                                                                               IDE.fireEvent(new RefreshBackendListEvent());
                                                                           }
                                                                       });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Perform updating backend. */
    private void updateBackend() {
        try {
            GoogleAppEngineClientService.getInstance().updateBackend(currentVfs.getId(), currentProject.getId(),
                                                                     backendName, new GoogleAppEngineWsRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                                                                          .updateBackendSuccessfully(backendName), Type.INFO));
                    IDE.fireEvent(new RefreshBackendListEvent());
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Perform updating backends. */
    private void updateAllBackends() {
        try {
            GoogleAppEngineClientService.getInstance().updateAllBackends(currentVfs.getId(), currentProject.getId(),
                                                                         new GoogleAppEngineWsRequestCallback<Object>() {

                                                                             @Override
                                                                             protected void onSuccess(Object result) {
                                                                                 IDE.fireEvent(new OutputEvent(
                                                                                         GoogleAppEngineExtension.GAE_LOCALIZATION





















                                                                                                                 .updateAllBackendsSuccessfully(),
                                                                                         Type.INFO));
                                                                                 IDE.fireEvent(new RefreshBackendListEvent());
                                                                             }
                                                                         });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Perform rollback backend. */
    private void rollbackBackend() {
        try {
            GoogleAppEngineClientService.getInstance().rollbackBackend(currentVfs.getId(), currentProject.getId(),
                                                                       backendName, new GoogleAppEngineAsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                                                                          .rollbackBackendSuccessfully(backendName), Type.INFO));
                    IDE.fireEvent(new RefreshBackendListEvent());
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Perform rollback all backends. */
    private void rollbackAllBackends() {
        try {
            GoogleAppEngineClientService.getInstance().rollbackAllBackends(currentVfs.getId(), currentProject.getId(),
                                                                           new GoogleAppEngineAsyncRequestCallback<Object>() {

                                                                               @Override
                                                                               protected void onSuccess(Object result) {
                                                                                   IDE.fireEvent(new OutputEvent(
                                                                                           GoogleAppEngineExtension.GAE_LOCALIZATION
                                                                                                                   .rollbackAllBackendsSuccessfully(),
                                                                                           Type.INFO));
                                                                                   IDE.fireEvent(new RefreshBackendListEvent());
                                                                               }
                                                                           });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Perform deleting backend. */
    private void deleteBackend() {
        try {
            GoogleAppEngineClientService.getInstance().deleteBackend(currentVfs.getId(), currentProject.getId(),
                                                                     backendName, new GoogleAppEngineAsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                                                                          .deleteBackendSuccessfully(backendName), Type.INFO));
                    IDE.fireEvent(new RefreshBackendListEvent());
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Perform configuring backend. */
    private void configureBackend() {
        try {
            GoogleAppEngineClientService.getInstance().configureBackend(currentVfs.getId(), currentProject.getId(),
                                                                        backendName, new GoogleAppEngineAsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                                                                          .configureBackendSuccessfully(backendName), Type.INFO));
                    IDE.fireEvent(new RefreshBackendListEvent());
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}

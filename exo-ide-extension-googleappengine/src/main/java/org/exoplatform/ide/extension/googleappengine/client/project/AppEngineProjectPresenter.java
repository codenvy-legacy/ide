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
package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.backends.*;
import org.exoplatform.ide.extension.googleappengine.client.cron.UpdateCronEvent;
import org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationEvent;
import org.exoplatform.ide.extension.googleappengine.client.dos.UpdateDosEvent;
import org.exoplatform.ide.extension.googleappengine.client.indexes.UpdateIndexesEvent;
import org.exoplatform.ide.extension.googleappengine.client.indexes.VacuumIndexesEvent;
import org.exoplatform.ide.extension.googleappengine.client.logs.ShowLogsEvent;
import org.exoplatform.ide.extension.googleappengine.client.model.*;
import org.exoplatform.ide.extension.googleappengine.client.pagespeed.UpdatePageSpeedEvent;
import org.exoplatform.ide.extension.googleappengine.client.queues.UpdateQueuesEvent;
import org.exoplatform.ide.extension.googleappengine.client.rollback.RollbackUpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 22, 2012 5:14:40 PM anya $
 */
public class AppEngineProjectPresenter extends GoogleAppEnginePresenter implements ManageAppEngineProjectHandler,
                                                                                   ViewClosedHandler, RefreshBackendListHandler {

    interface Display extends IsView {
        HasClickHandlers getCloseButton();

        HasClickHandlers getConfigureBackendButton();

        HasClickHandlers getDeleteBackendButton();

        HasClickHandlers getUpdateBackendButton();

        HasClickHandlers getUpdateAllBackendsButton();

        HasClickHandlers getRollbackBackendButton();

        HasClickHandlers getRollbackAllBackendsButton();

        HasClickHandlers getLogsButton();

        HasClickHandlers getUpdateButton();

        HasClickHandlers getRollbackButton();

        HasClickHandlers getUpdateCronButton();

        HasClickHandlers getUpdateDosButton();

        HasClickHandlers getUpdateIndexesButton();

        HasClickHandlers getVacuumIndexesButton();

        HasClickHandlers getUpdatePageSpeedButton();

        HasClickHandlers getUpdateQueuesButton();

        ListGridItem<CronEntry> getCronGrid();

        ListGridItem<Backend> getBackendGrid();

        ListGridItem<ResourceLimit> getResourceLimitGrid();

        void enableUpdateBackendButton(boolean enabled);

        void enableRollbackBackendButton(boolean enabled);

        void enableDeleteBackendButton(boolean enabled);

        void enableConfigureBackendButton(boolean enabled);

        HasBackendActions getBackendActions();
    }

    private Display display;

    private Backend selectedBackend;

    public AppEngineProjectPresenter() {
        IDE.getInstance().addControl(new AppEngineProjectControl());

        IDE.addHandler(ManageAppEngineProjectEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(RefreshBackendListEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getBackendGrid().addSelectionHandler(new SelectionHandler<Backend>() {

            @Override
            public void onSelection(SelectionEvent<Backend> event) {
                selectedBackend = event.getSelectedItem();
                boolean enabled = (selectedBackend != null);
                display.enableConfigureBackendButton(enabled);
                display.enableDeleteBackendButton(enabled);
                display.enableRollbackBackendButton(enabled);
                display.enableUpdateBackendButton(enabled);
            }
        });

        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getUpdateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateApplication();
            }
        });

        display.getRollbackButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                rollbackApplicationUpdate();
            }
        });

        display.getLogsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                showLogs();
            }
        });

        display.getUpdatePageSpeedButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updatePageSpeed();
            }
        });

        display.getUpdateIndexesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateIndexes();
            }
        });

        display.getVacuumIndexesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                vacuumIndexes();
            }
        });

        display.getUpdateDosButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateDos();
            }
        });

        display.getUpdateQueuesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateQueues();
            }
        });

        display.getUpdateCronButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateCrons();
            }
        });

        display.getUpdateAllBackendsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAllBackends();
            }
        });

        display.getRollbackAllBackendsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                rollbackAllBackends();
            }
        });

        display.getUpdateBackendButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateBackend();
            }
        });

        display.getRollbackBackendButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                rollbackBackend();
            }
        });

        display.getConfigureBackendButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                configureBackend();
            }
        });

        display.getDeleteBackendButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Dialogs.getInstance().ask(GoogleAppEngineExtension.GAE_LOCALIZATION.backendDeleteTitle(),
                                          GoogleAppEngineExtension.GAE_LOCALIZATION.backendDeleteQuestion(selectedBackend.getName()),
                                          new BooleanValueReceivedHandler() {

                                              @Override
                                              public void booleanValueReceived(Boolean value) {
                                                  if (value != null && value) {
                                                      deleteBackend();
                                                  }
                                              }
                                          });
            }
        });

        display.getBackendActions().addChangeStateHandler(new SelectionHandler<Backend>() {

            @Override
            public void onSelection(SelectionEvent<Backend> event) {
                updateBackendState(event.getSelectedItem().getName(),
                                   State.START.equals(event.getSelectedItem().getState()) ? State.STOP : State.START);
            }
        });
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.project.ManageAppEngineProjectHandler#onManageAppEngineProject(org
     * .exoplatform.ide.extension.googleappengine.client.project.ManageAppEngineProjectEvent) */
    @Override
    public void onManageAppEngineProject(ManageAppEngineProjectEvent event) {
        getResourceLimits();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    public void updateApplication() {
        IDE.getInstance().closeView(display.asView().getId());
        IDE.fireEvent(new DeployApplicationEvent());
    }

    public void rollbackApplicationUpdate() {
        IDE.getInstance().closeView(display.asView().getId());
        IDE.fireEvent(new RollbackUpdateEvent());
    }

    public void showLogs() {
        IDE.getInstance().closeView(display.asView().getId());
        IDE.fireEvent(new ShowLogsEvent());
    }

    public void updatePageSpeed() {
        IDE.getInstance().closeView(display.asView().getId());
        IDE.fireEvent(new UpdatePageSpeedEvent());
    }

    protected void updateIndexes() {
        IDE.getInstance().closeView(display.asView().getId());
        IDE.fireEvent(new UpdateIndexesEvent());
    }

    protected void vacuumIndexes() {
        IDE.getInstance().closeView(display.asView().getId());
        IDE.fireEvent(new VacuumIndexesEvent());
    }

    public void updateQueues() {
        IDE.getInstance().closeView(display.asView().getId());
        IDE.fireEvent(new UpdateQueuesEvent());
    }

    public void updateDos() {
        IDE.getInstance().closeView(display.asView().getId());
        IDE.fireEvent(new UpdateDosEvent());
    }

    public void updateCrons() {
        IDE.fireEvent(new UpdateCronEvent());
    }

    public void updateAllBackends() {
        IDE.fireEvent(new UpdateBackendsEvent(true));
    }

    public void updateBackend() {
        if (selectedBackend != null) {
            IDE.fireEvent(new UpdateBackendsEvent(selectedBackend.getName()));
        }
    }

    public void rollbackBackend() {
        if (selectedBackend != null) {
            IDE.fireEvent(new RollbackBackendsEvent(selectedBackend.getName()));
        }
    }

    public void rollbackAllBackends() {
        IDE.fireEvent((new RollbackBackendsEvent(true)));
    }

    public void deleteBackend() {
        if (selectedBackend != null) {
            IDE.fireEvent(new DeleteBackendEvent(selectedBackend.getName()));
        }
    }

    public void configureBackend() {
        if (selectedBackend != null) {
            IDE.fireEvent(new ConfigureBackendEvent(selectedBackend.getName()));
        }
    }

    private void getCrons() {
        try {
            GoogleAppEngineClientService.getInstance().cronInfo(
                    currentVfs.getId(),
                    currentProject.getId(),
                    new GoogleAppEngineAsyncRequestCallback<List<CronEntry>>(new CronListUnmarshaller(
                            new ArrayList<CronEntry>())) {

                        @Override
                        protected void onSuccess(List<CronEntry> result) {
                            display.getCronGrid().setValue(result);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public void updateBackendState(String backendName, State state) {
        IDE.fireEvent(new UpdateBackendStateEvent(backendName, state));
    }

    private void getBackends() {
        try {
            GoogleAppEngineClientService.getInstance().listBackends(currentVfs.getId(), currentProject.getId(),
                                                                    new GoogleAppEngineAsyncRequestCallback<List<Backend>>(
                                                                            new BackendsUnmarshaller(new ArrayList<Backend>())) {

                                                                        @Override
                                                                        protected void onSuccess(List<Backend> result) {
                                                                            display.getBackendGrid().setValue(result);
                                                                        }
                                                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getResourceLimits() {
        try {
            GoogleAppEngineClientService.getInstance().getResourceLimits(
                    currentVfs.getId(),
                    currentProject.getId(),
                    new GoogleAppEngineAsyncRequestCallback<List<ResourceLimit>>(new ResourceLimitsUnmarshaller(
                            new ArrayList<ResourceLimit>())) {

                        @Override
                        protected void onSuccess(List<ResourceLimit> result) {
                            openView();
                            display.getResourceLimitGrid().setValue(result);
                            getCrons();
                            getBackends();
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void openView() {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }
        display.enableConfigureBackendButton(false);
        display.enableDeleteBackendButton(false);
        display.enableRollbackBackendButton(false);
        display.enableUpdateBackendButton(false);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.backends.RefreshBackendListHandler#onRefreshBackendList(org.exoplatform
     * .ide.extension.googleappengine.client.backends.RefreshBackendListEvent) */
    @Override
    public void onRefreshBackendList(RefreshBackendListEvent event) {
        if (display != null) {
            getBackends();
        }
    }

}

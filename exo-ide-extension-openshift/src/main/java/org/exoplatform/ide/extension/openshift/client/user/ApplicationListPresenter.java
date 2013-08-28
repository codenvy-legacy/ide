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
package org.exoplatform.ide.extension.openshift.client.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.cartridge.AddCartridgeEvent;
import org.exoplatform.ide.extension.openshift.client.domain.CreateDomainEvent;
import org.exoplatform.ide.extension.openshift.client.info.Property;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.OpenShiftEmbeddableCartridge;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;
import org.exoplatform.ide.git.client.GitPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 14, 2011 2:38:17 PM anya $
 */
public class ApplicationListPresenter extends GitPresenter implements ShowApplicationListHandler, ViewClosedHandler, LoggedInHandler {
    interface Display extends IsView {
        /**
         * Get Ok button click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getOkButton();

        /**
         * Get login field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getLoginField();

        /**
         * Get domain field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getDomainField();

        /**
         * Get grid with application's information.
         *
         * @return {@link ListGridItem}
         */
        ListGridItem<Property> getApplicationInfoGrid();

        /**
         * Get grid with applications.
         *
         * @return {@link ListGridItem}
         */
        ListGridItem<AppInfo> getApplicationGrid();

        /**
         * Add handler for delete application button click.
         *
         * @param handler
         */
        void addDeleteButtonSelectionHandler(SelectionHandler<AppInfo> handler);

        /** Clear application's properties in grid. */
        void clearApplicationInfo();

        CartridgeGrid getCartridgesGrid();

        void addDeleteCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler);

        void addStartCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler);

        void addStopCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler);

        void addRestartCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler);

        void addReloadCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler);

        HasClickHandlers getAddCartridgeButton();

        HasClickHandlers getSwitchAccountButton();

        HasClickHandlers getChangeNamespaceButton();

        void setAddCartridgeButtonEnable(boolean isEnable);

        void clearCartridgesInfo();
    }

    private Display display;

    private AppInfo currentViewedApp;

    /**
     *
     */
    public ApplicationListPresenter() {
        IDE.addHandler(ShowApplicationListEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind presenter with display. */
    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getApplicationGrid().addSelectionHandler(new SelectionHandler<AppInfo>() {

            @Override
            public void onSelection(SelectionEvent<AppInfo> event) {
                if (event.getSelectedItem() != null) {
                    currentViewedApp = event.getSelectedItem();
                    displayAppInfo(event.getSelectedItem());
                } else {
                    display.clearApplicationInfo();
                    display.clearCartridgesInfo();
                }
            }
        });

        display.getApplicationGrid().addValueChangeHandler(new ValueChangeHandler<List<AppInfo>>() {

            @Override
            public void onValueChange(ValueChangeEvent<List<AppInfo>> event) {
                if (event.getValue() == null || event.getValue().size() == 0) {
                    display.clearApplicationInfo();
                }
            }
        });

        display.addDeleteButtonSelectionHandler(new SelectionHandler<AppInfo>() {

            @Override
            public void onSelection(SelectionEvent<AppInfo> event) {
                askDeleteApplication(event.getSelectedItem().getName());
            }
        });

        display.addDeleteCartridgeButtonSelectionHandler(new SelectionHandler<OpenShiftEmbeddableCartridge>() {
            @Override
            public void onSelection(SelectionEvent<OpenShiftEmbeddableCartridge> event) {
                askDeleteCartridge(event.getSelectedItem().getName());
            }
        });

        display.addStartCartridgeButtonSelectionHandler(new SelectionHandler<OpenShiftEmbeddableCartridge>() {
            @Override
            public void onSelection(SelectionEvent<OpenShiftEmbeddableCartridge> event) {
                sendStartCartridgeEvent(event.getSelectedItem().getName());
            }
        });

        display.addStopCartridgeButtonSelectionHandler(new SelectionHandler<OpenShiftEmbeddableCartridge>() {
            @Override
            public void onSelection(SelectionEvent<OpenShiftEmbeddableCartridge> event) {
                sendStopCartridgeEvent(event.getSelectedItem().getName());
            }
        });

        display.addRestartCartridgeButtonSelectionHandler(new SelectionHandler<OpenShiftEmbeddableCartridge>() {
            @Override
            public void onSelection(SelectionEvent<OpenShiftEmbeddableCartridge> event) {
                sendRestartCartridgeEvent(event.getSelectedItem().getName());
            }
        });

        display.addReloadCartridgeButtonSelectionHandler(new SelectionHandler<OpenShiftEmbeddableCartridge>() {
            @Override
            public void onSelection(SelectionEvent<OpenShiftEmbeddableCartridge> event) {
                sendReloadCartridgeEvent(event.getSelectedItem().getName());
            }
        });

        display.getAddCartridgeButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new AddCartridgeEvent(currentViewedApp));
            }
        });

        display.getSwitchAccountButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new LoginEvent());
                addLoggedInHandler();
            }
        });

        display.getChangeNamespaceButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new CreateDomainEvent(true));
            }
        });
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /**
     * @see ShowApplicationListHandler#onShowUserInfo(org.exoplatform.ide.extension
     *      .openshift.client.user.ShowApplicationListEvent)
     */
    @Override
    public void onShowUserInfo(ShowApplicationListEvent event) {
        getUserInfo();
    }

    /** Get user's information. */
    protected void getUserInfo() {
        try {
            AutoBean<RHUserInfo> rhUserInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.rhUserInfo();
            AutoBeanUnmarshaller<RHUserInfo> unmarshaller = new AutoBeanUnmarshaller<RHUserInfo>(rhUserInfo);
            OpenShiftClientService.getInstance().getUserInfo(true, new AsyncRequestCallback<RHUserInfo>(unmarshaller) {

                @Override
                protected void onSuccess(RHUserInfo result) {
                    if (display == null) {
                        display = GWT.create(Display.class);
                        bindDisplay();
                        IDE.getInstance().openView(display.asView());
                    }
                    display.clearCartridgesInfo();
                    display.getLoginField().setValue(result.getRhlogin());
                    display.getDomainField().setValue(result.getNamespace() != null ? result.getNamespace() : "Doesn't exist");
                    if (result.getApps() != null) {
                        display.getApplicationGrid().setValue(result.getApps());

                        if (result.getApps().size() > 0) {
                            display.setAddCartridgeButtonEnable(true);
                        } else {
                            display.setAddCartridgeButtonEnable(false);
                        }
                    }
                }

                /**
                 * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                 */
                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.OK == serverException.getHTTPStatus()
                            && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED))) {
                            addLoggedInHandler();
                            IDE.fireEvent(new LoginEvent());
                            return;
                        }
                    }
                    IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                 .getUserInfoFail()));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT.getUserInfoFail()));
        }
    }

    /**
     * Display application's properties.
     *
     * @param appInfo
     */
    protected void displayAppInfo(AppInfo appInfo) {
        List<Property> properties = new ArrayList<Property>();
        properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationName(), appInfo.getName()));
        properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationType(), appInfo.getType()));
        properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationPublicUrl(), "<a href =\""
                                                                                                     + appInfo.getPublicUrl() +
                                                                                                     "\" target=\"_blank\">" +
                                                                                                     appInfo.getPublicUrl() + "</a>"));
        properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationGitUrl(), appInfo.getGitUrl()));
        String time =
                DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date((long)appInfo.getCreationTime()));
        properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationCreationTime(), time));
        display.getApplicationInfoGrid().setValue(properties);
        display.getCartridgesGrid().setValue(appInfo.getEmbeddedCartridges());
        display.getCartridgesGrid().setApplicationInfo(appInfo);
    }

    /** Register {@link LoggedInHandler} handler. */
    protected void addLoggedInHandler() {
        IDE.addHandler(LoggedInEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift
     *      .client.login.LoggedInEvent)
     */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            getUserInfo();
        }
    }

    /**
     * Confirm the deleting of the application on OpenShift.
     *
     * @param name
     *         application's name
     */
    protected void askDeleteApplication(final String name) {
        Dialogs.getInstance().ask(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
                                  OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplication(name), new BooleanValueReceivedHandler() {

            @Override
            public void booleanValueReceived(Boolean value) {
                if (value != null && value) {
                    doDeleteApplication(name);
                }
            }
        });
    }

    private void askDeleteCartridge(final String cartridgeName) {
        Dialogs.getInstance().ask(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteCartridgeTitle(),
                                  OpenShiftExtension.LOCALIZATION_CONSTANT.deleteCartridge(cartridgeName),
                                  new BooleanValueReceivedHandler() {


                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value) {
                                              doDeleteCartridge(cartridgeName);
                                          }
                                      }
                                  });
    }

    /**
     * Perform deleting application on OpenShift.
     *
     * @param name
     *         application's name
     */
    protected void doDeleteApplication(final String name) {
        try {
            OpenShiftClientService.getInstance().destroyApplication(name, vfs.getId(), null,
                                                                    new AsyncRequestCallback<String>() {

                                                                        @Override
                                                                        protected void onSuccess(String result) {
                                                                            IDE.fireEvent(new OutputEvent(
                                                                                    OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                      .deleteApplicationSuccess(name),
                                                                                    Type.INFO));
                                                                            getUserInfo();
                                                                        }

                                                                        /**
                                                                         * @see org.exoplatform.gwtframework.commons.rest
                                                                         * .AsyncRequestCallback#onFailure(java.lang.Throwable)
                                                                         */
                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception,
                                                                                                                            OpenShiftExtension
                                                                                                                                    .LOCALIZATION_CONSTANT
                                                                                                                                    .deleteApplicationFail(
                                                                                                                                            name)));
                                                                        }
                                                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                 .deleteApplicationFail(name)));
        }
    }

    private void doDeleteCartridge(final String cartridgeName) {
        try {
            OpenShiftClientService.getInstance()
                                  .deleteCartridge(currentViewedApp.getName(), cartridgeName, new AsyncRequestCallback<Void>() {


                                      @Override
                                      protected void onSuccess(Void result) {
                                          IDE.fireEvent(new ShowApplicationListEvent());
                                          IDE.fireEvent(new OutputEvent("Cartridge " + cartridgeName + " successfully deleted."));
                                      }

                                      @Override
                                      protected void onFailure(Throwable exception) {
                                          Dialogs.getInstance().showError(exception.getMessage());
                                      }
                                  });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteCartridgeError());
        }
    }

    private void sendStartCartridgeEvent(final String cartridgeName) {
        try {
            OpenShiftClientService.getInstance()
                                  .startCartridge(currentViewedApp.getName(), cartridgeName, new AsyncRequestCallback<Void>() {


                                      @Override
                                      protected void onSuccess(Void result) {
                                          IDE.fireEvent(new ShowApplicationListEvent());

                                          String haproxyStatusUrl = "";
                                          if (cartridgeName.startsWith("haproxy")) {
                                              haproxyStatusUrl =
                                                      " Status url: <a href=\"" + currentViewedApp.getPublicUrl() +
                                                      "haproxy-status/\" target=\"_blank\">" +
                                                      currentViewedApp.getPublicUrl() + "haproxy-status/</a>";
                                          }

                                          IDE.fireEvent(new OutputEvent(
                                                  "Cartridge " + cartridgeName + " successfully started." + haproxyStatusUrl));

                                      }

                                      @Override
                                      protected void onFailure(Throwable exception) {
                                          Dialogs.getInstance()
                                                 .showError(OpenShiftExtension.LOCALIZATION_CONSTANT.sendEventFailed("start"));
                                      }
                                  });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.sendEventFailed("start"));
        }
    }

    private void sendStopCartridgeEvent(final String cartridgeName) {
        try {
            OpenShiftClientService.getInstance().stopCartridge(currentViewedApp.getName(), cartridgeName, new AsyncRequestCallback<Void>() {


                @Override
                protected void onSuccess(Void result) {
                    IDE.fireEvent(new ShowApplicationListEvent());
                    IDE.fireEvent(new OutputEvent("Cartridge " + cartridgeName + " successfully stopped."));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.sendEventFailed("stop"));
                }
            });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.sendEventFailed("stop"));
        }
    }

    private void sendRestartCartridgeEvent(final String cartridgeName) {
        try {
            OpenShiftClientService.getInstance().restartCartridge(currentViewedApp.getName(), cartridgeName,
                                                                  new AsyncRequestCallback<Void>() {


                                                                      @Override
                                                                      protected void onSuccess(Void result) {
                                                                          IDE.fireEvent(new ShowApplicationListEvent());
                                                                          IDE.fireEvent(new OutputEvent("Cartridge " + cartridgeName +
                                                                                                        " successfully restarted."));
                                                                      }

                                                                      @Override
                                                                      protected void onFailure(Throwable exception) {
                                                                          Dialogs.getInstance().showError(
                                                                                  OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                    .sendEventFailed("restart"));
                                                                      }
                                                                  });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.sendEventFailed("restart"));
        }
    }

    private void sendReloadCartridgeEvent(final String cartridgeName) {
        try {
            OpenShiftClientService.getInstance().reloadCartridge(currentViewedApp.getName(), cartridgeName,
                                                                 new AsyncRequestCallback<Void>() {


                                                                     @Override
                                                                     protected void onSuccess(Void result) {
                                                                         IDE.fireEvent(new ShowApplicationListEvent());
                                                                         IDE.fireEvent(new OutputEvent("Cartridge " + cartridgeName +
                                                                                                       " successfully reloaded."));
                                                                     }

                                                                     @Override
                                                                     protected void onFailure(Throwable exception) {
                                                                         Dialogs.getInstance().showError(
                                                                                 OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                   .sendEventFailed("reload"));
                                                                     }
                                                                 });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.sendEventFailed("reload"));
        }
    }
}

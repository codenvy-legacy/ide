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
package org.exoplatform.ide.extension.openshift.client.cartridge;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.marshaller.ListUnmarshaller;
import org.exoplatform.ide.extension.openshift.client.user.ShowApplicationListEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.OpenShiftEmbeddableCartridge;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AddCartridgePresenter implements ViewClosedHandler, AddCartridgeHandler {
    interface Display extends IsView {
        HasClickHandlers getOkButton();

        HasClickHandlers getCancelButton();

        void setCartridgesList(String[] cartridges);

        HasValue<String> getCartridgeName();
    }

    private Display display;

    private AppInfo appInfo;

    public AddCartridgePresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(AddCartridgeEvent.TYPE, this);
    }

    private void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addCartridge(display.getCartridgeName().getValue());
            }
        });
    }

    @Override
    public void onAddCartridge(AddCartridgeEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }
        appInfo = event.getAppInfo();
        getCartridges();
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void getCartridges() {
        try {

            OpenShiftClientService.getInstance()
                                  .getCartridges(new AsyncRequestCallback<List<String>>(new ListUnmarshaller(new ArrayList<String>())) {
                                      @Override
                                      protected void onSuccess(List<String> result) {
                                          for (OpenShiftEmbeddableCartridge cartridge : appInfo.getEmbeddedCartridges()) {
                                              if (result.contains(cartridge.getName())) {
                                                  result.remove(cartridge.getName());
                                              }
                                          }
                                          display.setCartridgesList(result.toArray(new String[result.size()]));
                                      }

                                      @Override
                                      protected void onFailure(Throwable exception) {
                                          Dialogs.getInstance()
                                                 .showError(OpenShiftExtension.LOCALIZATION_CONSTANT.errorGettingCartridgesList());
                                      }
                                  });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.errorGettingCartridgesList());
        }
    }

    private void addCartridge(final String cartridgeName) {
        try {
            OpenShiftClientService.getInstance().addCartridge(appInfo.getName(), cartridgeName, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    IDE.fireEvent(new ShowApplicationListEvent());
                    IDE.getInstance().closeView(display.asView().getId());
                    IDE.fireEvent(new OutputEvent("Cartridge " + cartridgeName +
                                                  " successfully added."));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Dialogs.getInstance().showError("Error cartridge adding.\nOpenShift response: " + exception.getMessage());
                }
            });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.errorAddingCartridge());
        }
    }
}

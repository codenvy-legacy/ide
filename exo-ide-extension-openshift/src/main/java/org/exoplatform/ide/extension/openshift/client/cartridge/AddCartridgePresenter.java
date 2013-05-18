/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

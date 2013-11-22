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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.preference.PreferencePerformer;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;
import org.exoplatform.ide.extension.ssh.client.SshKeyService;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.*;
import org.exoplatform.ide.extension.ssh.client.keymanager.ui.HasSshGrid;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.extension.ssh.shared.ListKeyItem;

/** Manage user keys. */
public class KeyManagerPresenter implements ShowKeyManagerHandler, ViewClosedHandler, PreferencePerformer, RefreshKeysHandler {
    public interface Display extends IsView {
        HasSshGrid<KeyItem> getKeyItemGrid();

        HasClickHandlers getGenerateButton();

        HasClickHandlers getUploadButton();

        HasClickHandlers getGenerateGithubKeyButton();
    }

    private Display display;

    public KeyManagerPresenter() {
        IDE.addHandler(ShowKeyManagerEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(RefreshKeysEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onShowSshKeyManager(ShowKeyManagerEvent event) {
        if (display != null) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();

        refreshKeys();
    }

    /** Refresh list of keys */
    private void refreshKeys() {
        AutoBean<ListKeyItem> keyItemList = SshKeyExtension.AUTO_BEAN_FACTORY.keyItems();
        AutoBeanUnmarshaller<ListKeyItem> unmarshaller = new AutoBeanUnmarshaller<ListKeyItem>(keyItemList);

        try {
            SshKeyService.get().getAllKeys(new AsyncRequestCallback<ListKeyItem>(unmarshaller) {
                @Override
                protected void onSuccess(ListKeyItem result) {
                    display.getKeyItemGrid().setValue(result.getKeys());
                }

                @Override
                protected void onFailure(Throwable e) {
                    GWT.log(e.getLocalizedMessage());
                    Dialogs.getInstance().showError(e.getLocalizedMessage());
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Components binding */
    private void bindDisplay() {
        display.getKeyItemGrid().addViewButtonSelectionHandler(new SelectionHandler<KeyItem>() {
            @Override
            public void onSelection(SelectionEvent<KeyItem> event) {
                if (event.getSelectedItem().isHasPublicKey()) {
                    IDE.fireEvent(new ShowPublicKeyEvent(event.getSelectedItem()));
                }
            }
        });

        display.getKeyItemGrid().addDeleteButtonSelectionHandler(new SelectionHandler<KeyItem>() {

            @Override
            public void onSelection(SelectionEvent<KeyItem> event) {
                deleteSshPublicKey(event.getSelectedItem());
            }
        });

        display.getGenerateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Dialogs.getInstance().askForValue("Generate Ssh Key", "Host name (w/o port): ", "",
                                                  new StringValueReceivedHandler() {

                                                      @Override
                                                      public void stringValueReceived(String value) {
                                                          if (value != null && !"".equals(value)) {
                                                              generateKey(value);
                                                          }
                                                      }
                                                  });
            }
        });

        display.getUploadButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new ShowUploadFormEvent());
            }
        });

        display.getGenerateGithubKeyButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new GenerateGitHubKeyEvent());
            }
        });
    }

    /** Ask user to delete key item. */
    private void deleteSshPublicKey(final KeyItem keyItem) {
        Dialogs.getInstance().ask("IDE", "Do you want to delete ssh keys for <b>" + keyItem.getHost() + "</b> host?",
                                  new BooleanValueReceivedHandler() {
                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value) {
                                              doDeleteKey(keyItem);
                                          }
                                      }
                                  });
    }

    /** Delete key item. */
    private void doDeleteKey(KeyItem keyItem) {
        try {
            SshKeyService.get().deleteKey(keyItem, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    refreshKeys();
                }

                @Override
                protected void onFailure(Throwable e) {
                    Dialogs.getInstance().showError(e.getLocalizedMessage());
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Generate key pair for specific host. */
    private void generateKey(String host) {
        try {
            SshKeyService.get().generateKey(host, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    refreshKeys();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
        if (event.getView() instanceof org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display) {
            refreshKeys();
        }
    }

    /** {@inheritDoc} */
    @Override
    public View getPreference() {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
        }
        refreshKeys();
        return display.asView();
    }

    /** {@inheritDoc} */
    @Override
    public void onRefreshKeys(RefreshKeysEvent event) {
        if (display != null) {
            refreshKeys();
        }
    }
}

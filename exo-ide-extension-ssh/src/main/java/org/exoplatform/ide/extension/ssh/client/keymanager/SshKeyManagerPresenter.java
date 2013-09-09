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
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.preference.PreferencePerformer;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.ssh.client.JsonpAsyncCallback;
import org.exoplatform.ide.extension.ssh.client.SshKeyService;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.RefreshKeysEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.RefreshKeysHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.ui.HasSshGrid;
import org.exoplatform.ide.extension.ssh.client.marshaller.SshKeysUnmarshaller;
import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeyManagerPresenter May 18, 2011 10:16:44 AM evgen $
 */
public class SshKeyManagerPresenter implements ShowSshKeyManagerHandler, ViewClosedHandler,
                                   ConfigurationReceivedSuccessfullyHandler, PreferencePerformer, RefreshKeysHandler {
    public interface Display extends IsView {
        String ID = "ideSshKeyManagerView";

        HasSshGrid<KeyItem> getKeyItemGrid();

        HasClickHandlers getGenerateButton();

        HasClickHandlers getUploadButton();

        HasClickHandlers getGenerateGithubKeyButton();

    }

    private Display          display;

    private IDEConfiguration configuration;

    /**
     *
     */
    public SshKeyManagerPresenter() {
        IDE.addHandler(ShowSshKeyManagerEvent.TYPE, this);
        IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
        // add hendler to handle Upload ssh key form closing, and refresh list of ssh keys
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(RefreshKeysEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerHandler#onShowSshKeyManager(org.exoplatform.ide
     *      .extension.ssh.client.keymanager.event.ShowSshKeyManagerEvent)
     */
    @Override
    public void onShowSshKeyManager(ShowSshKeyManagerEvent event) {
        if (display != null) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();

        refreshKeys();
    }

    /**
     *
     */
    private void refreshKeys() {
        SshKeyService.get().getAllKeys(new JsonpAsyncCallback<JavaScriptObject>() {

            @Override
            public void onSuccess(JavaScriptObject result) {
                getLoader().hide();
                try {
                    display.getKeyItemGrid().setValue(SshKeysUnmarshaller.unmarshal(result));
                } catch (UnmarshallerException e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                getLoader().hide();
                IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    /**
     *
     */
    private void bindDisplay() {
        display.getKeyItemGrid().addViewButtonSelectionHandler(new SelectionHandler<KeyItem>() {

            @Override
            public void onSelection(SelectionEvent<KeyItem> event) {
                if (event.getSelectedItem().getPublicKeyURL() != null) {
                    IDE.fireEvent(new ShowPublicSshKeyEvent(event.getSelectedItem()));
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
                new UploadSshKeyPresenter();
            }
        });

        display.getGenerateGithubKeyButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new GenerateGitHubKeyEvent());
            }
        });
    }

    /** @param keyItem */
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

    /** @param keyItem */
    private void doDeleteKey(KeyItem keyItem) {
        SshKeyService.get().deleteKey(keyItem, new JsonpAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                getLoader().hide();
                refreshKeys();
            }

            @Override
            public void onFailure(Throwable exception) {
                getLoader().hide();
                IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    private void generateKey(String host) {
        try {
            SshKeyService.get().generateKey(host, new AsyncRequestCallback<GenKeyRequest>() {
                @Override
                protected void onSuccess(GenKeyRequest result) {
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

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
        if (event.getView() instanceof org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display) {
            refreshKeys();
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.configuration.event
     *      .ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration
     *      .event.ConfigurationReceivedSuccessfullyEvent)
     */
    @Override
    public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event) {
        configuration = event.getConfiguration();
    }

    /** @see org.exoplatform.ide.client.framework.preference.PreferencePerformer#getPreference() */
    @Override
    public View getPreference() {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
        }
        refreshKeys();
        return display.asView();
    }

    @Override
    public void onRefreshKeys(RefreshKeysEvent event) {
        if (display != null) {
            refreshKeys();
        }
    }
}

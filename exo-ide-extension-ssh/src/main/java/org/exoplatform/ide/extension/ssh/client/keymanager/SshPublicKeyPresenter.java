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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;
import org.exoplatform.ide.extension.ssh.client.SshKeyService;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicKeyHandler;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.extension.ssh.shared.PublicKey;

/** Showing to user public key contents. */
public class SshPublicKeyPresenter implements ViewClosedHandler, ShowPublicKeyHandler {

    public interface Display extends IsView {
        HasClickHandlers getCloseButton();

        HasValue<String> getKeyField();

        void addHostToTitle(String host);
    }

    private Display display;

    public SshPublicKeyPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ShowPublicKeyEvent.TYPE, this);
    }

    @Override
    public void onShowPublicSshKey(ShowPublicKeyEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
        }

        bind();

        showPublicKey(event.getKeyItem());
    }

    /** Show public key content. */
    private void showPublicKey(KeyItem keyItem) {
        AutoBean<PublicKey> autoBean = SshKeyExtension.AUTO_BEAN_FACTORY.publicKey();
        AutoBeanUnmarshaller<PublicKey> unmarshaller = new AutoBeanUnmarshaller<PublicKey>(autoBean);

        try {
            SshKeyService.get().getPublicKey(keyItem, new AsyncRequestCallback<PublicKey>(unmarshaller) {
                @Override
                protected void onSuccess(PublicKey result) {
                    display.addHostToTitle(result.getHost());
                    display.getKeyField().setValue(result.getKey());

                    IDE.getInstance().openView(display.asView());
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

    /** Binding components. */
    private void bind() {
        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}

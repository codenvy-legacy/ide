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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.ssh.client.JsonpAsyncCallback;
import org.exoplatform.ide.extension.ssh.client.SshKeyService;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: SshPublicKeyPresenter May 19, 2011 12:32:14 PM evgen $
 */
public class SshPublicKeyPresenter implements ViewClosedHandler {

    public interface Display extends IsView {

        HasClickHandlers getCloseButton();

        HasValue<String> getKeyField();

        void addHostToTitle(String host);

    }

    private KeyItem keyItem;

    private HandlerRegistration viewClosedHandler;

    private Display display;

    /**
     *
     */
    public SshPublicKeyPresenter(KeyItem keyItem) {
        this.keyItem = keyItem;
        viewClosedHandler = IDE.addHandler(ViewClosedEvent.TYPE, this);

        display = GWT.create(Display.class);

        bind();

        display.addHostToTitle(keyItem.getHost());

        IDE.getInstance().openView(display.asView());

        showPublicKey();
    }

    /**
     *
     */
    private void showPublicKey() {
        SshKeyService.get().getPublicKey(keyItem, new JsonpAsyncCallback<JavaScriptObject>() {

            @Override
            public void onSuccess(JavaScriptObject result) {
                getLoader().hide();
                JSONObject key = new JSONObject(result);
                display.getKeyField().setValue(key.get("key").isString().stringValue());
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
    private void bind() {
        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            viewClosedHandler.removeHandler();
        }
    }

}

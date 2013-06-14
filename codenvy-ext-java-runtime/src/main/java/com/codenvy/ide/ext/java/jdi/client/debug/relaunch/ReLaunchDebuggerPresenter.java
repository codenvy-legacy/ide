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
package com.codenvy.ide.ext.java.jdi.client.debug.relaunch;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.client.marshaller.DebuggerInfoUnmarshaller;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.ApplicationInstance;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Provides relaunch debugger process.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
@Singleton
public class ReLaunchDebuggerPresenter implements ReLaunchDebuggerView.ActionDelegate {
    private ReLaunchDebuggerView        view;
    private DebuggerClientService       service;
    private ApplicationInstance         instance;
    private ConsolePart                 console;
    private AsyncCallback<DebuggerInfo> callback;
    /** A timer for checking events. */
    private Timer tryConnectDebuger = new Timer() {
        @Override
        public void run() {
            connectDebugger();
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param console
     */
    @Inject
    protected ReLaunchDebuggerPresenter(ReLaunchDebuggerView view, DebuggerClientService service, ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.console = console;
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        tryConnectDebuger.cancel();
        view.close();
        callback.onSuccess(null);
    }

    /** Shows dialog. */
    public void showDialog(@NotNull ApplicationInstance instance, @NotNull AsyncCallback<DebuggerInfo> callback) {
        this.instance = instance;
        this.callback = callback;
        this.view.showDialog();
    }

    /** Connect to debbuger. */
    protected void connectDebugger() {
        DtoClientImpls.DebuggerInfoImpl debuggerInfo = DtoClientImpls.DebuggerInfoImpl.make();
        DebuggerInfoUnmarshaller unmarshaller = new DebuggerInfoUnmarshaller(debuggerInfo);

        try {
            service.connect(instance.getDebugHost(), instance.getDebugPort(), new AsyncRequestCallback<DebuggerInfo>(unmarshaller) {
                @Override
                public void onSuccess(DebuggerInfo result) {
                    tryConnectDebuger.cancel();
                    view.close();
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    console.print(exception.getMessage());
                    callback.onFailure(exception);

                }
            });
        } catch (RequestException e) {
            console.print(e.getMessage());
            callback.onFailure(e);
        }
    }
}
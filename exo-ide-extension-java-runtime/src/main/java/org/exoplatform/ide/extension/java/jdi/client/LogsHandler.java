/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.java.jdi.client.events.*;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 22, 2012 2:57:45 PM anya $
 */
public class LogsHandler implements ShowLogsHandler, AppStartedHandler, AppStoppedHandler {
    private ApplicationInstance application;

    public LogsHandler() {
        IDE.getInstance().addControl(new ShowLogsControl());

        IDE.addHandler(ShowLogsEvent.TYPE, this);
        IDE.addHandler(AppStartedEvent.TYPE, this);
        IDE.addHandler(AppStoppedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.ShowLogsHandler#onShowLogs(org.exoplatform.ide.extension.java.jdi.client
     * .events.ShowLogsEvent) */
    @Override
    public void onShowLogs(ShowLogsEvent event) {
        if (application != null) {
            getLogs();
        } else {
            Dialogs.getInstance().showInfo(DebuggerExtension.LOCALIZATION_CONSTANT.noRunningApplicationMessage());
        }
    }

    private void getLogs() {
        try {
            ApplicationRunnerClientService.getInstance().getLogs(application.getName(),
                                                                 new AsyncRequestCallback<StringBuilder>(
                                                                         new StringUnmarshaller(new StringBuilder())) {
                                                                     @Override
                                                                     protected void onSuccess(StringBuilder result) {
                                                                         IDE.fireEvent(
                                                                                 new OutputEvent("<pre>" + result.toString() + "</pre>",
                                                                                                 Type.OUTPUT));
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

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.AppStoppedHandler#onAppStopped(org.exoplatform.ide.extension.java.jdi
     * .client.events.AppStoppedEvent) */
    @Override
    public void onAppStopped(AppStoppedEvent appStopedEvent) {
        this.application = null;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.AppStartedHandler#onAppStarted(org.exoplatform.ide.extension.java.jdi
     * .client.events.AppStartedEvent) */
    @Override
    public void onAppStarted(AppStartedEvent event) {
        this.application = event.getApplication();
    }
}

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
package com.codenvy.ide.extension.html.client.logs;

import com.codenvy.ide.extension.html.client.HtmlRuntimeExtension;
import com.codenvy.ide.extension.html.client.HtmlRuntimeService;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStartedEvent;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStartedHandler;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedEvent;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedHandler;
import com.codenvy.ide.extension.html.shared.ApplicationInstance;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: LogsHandler.java Jun 26, 2013 11:05:33 AM azatsarynnyy $
 */
public class LogsHandler implements ShowLogsHandler, ApplicationStartedHandler, ApplicationStoppedHandler {
    private ApplicationInstance runApplication;

    public LogsHandler() {
        IDE.getInstance().addControl(new ShowLogsControl());

        IDE.addHandler(ShowLogsEvent.TYPE, this);
        IDE.addHandler(ApplicationStartedEvent.TYPE, this);
        IDE.addHandler(ApplicationStoppedEvent.TYPE, this);
    }

    /**
     * @see com.codenvy.ide.extension.html.client.logs.ShowLogsHandler#onShowLogs(com.codenvy.ide.extension.html.client.logs .ShowLogsEvent)
     */
    @Override
    public void onShowLogs(ShowLogsEvent event) {
        if (runApplication != null) {
            getLogs();
        } else {
            Dialogs.getInstance().showInfo(HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.noRunningApplication());
        }
    }

    private void getLogs() {
        try {
            HtmlRuntimeService.getInstance().getLogs(runApplication.getName(),
                                                     new AsyncRequestCallback<StringBuilder>(
                                                                                             new StringUnmarshaller(new StringBuilder())) {

                                                         @Override
                                                         protected void onSuccess(StringBuilder result) {
                                                             IDE.fireEvent(new OutputEvent("<pre>" + result.toString() + "</pre>",
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

    /** @see com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedHandler#onApplicationStopped(com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        this.runApplication = null;
    }

    /** @see com.codenvy.ide.extension.html.client.run.event.ApplicationStartedHandler#onApplicationStarted(com.codenvy.ide.extension.html.client.run.event.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        this.runApplication = event.getApplication();
    }
}

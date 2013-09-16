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
package org.exoplatform.ide.extension.python.client.logs;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.python.client.PythonRuntimeExtension;
import org.exoplatform.ide.extension.python.client.PythonRuntimeService;
import org.exoplatform.ide.extension.python.client.StringUnmarshaller;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedEvent;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedHandler;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedEvent;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedHandler;
import org.exoplatform.ide.extension.python.shared.ApplicationInstance;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 22, 2012 2:57:45 PM anya $
 */
public class LogsHandler implements ShowLogsHandler, ApplicationStartedHandler, ApplicationStoppedHandler {
    private ApplicationInstance runApplication;

    public LogsHandler() {
        IDE.getInstance().addControl(new ShowLogsControl());

        IDE.addHandler(ShowLogsEvent.TYPE, this);
        IDE.addHandler(ApplicationStartedEvent.TYPE, this);
        IDE.addHandler(ApplicationStoppedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.python.client.logs.ShowLogsHandler#onShowLogs(org.exoplatform.ide.extension.python.client.logs
     * .ShowLogsEvent) */
    @Override
    public void onShowLogs(ShowLogsEvent event) {
        if (runApplication != null) {
            getLogs();
        } else {
            Dialogs.getInstance().showInfo(PythonRuntimeExtension.PYTHON_LOCALIZATION.noRunningApplication());
        }
    }

    private void getLogs() {
        try {
            PythonRuntimeService.getInstance().getLogs(runApplication.getName(),
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

    /** @see org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedHandler#onApplicationStopped(org.exoplatform.ide
     * .extension.python.client.run.event.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        this.runApplication = null;
    }

    /** @see org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedHandler#onApplicationStarted(org.exoplatform.ide
     * .extension.python.client.run.event.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        this.runApplication = event.getApplication();
    }
}

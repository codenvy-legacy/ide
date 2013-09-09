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
package org.exoplatform.ide.extension.openshift.client;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Handler for OpenShift exceptions. Error, handled by {@link OpenShiftExceptionsHandler} is passed with
 * {@link OpenShiftExceptionThrownEvent} event. Checks whether it is {@link ServerException} and contains provided message and
 * Express exit code. Alternative error message also can be passed by {@link OpenShiftExceptionThrownEvent}.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 10, 2011 5:08:56 PM anya $
 */
public class OpenShiftExceptionsHandler implements OpenShiftExceptionThrownHandler {
    /**
     *
     */
    public OpenShiftExceptionsHandler() {
        IDE.addHandler(OpenShiftExceptionThrownEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownHandler#onOpenShiftExceptionThrown(org.exoplatform.ide
     * .extension.openshift.client.OpenShiftExceptionThrownEvent) */
    @Override
    public void onOpenShiftExceptionThrown(OpenShiftExceptionThrownEvent event) {
        Throwable error = event.getException();

        if (error instanceof ServerException) {
            ServerException serverException = (ServerException)error;
            String exitCode = serverException.getHeader("Express-Exit-Code");
            String expressExitCodeMessage =
                    (exitCode != null) ? "<br>" + OpenShiftExtension.LOCALIZATION_CONSTANT.expressExitCode(exitCode) : "";

            if (serverException.isErrorMessageProvided()) {
                String html =
                        "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText() + "<br><br><hr><br>"
                        + serverException.getMessage() + expressExitCodeMessage;
                Dialogs.getInstance().showError(html);
            } else {
                String html = "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText();

                if (event.getErrorMessage() != null) {
                    html += "<br><hr><br>Possible reasons:<br>" + event.getErrorMessage() + expressExitCodeMessage;
                }
                Dialogs.getInstance().showError(html);
            }
        } else {
            if (error != null)
                Dialogs.getInstance().showError(error.getMessage());
            else
                Dialogs.getInstance().showError(event.getErrorMessage());
        }
    }

}

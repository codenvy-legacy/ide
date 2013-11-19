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
package org.exoplatform.ide.client;

import com.codenvy.ide.client.util.logging.Log;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerDisconnectedException;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ExceptionThrownEventHandler implements ExceptionThrownHandler {

    private static final String EXIT_CODE = "X-Exit-Code";

    public ExceptionThrownEventHandler() {
        IDE.addHandler(ExceptionThrownEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception
     *      .ExceptionThrownEvent)
     */
    public void onError(ExceptionThrownEvent event) {
        if (event.getException() == null) {
            Dialogs.getInstance().showError(event.getErrorMessage());
            return;
        }
        
        Throwable error = event.getException();
        Log.error(getClass(), error);
        error.printStackTrace();

        if (error instanceof UnauthorizedException) {
            return;
        }

        if (error instanceof ServerDisconnectedException) {
            showServerDisconnectedDialog((ServerDisconnectedException)error);
            return;
        }

        if (error instanceof ServerException) {
            ServerException serverException = (ServerException)error;
            processServerError(serverException, event.getErrorMessage());
        } else {
            Dialogs.getInstance().showError(error.getMessage());
        }
    }

    private void showServerDisconnectedDialog(final ServerDisconnectedException exception) {
        String message = IDE.IDE_LOCALIZATION_CONSTANT.serverDisconnected();
        Dialogs.getInstance().showError(message);
    }

    private void processServerError(ServerException serverException, String errorMessage) {
        if (serverException.isErrorMessageProvided()
            || (serverException.getHeader(EXIT_CODE) != null && !"0".equals(serverException.getHeader(EXIT_CODE)))) {
            String html =
                    "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText() + "<br><br><hr><br>"
                    + serverException.getMessage();
            Dialogs.getInstance().showError(html);
        } else {
            String html = "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText();
            if (errorMessage != null) {
                html += "<br><hr><br>Possible reasons:<br>" + errorMessage;
            }
            Dialogs.getInstance().showError(html);
        }
    }
}

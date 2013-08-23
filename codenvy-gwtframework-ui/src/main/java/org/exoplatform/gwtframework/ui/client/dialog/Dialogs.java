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

package org.exoplatform.gwtframework.ui.client.dialog;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class Dialogs {

    private static Dialogs  instance;

    protected Queue<Dialog> dialogs = new LinkedList<Dialog>();

    protected Dialog        currentDialog;

    protected Dialogs() {
        instance = this;
    }

    public static Dialogs getInstance() {
        return instance;
    }

    /*
     * INFO
     */

    public void showInfo(String message) {
        showInfo("IDE", message, null);
    }

    public void showInfo(String title, String message) {
        showInfo(title, message, null);
    }

    public void showInfo(String title, String message, BooleanValueReceivedHandler handler) {
        showInfo(title, message, handler, true);
    }

    public void showInfo(String title, String message, BooleanValueReceivedHandler handler, boolean modal) {
        Dialog dialog = new Dialog(title, message + "<br>&nbsp;", Dialog.Type.INFO, modal);
        dialog.setBooleanValueReceivedHandler(handler);
        showDialog(dialog);
    }

    /*
     * WARNING
     */

    public void showError(String message) {
        showError("IDE", message, null);
    }

    public void showError(String title, String message) {
        showError(title, message, null);
    }

    public void showError(String title, String message, BooleanValueReceivedHandler handler) {
        showError(title, message, handler, true);
    }

    public void showError(String title, String message, BooleanValueReceivedHandler handler, boolean modal) {
        Dialog dialog = new Dialog(title, message + "<br>&nbsp;", Dialog.Type.WARNING, modal);
        dialog.setBooleanValueReceivedHandler(handler);
        showDialog(dialog);
    }

    /*
     * BOOLEAN ASKING
     */

    public void ask(String title, String message, BooleanValueReceivedHandler handler) {
        ask(title, message, handler, true);
    }

    public void ask(String title, String message, BooleanValueReceivedHandler handler, boolean modal) {
        Dialog dialog = new Dialog(title, message + "<br>&nbsp;", Dialog.Type.ASK, modal);
        dialog.setBooleanValueReceivedHandler(handler);
        showDialog(dialog);
    }

    /*
     * VALUE ASKING
     */

    public void askForValue(String title, String message, String defaultValue, StringValueReceivedHandler handler) {
        askForValue(title, message, defaultValue, handler, true);
    }

    public void askForValue(String title, String message, String defaultValue, StringValueReceivedHandler handler, boolean modal) {
        Dialog dialog = new Dialog(title, message + "<br>&nbsp;", Dialog.Type.ASKVALUE, modal);
        dialog.setDefaultValue(defaultValue);
        dialog.setStringValueReceivedHandler(handler);
        showDialog(dialog);
    }

    /*
     * OPEN CUSTOM DIALOG
     */

    public void showDialog(Dialog dialog) {
        dialogs.add(dialog);
        if (currentDialog != null) {
            return;
        }

        showQueueDialog();
    }

    protected void showQueueDialog() {
        currentDialog = null;

        if (dialogs.size() == 0) {
            return;
        }

        currentDialog = dialogs.poll();

        if (currentDialog.getType() == Dialog.Type.ASKVALUE) {
            openAskForValueDialog(currentDialog);
        } else if (currentDialog.getType() == Dialog.Type.ASK) {
            openAskDialog(currentDialog);
        } else if (currentDialog.getType() == Dialog.Type.WARNING) {
            openWarningDialog(currentDialog);
        } else {
            openInfoDialog(currentDialog);
        }
    }

    /**
     * protected abstract void openAskForValueDialog(String title, String message, String defaultValue); protected abstract void
     * openAskDialog(String title, String message); protected abstract void openWarningDialog(String title, String message); protected
     * abstract void openInfoDialog(String title, String message);
     */

    protected abstract void openAskForValueDialog(Dialog dialog);

    protected abstract void openAskDialog(Dialog dialog);

    protected abstract void openWarningDialog(Dialog dialog);

    protected abstract void openInfoDialog(Dialog dialog);

}

/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
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

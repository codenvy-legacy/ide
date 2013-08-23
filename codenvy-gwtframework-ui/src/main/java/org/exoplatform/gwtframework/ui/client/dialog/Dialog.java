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


/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Dialog {

    public enum Type {
        ASK,
        ASKVALUE,
        INFO,
        WARNING

    }

    private BooleanValueReceivedHandler booleanValueReceivedHandler;
    private String                      defaultValue;
    private String                      message;
    private StringValueReceivedHandler  stringValueReceivedHandler;
    private String                      title;
    private String                      confirmButton;
    private String                      cancelButton;
    private Type type;
    private boolean modal;

    public Dialog(String title, String message, Type type) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.modal = false;
    }

    public Dialog(String title, String message, Type type, boolean modal) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.modal = modal;
    }

    public Dialog(String title, String message, String confirmButton, Type type, boolean modal) {
        this.title = title;
        this.message = message;
        this.confirmButton = confirmButton;
        this.type = type;
        this.modal = modal;
    }

    public BooleanValueReceivedHandler getBooleanValueReceivedHandler() {
        return booleanValueReceivedHandler;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getMessage() {
        return message;
    }

    public StringValueReceivedHandler getStringValueReceivedHandler() {
        return stringValueReceivedHandler;
    }

    public String getTitle() {
        return title;
    }

    public boolean getModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public Type getType() {
        return type;
    }

    public void setBooleanValueReceivedHandler(BooleanValueReceivedHandler booleanValueReceivedHandler) {
        this.booleanValueReceivedHandler = booleanValueReceivedHandler;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStringValueReceivedHandler(StringValueReceivedHandler stringValueReceivedHandler) {
        this.stringValueReceivedHandler = stringValueReceivedHandler;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getConfirmButton() {
        return confirmButton;
    }

    public void setConfirmButton(String confirmButton) {
        this.confirmButton = confirmButton;
    }

    public String getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(String cancelButton) {
        this.cancelButton = cancelButton;
    }
}

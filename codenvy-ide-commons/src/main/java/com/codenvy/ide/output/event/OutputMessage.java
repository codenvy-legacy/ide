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
package com.codenvy.ide.output.event;

/** @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a> */

public class OutputMessage {

    public enum Type {

        INFO, ERROR, WARNING, LOG, OUTPUT

    }

    private String message;

    private OutputMessage.Type type;

    public OutputMessage(String message, OutputMessage.Type type) {
        this.message = message;
        this.type = type;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

    /** @return the type */
    public OutputMessage.Type getType() {
        return type;
    }
}
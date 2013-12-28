/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.ant.tools;

import java.io.Serializable;

/** @author andrew00x */
public class AntMessage implements Serializable {
    public static final int BUILD_ERROR      = -1;
    public static final int BUILD_STARTED    = 1;
    public static final int BUILD_SUCCESSFUL = 1 << 1;
    public static final int BUILD_LOG        = 1 << 2;

    private static final long serialVersionUID = 6112041830147092037L;

    private int    type;
    private String target;
    private String task;
    private String text;

    public AntMessage(int type, String target, String task, String text) {
        this.type = type;
        this.target = target;
        this.task = task;
        this.text = text;
    }

    public AntMessage(int type) {
        this(type, null, null, null);
    }

    public AntMessage() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getText() {
        return text;
    }

    public void setText(String message) {
        this.text = message;
    }

    @Override
    public String toString() {
        return "AntMessage{" +
               "type=" + type +
               ", target='" + target + '\'' +
               ", task='" + task + '\'' +
               ", text='" + text + '\'' +
               '}';
    }
}

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
package com.codenvy.runner.docker.json;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class ContainerState {
    private boolean running;
    private int     pid;
    private int     exitCode;
    // Date format: yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX
    private String  startedAt;
    private boolean ghost;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public boolean isGhost() {
        return ghost;
    }

    public void setGhost(boolean ghost) {
        this.ghost = ghost;
    }

    @Override
    public String toString() {
        return "ContainerState{" +
               "running=" + running +
               ", pid=" + pid +
               ", exitCode=" + exitCode +
               ", startedAt='" + startedAt + '\'' +
               ", ghost=" + ghost +
               '}';
    }
}

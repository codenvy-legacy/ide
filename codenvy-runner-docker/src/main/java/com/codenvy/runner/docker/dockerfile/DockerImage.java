/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.runner.docker.dockerfile;

import com.codenvy.api.core.util.Pair;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author andrew00x
 */
public class DockerImage {
    private String                     from;
    private List<String>               maintainer;
    private List<String>               run;
    private String                     cmd;
    private List<String>               expose;
    private Map<String, String>        env;
    private List<Pair<String, String>> add;
    private String                     entrypoint;
    private List<String>               volume;
    private String                     user;
    private String                     workdir;
    private List<String>               onbuild;
    private List<String>               comments;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getMaintainer() {
        if (maintainer == null) {
            maintainer = new LinkedList<>();
        }
        return maintainer;
    }

    public List<String> getRun() {
        if (run == null) {
            run = new LinkedList<>();
        }
        return run;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public List<String> getExpose() {
        if (expose == null) {
            expose = new LinkedList<>();
        }
        return expose;
    }

    public Map<String, String> getEnv() {
        if (env == null) {
            env = new LinkedHashMap<>();
        }
        return env;
    }

    public List<Pair<String, String>> getAdd() {
        if (add == null) {
            add = new LinkedList<>();
        }
        return add;
    }

    public String getEntrypoint() {
        return entrypoint;
    }

    public void setEntrypoint(String entrypoint) {
        this.entrypoint = entrypoint;
    }

    public List<String> getVolume() {
        if (volume == null) {
            volume = new LinkedList<>();
        }
        return volume;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWorkdir() {
        return workdir;
    }

    public void setWorkdir(String workdir) {
        this.workdir = workdir;
    }

    public List<String> getOnbuild() {
        if (onbuild == null) {
            onbuild = new LinkedList<>();
        }
        return onbuild;
    }

    public List<String> getComments() {
        if (comments == null) {
            comments = new LinkedList<>();
        }
        return comments;
    }
}

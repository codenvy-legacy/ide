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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** @author andrew00x */
public class ContainerInfo {
    private String          id;
    // Date format: yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX
    private String          created;
    private String          path;
    private String[]        args;
    private ContainerConfig config;
    private ContainerState  state;
    private String          image;
    private NetworkSettings networkSettings;
    private String          sysInitPath;
    private String          resolvConfPath;
    private Map<String, String> volumes = new HashMap<>();

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public ContainerConfig getConfig() {
        return config;
    }

    public void setConfig(ContainerConfig config) {
        this.config = config;
    }

    public ContainerState getState() {
        return state;
    }

    public void setState(ContainerState state) {
        this.state = state;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public NetworkSettings getNetworkSettings() {
        return networkSettings;
    }

    public void setNetworkSettings(NetworkSettings networkSettings) {
        this.networkSettings = networkSettings;
    }

    public String getSysInitPath() {
        return sysInitPath;
    }

    public void setSysInitPath(String sysInitPath) {
        this.sysInitPath = sysInitPath;
    }

    public String getResolvConfPath() {
        return resolvConfPath;
    }

    public void setResolvConfPath(String resolvConfPath) {
        this.resolvConfPath = resolvConfPath;
    }

    public Map<String, String> getVolumes() {
        return volumes;
    }

    public void setVolumes(Map<String, String> volumes) {
        this.volumes = volumes;
    }

    @Override
    public String toString() {
        return "ContainerInfo{" +
               "id='" + id + '\'' +
               ", created='" + created + '\'' +
               ", path='" + path + '\'' +
               ", args=" + Arrays.toString(args) +
               ", config=" + config +
               ", state=" + state +
               ", image='" + image + '\'' +
               ", networkSettings=" + networkSettings +
               ", sysInitPath='" + sysInitPath + '\'' +
               ", resolvConfPath='" + resolvConfPath + '\'' +
               ", volumes=" + volumes +
               '}';
    }
}

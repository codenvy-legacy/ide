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
package org.exoplatform.ide.testframework.server.cloudfoundry.bean;

import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryApplication {
    private String name;

    private List<String> uris;

    private int instances;

    private int runningInstances;

    private String state;

    private List<String> services;

    private String version;

    private List<String> env;

    private CloudfoundryApplicationResources resources;

    private Staging staging;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public int getRunningInstances() {
        return runningInstances;
    }

    public void setRunningInstances(int runningInstances) {
        this.runningInstances = runningInstances;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getEnv() {
        return env;
    }

    public void setEnv(List<String> env) {
        this.env = env;
    }

    public CloudfoundryApplicationResources getResources() {
        return resources;
    }

    public void setResources(CloudfoundryApplicationResources resources) {
        this.resources = resources;
    }

    public Staging getStaging() {
        return staging;
    }

    public void setStaging(Staging staging) {
        this.staging = staging;
    }
}

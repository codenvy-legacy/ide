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
package com.codenvy.runner.docker;

/**
 * Describes docker based environment for deploy an application. Environment might be described in file 'dockerenv.c5y.json' in root folder
 * of project. The following snippet is an example.
 * <pre>
 *     {
 *         "description":"Tomcat7",
 *         "webPort":8080,
 *         "debugPort":8000,
 *         "runDockerfileName":"run.dc5y",
 *         "debugDockerfileName":"debug.dc5y"
 *     }
 * </pre>
 * Valid keys and values for the <i>dockerenv.c5y.json</i> file include the following:
 * <ul>
 * <li><b>description</b> - description of environment. It is possible to provide more than one environment for one type of application,
 * e.g. run java application with different application servers. Description helps user recognize specific of the environment.
 * </li>
 * <li><b>webPort</b> - runner uses webPort value to connect the Docker container and route requests from the Internet to the user
 * application. This value may be omitted for other than web application.</li>
 * <li><b>debugPort</b> - runner uses debugPort value when application is running under debugger. This value may be omitted if debug is not
 * supported.</li>
 * <li><b>runDockerfileName</b> - name of dockerfile that must be used for starting application. Default value is <i>run.dc5y</i>.</li>
 * <li><b>debugDockerfileName</b> - name of dockerfile that must be used for starting application under debugger. Default value is
 * <i>debug.dc5y</i>.</li>
 * </ul>
 *
 * @author andrew00x
 */
public class DockerEnvironment {
    private String id;
    private String description;

    private int    webPort             = -1;
    private int    debugPort           = -1;
    private String runDockerfileName   = "run.dc5y";
    private String debugDockerfileName = "debug.dc5y";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public String getRunDockerfileName() {
        return runDockerfileName;
    }

    public void setRunDockerfileName(String runDockerfileName) {
        this.runDockerfileName = runDockerfileName;
    }

    public String getDebugDockerfileName() {
        return debugDockerfileName;
    }

    public void setDebugDockerfileName(String debugDockerfileName) {
        this.debugDockerfileName = debugDockerfileName;
    }

    @Override
    public String toString() {
        return "DockerEnvironment{" +
               "id='" + id + '\'' +
               ", description='" + description + '\'' +
               ", webPort=" + webPort +
               ", debugPort=" + debugPort +
               ", runDockerfileName='" + runDockerfileName + '\'' +
               ", debugDockerfileName='" + debugDockerfileName + '\'' +
               '}';
    }
}
